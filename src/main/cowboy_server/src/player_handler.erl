-module(player_handler).
-export([login/2, start/2, send_msg_to_friends/2, attemptGuessWord/2]).

login (DecodedJson, State = {User, Role, Friends, GenericMessage, TabooWord}) ->
    Username = maps:get(<<"username">>, DecodedJson),
    PidPlayer = global:whereis_name(Username),
    case PidPlayer of
        undefined ->
            global:register_name(Username, self());
        _ ->
            global:unregister_name(Username),
            global:register_name(Username, self())
    end,
    io:format("LOGIN: MyPID=~p login ~s~n", [self(), Username]),
    {Username, Role, Friends, " ", TabooWord}.

start (DecodedJson, State = {Username, MyRole, Friends, GenericMessage, TabooCardEmpty}) ->
    FriendList = maps:get(<<"friendList">>, DecodedJson),
    Role = maps:get(<<"role">>, DecodedJson),
    {Resp, TabooCard} = getUpdatedState(Role),
    io:format("START: Username=~p Role=~p FriendList=~p GenericMessage=~p TabooCard=~p ~n", [Username, Role, FriendList,GenericMessage, TabooCard]),
    {Resp, {Username, Role, FriendList, GenericMessage, TabooCard} }.

send_msg_to_friends( DecodedJson, State = {Username, Role, FriendList, GenericMessage, TabooCard} ) ->
    %% if the GenericMessage came from Prompter, then it must be broacasted to all FriendList
    MessageToFriend = maps:get(<<"msg">>, DecodedJson),
    [ send_msg(MessageToFriend, Friend) || Friend <- FriendList ], % Foreach equivalent
    io:format("send_msg_to_friends ~p~n", [FriendList]),
    {Username, Role, FriendList, GenericMessage, TabooCard}.

attemptGuessWord(DecodedJson, State = {Username, Role, [PrompterName | []], GenericMessage, TabooCard}) when Role == <<"Guesser">> ->
    %MyPrompterName = maps:get(<<"myPrompterName">>, DecodedJson),
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
    io:format("ATTEMPT_GUESS: rispondo con ~p~n", [ResultAttempt]),
    JsonResponse = jsx:encode([{<<"action">>, attemptGuessWord}, {<<"msg">>, ResultAttempt}]),
    { {text, JsonResponse} , {Username, Role, [PrompterName | []], GenericMessage, TabooCard}}.

send_msg(Msg, Friend) ->
    io:format("send_msg(~p, ~p) --> ", [Msg, Friend]),
    PidFriend = global:whereis_name(Friend),
    case(is_pid(PidFriend)) of
        true ->
            PidFriend ! {msgFromFriend, Msg},
            io:format(" PID_AMICO = ~p~n", [PidFriend]),
            send_ok;
        false ->
            io:format("Ramo false send_msg  ~n"),
            send_no
    end.

getUpdatedState(MyRole) when MyRole == <<"Prompter">> ->
    getRandomTabooCard();

getUpdatedState(_) ->
    JsonMessage = jsx:encode([{<<"action">>, ignore}, {<<"msg">>, ""}]),
    { {text, JsonMessage} , []}.

getRandomTabooCard() ->
    AllTabooCards = [
        [<<"Car">>, <<"Driver">>, <<"Ride">>, <<"Transport">>, <<"Fast">>, <<"Travel">>],
        [<<"Dance">>, <<"Shoes">>, <<"Romantic">>, <<"Music">>, <<"Sing">>, <<"Town Square">>],
        [<<"Proud">>, <<"Feeling">>, <<"Accomplish">>, <<"Great">>, <<"Boast">>, <<"Humble">>],
        [<<"Husband">>, <<"Wife">>, <<"Ring">>, <<"Marry">>, <<"Man">>, <<"Friend">>],
        [<<"Camera">>, <<"Photos">>, <<"Pictures">>, <<"Snapshot">>, <<"Travel">>, <<"Memories">>]
    ],
    RandomIndex = getRandomInt(length(AllTabooCards)),
    SelectedTabooCard = lists:nth(RandomIndex, AllTabooCards),
    JsonMessage = jsx:encode([{<<"action">>, tabooCard}, {<<"msg">>, SelectedTabooCard}]),
    { {text, JsonMessage} , SelectedTabooCard }.

getRandomInt(Max) ->
    RandomIndex = rand:uniform(Max),
    RandomIndex.

waitResult(MyPrompterPID, AttemptedWord) ->
    MyPrompterPID ! {attemptGuessWord, self(), AttemptedWord},
    receive % mettere una after
        {resultAttemptGuessWord, Result} ->
            io:format("checkWord result: ~p~n", [Result]),
            Result;
        _ ->
            io:format("checkWord messaggio no sense ~n"),
            no
    end.









































%send_msg_to_everyone(_, []) -> ok;
%send_msg_to_everyone(Msg, [Friend | OtherFriends]) -> % [Friend1, Friend2,... FriendN]
%    PidFriend = global:whereis_name(Friend),
%    PidFriend ! {msgFromFriend, Msg},
%    send_msg_to_everyone(Msg, OtherFriends).






