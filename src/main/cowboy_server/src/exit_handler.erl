-module(exit_handler).
-export([init/2]).

init (Req, State) ->
		Resp = cowboy_req:reply(200,
			#{<<"Content-Type">> => <<"text/html">>},
			<<"<h1>Pagina web EXIT Erlang & Cowboy </h1>">>,
			Req),
		{ok, Resp, State}.