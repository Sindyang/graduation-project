File: README.txt
Date: September 10, 2004
Version: 1.4

Description:
------------

This directory is the HTD scoring software.  To use the code, you must
first install an XML parser called 'expat', and then two CPAN modules
HTML::Table and XML::Parser.  See the instructions below for doing so.
Once the CPAN modules are installed, you can run the code with these
commands:

% cd <PATH_TO_THIS_DIRECTORY>
% perl -I src src/HTDEval.pl  -ref test_suite/example_fig2.xml \
  -sys test_suite/example_fig1.xml -stories test_suite/example_fig1.stories \
  -metric min -report reportFile

Load reportFile.minCost.htm into a browser to get the scoring report.
The report HTML file contains a comment with the performance
statistics in a grepable form if you want to scape the web page for
results.

The example above is a toy system output.  There are two other
examples in the 'test_suite' directory:
'test_suite/example_binTree.NStory1024*' and
'test_suite/example_binTree.NStory65536*'.  These are binary trees
distributing the specified number of stories in individual leaf
vertices of the tree.  To run the scorer on these examples, execute the command:

% perl -I src src/HTDEval.pl \
  -ref test_suite/example_binTree.NStory1024.NTopic10.ref.xml \
  -sys test_suite/example_binTree.NStory1024.sys.xml \
  -stories test_suite/example_binTree.NStory1024.NTopic10.stories \
  -metric min -report reportFile

To make a really big tree, there is a program called
'src/example_binTree.pl' that is set up to build a tree for 500K
stories.  This is the expected story count for the TDT2004 evaluation.
The file is huge so it is not included in this distribution.

To install the 'expat' XML parser:
----------------------------------

To install the 'expat', download the latest version from its Source
Forge project web site, 'http://sourceforge.net/projects/expat/'.  The
HTD code has been tested on Linux, Mac and Windows OS so you can use
either release.  To find the latest versions, scroll down on the page
until you find the section titled "Latest File Releases".

Follow the installation instructions that come packaged in the release.

To Install CPAN's HTML::Table and XML::Parser:
----------------------------------------------

After installing expat, (you must do expat first, otherwise the
installation of XML::Parser will fail), install the HTML::Table and
XML::Parser classes available on CPAN.  To install the modules, su to
root (for UNIX or MAC) and execute the following commands:

    % su root
    % perl -MCPAN -e shell
    CPAN> install HTML::Table	
    CPAN> install XML::Parser

See the CPAN installation site
'http://search.cpan.org/~jhi/perl-5.8.0/lib/CPAN.pm' for more details.

Release History:
----------------
 1.0 - Initial release
 1.2 - After beta testing
 1.3 - Added -stories argument
