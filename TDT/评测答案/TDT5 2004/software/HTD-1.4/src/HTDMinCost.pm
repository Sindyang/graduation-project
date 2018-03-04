package HTDMinCost;
use strict;
use HTDStruct;
use Data::Dumper;
use HTML::Table; 
 
########################################################################################################
### The class to compute the minimum cost error rate.
sub new {
    my $class = shift;
    my $ref = shift;
    my $sys = shift;
    my $docsptr = shift;
    my $params = shift;
    my $diag = shift;

    my $self = { 
	### an HTDStruct with the reference data in it
	REF => $ref,
	### an HTDStruct with the system data in it
	SYS => $sys,
	### a reference to a list of stories (doc IDs) to score
	DOCS => $docsptr,
	### a hash of strings used to CACHE the story lists for each vertex
	SYSNODESTORYCACHE => undef,
	### Evaluation parameter defaults.  These can be changed through the params argument with is a 
	### comma separated attribute/value list.
	PARAMS => { PTARGET => { VALUE => 0.02, DESC => "The probability of a TARGET"},
		    CMISS =>   { VALUE => 1.0,  DESC => "The cost of a Missed Detection"},
		    CFA =>     { VALUE => 0.1,  DESC => "The cost of a False Alarm"},
		    WDET =>    { VALUE => 0.66, DESC => "The weight assigned to the cost of detection"},
#		    OPTBR =>   { VALUE => 3,    DESC => "The optimal branching factor"},
		    CBRANCH => { VALUE => 2.0,  DESC => "The cost per branch in a vertex"},
		    CTITLE =>  { VALUE => 1.0,  DESC => "The cost of reading the title of a vertex"}, 
		    AVESPT =>  { VALUE => 88.0, DESC => "Expected average # stories per topic"},
		    MAXVTS =>  { VALUE => 3.0,  DESC => "Expected max ratio of vertices to stories"}, 
		},
	### flag for diagnostic output
	DIAG => $diag
    };

    bless $self;

    ### Set the params from the argument
    if (defined($params)){
	foreach my $param(keys %{ $params }){
	    die "Undefined parameter sent to MinimumCost Function '$param'.\n".
		HTDMinCost::paramDump("   ",$self)
		if (!defined($self->{PARAMS}->{$param}));
	    $self->{PARAMS}->{$param}->{VALUE} = $params->{$param};
	}
    }

    return $self;
}

### Dump the parameters for users to see
sub paramDump{
    my ($prefix, $cla) = @_;
    my $self = (defined($cla) ? $cla : new HTDMinCost(undef, undef, undef, undef));
    my $s = $prefix."Available Min Cost Parameters:\n"; 
    $s .= sprintf($prefix."%10s   %5s    %s\n","Name","Value","Description");
    $s .= sprintf($prefix."%10s   %5s    %s\n","----","-----","-----------");
    foreach $_(sort keys %{ $self->{PARAMS} }){
	$s .= sprintf($prefix."%10s = %5s -> %s\n",$_,$self->{PARAMS}->{$_}->{VALUE},$self->{PARAMS}->{$_}->{DESC});
    }
    $s;
}

### Simple min function
sub MIN{
    my ($a, $b) = @_;
    ($a < $b) ? $a : $b;
}

