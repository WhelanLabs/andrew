Fetches VIX data.

uses https://finance.yahoo.com/quote/%5EVIX/history/ to fetch the CSV contents.
In order to refresh this content, find the "download" link on the page, copy the 
link, and modify it to set "period1" to zero, and "period2" to a bigger number.
The following is an example of a modified URL where the leading one in the 
"period2" value was changed to a nine.  (This example should work for at least
a few hunred years.)

https://query1.finance.yahoo.com/v7/finance/download/%5EVIX?period1=0&period2=9657384095&interval=1d&events=history&includeAdjustedClose=true
