# Commandline Wrappers

## JGenhtml CLI

The wrapper scripts allow you to use jgenhtml as a command line tool.

Without them you must type this:

```bash
java -jar jgenhtml.jar path/to/lcov.dat
```

The wrappers enable this:

```bash
jgenhtml path/to/lcov.dat
```

## Installation

1. Create a directory on your computer to contain the jar and wrappers

1. Add the directory to your [PATH](https://en.wikipedia.org/wiki/PATH_(variable))

1. Download the wrapper scripts to the directory. You can safely download both regardless of platform (windows, linux or mac).

1. Download the executable jar as described on the main page and put it in the directory.

The wrappers themselves contain more instructions.

## Usage

The CLI is exactly like genhtml, pass it the `--help` flag for info.

## Updating

The scripts look for the jar with the most recent modified time. To update, simply download a newer version of the jar and put it in the directory.