### Builds the cache for the stories per vertex
sub buildNodeStoryCache{
    my ($self, $htd) = @_;
    
    my %cache = ();
    my @undefinedNodes = $htd->getRootVertex();
    
    while (@undefinedNodes > 0){
	### This makes it a depth first search so that it's a O(N) operation
	my $thisNode = $undefinedNodes[$#undefinedNodes];
	my $pushed = 0;
	my $successor;
	foreach $successor($htd->successors($thisNode)){
	    if (! defined($cache{$successor})){
		push @undefinedNodes, $successor;
		$pushed = 1;
	    }
	}
	if (! $pushed){
	    ### if all successors are defined, then build this vertex's cache
	    my $stories = $htd->getNodeStoriesAsHT($thisNode, 0);
	    foreach $successor($htd->successors($thisNode)){
		foreach $_ (keys %{ $cache{$successor} }){
		    $stories->{$_} = 1;
		}
	    }
	    $cache{$thisNode} = $stories;
	    pop @undefinedNodes;
	}
    }
    \%cache;
}

### Perform the accual scoring calculations
sub score {
     my ($self) = @_;

#     $self->{REF}->toXML("-");

     print "Pre-computing system node story cache\n";
     $self->{SYSNODESTORYCACHE} = $self->buildNodeStoryCache($self->{SYS});
     print "   Completed\n";

#     my $numStories = keys %{ $self->{SYSNODESTORYCACHE}->{$self->{SYS}->getRootVertex()}};
     my $numStories = @{ $self->{DOCS} };

     print "Performing minimal cost scoring. Number of stories = $numStories.\n";

     $self->diagDAG();

     ### Pre compute the travel cost normalization constant just once
#     my $travelNormConstant =
#         ($self->{PARAMS}->{CBRANCH}->{VALUE} * $self->{PARAMS}->{OPTBR}->{VALUE} *
#	  (log($numStories) / log($self->{PARAMS}->{OPTBR}->{VALUE}))) +
#         ($self->{PARAMS}->{CTITLE}->{VALUE} * (log($numStories) / log($self->{PARAMS}->{OPTBR}->{VALUE})));

#    new formula:     
     my $travelNormConstant = ($self->{PARAMS}->{CBRANCH}->{VALUE} * 
			       ($self->{PARAMS}->{MAXVTS}->{VALUE} * $numStories / 
				$self->{PARAMS}->{AVESPT}->{VALUE})) + 
	 $self->{PARAMS}->{CTITLE}->{VALUE};

     ### loop through all nodes in the reference HTD
     foreach my $refNode ($self->{REF}->vertices()){
	 next if (! $self->{REF}->getVertexInfo($refNode));    # skip if empty vertex
	 print "   Searching for reference node $refNode\n";
	 my $res = $self->findBestSysNode($refNode, $self->{SYS}->getRootVertex(), $numStories, $travelNormConstant);
	 $self->{MAP}->{$refNode} = $res;
     }

}

### report diagnostic info about the system DAG
sub diagDAG{
    my ($self) = @_;

    return if !$self->{DIAG};

    ### collate and print info about the number of stories and successors for each node
    print "\nSystem DAG:\n";
    print "         Vertex\tStories\tBranches\n";
    my $nbranches = 0;
    my $ninternal = 0;
    my $totnodes = 0;
    my $totstories = 0;
    my $nleafstories = 0;
    my $root = $self->{SYS}->getRootVertex();
    foreach my $sysNode (sort($self->{SYS}->vertices())) {
	my $nstories = scalar(keys( %{$self->{SYS}->getNodeStoriesAsHT($sysNode,0)} ));
	my $nsucc = scalar($self->{SYS}->successors($sysNode));
	print "Vertex:  $sysNode\t$nstories\t$nsucc";
	print "\tROOT" if ($sysNode eq $root);
	print "\n";
	$totnodes++;
	$totstories += $nstories;
	if ($nsucc){
	    $nbranches += $nsucc;
	    $ninternal++;
	}
	else{
	    $nleafstories += $nstories;
	}
    }
    print "Total number of vertices:\t$totnodes\n";
    print "Number of internal vertices:\t$ninternal\n";
    printf ("Average branching factor:\t%.2f\n", $nbranches/$ninternal);
    print "Total number of stories:\t$totstories\n";
    printf ("Ave stories per leaf vertex:\t%.2f\n", $nleafstories/($totnodes-$ninternal));
    print "\n";
}

### Given the reference node (the topic), search the data structure for the best node
sub findBestSysNode{
    my ($self, $refNode, $sysNode, $numStories, $travelNormConstant) = @_;

    my $computedNodes = 1;
    ### the result for the top node
    my $res = $self->computeCost($refNode, $sysNode, $numStories, undef, $travelNormConstant);
    my $minRes = $res;
    my $searchHistory = {};
    $searchHistory->{$sysNode} = $res;

    ### Start the stack
    my @searchStack = ( $res );
    while (@searchStack > 0){
	$res = shift @searchStack;
	### Keep track of the minimum
	if ($minRes->{costTotal} > $res->{costTotal}){
	    $minRes = $res;
	}

	foreach $sysNode ($self->{SYS}->successors($res->{sysNode})){
#	    print "     sub Search $sysNode\n";
	    my $tRes = $self->computeCost($refNode, $sysNode, $numStories, $res, $travelNormConstant);
	    $computedNodes ++;

	    $searchHistory->{$sysNode} = $tRes;
	    ### Stop searching IF the detection cost increases
	    next if ($res->{costDetect} < $tRes->{costDetect});
	    push @searchStack, $tRes;	    
	}
	### Sort the search stack
	@searchStack = sort {$a->{costTotal} <=> $b->{costTotal}} @searchStack;
    }
    print "      $computedNodes vertices of ".$self->{SYS}->vertices()." searched\n";
#    print Dumper($searchHistory);
    $minRes;
}
 
### Given the two nodes to compare, do All the calculations and return a results hash with the values.  
sub computeCost{
    my ($self, $refNode, $sysNode, $numStories, $parentRes, $travelNormConstant) = @_;

    my $refStoryHt = $self->{REF}->getNodeStoriesAsHT($refNode, 1);
    my $sysStoryHt = $self->{SYSNODESTORYCACHE}->{$sysNode};
#    print "---------------------";
#    print "   Compute cost\n";
#    print "   ref $refNode ".join(" ",keys(%{ $refStoryHt }))."\n";
#    print "   sys $sysNode ".join(" ",keys(%{ $sysStoryHt }))."\n";

    my ($union) = 0;
    my @keys1 = keys %$refStoryHt;
    my @keys2 = keys %$sysStoryHt;

    foreach $_(@keys1){
	$union ++ if (defined($sysStoryHt->{$_}));
    }

    my $pMiss = (scalar(@keys1) - $union) / scalar(@keys1);
    my $pFa   = (scalar(@keys2) - $union) / ($numStories - scalar(@keys1));    
    my ($cTravel, $cDet) = ((! defined($parentRes) ? 0 :
			     ($parentRes->{costTravel} + 
			      ($self->{PARAMS}->{CBRANCH}->{VALUE} * 
			       scalar($self->{SYS}->successors($parentRes->{sysNode}))) +
			      $self->{PARAMS}->{CTITLE}->{VALUE})),
			    $self->costFunct($pFa, $pMiss));

    ### Normalize the costs
    my ($normCTravel, $normCDet) = ($cTravel / $travelNormConstant,
				    $cDet / $self->detNorm());
#				    $cDet / MIN($self->costFunct(1.0, 0), $self->costFunct(0, 1.0)));

    my $tRes = { numRefStory => scalar (@keys1),  
		 numSysStory => scalar (@keys2),  
		 costTravel => $cTravel,
		 normCostTravel => $normCTravel,
		 pMiss => $pMiss,
		 pFa => $pFa,
		 depth => (defined($parentRes) ? $parentRes->{depth} + 1 : 0),
		 costDetect => $cDet,
		 normCostDetect => $normCDet, 
		 costTotal  => $self->{PARAMS}->{WDET}->{VALUE} * $normCDet +
		               (1 - $self->{PARAMS}->{WDET}->{VALUE}) * $normCTravel,
		 numUnionStory => $union,
		 sysNode => $sysNode};
    $tRes;
}

## Determine detection cost normalization factor
sub detNorm{
    my ($self) = @_;
    
    if ($self->{PARAMS}->{CFA}->{VALUE} == 0) {
	return $self->costFunct(0, 1.0);                # fa are free,  norm by miss params
    } elsif ($self->{PARAMS}->{CMISS}->{VALUE} == 0) {
	return $self->costFunct(1.0, 0);                # miss are free, norm by fa params
    } else {
	return MIN($self->costFunct(1.0, 0), $self->costFunct(0, 1.0));    # usual case
    }
}

### Given pMiss and pFa, compute the cost of detection
sub costFunct{
    my ($self, $pFa, $pMiss) = @_;
    (($self->{PARAMS}->{CFA}->{VALUE} * $pFa * (1 - $self->{PARAMS}->{PTARGET}->{VALUE})) + 
     ($self->{PARAMS}->{CMISS}->{VALUE} * $pMiss * $self->{PARAMS}->{PTARGET}->{VALUE}));
}

### Write a report to HTML
sub reportAsHTML {
    my ($self, $outFile, $commandLine, $time) = @_;
    my $tab;

    open (OUT, ">$outFile.minCost.htm") || die "Error: Can't open '$outFile.minCost.htm' for write: $!\n";
    print "Writing MinCost Report to '$outFile.minCost.htm'\n";
    print OUT "<HTML>\n";
    print OUT "<BODY>\n";

    print OUT "<CENTER> <H1> Scoring Report for Minimum Cost </H1> </CENTER>\n";
    print OUT "<CENTER> <H3> Exec Time: $time </H1> </CENTER>\n";
    $tab = new HTML::Table(-rows => 1, -cols => 7, 
			   -align => 'center', -border=>2,
			   -head => ["RefNodeId", "SysNodeId", "#Ref", "#Sys", "#Union", 
				     "Depth", "Cost(travel)",  "Norm<br>(Cost(travel))",
				     "Cost(detect)", "Norm<br>(Cost(detect))", "MinimumCost",
				     "P(miss)", "P(fa)"]);
    my @sums = (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    foreach my $refNode (sort($self->{REF}->vertices())){
	next if (! $self->{REF}->getVertexInfo($refNode));    # skip if empty vertex
        my $res = $self->{MAP}->{$refNode};
	$tab->addRow($refNode, $res->{sysNode}, 
		     $res->{numRefStory},
		     $res->{numSysStory},
		     $res->{numUnionStory},
		     $res->{depth},
		     sprintf ("%.4f",$res->{costTravel}),
		     sprintf ("%.4f",$res->{normCostTravel}),
		     sprintf ("%.4f",$res->{costDetect}), 
		     sprintf ("%.4f",$res->{normCostDetect}), 
		     sprintf ("%.4f",$res->{costTotal}),
		     sprintf ("%.4f",$res->{pMiss}), 
		     sprintf ("%.4f",$res->{pFa}));
	$sums[0] += $res->{numRefStory};
	$sums[1] += $res->{numSysStory};
	$sums[2] += $res->{numUnionStory};
	$sums[3] += $res->{depth};
	$sums[4] += $res->{costTravel};
	$sums[5] += $res->{normCostTravel};
	$sums[6] += $res->{costDetect}; 
	$sums[7] += $res->{normCostDetect}; 
	$sums[8] += $res->{costTotal};
	$sums[9] += $res->{pMiss}, 
	$sums[10] += $res->{pFa};
	$sums[11] ++;
    }    
    $tab->addRow("Averages", "",
		 sprintf ("%.2f",$sums[0] / $sums[11]), 
		 sprintf ("%.2f",$sums[1] / $sums[11]), 
		 sprintf ("%.2f",$sums[2] / $sums[11]), 
		 sprintf ("%.2f",$sums[3] / $sums[11]), 
		 sprintf ("%.4f",$sums[4] / $sums[11]),
		 sprintf ("%.4f",$sums[5] / $sums[11]), 
		 sprintf ("%.4f",$sums[6] / $sums[11]),
		 sprintf ("%.4f",$sums[7] / $sums[11]), 
		 sprintf ("%.4f",$sums[8] / $sums[11]), 
		 sprintf ("%.4f",$sums[9] / $sums[11]), 
		 sprintf ("%.4f",$sums[10] / $sums[11]));
    $tab->setRowBGColor($tab->getTableRows(), "lightblue");
    print OUT $tab;

    my @keys = ();
    print OUT "<!-- CSV \n";
    foreach my $refNode (sort($self->{REF}->vertices())){
	next if (! $self->{REF}->getVertexInfo($refNode));    # skip if empty vertex
	my $res = $self->{MAP}->{$refNode};
	if (@keys == 0){
	    @keys = sort (keys %$res);
	    foreach $_(@keys){ print OUT $_."," } print OUT "\n";
	}
	foreach $_(@keys){ print OUT $res->{$_}."," } print OUT "\n";	
    }
    print OUT "CSV --!>\n";
    print OUT "<H2>CommandLine:</H2>\n";
    print OUT "<DIR>\n";
    print OUT "$commandLine\n";
    print OUT "</DIR>\n";
    print OUT "<H2>Parameters:</H2>\n";
    print OUT "<DIR>\n";
    $tab = new HTML::Table(-rows => 1, -cols => 3, 
			      -align => 'center', -border=>2,
			      -head => ["Parameter Name", "Value", "Description"]);
    foreach $_(sort keys %{ $self->{PARAMS} }){
	$tab->addRow($_, $self->{PARAMS}->{$_}->{VALUE}, $self->{PARAMS}->{$_}->{DESC});
    }
    print OUT $tab;
    print OUT "</DIR>\n";
    print OUT "</BODY>\n";
    print OUT "</HTML>\n";
    close OUT
}

### Write a report to Text
sub reportAsText {
    my ($self, $outFile, $commandLine, $time) = @_;
    my @tab;

    open (OUT, ">$outFile.minCost.txt") || die "Error: Can't open '$outFile.minCost.htm' for write\n";
    print "Writing MinCost Report to '$outFile.minCost.txt'\n";
    print OUT "                                Scoring Report for Minimum Cost\n";
    print OUT "                              Exec Time: $time\n\n";
    @tab = ();
    push @tab, [ ("RefNodeId", "SysNodeId", "#Ref", "#Sys", "#Union", 
		 "Depth", "Cost(travel)",  "Norm(Cost(travel))",
		 "Cost(detect)", "Norm(Cost(detect))", "MinimumCost",
		 "P(miss)", "P(fa)" )];
    push @tab, [ ("---------", "---------", "----", "----", "------", 
		 "-----", "------------",  "------------------",
		 "------------", "------------------", "-----------",
		 "-------", "-----" )];

    my @sums = (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    foreach my $refNode (sort($self->{REF}->vertices())){
	next if (! $self->{REF}->getVertexInfo($refNode));    # skip if empty vertex
        my $res = $self->{MAP}->{$refNode};
	push @tab, [ ($refNode, $res->{sysNode}, 
		      $res->{numRefStory},
		      $res->{numSysStory},
		      $res->{numUnionStory},
		      $res->{depth},
		      sprintf ("%.4f",$res->{costTravel}),
		      sprintf ("%.4f",$res->{normCostTravel}),
		      sprintf ("%.4f",$res->{costDetect}), 
		      sprintf ("%.4f",$res->{normCostDetect}), 
		      sprintf ("%.4f",$res->{costTotal}),
		      sprintf ("%.4f",$res->{pMiss}), 
		      sprintf ("%.4f",$res->{pFa})) ];
	$sums[0] += $res->{numRefStory};
	$sums[1] += $res->{numSysStory};
	$sums[2] += $res->{numUnionStory};
	$sums[3] += $res->{depth};
	$sums[4] += $res->{costTravel};
	$sums[5] += $res->{normCostTravel};
	$sums[6] += $res->{costDetect}; 
	$sums[7] += $res->{normCostDetect}; 
	$sums[8] += $res->{costTotal};
	$sums[9] += $res->{pMiss}, 
	$sums[10] += $res->{pFa};
	$sums[11] ++;
    }    
    push @tab, [ ("---------", "---------", "----", "----", "------", 
		 "-----", "------------",  "------------------",
		 "------------", "------------------", "-----------",
		 "-------", "-----" )];
    push @tab, [ ("Averages", "",
		  $sums[0] / $sums[11], 
		  $sums[1] / $sums[11], 
		  $sums[2] / $sums[11], 
		  $sums[3] / $sums[11], 
		  sprintf ("%.4f",$sums[4] / $sums[11]),
		  sprintf ("%.4f",$sums[5] / $sums[11]), 
		  sprintf ("%.4f",$sums[6] / $sums[11]),
		  sprintf ("%.4f",$sums[7] / $sums[11]), 
		  sprintf ("%.4f",$sums[8] / $sums[11]), 
		  sprintf ("%.4f",$sums[9] / $sums[11]), 
		  sprintf ("%.4f",$sums[10] / $sums[11])) ];
    tabby(*OUT, \@tab, "l", 2, "");
    print OUT "\n";


    print OUT "CommandLine:\n";
    print OUT "    $commandLine\n\n";
    print OUT "Parameters:\n";
    @tab = ();
    push @tab, [("Parameter Name", "Value", "Description")];
    foreach $_(sort keys %{ $self->{PARAMS} }){
	push @tab, [ ($_, $self->{PARAMS}->{$_}->{VALUE}, $self->{PARAMS}->{$_}->{DESC}) ];
    }
    tabby(*OUT, \@tab, "l", 2, "");
    close OUT
}

sub tabby{
    my($OUT, $ra_tab, $Justification,$ics,$offset) = @_;
    my($x,$y);
    my(@Maxs) = ();
    my(@fmt) = ();
    my($icsfmt);

    ### Measure the columns
    for ($x=0; $x<= $#{ $ra_tab->[0] }; $x++){ $Maxs[$x] = 0; }
    for ($y=0; $y<= $#$ra_tab; $y++){
	for ($x=0; $x<= $#{ $ra_tab->[$y] }; $x++){
	    $Maxs[$x] = length($ra_tab->[$y][$x])
		if ($Maxs[$x] < length($ra_tab->[$y][$x]));
	}
    }
    ### Make formats 
    for ($x=0; $x <= $#Maxs; $x++){
	if ($Justification =~ /r/){
	    $fmt[$x] = "%$Maxs[$x]s";
	} else {
	    $fmt[$x] = "%-$Maxs[$x]s";
	}
    }
    $icsfmt = "%".$ics."s";
    ### Write the table
    for ($y=0; $y<= $#$ra_tab; $y++){
	print OUT $offset;
	for ($x=0; $x<= $#{ $ra_tab->[$y] }; $x++){
	    printf OUT $fmt[$x],$ra_tab->[$y][$x];
            if ($x <= ($#Maxs - 1)){
                printf OUT $icsfmt,"";
            }
	}
	print OUT "\n";
    }
				       
}


1;
