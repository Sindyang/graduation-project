package HTDStruct;
use strict;

use XML::Parser;
use Data::Dumper;
########################################################################################################
### The main structure for an HTD input/ouput file.  The class keeps a list of vertices and the
### edges linking each vertex to its successors.  
sub new
{
    my ($class) = @_;
    my $self = { 
	## VERTICES is a list of strings that define the vertex name and the set of stories contained within it.
	##          It is sorted so that a binary search can be used to find a specific vertex.
	VERTICES      =>  [()],
	## VERTEXSORTED is a boolean variable recording whether or not the VERTICES list is sorted
	VERTEXSORTED  => 0,
	## VALIDATED is a boolean variable recording whether or not the data structure was validated
	VALIDATED  => 0,
	## SUCCESSOREDGES is a hash of hashes showing the linkage of edges
	SUCCESSOREDGES  => {},
	## ROOTVERTEX  is the name of the top most vertex.  It does not have to be defined
	ROOTVERTEX => undef,
	## SYSTEM      is a description what the data is
	SYSTEM     => undef
	};
    bless $self;
    return $self;
}

### The class' unit test
sub unitTest{
    print "Running Unit Test for HTDStruct\n";

    print "   Check for Edges being added for undefined vertex\n";
    my $htd = new HTDStruct();
    $htd->setSystem('ExampleReferenceAnnotations');
    $htd->addVertex('topic1', [ ('s5', 's6') ]);
    $htd->addVertex('topic2', [ ('s1', 's2') ]);
    die "Error: Unable to add a legal edge" if (! $htd->addEdge("topic1", "topic2"));
    die "Error: Illegal edge passed" if ($htd->addEdge("to", "topic2"));
    die "Error: Illegal edge passed" if ($htd->addEdge("topic1", "top"));
    print "   Check for trying to add duplicate Vertex IDs\n";
    $htd->addVertex('topic1', [ ('s7', 's8') ]);
    die "Error: Multiple vertex names were created" if ($htd->validate(0) == 1);

    print "   A read Test\n";
    my @doclist = qw/s1 s2 s3 s4 s5 s6 s7 s8 s9 s10 s11 s12 s13 s14 s15 s16/;
    @doclist = sort(@doclist);
    $htd = HTDStruct::fromXML("../test_suite/example_fig1.xml",\@doclist);
    my %exp = ('a' => 16, 
	       'b' => 10, 
	       'c' => 11, 
	       'd' => 2,
	       'e' => 2,
	       'f' => 5,
	       'g' => 6,
	       'h' => 3,
	       'i' => 2,
	       'j' => 2);
    foreach my $id(keys %exp){
	my @s = keys %{ $htd->getNodeStoriesAsHT($id, 1) };
	die "Failed for node '$id'" if (@s != $exp{$id});
    }
    
    print "   A write/read Test\n";
    $htd->toXML("/tmp/htd.test");
    my $htd2 = HTDStruct::fromXML("/tmp/htd.test",\@doclist);
    foreach my $id(keys %exp){
	my @s = keys %{ $htd->getNodeStoriesAsHT($id, 1) };
	die "Failed for node '$id'" if (@s != $exp{$id});
    }
    unlink("/tmp/htd.test");

    print "   Test succeeded\n";
}

### returns a hash table with keys being the story ids.  if Recur is true, the include all
### the stories in the successor nodes.
sub getNodeStoriesAsHT{
    my ($self, $node, $recur) = @_;

    $self->validate(1);
    my @stack = ($node);
    my %ht = ();
    while (@stack > 0){
	my $id = shift @stack;
	my $vertexInfo = $self->getVertexInfo($id);
	foreach $_(split(/:/, $vertexInfo)){
	    $ht{$_} = 1;
	}
	push @stack, $self->successors($id) if ($recur);
    }
    \%ht;
}

### Set a value for the root vertex
sub setRootVertex{
    my $self = shift;
    $self->{ROOTVERTEX} = shift;
}

### get the root vertex.  It can be undefined
sub getRootVertex{
    my $self = shift;
    $self->{ROOTVERTEX};
}

### set the system description
sub setSystem{
    my $self = shift;
    $self->{SYSTEM} = shift;
}

### get the system description.  It can be undefined
sub getSystem{
    my $self = shift;
    $self->{SYSTEM};
}

### Sort sub-function for sorting the vertices
sub sortVert{
    my ($x, $y);
    ($x = $a) =~ s/ .*$//;
    ($y = $b) =~ s/ .*$//;
    $x cmp $y;
}

