#!/bin/bash

startYear=1929
endYear=1965

/bin/rm $1

for year in `seq $startYear $endYear`
do
		echo 'appending data for' $year '...'
		# cat $year | cut -d' ' -f1,4,8 >> $1
		cat $year | cut -c1-7,15-23,25-30 >> $1
done

# for gsod data format, see: http://www7.ncdc.noaa.gov/CDO/GSOD_DESC.txt
		
		
