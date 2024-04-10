-module(server_handler).
-export([init/2, websocket_handle/2, websocket_info/2]).

    %% Formato dello Stato del Server:  {User, Role, Player1, WordList}
init (Req, State) -> { cowboy_websocket, Req, { "", "", [], [] , [] } }.

websocket_handle(Frame = {text, JsonMsg}, State = {Username, Role, FriendList, GenericMessage, TabooCard}) ->
%io:format("[Taboo WebSocket Handler]: => Frame: ~p, State: ~p~n", [JsonMsg, State]),
	DecodedJson = jsx:decode(JsonMsg),
	UserAction = maps:get(<<"action">>, DecodedJson),
	{Response, UpdatedState} =
		if
			UserAction == <<"login">> ->
				player_handler:login(DecodedJson, State);

            UserAction == <<"assignTabooCard">> ->
                player_handler:assignTabooCard(State);

            UserAction == <<"startGame">> ->
                S = player_handler:wakeUpAllGuessers(State),
                {Frame, S};

            UserAction == <<"send_msg_to_friends">> ->
                player_handler:send_msg_to_friends(DecodedJson, State);

            UserAction == <<"attemptGuessWord">> ->
                player_handler:attemptGuessWord(DecodedJson, State);

            UserAction == <<"keepAlive">> ->
                io:format("keepAlive di ~p~n", [Username]),
                {Frame, State}
                %player_handler:passTabooCard(State);

        end,
	%io:format("~p Response ~p~n", [Username,Response]),
	{reply, [Response], UpdatedState}.

    websocket_info( { start }, State = {Username, Role, FriendList, GenericMessage, TabooCard}) ->
        io:format("Invocata websocket_info da parte di ~p~n", [Username]),
        JsonMessage = jsx:encode([{<<"action">>, wakeUpGuesser}]),
        {[{text, JsonMessage}], State};

	websocket_info( {msgFromFriend, MsgFromFriend}, State) ->
    	JsonMessage = jsx:encode([{<<"action">>, msgFromFriend}, {<<"msg">>, MsgFromFriend}]),
    	{[{text, JsonMessage}], State};

    websocket_info( {attemptGuessWord, AttemptedWord},
                    State = {Username, Role, FriendList, GenericMessage, [AttemptedWord, TabooWord1, TabooWord2, TabooWord3, TabooWord4, TabooWord5]} ) ->
        Result = true,
        player_handler:
        NewTabooCard = player_handler:getRandomTabooCard(),
        JsonMessage = jsx:encode([{<<"action">>, attemptGuessWord}, {<<"msg">>, true}, {<<"newTabooCard">>, NewTabooCard}]),
        {[{text, JsonMessage}], {Username, Role, FriendList, GenericMessage, NewTabooCard}};

    websocket_info( {attemptGuessWord, RequesterPID, AttemptedWord},
                    State = {Username, Role, FriendList, GenericMessage, [Word, TabooWord1, TabooWord2, TabooWord3, TabooWord4, TabooWord5]} ) ->
        Result = false,
        RequesterPID ! {resultAttemptGuessWord, Result},
        JsonMessage = jsx:encode([{<<"action">>, attemptGuessWord}, {<<"msg">>, false}]),
        {[{text, JsonMessage}], State};

    websocket_info(Info, State) ->
    	io:format("Taboo:websocket_info(Info, State) => Received info ~p~n e STATE ~p~n", [Info, State]),
    	{ok, State}.