### Main validation routine
sub validate{
    my ($self, $die) = @_;

    if (! $self->{VERTEXSORTED}){
	$self->{VERTICES} = [ sort sortVert @{ $self->{VERTICES} } ];
	$self->{VERTEXSORTED} = 1;
	$self->{VALIDATED} = 0;
    }
    if (! $self->{VALIDATED}){
	### Look for replicated vertex names
	my $x;
	my $last = "";
	foreach $_(@{ $self->{VERTICES} }){
	    ($x = $_) =~ s/ .*$//;
	    if ($last eq $x){
		die "Validation Failed" if ($die) ;
		return 0;
	    }
	    $last = $x;
	}
	$self->{VALIDATED} = 1;
    }    
    1;
}

### Returns a list of vertices in the HTD
sub vertices{
    my ($self) = @_;
    $self->validate(1);
    my @a = ();
    my $x;
    foreach $_(@{ $self->{VERTICES} }){
	$x = $_;
	$x =~ s/ .*$//;
	push @a, $x;
    }
    @a;
}

### Gets the information attached to each vertex.  The vertex id is not returned
sub getVertexInfo{
    my ($self, $vertex) = @_;

    $self->validate(1);

    my $vInd = $self->getVertexInd($vertex);
    die "Error: Tried to get info for undefined vertex '$vertex'" if ($vInd < 0);
    my $vInfo = $self->{VERTICES}->[$vInd]; 
#    $vInfo =~ s/^.* //; 
    $vInfo =~ s/^\S+\s*//;
    $vInfo;
}

### returns the index into the VERTICES list for the given vertex.  It does a binary search for speed.
sub getVertexInd{
    my ($self, $vertex) = @_;
    $self->validate(1);
    ### Do a binary search
    my $mid = @{ $self->{VERTICES} } / 2;
    my $beg = 0;
    my $end = $#{ $self->{VERTICES} };
    my $curr;
    while ($beg <= $end){
	my $mid = ($end + $beg - (($end + $beg) % 2)) / 2;
#	print "vertex=$vertex beg=$beg mid=$mid end=$end\n";	
	($curr = $self->{VERTICES}->[$mid]) =~ s/ .*$//;
	return $mid if ($curr eq $vertex);
	if ($curr gt $vertex) {
	    $end = $mid - 1;
	} else {
	    $beg = $mid + 1;
	}
    }
    -1;
}

### Return a list of successor vertices for a given vertex.  If none are defined,
### an empty list is returned
sub successors{
    my ($self, $vertex) = @_;
    $self->validate(1);
    my $vInd = $self->getVertexInd($vertex);
    die "Error: Tried to get successors of undefined vertex '$vertex'" if ($vInd < 0);
    my $lu = $self->{SUCCESSOREDGES}->{$vertex};
    (defined($lu) ? keys %$lu : @{[()]});
}

### Inserts a vertex into the data structure.  This does not do ANY validation.  That happens before the data
### structure is used.
sub addVertex{
    my ($self, $vertex, $stories) = @_;

    push @{ $self->{VERTICES} }, "$vertex ".join(":",@$stories);
    $self->{VERTEXSORTED} = 0;
    $self->{VALIDATED} = 0;
}

### Adds an edge to the data structure.  It will only succeed IF the two vertices are already defined.
sub addEdge{
    my ($self, $parentVertex, $thisVertex) = @_;
    return(0) if ($self->getVertexInd($parentVertex) < 0);
    return(0) if ($self->getVertexInd($thisVertex) < 0);
    $self->{SUCCESSOREDGES}->{$parentVertex} = {} if (!defined($self->{SUCCESSOREDGES}->{$parentVertex}));
    $self->{SUCCESSOREDGES}->{$parentVertex}->{$thisVertex} = 1;
    $self->validate(1);
    1;
}

### Change the story list associated with a vertex. Valid only if vertex already exists in the data structure.
sub changeNodeStories{
    my ($self, $vertex, $stories) = @_;

    my $vInd = $self->getVertexInd($vertex);
    die "Error: Tried to change non-existing vertex $vertex\n" if ($vInd == -1);
 
    my $vInfo = "$vertex " . join(":", @$stories);
    splice (@{ $self->{VERTICES} }, $vInd, 1, $vInfo);
}

### Export the data structure to XML
sub toXML{
    my ($self, $file) = @_;
    $self->validate(1);
    open XML, ">$file" || die "Failed to open $file for output: $!\n";
    print XML  "<htd rootVertex=\"".(defined($self->getRootVertex()) ? $self->getRootVertex() : "undef")."\"";
    print XML  " system=\"".(defined($self->getSystem()) ? $self->getSystem() : "undef")."\">\n";
    print XML  "<vertexSet>\n";
    my ($vertex, $stories);
    for (my $v=0; $v<@{ $self->{VERTICES} }; $v++){	
	($vertex, $stories) = split(/ /,$self->{VERTICES}->[$v]);
	print XML  "   <vertex name=\"$vertex\">\n";
	foreach $_(split(/:/,$stories)){
	    print XML  "      <story docID=\"$_\"\/>\n";
	}
	print XML  "   </vertex>\n";
    }	
    print XML  "</vertexSet>\n";
    print XML  "<edgeSet>\n";
    foreach my $v(keys %{ $self->{SUCCESSOREDGES} }){
	foreach $_ ($self->successors($v)){
	    print XML  "   <edge srcVertex=\"$v\" destVertex=\"$_\"/>\n";
	}
    }
    print XML  "</edgeSet>\n";
    print XML  "</htd>\n";
    close XML;
}

