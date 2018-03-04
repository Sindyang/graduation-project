# **********************************************************
# Makefile for SRC Binary Directory
# **********************************************************
SHELL = /bin/sh

examples:
	example_fig1.pl
	example_fig2.pl

example_binTree:
	example_binTree.pl

test:
	@perl -e "use HTDStruct; HTDStruct::unitTest()"
	@echo ""
	@echo "Running the test case using reference file in XML format"
	./HTDEval.pl -ref ../test_suite/example_fig2.xml -sys ../test_suite/example_fig1.xml -stories ../test_suite/example_fig1.stories -metric min -report example_figs.report
	@echo ""
	@echo "The following diff command should output NOTHING"
	diff -I "Exec Time" -I "./HTDEval.pl -ref ../test_suite/example_fig2.xml"  ../test_suite/example_figs.report.minCost.htm example_figs.report.minCost.htm
	@echo ""
	@echo "Running the test case using reference file in traditional TDT format"
	./HTDEval.pl -ref ../test_suite/example_fig2.rel -sys ../test_suite/example_fig1.xml -stories ../test_suite/example_fig1.stories -metric min -report example_figs.report1
	@echo ""
	@echo "The following diff command should output NOTHING"
	diff -I "Exec Time" -I "./HTDEval.pl -ref ../test_suite/example_fig2.rel"  ../test_suite/example_figs.report1.minCost.htm example_figs.report1.minCost.htm

clean:
	rm -f *~ \#*\#

dist:
	echo "HTD-"`grep Version:\  ../README.txt | head -1 | perl -pe 's/.*Version:\s+//'` > .fname	
	echo "Building a release version" `cat .fname`
	echo "Verifying and up-to-date CVS copy"
	(cd .. ; cvs up )
	cvs co -d `cat .fname` htd
	echo "Building the TAR file"
	(cd `cat .fname`; rm -r fromAO test_suite/medium* test_suite/example_figs*txt test_suite/judge* src/loadDump.pl src/VertexInfo.pm src/htd-1.0.dtd)
	tar czf `cat .fname`"-"`date +%Y%m%d-%H%M`.tgz --exclude CVS `cat .fname` 
	rm -r `cat .fname`

