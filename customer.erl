%% @author Harmeet
%% @doc @todo Add description to customer.


-module(customer).

%% ====================================================================
%% API functions
%% ====================================================================
-export([customermain/1]).

customermain(Elem)->
	{Name, Value} = Elem,
	io:format("~p: ~p ~n", [Name,Value]),
	{Temp2, Banks} = file:consult("banks.txt"),
	timer:sleep(200),
	random:seed(now()),
	request(Name, Value, Value, Banks).


%% ====================================================================
%% Internal functions
%% ====================================================================

request(Name, Value, Value1, Banks)->
	timer:sleep(crypto:rand_uniform(10, 100)),
	if Value1 > 0->
		Value2 = random:uniform(50),
		if
			Value2 =< Value1 ->
				bank(Name, Value, Value1, Value2, Banks);
			true->
				bank(Name, Value, Value1, Value1, Banks)
		end;
	   true->
		   whereis(money) ! {Name, "has reached the objective", Value, "Woo Hoo!"}
	end.

bank(Name, Value, Value1, Value2, Banks)->
	if
		length(Banks) > 0->
			Randomnum = random:uniform(length(Banks)),
			List3 = lists:nth(Randomnum, Banks),
			{Name2,ReqAmount} = List3,
			whereis(Name2) ! {Name, Value2},
			whereis(money) ! {Name, requests, Value2, "dollar(s)", Name2},
			networkthread(Name, Value, Value1, Value2, Banks, Name2, Value2);
		true->
			Borrowed = Value - Value1,
			whereis(money) ! {Name, "was only able to borrow", Borrowed, "Boo Hoo!"}
	end.

networkthread(Name, Value, Value1, Value2, Banks, Banks2, ValueAB)->
	receive
		{Result}->
			if
				Result == p->
					Value3 = Value1 - Value2,
					request(Name, Value, Value3, Banks);
				true->
					Banks3 = lists:keydelete(Banks2, 1, Banks),
					request(Name, Value, Value1, Banks3)
			end
	after 2000->
		io:format("")
	end.