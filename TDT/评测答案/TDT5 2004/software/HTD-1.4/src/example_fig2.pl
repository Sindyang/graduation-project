#!/usr/bin/perl -w

use strict;
use HTDStruct;
use VertexInfo;

my $htd = new HTDStruct();
$htd->setSystem('ExampleReferenceAnnotations');
$htd->addVertex('topic1', new VertexInfo([ ('s5', 's6') ]));
$htd->addVertex('topic2', new VertexInfo([ ('s7', 's8') ]));
$htd->addVertex('topic3', new VertexInfo([ ('s7', 's8', 's11', 's12', 's13', 's14') ]));
$htd->addVertex('topic4', new VertexInfo([ ('s13', 's4') ]));
$htd->addVertex('topic5', new VertexInfo([ ('s9', 's10', 's13', 's14', 's15', 's16') ]));

$htd->toXML("../test_suite/example_fig2.xml");
