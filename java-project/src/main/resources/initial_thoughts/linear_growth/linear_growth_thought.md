# Overview

The thought assumes that stocks move in a linear direction over time, and that the difference in price in the future is the same as in the past.

[ future_price(+days) = current_price + ( current_price - past_price(-days) ) ]

# Goal Inputs

* stock_symbol
* starting_date
* time_in_future

# thought outputs

* RESULT.output : the closing price at the given date in the future.

# supported mutations

* ...