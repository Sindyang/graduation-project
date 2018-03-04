#!/bin/sh

TDTROOT=/Volumes/fudd/TDT/Corpora/SCRATCH/tdt5_multilanguage_text_v1.1

rm -rf *ndx *ctl *ssd *key

if [ ! -f index.flist ] ; then
    (cd $TDTROOT; find *_bnd -type f -print) | grep .bnd | \
	sed -e 's:^.*/::' -e 's/\..*$//' | sort -u > index.flist
fi    

perl -I /Volumes/fudd/TDT/Software/TDT3eval-cvs \
/Volumes/fudd/TDT/Software/TDT3eval-cvs/TDT3BuildIndex.pl \
	-t 4 \
	-v 2 \
	-s \
	-A \
	-R $TDTROOT \
	-r 92810317 \
        -f index.flist \
	-O . \
	-y 2000 \
        -j /Volumes/fudd/TDT/Eval04/relevance_judgements/tdt2004_topic_annotations_v1.0/TDT2004.topic_rel.v1.0 \
	-m \
	-X bn



