jgenhtml
========

lcov genhtml tool ported to Java

About JGenHtml
This tool is a java implementation of lcov's genhtml tool, primarily intended for use with the output file produced by JsTestDriver's coverage plugin.

Why? Because:

You can run it on any platform that JsTestDriver will run on.
You do not need to tweak the .dat file (for example to replace backslashes to forward slashes on windows).
You do not need to worry about whether external tools/dependencies like lcov / perl are installed.
It includes an Ant task so you can invoke it from your build scripts.
It includes a Maven plugin and is hosted on Maven Central so you can easily include it as a dependency.
Additionally:

It generates accessible HTML (for anyone using assistive technologies).
It also generates XML (so you can transform it to anything you want with a bit of XSLT).

== News ==
Release 1.5

  * From this release onwards the jgenhtml jar is also a Maven plugin and is available on [http://search.maven.org/#search%7Cga%7C1%7Cjgenhtml Maven Central].
    * Thanks to the team from [http://code.google.com/p/jstd-maven-plugin/ jstd-maven-plugin] for the motivation and offers of assistance.
  * Fixed a bug where counters would be wrong if the same source file was covered more than once in the tracefile/s.

Release 1.4

  * Renamed source package to com.googlecode.jgenhtml as it's more appropriate.
    * *Important* you need to update your ant taskdef to reflect this (see [Usage]).
  * Added support for baseline file.

Release 1.3

  * Added 'functions' page which I overlooked in previous release.
  * Ant task can now accept multiple tracefiles.
  * Added support for descriptions.
  * Added support for html-gzip.

Release 1.2

  * Supports branch and function coverage.
  * Supports processing multiple tracefiles.
  * Table sorting implemented.
  * Added new "html only" config file option so that the XML version of the report is not generated (see [Usage]).
  * Added grey and white "zebra row striping" to the source code view which I now regret - it will be removed next release.
  * Scores of other tweaks.

Release 1.1

  * Many more command line switches supported.
  * Supports lcovrc config file.
  * Produces reports that look more like native genhtml.

Release 1.0

  * What you need to build jsTestDriver reports - the only command line switch supported is output-directory.

----
