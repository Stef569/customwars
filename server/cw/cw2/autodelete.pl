#!/usr/bin/perl
# autodelete.pl
# automatically deletes old games
# meant to be run by cron

#Number of days until deletion
$nostart = 5;		#games still on first day and turn
$noactivity = 14;	#games with no activity
$lingertime = 7;	#time that completed games linger

#read gamelist


#for each game in the list


	#find time of last activity
	

	#find current time
	

	#find difference in days
	
	
	#find day and turn
	

	#is it completed?
	

	#if it is in violation of the rules, delete it
	
	

#rewrite gamelist


exit 0;

#print "content-type: text/plain\n\n";
#@cont = <>;
#chomp($cont[0]);
#open(OUT, "> ./games/$cont[0]/map");
#$f = 1;
#foreach $i (@cont){
#	if($f == 1){
#		$f = 0;
#	}else{
#		print OUT $i;
#	}
#}
#close (OUT);
#
#print "File Recieved";
#exit 0;
