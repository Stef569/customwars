#!/usr/bin/perl

use String::CRC32;

open(FILECHECK, "test.save");
$crc = crc32(*FILECHECK);
close(FILECHECK);

print "$crc\n";
