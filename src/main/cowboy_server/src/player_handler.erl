-module(player_handler).
-export([login/2, start/2, send_msg_to_friends/2]).

login (DecodedJson, State = {User, Role, Player1, TabooWord}) ->
    Username = maps:get(<<"username">>, DecodedJson),
    PidPlayer = global:whereis_name(Username),
    case PidPlayer of
        undefined ->
            global:register_name(Username, self());
        _ ->
            global:unregister_name(Username),
            global:register_name(Username, self())
    end,
    io:format("PlayerHandler: MyPID=[~p] login ~p~n OK", [self(), Username]),
    {Username, Role, Player1, TabooWord}.

  start (DecodedJson, State = {Username, MyRole, Player1, TabooWord}) ->
    Friend1 = maps:get(<<"friend1">>, DecodedJson),
    %Friend2 = maps:get(<<"friend2">>, DecodedJson),
    Role = maps:get(<<"role">>, DecodedJson),
    % the players exchange the word that needs to be guessed
    %{Response, WordToGuess} = word_distribution(Role, [Player1 | Player2]),
    %io:format("PlayerHandler: MyPID=[~p] Username=~p Role=~p Friend1=~p Friend2=~p ~n", [self(), Username, Role, Friend1, Friend2]),
    {Username, Role, Friend1, TabooWord}.

  send_msg_to_friends( DecodedJson, State = {Username, MyRole, Friend1, TabooWord} ) ->
    Word = maps:get(<<"msg">>, DecodedJson),
    %send_msg_to_friend(Word, Friend1),
    send_msg_to_everyone(Word, Friend1),

    {Username, MyRole, Friend1, TabooWord}.

send_msg_to_friend(Msg, Friend) ->
    PidFriend = global:whereis_name(Friend),
    PidFriend ! {msgFromFriend, Msg},
    ok_OneFriend.

send_msg_to_everyone(_, []) -> ok;
send_msg_to_everyone(Msg, [Friend | OtherFriends]) -> % [Friend1, Friend2,... FriendN]
    PidFriend = global:whereis_name(Friend),
    PidFriend ! {msgFromFriend, Msg},

    send_msg_to_friends(Msg, OtherFriends).







