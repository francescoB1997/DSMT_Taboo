-module(player_handler).
-export([login/2, send_msg_to_friends/2, attemptGuessWord/2, wakeUpAllGuessers/1, assignTabooCard/1, getRandomTabooCard/0, send_result_checkWord/2]).


login (DecodedJson, State = {EmptyUser, EmptyRole, EmptyPrompterName, EmptyFriends, GenericMessage, EmptyTabooCard}) ->
% This function creates an ErlangProcess for the relative Taboo-Player
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
            %io:format("I am ~p, and I'm going to sleep ~n", [Username]),
            receive
                { start } ->
                    %io:format("I am ~p, WOKE UP -> received start ~n", [Username]),
                    JsonResponse = jsx:encode([{<<"action">>, wakeUpGuesser}])
            end;
        true ->
            JsonResponse = jsx:encode([{<<"action">>, loginOk}])
    end,
    { {text, JsonResponse} , {Username, Role, PrompterName, FriendList, GenericMessage , EmptyTabooCard} }.


send_msg_to_friends( DecodedJson, State = {Username, Role, PrompterName, FriendList, GenericMessage, TabooCard} ) when Role == <<"Prompter">> ->
% This function it's used only by the Prompter to send a message. So it must be checked if the Prompter breaks the Taboo-Rules
    MessageToFriend = maps:get(<<"msg">>, DecodedJson),
    %io:format("The "send" sent da ~p, genericMess =~p~n", [Username, MessageToFriend]),
    Result = checkTabooWords(MessageToFriend, TabooCard),
    if
        Result == [[],[],[],[],[],[]] -> % If there are no error...
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


send_msg_to_friends( DecodedJson, State = {Username, Role, PrompterName, FriendList, GenericMessage, TabooCard} ) when Role == <<"Guesser">> ->
% This function broadcast the message to all Friend in the FriendList
    MessageToFriend = maps:get(<<"msg">>, DecodedJson),
    [ send_msg(msgFromFriend, MessageToFriend, Friend) || Friend <- FriendList ], % Foreach equivalent
    JsonResponse = jsx:encode([{<<"action">>, ignore}]),
    { {text, JsonResponse} , State}.


attemptGuessWord(DecodedJson, State = {Username, Role, PrompterName, FriendList, GenericMessage, TabooCard}) when Role == <<"Guesser">> ->
% This function checks if the Guesser guesses the Word
    AttemptedWord = maps:get(<<"word">>, DecodedJson),
    io:format("ATTEMPT GUESS: sender=| ~p |, word=| ~p | , myPrompter=| ~p |~n", [Username, AttemptedWord, PrompterName]),
    MyPrompterPID = global:whereis_name(PrompterName),
    case(is_pid(MyPrompterPID)) of
            true ->
                io:format("ATTEMPT GUESS: | ~p | waiting for result~n", [Username]),
                ResultAttempt = waitResult(MyPrompterPID, AttemptedWord);
            false ->
                ResultAttempt = false,
                io:format("ATTEMPT GUESS: unknown PID Prompter from | ~p |~n", [Username])
    end,
    %io:format("ATTEMPT_GUESS: Sending JSON with ~p~n", [ResultAttempt]),
    JsonResponse = jsx:encode([{<<"action">>, attemptGuessWord}, {<<"msg">>, ResultAttempt}]),
    { {text, JsonResponse} , {Username, Role, PrompterName, FriendList, GenericMessage, TabooCard}}.



assignTabooCard(State = {Username, Role, PrompterName, FriendList, GenericMessage, OldTabooCard} ) when Role == <<"Prompter">> ->
% This function can be used ONLY by the prompter to ASK a Taboo-Card
    TabooCard = getRandomTabooCard(),
    %io:format("I am ~p, assignTabooCard: Role=~p , NewTaboo = ~p~n", [Username, Role, TabooCard]),
    JsonResponse = jsx:encode([{<<"action">>, tabooCard}, {<<"msg">>, TabooCard}]),
    { { text, JsonResponse } , {Username, Role, PrompterName, FriendList, GenericMessage, TabooCard} }.


wakeUpAllGuessers( State = {Username, Role, PrompterName, FriendList, GenericMessage, OldTabooCard} ) when Role == <<"Prompter">> ->
% This function is used ONLY by the prompter to advice all the waiting guessers that the match can start
    [ send_start_msg( Friend ) || Friend <- FriendList ],
    State.


send_start_msg(FriendName) ->
% Utility Function
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
% Utility Function
    %io:format("send_msg(~p, ~p) --> ", [Msg, Friend]),
    PidFriend = global:whereis_name(Friend),
    case(is_pid(PidFriend)) of
        true ->
            PidFriend ! {Atom, Msg},
            %io:format("The sending of "send_msg" ok to ~p, its PID is ~p~n", [Friend, PidFriend]),
            send_ok;
        false ->
            %io:format("The sending of "send_msg" FAILED for ~p~n", [Friend]),
            send_no
    end.

send_result_checkWord([], _) -> ok; % Used only by the prompter to notify all guesser to increase its scoreCounter or not.
send_result_checkWord([Friend | OtherFriends], Result) ->
    PidFriend = global:whereis_name(Friend),
    PidFriend ! {resultAttemptGuessWord, Result},
    send_result_checkWord(OtherFriends, Result).


