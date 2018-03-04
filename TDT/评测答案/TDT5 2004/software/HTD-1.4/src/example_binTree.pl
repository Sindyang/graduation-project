#!/usr/bin/perl -w

use strict;
use HTDStruct;
$SIG{__WARN__} = sub { CORE::die "Warning:\n", @_, "\n" };

foreach my $setDegree(20){
    my $degree = $setDegree;
    my $numStories = 2 ** ($degree - 1);
    my $sn;

    print "Building the binary tree system XML for degree $setDegree, num stories=$numStories\n";
    my $htd = new HTDStruct();
    $htd->setRootVertex('l1-0');
    $htd->setSystem('Binary Tree for System. '.$numStories.' Stories');

    for ($sn=0; $sn<$numStories; $sn++){
	$htd->addVertex("l$degree-".$sn, [ ("s$sn") ]);
    }
    print "   Leaf Vertices added\n";
    foreach my $loop(0, 1){
	my $deg = $degree - 1;
	while ($deg > 0){
	    print "   Adding interal ".($loop == 0 ? "vertices" : "edges")." @ level $deg\n";
	    for ($sn=0; $sn<2 ** ($deg-1); $sn++){
		if ($loop == 0){
		    $htd->addVertex("l$deg-$sn", [ () ]);
		} else {
		    $htd->addEdge("l$deg-$sn", "l".($deg+1)."-".($sn*2));
		    $htd->addEdge("l$deg-$sn", "l".($deg+1)."-".(($sn*2)+1));
		}
	    }
	    $deg--;
	}
    }
    
    print "    Writing the system XML\n";
    $htd->toXML("../test_suite/example_binTree.NStory$numStories.sys.xml");

    foreach my $numTopic(10, 20, 40, 80){
	$htd = new HTDStruct();
	$htd->setSystem("Reference for example_binTree.NStory$numStories.sys.xml $numTopic topics");
	for (my $topic=0; $topic < $numTopic; $topic++){
	    my @a = ();
	    my $start = sprintf "%d", ($numStories/$numTopic) * $topic;
	    for (my $story=$start; $story < $start + 15; $story++){
		push @a, "s".$story;
	    }
	    $htd->addVertex("topic$topic", \@a);
	}
	print "    Writing the Reference XML for $numTopic topics\n";
	$htd->toXML("../test_suite/example_binTree.NStory$numStories.NTopic$numTopic.ref.xml");
    }
}
