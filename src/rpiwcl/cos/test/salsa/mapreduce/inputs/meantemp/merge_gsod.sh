#!/bin/bash

startYear=1929
endYear=1955

for year in `seq $startYear $endYear`
do
		echo 'appending data for' $year '...'
		cat $year >> $1
done

		
		