checkTabooWords(WordList , TabooCard) ->
% This function is responsible to verify if the attemptGuessWord is the right one.
% We exploited the List Comprehension to create a forEach loop. See below
    %io:format("      checkTabooWords of ~p, ~p~n", [WordList, TabooCard]),
    Result = [ [ Word || Word <- WordList , Word == TabooCardWord] || TabooCardWord <- TabooCard],
    Result.


waitResult(MyPrompterPID, AttemptedWord) ->
    MyPrompterPID ! {attemptGuessWord, AttemptedWord},
    receive
        {resultAttemptGuessWord, Result} ->
            io:format("ATTEMPT GUESS: received result | ~p |~n", [Result]),
            Result;
        _ ->
            io:format("ALERT: UNKNOWN CHECKWORD MESSAGE RECEIVED~n"),
            no
    after
        5000 ->
            io:format("ALERT: TIMEOUT RECEIVE RESULT CHECKWORD for | ~p |~n", [AttemptedWord]),
            false
    end.


getRandomTabooCard() ->
    AllTabooCards = [
        [<<"car">>, <<"driver">>, <<"ride">>, <<"transport">>, <<"fast">>, <<"travel">>],
        [<<"dance">>, <<"shoes">>, <<"romantic">>, <<"music">>, <<"sing">>, <<"pole">>],
        [<<"proud">>, <<"feeling">>, <<"accomplish">>, <<"great">>, <<"boast">>, <<"humble">>],
        [<<"husband">>, <<"wife">>, <<"ring">>, <<"marry">>, <<"man">>, <<"friend">>],
        [<<"camera">>, <<"photos">>, <<"pictures">>, <<"snapshot">>, <<"travel">>, <<"memories">>],
        [<<"wok">>, <<"cook">>, <<"china">>, <<"iron">>, <<"dishes">>, <<"pot">>],
        [<<"giraffe">>, <<"tall">>, <<"africa">>, <<"neck">>, <<"long">>, <<"yellow">>],
        [<<"girlfriend">>, <<"boyfriend">>, <<"beautiful">>, <<"flowers">>, <<"date">>, <<"female">>],
        [<<"glasses">>, <<"eyes">>, <<"see">>, <<"contacts">>, <<"wear">>, <<"face">>],
        [<<"popcorn">>, <<"kernel">>, <<"cinema">>, <<"sweet">>, <<"microwave">>, <<"food">>],
        [<<"dragonfly">>, <<"red">>, <<"wings">>, <<"insect">>, <<"dragon">>, <<"fly">>],
        [<<"snowflake">>, <<"cold">>, <<"winter">>, <<"flower">>, <<"snow">>, <<"fall">>],
        [<<"hungry">>, <<"feeling">>, <<"eat">>, <<"food">>, <<"breakfast">>, <<"meal">>],
        [<<"duck">>, <<"bird">>, <<"yellow">>, <<"chicken">>, <<"quack">>, <<"food">>],
        [<<"pillow">>, <<"head">>, <<"sleep">>, <<"soft">>, <<"bed">>, <<"blanket">>],
        [<<"blanket">>, <<"warm">>, <<"bed">>, <<"pillow">>, <<"soft">>, <<"cold">>],
        [<<"shanghai">>, <<"modern">>, <<"big">>, <<"china">>, <<"expo">>, <<"famous">>],
        [<<"soccer">>, <<"ball">>, <<"worldcup">>, <<"black">>, <<"sport">>, <<"team">>],
        [<<"cheese">>, <<"yellow">>, <<"white">>, <<"pizza">>, <<"food">>, <<"italy">>],
        [<<"snail">>, <<"round">>, <<"slow">>, <<"river">>, <<"eat">>, <<"animal">>],
        [<<"dinosaur">>, <<"big">>, <<"animal">>, <<"extinct">>, <<"reptile">>, <<"prehistory">>],
        [<<"japan">>, <<"country">>, <<"asia">>, <<"sushi">>, <<"tokyo">>, <<"flowers">>],
        [<<"underwear">>, <<"small">>, <<"clothing">>, <<"personal ">>, <<"secret">>, <<"under">>],
        [<<"girlfriend">>, <<"boyfriend">>, <<"beautiful">>, <<"flowers">>, <<"date">>, <<"female">>],
        [<<"slippers">>, <<"warm">>, <<"feet">>, <<"house">>, <<"winter">>, <<"soft">>],
        [<<"comb">>, <<"brush">>, <<"hair">>, <<"smooth">>, <<"small">>, <<"beautiful">>],
        [<<"speech">>, <<"give">>, <<"speak">>, <<"important">>, <<"audience">>, <<"nervous">>],
        [<<"furious">>, <<"angry">>, <<"red">>, <<"hurt">>, <<"rage">>, <<"violent">>],
        [<<"crown">>, <<"head">>, <<"gold">>, <<"king">>, <<"queen">>, <<"jewels">>]
    ],
    RandomIndex = getRandomInt(length(AllTabooCards)),
    SelectedTabooCard = lists:nth(RandomIndex, AllTabooCards),
    SelectedTabooCard.

getRandomInt(Max) ->
    RandomIndex = rand:uniform(Max),
    RandomIndex.





