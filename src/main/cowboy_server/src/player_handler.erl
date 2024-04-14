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
    io:format("LOGIN: MyPID=~p i'm=~p Promptername=~p Role=~p FriendList=~p EmptyTabooCard=~p~n", [self(), Username, PrompterName, Role, FriendList, EmptyTabooCard]),
    if
        Role == <<"Guesser">> ->
            io:format("Sono ~p, mi addormento ~n", [Username]),
            receive
                { start } ->
                    io:format("Sono ~p, SVEGLIATO -> ricevuto start ~n", [Username]),
                    JsonResponse = jsx:encode([{<<"action">>, wakeUpGuesser}])
            end;
        true ->
            JsonResponse = jsx:encode([{<<"action">>, loginOk}])
    end,
    { {text, JsonResponse} , {Username, Role, PrompterName, FriendList, GenericMessage , EmptyTabooCard} }.


%% when you are PROMPTER
send_msg_to_friends( DecodedJson, State = {Username, Role, PrompterName, FriendList, GenericMessage, TabooCard} ) when Role == <<"Prompter">> ->
    MessageToFriend = maps:get(<<"msg">>, DecodedJson),
    io:format("send inviata da ~p, genericMess =~p~n", [Username, MessageToFriend]),
    Result = checkTabooWords(MessageToFriend, TabooCard),
    if
        Result == [[],[],[],[],[],[]] ->
            [ send_msg(msgFromFriend, MessageToFriend, Friend) || Friend <- FriendList ], % Foreach equivalent
            io:format("Result è vuota! => send_msg_to_friends ~p~n", [FriendList]),
            Error = false,
            NewTabooCard = TabooCard;
        true ->
            Error = true,
            NewTabooCard = getRandomTabooCard(),
            [ send_msg(msgFromFriend, errorFromPrompter, Friend) || Friend <- FriendList ], % Foreach equivalent
            io:format("Result è piena! => ~p~n *** NUOVA CARTA ~p~n", [Result, NewTabooCard])
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
    io:format("ATTEMPT_GUESS: io sono ~p, word = ~p, mioPrompter = ~p~n", [Username, AttemptedWord, PrompterName]),
    MyPrompterPID = global:whereis_name(PrompterName),
    case(is_pid(MyPrompterPID)) of
            true ->
                io:format("MyPrompterPID esiste. Attendo...~n"),
                ResultAttempt = waitResult(MyPrompterPID, AttemptedWord);
            false ->
                ResultAttempt = false,
                io:format("MyPrompterPID non esiste: ~n")
    end,
    io:format("ATTEMPT_GUESS: invio JSON con ~p~n", [ResultAttempt]),
    JsonResponse = jsx:encode([{<<"action">>, attemptGuessWord}, {<<"msg">>, ResultAttempt}]),
    { {text, JsonResponse} , {Username, Role, PrompterName, FriendList, GenericMessage, TabooCard}}.


% Only when you are PROMPTER
assignTabooCard(State = {Username, Role, PrompterName, FriendList, GenericMessage, OldTabooCard} ) when Role == <<"Prompter">> ->
    TabooCard = getRandomTabooCard(),
    io:format("Sono ~p, assignTabooCard: Role=~p , NewTaboo = ~p~n", [Username, Role, TabooCard]),
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
            io:format("Invio start a ~p, il suo PID è ~p~n", [FriendName, PidFriend]),
            send_ok;
        false ->
            io:format("Invio start FALLITO per ~p~n", [FriendName]),
            send_no
    end.


send_msg(Atom, Msg, Friend) ->
    %io:format("send_msg(~p, ~p) --> ", [Msg, Friend]),
    PidFriend = global:whereis_name(Friend),
    case(is_pid(PidFriend)) of
        true ->
            PidFriend ! {Atom, Msg},
            io:format("Invio send_msg ok a ~p, il suo PID è ~p~n", [Friend, PidFriend]),
            send_ok;
        false ->
            io:format("Invio send_msg FALLITO per ~p~n", [Friend]),
            send_no
    end.

send_result_checkWord([], _) -> ok; % Used only by the prompter to notify all guesser to increase its scoreCounter or not.
send_result_checkWord([Friend | OtherFriends], Result) ->
    PidFriend = global:whereis_name(Friend),
    PidFriend ! {resultAttemptGuessWord, Result},
    send_result_checkWord(OtherFriends, Result).


% [si, usa, per, bere] , [bicchiere, bibita, bere, mano, sete, acqua] -----------------------> [[],[],[],[bere],[],[]]
checkTabooWords(WordList , TabooCard) ->
    io:format("      checkTabooWords di ~p, ~p~n", [WordList, TabooCard]),
    Result = [ [ Word || Word <- WordList , Word == TabooCardWord] || TabooCardWord <- TabooCard],
    Result.


waitResult(MyPrompterPID, AttemptedWord) ->
    MyPrompterPID ! {attemptGuessWord, AttemptedWord},
    receive
        {resultAttemptGuessWord, Result} ->
            io:format("checkWord result: ~p~n", [Result]),
            Result;
        _ ->
            io:format("checkWord messaggio no sense ~n"),
            no
    after
        5000 ->
            io:format("****************************** Timer scaduto waitResult : ~p~n", [AttemptedWord])
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





