## Changelog

## Release 1.6

* Changed the maven goal and phase, this might break existing maven config (sorry). See the readme for example maven usage.
* Does not crash if Test Name is empty in tracefile `TN: `
* Minor style tweaks.

## Release 1.5

* From this release onwards the jgenhtml jar is also a Maven plugin and is available on [Maven Central](https://repo.maven.apache.org/maven2/com/googlecode/jgenhtml/jgenhtml/).
	* Thanks to the team from [jstd-maven-plugin](http://code.google.com/p/jstd-maven-plugin/) for the motivation and
	  offers of assistance.
* Fixed a bug where counters would be wrong if the same source file was covered more than once in the tracefile/s.

## Release 1.4

* Renamed source package to com.googlecode.jgenhtml as it's more appropriate.
	* *Important* you need to update your ant taskdef to reflect this (see [Usage]).
* Added support for baseline file.

## Release 1.3

* Added 'functions' page which I overlooked in previous release.
* Ant task can now accept multiple tracefiles.
* Added support for descriptions.
* Added support for html-gzip.

## Release 1.2

* Supports branch and function coverage.
	* Supports processing multiple tracefiles.
	* Table sorting implemented.
	* Added new "html only" config file option so that the XML version of the report is not generated (see [Usage]).
	* Added grey and white "zebra row striping" to the source code view which I now regret - it will be removed next
	  release.
	* Scores of other tweaks.

## Release 1.1

* Many more command line switches supported.
* Supports lcovrc config file.
* Produces reports that look more like native genhtml.

## Release 1.0

* What you need to build jsTestDriver reports - the only command line switch supported is output-directory.
