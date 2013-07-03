#!/bin/bash

startYear=1974
endYear=1980

for year in `seq $startYear $endYear`
do
		echo 'downloading data for' $year '...'
		wget http://s3.amazonaws.com/gsod/gsod-merged/$year
		cat $year >> $1
done

		
		
