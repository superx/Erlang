%% @author Harmeet
%% @doc @todo Add description to bank.


-module(bank).

%% ====================================================================
%% API functions
%% ====================================================================
-export([bankmain/1]).

bankmain(Elem)->
	{Name, Value} = Elem,
	io:format("~p: ~p ~n", [Name,Value]),
	networkthread(Name, Value).


%% ====================================================================
%% Internal functions
%% ====================================================================

processrequest(Name, Value, NameTwo, Val)->
	if
		Value >= Val->
			whereis(money) ! {Name, approves, Val, "dollar(s)", NameTwo},
			whereis(NameTwo) ! {p},
			networkthread(Name, (Value - Val));
		true->
			whereis(money) ! {Name, denies, Val, "dollar(s)", NameTwo},
			whereis(NameTwo) ! {n},
			networkthread(Name, Value)
	end.

networkthread(Name, Value)->
	receive
		{NameTwo, Val}->
			processrequest(Name, Value, NameTwo, Val)
	after 2000->
		whereis(money) ! {Name, Value}
	end.