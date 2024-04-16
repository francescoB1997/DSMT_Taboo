-module(player_handler).
-export([login/2, send_msg_to_friends/2, attemptGuessWord/2, wakeUpAllGuessers/1, assignTabooCard/1, getRandomTabooCard/0, send_result_checkWord/2]).


login (DecodedJson, State = {EmptyUser, EmptyRole, EmptyPrompterName, EmptyFriends, GenericMessage, EmptyTabooCard}) ->
    Username = maps:get(<<"username">>, DecodedJson),
    PidPlayer = global:whereis_name(Username),
    case PidPlayer of
        undefined ->
            global:register_name(Username, self());
        _ ->
            global:unregister_name(Username),
            global:register_name(Username, self())
    end,
    PrompterName = maps:get(<<"prompterName">>, DecodedJson),
    FriendList = maps:get(<<"friendList">>, DecodedJson),
    Role = maps:get(<<"role">>, DecodedJson),
    io:format("LOGIN INFO: MyPID=~p i'm=~p Promptername=~p Role=~p FriendList=~p EmptyTabooCard=~p~n", [self(), Username, PrompterName, Role, FriendList, EmptyTabooCard]),
    if
        Role == <<"Guesser">> ->
            %io:format("Sono ~p, mi addormento ~n", [Username]),
            receive
                { start } ->
                    %io:format("Sono ~p, SVEGLIATO -> ricevuto start ~n", [Username]),
                    JsonResponse = jsx:encode([{<<"action">>, wakeUpGuesser}])
            end;
        true ->
            JsonResponse = jsx:encode([{<<"action">>, loginOk}])
    end,
    { {text, JsonResponse} , {Username, Role, PrompterName, FriendList, GenericMessage , EmptyTabooCard} }.


%% when you are PROMPTER
send_msg_to_friends( DecodedJson, State = {Username, Role, PrompterName, FriendList, GenericMessage, TabooCard} ) when Role == <<"Prompter">> ->
    MessageToFriend = maps:get(<<"msg">>, DecodedJson),
    %io:format("send inviata da ~p, genericMess =~p~n", [Username, MessageToFriend]),
    Result = checkTabooWords(MessageToFriend, TabooCard),
    if
        Result == [[],[],[],[],[],[]] ->
            [ send_msg(msgFromFriend, MessageToFriend, Friend) || Friend <- FriendList ], % Foreach equivalent
            io:format("Check Work: | OK |~n"),
            Error = false,
            NewTabooCard = TabooCard;
        true ->
            Error = true,
            NewTabooCard = getRandomTabooCard(),
            [ send_msg(msgFromFriend, errorFromPrompter, Friend) || Friend <- FriendList ], % Foreach equivalent
            io:format("Check Work: | You WRONG => ~p |~n Sending a new TabooCard~n", [Result])
    end,
    UpdateState = {Username, Role, PrompterName, FriendList, GenericMessage, NewTabooCard},
    JsonResponse = jsx:encode([{<<"action">>, checkWordResult}, {<<"msg">>, Error}, {<<"newTabooCard">>, NewTabooCard}]),
    { {text, JsonResponse} , UpdateState};


%% when you are GUESSER
send_msg_to_friends( DecodedJson, State = {Username, Role, PrompterName, FriendList, GenericMessage, TabooCard} ) when Role == <<"Guesser">> ->
    MessageToFriend = maps:get(<<"msg">>, DecodedJson),
    [ send_msg(msgFromFriend, MessageToFriend, Friend) || Friend <- FriendList ], % Foreach equivalent
    JsonResponse = jsx:encode([{<<"action">>, ignore}]),
    { {text, JsonResponse} , State}.


% Only when you are GUESSER
attemptGuessWord(DecodedJson, State = {Username, Role, PrompterName, FriendList, GenericMessage, TabooCard}) when Role == <<"Guesser">> ->
    AttemptedWord = maps:get(<<"word">>, DecodedJson),
    io:format("ATTEMPT GUESS: sender=| ~p |, word=| ~p | , myPrompter=| ~p |~n", [Username, AttemptedWord, PrompterName]),
    MyPrompterPID = global:whereis_name(PrompterName),
    case(is_pid(MyPrompterPID)) of
            true ->
                io:format("ATTEMPT GUESS: | ~p | waiting for result~n", [Username]),
                ResultAttempt = waitResult(MyPrompterPID, AttemptedWord);
            false ->
                ResultAttempt = false,
                io:format("ATTEMPT GUESS: unknown PID Promter from | ~p |~n", [Username])
    end,
    %io:format("ATTEMPT_GUESS: invio JSON con ~p~n", [ResultAttempt]),
    JsonResponse = jsx:encode([{<<"action">>, attemptGuessWord}, {<<"msg">>, ResultAttempt}]),
    { {text, JsonResponse} , {Username, Role, PrompterName, FriendList, GenericMessage, TabooCard}}.


