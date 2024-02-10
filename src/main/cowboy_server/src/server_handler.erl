-module(server_handler).
-export([init/2]).

    %% Formato dello Stato del Server:  {User, Role, Player1, Player2, Player3, WordList}
init (Req, State) -> { cowboy_websocket, Req, { undefined, undefined, undefined, undefined, undefined, undefined } }.


websocket_handle(Frame = {text, JsonMsg}, State = {Username, Role, Player1, Player2, Player3, WordList}) ->
	io:format("[Taboo WebSocket Handler]: => Frame: ~p, State: ~p~n", [JsonMsg, State]),
	DecodedJson = jsx:decode(JsonMsg),
	UserAction = maps:get(<<"action">>, DecodedJson),
	{Response, UpdatedState} =
		if
			UserAction == <<"login">> ->
				S = player_handler:login(DecodedJson, State),
				{Frame, S};
		true ->
		    io:format("[Taboo WebSocket Handler]: UserAction non riconosciuta -> ~p~n", [UserAction])
		%%	UserAction == <<"start">> ->
		%%		player_handler:start(DecodedJson, State);
		%%	UserAction == <<"word">> ->
		%%		S = player_handler:word(DecodedJson, State),
		%%		{Frame, S};
		%%	UserAction == <<"trigger_wait">> ->
		%%		player_handler:wait_for_messages(State);
		%%	UserAction == <<"guess_word">> ->
		%%		player_handler:guess(DecodedJson, State)
		end,
	io:format("~p Response ~p~n", [Username,Response]),
	{reply, [Response], UpdatedState}.
