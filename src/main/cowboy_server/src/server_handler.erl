-module(server_handler).
-export([init/2, websocket_handle/2, websocket_info/2]).

    %% Formato dello Stato del Server:  {User, Role, Player1, WordList}
init (Req, State) -> { cowboy_websocket, Req, { "", "", [], "" , [] } }.


websocket_handle(Frame = {text, JsonMsg}, State = {Username, Role, FriendList, GenericMessage, TabooCard}) ->
	%io:format("[Taboo WebSocket Handler]: => Frame: ~p, State: ~p~n", [JsonMsg, State]),
	DecodedJson = jsx:decode(JsonMsg),
	UserAction = maps:get(<<"action">>, DecodedJson),
	{Response, UpdatedState} =
		if
			UserAction == <<"login">> ->
				S = player_handler:login(DecodedJson, State),
				{Frame, S};

            UserAction == <<"start">> ->
                player_handler:start(DecodedJson, State);

            UserAction == <<"send_msg_to_friends">> ->
                S = player_handler:send_msg_to_friends(DecodedJson, State),
                {Frame, S};

            UserAction == <<"wait">> ->
                %% Defininire la waitFunction
                %%{Frame, S};


        end,
            %true -> io:format("[Taboo WebSocket Handler]: UserAction non riconosciuta -> ~p~n", [UserAction])
		%%	UserAction == <<"word">> ->
		%%		S = player_handler:word(DecodedJson, State),
		%%		{Frame, S};
		%%	UserAction == <<"trigger_wait">> ->
		%%		player_handler:wait_for_messages(State);
		%%	UserAction == <<"guess_word">> ->
		%%		player_handler:guess(DecodedJson, State)

	%io:format("~p Response ~p~n", [Username,Response]),
	{reply, [Response], UpdatedState}.


	websocket_info( {msgFromFriend, MsgFromFriend}, State) ->
    	JsonMessage = jsx:encode([{<<"action">>, msgFromFriend}, {<<"msg">>, MsgFromFriend}]),
    	{[{text, JsonMessage}], State}.
