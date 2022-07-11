# Overview

The thought assumes that the current vix price impacts future stock price.

[ future_price(+days) = current_price + ( current_price - past_price(-days) ) ]

# Goal Inputs

* stock_symbol
* starting_date
* time_in_future

# thought outputs

* RESULT.output : the closing price at the point in the future


# supported mutations

* ...