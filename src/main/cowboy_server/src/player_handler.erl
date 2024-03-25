-module(player_handler).
-export([login/2, start/2, send_msg_to_friends/2, startWait/1]).

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
    io:format("PlayerHandler: MyPID=[~p] login ~s~n", [self(), Username]),
    {Username, Role, Friends, GenericMessage, TabooWord}.

start (DecodedJson, State = {Username, MyRole, Friends, GenericMessage, TabooCardEmpty}) ->
    FriendList = maps:get(<<"friendList">>, DecodedJson),
    Role = maps:get(<<"role">>, DecodedJson),
    {Resp, TabooCard} = getUpdatedState(Role),
    io:format("PlayerHandler: Username=~p Role=~p FriendList=[~p] TabooCard=[~p] ~n", [Username, Role, FriendList, TabooCard]),
    {Resp, {Username, Role, FriendList, GenericMessage, TabooCard} }.

send_msg_to_friends( DecodedJson, State = {Username, Role, FriendList, GenericMessage, TabooCard} ) when Role == <<"Prompter">> ->
    %% if the GenericMessage came from Prompter, then it must be broacasted to all FriendList
    MessageToFriend = maps:get(<<"msg">>, DecodedJson),
    [ send_msg(MessageToFriend, Friend) || Friend <- FriendList ], % Foreach equivalent
    io:format("send_msg_to_friends Prompter ~p~n", [FriendList]),
    {Username, Role, FriendList, GenericMessage, TabooCard}; % aggiungere nello stato anche il Taboo

send_msg_to_friends( DecodedJson, State = {Username, Role, [PrompterFriend | _], GenericMessage, TabooCard} ) when Role == <<"Guesser">> ->
    %% if the GenericMessage came from Guesser, then the Msg must be unicasted to the PrompterFriend
    MessageToFriend = maps:get(<<"msg">>, DecodedJson),
    send_msg(MessageToFriend, PrompterFriend),
    io:format("send_msg_to_friends Guesser ~n"),
    {Username, Role, PrompterFriend, GenericMessage, TabooCard}.

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

startWait(State = { Username, Role, FriendList, GenericMessage, TabooCard }) ->
    receive
      { msgFromFriend, MsgFromFriend} ->
          io:format("receive di [~p]: msgFromFriend [~p]~n", [Username, MsgFromFriend]),
          JsonMessage = jsx:encode([{<<"action">>, msgFromFriend}, {<<"msg">>, MsgFromFriend}]),
          {{text, JsonMessage}, State}
    end.

getUpdatedState(MyRole) when MyRole == <<"Prompter">> ->
    getRandomTabooCard();

getUpdatedState(_) ->
    JsonMessage = jsx:encode([{<<"action">>, ignore}, {<<"msg">>, ""}]),
    { {text, JsonMessage} , []}.

getRandomTabooCard() ->
    TabooCard = [
        [<<"Car">>, <<"Driver">>, <<"Ride">>, <<"Transport">>, <<"Fast">>, <<"Travel">>],
        [<<"Dance">>, <<"Shoes">>, <<"Romantic">>, <<"Music">>, <<"Sing">>, <<"Town Square">>],
        [<<"Proud">>, <<"Feeling">>, <<"Accomplish">>, <<"Great">>, <<"Boast">>, <<"Humble">>],
        [<<"Husband">>, <<"Wife">>, <<"Ring">>, <<"Marry">>, <<"Man">>, <<"Friend">>],
        [<<"Camera">>, <<"Photos">>, <<"Pictures">>, <<"Snapshot">>, <<"Travel">>, <<"Memories">>]
    ],
    RandomIndex = getRandomInt(length(TabooCard)),
    JsonMessage = jsx:encode([{<<"action">>, tabooCard}, {<<"msg">>, lists:nth(RandomIndex, TabooCard)}]),
    { {text, JsonMessage} , TabooCard }.

getRandomInt(Max) ->
    RandomIndex = rand:uniform(Max),
    RandomIndex.











































%send_msg_to_everyone(_, []) -> ok;
%send_msg_to_everyone(Msg, [Friend | OtherFriends]) -> % [Friend1, Friend2,... FriendN]
%    PidFriend = global:whereis_name(Friend),
%    PidFriend ! {msgFromFriend, Msg},
%    send_msg_to_everyone(Msg, OtherFriends).






