#!/usr/bin/perl -w

use strict;
use HTDStruct;
use HTDMinCost;
#$SIG{__WARN__} = sub { CORE::die "Warning:\n", @_, "\n" };

my $Version = "V1.4";
my $usage = 
"Version: $Version\n".
"Usage: HTDEval -ref REFFILE -sys HYPFILE -stories STORYLIST -report REPORTFILE [-metric min]+ [-metricParam min:xx=c,xxx=4]* --diag\n".
"    ref           ->  The reference file (may be in conventional TDT format or in XML)\n".
"    sys           ->  The system output file (must be in XML format)\n".
"    stories       ->  The file containing the list of all stories in the test corpus\n".
"    report        ->  Root name of the report file to generate (without .txt or .htm extension)\n".
"    metric        ->  A comma separated list of metrics to compute\n".
"                      Currently only 'min' is defined\n".
"    metricParam   ->  A comma separated list of metric parameters that follow the following BNF\n".
"                      min:<NAME>=<VALUE>,(<NAME>=<VALUE>)*\n\n".
HTDMinCost::paramDump("                      ").
"\n    diag          ->  Print diagnostic info about system DAG.\n".
"    \n";

use Getopt::Long;
my $commandLine = $0." ".join(" ",@ARGV);
my $time = scalar localtime;
my $ref = undef;
my $sys = undef;
my $stories = undef;
my $reportRoot = undef;
my @metrics = ();
my @metricParams = ();
my %ParamsHT = ();
my $diag = 0;
my $result = GetOptions ("ref=s" => \$ref,
			 "sys=s" => \$sys,
			 "stories=s" => \$stories,
			 "report=s" => \$reportRoot,
			 "metric=s" => \@metrics,
                         "metricParam=s" => \@metricParams,
			 "-diag" => \$diag);
die "Aborting:\n$usage\n:" if (!$result);
die "Error: -ref required\n$usage\n" if (!defined($ref));
die "Error: -sys required\n$usage\n" if (!defined($sys));
die "Error: -stories required\n$usage\n" if (!defined($stories));
die "Error: -report required\n$usage\n" if (!defined($reportRoot));
die "Error: -metric required\n$usage\n" if (@metrics == 0);

### Check the metrics
foreach $_(@metrics){   
    die "Error: Unknown Metric '$_'\n$usage\n" if ($_ !~ /^(min)$/); 
}
### Setup the parameter hash table
foreach $_(@metricParams){
    die "Error: Bad format for parameters '$_'" if ($_ !~ /^([^:]+):(([^:,]+=[^,:]+)(,[^:,]+=[^,:]+)*)$/);
    my $t = $1;
    $ParamsHT{$1} = { split(/[,=]/,$2) };
    die "Error: Unknown metric for specified parameters '$_'" if ($t !~ /^(min)$/); 
}

### Run the scoring
print "Loading the list of stories '$stories'\n";
my @docids = loadDocids ($stories);

print "Loading the reference '$ref'\n";
my $refHtd = HTDStruct::fromTDTrel($ref, \@docids);       # first try TDT format
$refHtd or $refHtd = HTDStruct::fromXML($ref, \@docids);  # if that fails, try XML

print "Loading the system '$sys'\n";
my $sysHtd = HTDStruct::fromXML($sys, \@docids);

foreach $_(@metrics){
    my $metricCalc;
    if ($_ =~ /^(min)$/){
	$metricCalc = new HTDMinCost($refHtd, $sysHtd, \@docids, (defined($ParamsHT{$_}) ? $ParamsHT{$_} : undef), $diag) ;
    } else {
	print "Warning: Unknown metric '$_'.  Can not compute\n";
    } 
    $metricCalc->score();
    $metricCalc->reportAsHTML($reportRoot, "HTDEval Version $Version:<BR>".$commandLine, $time);
#    $metricCalc->reportAsText($reportRoot, "HTDEval Version $Version:<BR>".$commandLine, $time);
}

### Read list of stories to be scored
### Return sorted list of docids
sub loadDocids
{
    my $stories = shift;

    open (FILE, $stories) or die "Failed to open stories list '$stories': $!\n";
    
    my @docids;
    while (<FILE>)
    {
	next if /^#/;   # skip comments

	# first field is file name, rest of line contains doc ids for stories
	my @fields = split;
	push (@docids, splice (@fields, 1)); 
    }
    @docids = sort @docids;
    
    close (FILE);
    return @docids;
}
 