% Only when you are PROMPTER
assignTabooCard(State = {Username, Role, PrompterName, FriendList, GenericMessage, OldTabooCard} ) when Role == <<"Prompter">> ->
    TabooCard = getRandomTabooCard(),
    %io:format("Sono ~p, assignTabooCard: Role=~p , NewTaboo = ~p~n", [Username, Role, TabooCard]),
    JsonResponse = jsx:encode([{<<"action">>, tabooCard}, {<<"msg">>, TabooCard}]),
    { { text, JsonResponse } , {Username, Role, PrompterName, FriendList, GenericMessage, TabooCard} }.


% Only when you are PROMPTER
wakeUpAllGuessers( State = {Username, Role, PrompterName, FriendList, GenericMessage, OldTabooCard} ) when Role == <<"Prompter">> ->
    [ send_start_msg( Friend ) || Friend <- FriendList ],
    State.


send_start_msg(FriendName) ->
    PidFriend = global:whereis_name(FriendName),
    case(is_pid(PidFriend)) of
        true ->
            PidFriend ! { start },
            io:format("Sending START to | ~p |~n", [FriendName]),
            send_ok;
        false ->
            io:format("Sending START failed for | ~p |~n", [FriendName]),
            send_no
    end.


send_msg(Atom, Msg, Friend) ->
    %io:format("send_msg(~p, ~p) --> ", [Msg, Friend]),
    PidFriend = global:whereis_name(Friend),
    case(is_pid(PidFriend)) of
        true ->
            PidFriend ! {Atom, Msg},
            %io:format("Invio send_msg ok a ~p, il suo PID e' ~p~n", [Friend, PidFriend]),
            send_ok;
        false ->
            %io:format("Invio send_msg FALLITO per ~p~n", [Friend]),
            send_no
    end.

send_result_checkWord([], _) -> ok; % Used only by the prompter to notify all guesser to increase its scoreCounter or not.
send_result_checkWord([Friend | OtherFriends], Result) ->
    PidFriend = global:whereis_name(Friend),
    PidFriend ! {resultAttemptGuessWord, Result},
    send_result_checkWord(OtherFriends, Result).


% [si, usa, per, bere] , [bicchiere, bibita, bere, mano, sete, acqua] -----------------------> [[],[],[],[bere],[],[]]
checkTabooWords(WordList , TabooCard) ->
    %io:format("      checkTabooWords di ~p, ~p~n", [WordList, TabooCard]),
    Result = [ [ Word || Word <- WordList , Word == TabooCardWord] || TabooCardWord <- TabooCard],
    Result.


waitResult(MyPrompterPID, AttemptedWord) ->
    MyPrompterPID ! {attemptGuessWord, AttemptedWord},
    receive
        {resultAttemptGuessWord, Result} ->
            io:format("ATTEMPT GUESS: received result | ~p |~n", [Result]),
            Result;
        _ ->
            io:format("****************************** UNKNOWN CHECKWORD MESSAGE RECEIVED~n"),
            no
    after
        5000 ->
            io:format("****************************** TIMEOUT RECEIVE RESULT CHECKWORD for | ~p |~n", [AttemptedWord]),
            false
    end.


getRandomTabooCard() ->
    AllTabooCards = [
        [<<"car">>, <<"driver">>, <<"ride">>, <<"transport">>, <<"fast">>, <<"travel">>],
        [<<"dance">>, <<"shoes">>, <<"romantic">>, <<"music">>, <<"sing">>, <<"town square">>],
        [<<"proud">>, <<"feeling">>, <<"accomplish">>, <<"great">>, <<"boast">>, <<"humble">>],
        [<<"husband">>, <<"wife">>, <<"ring">>, <<"marry">>, <<"man">>, <<"friend">>],
        [<<"camera">>, <<"photos">>, <<"pictures">>, <<"snapshot">>, <<"travel">>, <<"memories">>],
        [<<"wok">>, <<"cook">>, <<"china">>, <<"iron">>, <<"dishes">>, <<"pot">>],
        [<<"giraffe">>, <<"tall">>, <<"africa">>, <<"neck">>, <<"long">>, <<"yellow">>],
        [<<"girlfriend">>, <<"boyfriend">>, <<"beautiful">>, <<"flowers">>, <<"date">>, <<"female">>],
        [<<"glasses">>, <<"eyes">>, <<"see">>, <<"contacts">>, <<"wear">>, <<"face">>],
        [<<"popcorn">>, <<"kernel">>, <<"cinema">>, <<"sweet">>, <<"microwave">>, <<"food">>],
        [<<"erlang">>, <<"evaluation">>, <<"functional">>, <<"programming">>, <<"stateless">>, <<"concurrent">>]
    ],
    RandomIndex = getRandomInt(length(AllTabooCards)),
    SelectedTabooCard = lists:nth(RandomIndex, AllTabooCards),
    SelectedTabooCard.

getRandomInt(Max) ->
    RandomIndex = rand:uniform(Max),
    RandomIndex.





