# jgenhtml

lcov genhtml tool ported to Java

![Maven Build](https://github.com/ricksbrown/jgenhtml/actions/workflows/maven.yml/badge.svg)

## Download

The Jar available on Maven Central is _The One Jar_ and can be used in any of the advertised ways:

* [Command Line Tool](#as-a-command-line-utility)
* [Executable Jar](#as-an-executable-jar)
* [Maven Plugin](#as-a-maven-plugin)
* [Ant Task](#as-an-ant-task)

The first two scenarios require you to manually download _The One Jar_ from Maven. It will be named following the convention `jgenhtml-{version}.jar`.

E.g. for version `1.5` you would go [here](https://repo.maven.apache.org/maven2/com/googlecode/jgenhtml/jgenhtml/1.5/) and download [jgenhtml-1.5.jar](https://repo.maven.apache.org/maven2/com/googlecode/jgenhtml/jgenhtml/1.5/jgenhtml-1.5.jar).

## Usage

### As a Command Line Utility

#### CLI Installation

1. Download the executable jar as [described above](#download).
2. Download the [wrapper scripts](wrappers).
3. Update your PATH

#### CLI Usage

Once installed you can execute it from the command line like so:

```
% jgenhtml --help

usage: jgenhtml [option] tracefile
 -b,--baseline-file <arg>      Use BASEFILE as baseline file
    --branch-coverage          Enable branch coverage display
 -c,--css-file <arg>           Use external style sheet file css-file
    --config-file <arg>        Specify a configuration file to use
 -d,--description-file <arg>   Read test case descriptions from DESCFILE
    --demangle-cpp             not implemented
 -f,--frames                   not implemented
    --function-coverage        Enable function coverage display
 -h,--help                     Print this help, then exit
    --highlight                not implemented
    --html-epilog <arg>        not implemented
    --html-extension <arg>     not implemented
    --html-gzip                Use gzip to compress HTML
    --html-prolog <arg>        not implemented
 -k,--keep-descriptions        Do not remove unused test descriptions
    --legend                   Include color legend in HTML output
    --no-branch-coverage       Disable branch coverage display
    --no-function-coverage     Disable function coverage display
    --no-prefix                Do not remove prefix from directory names
    --no-sort                  Turn off table sorting
    --no-source                Do not create source code view
    --num-spaces <arg>         Replace tabs in source view with num spaces
 -o,--output-directory <arg>   Write HTML output to OUTDIR
 -p,--prefix <arg>             Remove PREFIX from all directory names
 -q,--quiet                    Do not print progress messages
 -s,--show-details             not implemented
    --sort                     Turn on table sorting (on by default so
                               this is pointless)
 -t,--title <arg>              Display TITLE in header of all pages
 -v,--version                  Print version number, then exit
```

### As an executable jar

Download the executable jar as [described above](#download) and run it like so:

```bash
java -jar jgenhtml.jar --help
```

### As a Maven plugin

```xml
<plugin>
	<groupId>com.googlecode.jgenhtml</groupId>
	<artifactId>jgenhtml</artifactId>
	<version>1.6</version>
	<executions>
		<execution>
			<id>genhtml</id>
			<phase>package</phase>
			<goals>
				<goal>genthml</goal>
			</goals>
			<configuration>
				<in>path/to/tracefile.lcov</in>
				<outdir>${project.build.directory}/jgenhtml</outdir>
				<!-- Other options:
				<config>path/to/configfile</config>
				-->
			</configuration>
		</execution>
	</executions>
</plugin>
```

### As an Ant task

```xml
<project>
	<taskdef name="jgenhtml" classname="com.googlecode.jgenhtml.ant.JGenHtmlTask" classpath="path/to/jgenhtml.jar"/>

	<target name="genhtml">
		<jgenhtml in="jsTestDriver.conf-coverage.dat" outdir="${outdir}" config="lcovrc"/>
	</target>

	<!-- OR -->

	<target name="genhtml">
		<jgenhtml outdir="${outdir}" config="lcovrc">
			<path>
				<fileset dir="${somedir}" includes="*.info"/>
			</path>
		</jgenhtml>
	</target>
</project>
```

## Why?

* You do not need to tweak the .dat file (for example to replace backslashes to forward slashes on windows).
* You do not need to worry about whether external tools/dependencies like lcov / perl are installed.
* It includes an Ant task so you can invoke it from your build scripts.
* It includes a Maven plugin and is hosted on Maven Central so you can easily include it as a dependency.

* It generates accessible HTML (for anyone using assistive technologies).
* It also generates XML (so you can transform it to anything you want with a bit of XSLT).

## News

Detailed information can be found in the [changelog](changelog.md)