################################################################################################
#############  STANDARD XML PARSER.   Low memory usage              ############################
my $builtHTD;
my $inVertex = "";
my @storyList = ();
sub hdl_start{  
    my ($p, $elt, %atts) = @_;

    if ($elt eq "htd"){
	$builtHTD->setSystem($atts{"system"});
	$builtHTD->setRootVertex($atts{"rootVertex"});
    } elsif ($elt eq "vertex"){
	$inVertex = $atts{"name"};
    } elsif ($elt eq "story"){
	push @storyList,  $atts{"docID"};
    } elsif ($elt eq "edge"){
	$builtHTD->addEdge($atts{"srcVertex"}, $atts{"destVertex"});
    }
}

sub hdl_end{  
    my ($p, $elt) = @_;
    if ($elt eq "vertex"){	
	$builtHTD->addVertex($inVertex, \@storyList);
	@storyList = ();
    }
}

sub fromXML{
    my ($file, $docsptr) = @_;

    $builtHTD = new HTDStruct();
    
    my $parser = new XML::Parser ( Handlers => {   # Creates our parser object
	                                 Start   => \&hdl_start,
	                                 End   => \&hdl_end,
				       }
			           );
    open (FILE, $file) || die("Failed to open file '$file': $!\n");
    $parser->parse(*FILE);
    close FILE;

    # keep only the stories that are in the list of docids for this test set
    selectStories(\$builtHTD, $docsptr);

    $builtHTD->validate(1);
    $builtHTD;			      
}

### read relevance judgements from traditional TDT file format
sub fromTDTrel {
    my ($file, $docsptr) = @_;

    # open reference file, identify fields in first line, and verify format
    open (FILE, $file) || die("Failed to open file '$file': $!\n");
    my $topicset = <FILE>;
    if ($topicset !~ /TOPICSET\s.*annot_type=\"(.+)\"\s+version=\"(.+)\"\s.*release_date=\"(.+)\"/)
    {   # file is not in TDT format - close it and return
	close (FILE);
	return undef;   # indicates no data read
    }

    $builtHTD = new HTDStruct();
    $builtHTD->setRootVertex('undef');   # ref has no root

    # use fields identified above to construct system name: 
    # annot_type.version.release_date
    my $refname = $1 . "." . $2 . "." . $3;
    $builtHTD->setSystem($refname);

    # read topic ids and stories
    my $topicid;
    my @docno;
    while (<FILE>)
    {
	/topicid=(\S+).*docno=(\S+)/ or next;

	$topicid = $1 if !$topicid;      # set topic id for first time

	if ($topicid ne $1)  # new topic - create vertex for previous one
	{
	    $builtHTD->addVertex($topicid, \@docno);
	    @docno = ();
	    $topicid = $1;
	}
	push (@docno, $2);
    }
    # create vertex for last topic
    $builtHTD->addVertex($topicid, \@docno);

    close FILE;

    # keep only the stories that are in the list of docids for this test set
    selectStories(\$builtHTD, $docsptr);

    $builtHTD->validate(1);
    $builtHTD;			      
}

### Check stories in HTD object against list of documents to be scored.
### Drop any that are not on the list.
sub selectStories
{
    my ($HTDptr, $docsptr) = @_;

    foreach $_(@{ $$HTDptr->{VERTICES} }) { 
	my ($vertex, $stories) = split;
	my @keepers = ();
	foreach my $story (split(/:/,$stories))
	{
	    push (@keepers, $story) if (inList($story, $docsptr));
	}
	# replace original story list with new list
	$$HTDptr->changeNodeStories($vertex,\@keepers);
    }
}

### Determine whether given story is in given list of doc ids.
### Return 1 if found, 0 if not found.
### Doc ids list must be in sorted order.
sub inList{
    my ($story, $docsptr) = @_;

    ### Do a binary search
    my $beg = 0;
    my $end = $#$docsptr;
    my $curr;
    while ($beg <= $end){
	my $mid = ($end + $beg - (($end + $beg) % 2)) / 2;
#	print "story=$story beg=$beg mid=$mid end=$end\n";	
	$curr = $$docsptr[$mid];
	return 1 if ($curr eq $story);
	if ($curr gt $story) {
	    $end = $mid - 1;
	} else {
	    $beg = $mid + 1;
	}
    }
    0;
}


### End of the XML import functions
###############################################################################################################
1;
