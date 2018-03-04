#!/usr/bin/perl -w

use strict;
use HTDStruct;
use VertexInfo;

my $htd = new HTDStruct();
$htd->setRootVertex('a');
$htd->setSystem('ExampleSystemOutput');
$htd->addVertex('a', new VertexInfo([ () ]));
$htd->addVertex('b', new VertexInfo([ ('s1', 's2') ]));
$htd->addVertex('c', new VertexInfo([ ('s3', 's4') ]));
$htd->addVertex('d', new VertexInfo([ ('s5', 's6') ]));
$htd->addVertex('e', new VertexInfo([ ('s7', 's8') ]));
$htd->addVertex('f', new VertexInfo([ () ]));
$htd->addVertex('g', new VertexInfo([ ('s9', 's10') ]));
$htd->addVertex('h', new VertexInfo([ ('s8', 's11', 's12') ]));
$htd->addVertex('i', new VertexInfo([ ('s13', 's14') ]));
$htd->addVertex('j', new VertexInfo([ ('s15', 's16') ]));

$htd->addEdge('a', 'b');
$htd->addEdge('b', 'd');
$htd->addEdge('b', 'e');
$htd->addEdge('b', 'f');
$htd->addEdge('a', 'c');
$htd->addEdge('c', 'f');
$htd->addEdge('c', 'g');
$htd->addEdge('f', 'h');
$htd->addEdge('f', 'i');
$htd->addEdge('g', 'i');
$htd->addEdge('g', 'j');

$htd->toXML("../test_suite/example_fig1.xml");
