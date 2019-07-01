%% @author Harmeet
%% @doc @todo Add description to money.


-module(money).

-import(customer, [customermain/1]).
-import(bank, [bankmain/1]).

%% ====================================================================
%% API functions
%% ====================================================================
-export([start/0]).

start()->
	{Temp, Customers} = file:consult("customers.txt"),
	{Temp2, Banks} = file:consult("banks.txt"),
	
	OwnId = self(),
	register(money, OwnId),
	
	io:format("** Customers and loan objectives **~n"),
	FunctionCustomer = fun(Elem) ->
							   {Name, Value} = Elem,
							   register(Name, spawn(fun()-> customermain(Elem) end))
					   end,
	[FunctionCustomer(H) || H <- Customers],
	
	timer:sleep(50),
	io:format("~n** Banks and financial resources **~n"),
	FunctionBank = fun(Elem) ->
							   {Name, Value} = Elem,
							   register(Name, spawn(fun()-> bankmain(Elem) end))
					   end,
	[FunctionBank(H) || H <- Banks],
	timer:sleep(50),
	consoleoutput().

%% ====================================================================
%% Internal functions
%% ====================================================================

consoleoutput()->
	receive
		{Name, Action, Amount, DollarPrint, NameTwo}->
			io:format("~p ~p a loan of ~p ~s from ~p~n", [Name, Action, Amount, DollarPrint, NameTwo]),
			consoleoutput();
		{Name, Text, Value, Hurrah}->
			io:format("~p ~s ~p dollar(s). ~s~n", [Name, Text, Value, Hurrah]),
			consoleoutput();
		{Name, Value}->
			io:format("~p has ~p dollar(s) remaining.~n", [Name, Value]),
			consoleoutput();
		{Name, Value, Junk}->
			io:format("~p: ~p",[Name,Value]),
			consoleoutput()
	after 2000->
		io:format("~n")
	end.

