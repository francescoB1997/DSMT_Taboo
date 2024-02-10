-module(server_handler).
-export([init/2]).

init (Req, State) ->
		Resp = cowboy_req:reply(200,
			#{<<"Content-Type">> => <<"text/html">>},
			<<"<h1>Pagina web Erlang & Cowboy </h1>">>,
			Req),
		{cowboy_websocket, Req, {undefined, undefined, undefined, undefined, undefined, undefined}}.


    %% {User, Role, Player1, Player2, Player3, WordList}
websocket_handle(Frame = {text, Json}, State = {Username, Role, Player1, Player2, Player3, WordList}) ->
	io:format("[Taboo WebSocket Opened] websocket_handle => Frame: ~p, State: ~p~n", [Json, State]),
	DecodedMessage = jsx:decode(Json),
	% the field action can have the following values:
	% login, start, word, wait, guess
	% it's used to decode which action has to be performed
	Action = maps:get(<<"action">>, DecodedMessage),
	{Response, UpdatedState} =
		if
			Action == <<"login">> ->
				S = player_handler:login(DecodedMessage, State),
				{Frame, S};
			Action == <<"start">> ->
				player_handler:start(DecodedMessage, State);
			Action == <<"word">> ->
				S = player_handler:word(DecodedMessage, State),
				{Frame, S};
			Action == <<"wait">> ->
				player_handler:wait_for_messages(State);
			Action == <<"guess">> ->
				player_handler:guess(DecodedMessage, State)
		end,
	io:format("~p Response ~p~n", [Username,Response]),
	{reply, [Response], UpdatedState}.
