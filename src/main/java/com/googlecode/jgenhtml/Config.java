/*
	Copyright (C) 2012  Rick Brown

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package com.googlecode.jgenhtml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Configuration options controlled from command line flags or configuration file.
 * @author Rick Brown
 */
public final class Config
{
	public static final String DEFAULT_HTML_EXT = ".html";
	private static final String MSG_NOT_IMPLEMENTED = "not implemented";
	private static final Logger LOGGER = Logger.getLogger(Config.class.getName());
	static{
		JGenHtmlUtils.setLogFormatter(LOGGER);
	}
	private boolean functionCoverage = true;
	private boolean branchCoverage = true;
	private File outRootDir = new File(".");
	private String title = null;
	private File cssFile = null;
	private File descFile = null;
	private File baseFile = null;
	private boolean keepDescriptions = false;
	private boolean noPrefix = false;
	private String prefix = null;
	private int numSpaces = -1;//the default in genhtml is 8 but I want to leave tabs alone by default
	private boolean noSource = false;
	private boolean noSort = false;
	private byte hiLimit = 90;
	private byte medLimit = 75;
	private boolean legend = false;
	private String htmlExt = DEFAULT_HTML_EXT;
	private boolean help = false;
	private boolean version = false;
	private boolean gzip = false;
	private boolean quiet = false;
	private boolean showDetails = false;
	private String[] traceFiles;
	private Options options;
	private boolean htmlOnly;

	/**
	 * Command line argument constants.
	 * got rid of rc and ingore-errors because not all genhtml has them
	 * kept config-file despite this because it's darn useful
	 */
	public static enum CmdLineArg {
		HELP("help"),
		VERSION("version"),
		QUIET("quiet"),
		OUTPUT("output-directory"),
		SHOW_DETAILS("show-details"),
		DESCFILE("description-file"),
		KEEPDESC("keep-descriptions"),
		BASEFILE("baseline-file"),
		PREFIX("prefix"),
		NOPREFIX("no-prefix"),
		FUNCOV("function-coverage"),
		NOFUNCOV("no-function-coverage"),
		BRANCOV("branch-coverage"),
		NOBRANCOV("no-branch-coverage"),
		FRAMES("frames"),
		TITLE("title"),
		CSS("css-file"),
		NOSOURCE("no-source"),
		SPACES("num-spaces"),
		HILITE("highlight"),
		LEGEND("legend"),
		PROLOG("html-prolog"),
		EPILOG("html-epilog"),
		HTML_EXT("html-extension"),
		GZIP("html-gzip"),
		SORT("sort"),//what the heck point is this?
		NOSORT("no-sort"),
		CONFFILE("config-file"),
		DEMANGLE("demangle-cpp");

		private CmdLineArg(final String text) {
			this.text = text;
		}

		private final String text;

		@Override
		public String toString() {
			return text;
		}
	}

	/**
	 * config file argument constants.
	 */
	public static enum ConfFileArg {
		CSS("genhtml_css_file"),
		NOPREFIX("genhtml_no_prefix"),
		NOSOURCE("genhtml_no_source"),
		SORT("genhtml_sort"),
		SPACES("genhtml_num_spaces"),
		FUNCOV("genhtml_function_coverage"),
		KEEPDESC("genhtml_keep_descriptions"),
		BRANCOV("genhtml_branch_coverage"),
		HILIMIT("genhtml_hi_limit"),
		MEDLIMIT("genhtml_med_limit"),
		LEGEND("genhtml_legend"),
		GZIP("genhtml_html_gzip"),
		HTML_EXT("genhtml_html_extension"),
		HTMLONLY("jgenhtml_html_only"),
		VERBOSE("jgenhtml_verbose");

		private ConfFileArg(final String text) {
			this.text = text;
		}

		private final String text;

		@Override
		public String toString() {
			return text;
		}
	}

	/**
	 * Creates a config object in a default, uninitialized state.
	 * Can be used for show help or parsing a command line.
	 */
	public Config()
	{
		options = new Options();
		initOptions(options);
	}

	/**
	 * Create an XML representation of this object.
	 * @param document The ownerDocument for the resulting XML.
	 * @return An XML node containing the state of this object.
	 */
	public Element toXml(Document document)
	{
		Element configElement = document.createElement("config");
		configElement.setAttribute(Config.CmdLineArg.BRANCOV.toString(), String.valueOf(isBranchCoverage()));
		configElement.setAttribute(Config.CmdLineArg.FUNCOV.toString(), String.valueOf(isFunctionCoverage()));
		configElement.setAttribute(Config.CmdLineArg.NOSOURCE.toString(), String.valueOf(isNoSource()));
		configElement.setAttribute(Config.CmdLineArg.LEGEND.toString(), String.valueOf(isLegend()));
		configElement.setAttribute(Config.ConfFileArg.MEDLIMIT.toString(), String.valueOf(getMedLimit()));
		configElement.setAttribute(Config.ConfFileArg.HILIMIT.toString(), String.valueOf(getHiLimit()));
		configElement.setAttribute(Config.CmdLineArg.NOSORT.toString(), String.valueOf(isNoSort()));
		configElement.setAttribute(Config.CmdLineArg.DESCFILE.toString(), String.valueOf(getDescFile() != null));
		configElement.setAttribute(Config.CmdLineArg.SHOW_DETAILS.toString(), String.valueOf(isShowDetails()));
		return configElement;
	}

	/**
	 * Add a command line option to the collection of accepted options accepted by jgenhtml.
	 * @param options The options collection to which we are adding the option.
	 * @param arg The arg to add.
	 * @param needsArg true if this command line option takes an argument.
	 * @param description Human readable description of the option.
	 * @param hasShortVersion true if there is a single character version of this option.
	 */
	private static void addOption(final Options options, final CmdLineArg arg, final boolean needsArg, final String description, final boolean hasShortVersion)
	{
		String name = arg.toString();
		String shortVersion = hasShortVersion? name.substring(0, 1) : null;
		options.addOption(shortVersion, name, needsArg, description);
	}

	/**
	 * Initialize the options collection with the accepted command line args.
	 * @param options The options collection to initialize.
	 */
	public static void initOptions(final Options options)
	{
		addOption(options, CmdLineArg.HELP, false, "Print this help, then exit", true);
		addOption(options, CmdLineArg.VERSION, false, "Print version number, then exit", true);
		addOption(options, CmdLineArg.QUIET, false, "Do not print progress messages", true);
		addOption(options, CmdLineArg.SHOW_DETAILS, false, "Generate detailed directory view", true);
		addOption(options, CmdLineArg.FRAMES, false, MSG_NOT_IMPLEMENTED, true);
		addOption(options, CmdLineArg.BASEFILE, true, "Use the given file as baseline file", true);
		addOption(options, CmdLineArg.OUTPUT, true, "Write HTML output to the given directory", true);
		addOption(options, CmdLineArg.TITLE, true, "Display the given title in header of all pages", true);
		addOption(options, CmdLineArg.DESCFILE, true, "Read test case descriptions from the given file", true);
		addOption(options, CmdLineArg.KEEPDESC, false, "Do not remove unused test descriptions", true);
		addOption(options, CmdLineArg.CSS, true, "Use external style sheet file css-file", true);
		addOption(options, CmdLineArg.PREFIX, true, "Remove the given prefix from all directory names", true);
		addOption(options, CmdLineArg.NOPREFIX, false, "Do not remove prefix from directory names", false);
		addOption(options, CmdLineArg.NOSOURCE, false, "Do not create source code view", false);
		addOption(options, CmdLineArg.SPACES, true, "Replace tabs in source view with num spaces", false);
		addOption(options, CmdLineArg.HILITE, false, MSG_NOT_IMPLEMENTED, false);
		addOption(options, CmdLineArg.LEGEND, false, "Include color legend in HTML output", false);
		addOption(options, CmdLineArg.PROLOG, true, MSG_NOT_IMPLEMENTED, false);
		addOption(options, CmdLineArg.EPILOG, true, MSG_NOT_IMPLEMENTED, false);
		addOption(options, CmdLineArg.HTML_EXT, true, "Use the given ext as filename extension for pages", false);
		addOption(options, CmdLineArg.GZIP, false, "Use gzip to compress HTML", false);
		addOption(options, CmdLineArg.SORT, false, "Turn on table sorting (on by default so this is pointless)", false);
		addOption(options, CmdLineArg.NOSORT, false, "Turn off table sorting", false);
		addOption(options, CmdLineArg.DEMANGLE, false, MSG_NOT_IMPLEMENTED, false);
		addOption(options, CmdLineArg.CONFFILE, true, "Specify a configuration file to use", false);
		addOption(options, CmdLineArg.FUNCOV, false, "Enable function coverage display", false);
		addOption(options, CmdLineArg.NOFUNCOV, false, "Disable function coverage display", false);
		addOption(options, CmdLineArg.BRANCOV, false, "Enable branch coverage display", false);
		addOption(options, CmdLineArg.NOBRANCOV, false, "Disable branch coverage display", false);
	}

	/**
	 * Load command line args and config file properties into this config instance and reconfigure as appropriate.
	 * @param argv Command line args.
	 * @throws ParseException
	 */
	public void initializeUserPrefs(String[] argv) throws ParseException
	{
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, argv);

		if(cmd.hasOption(CmdLineArg.HELP.toString()))
		{
			this.help = true;
		}
		else if(cmd.hasOption(CmdLineArg.VERSION.toString()))
		{
			this.version = true;
		}
		else
		{
			if(cmd.hasOption(Config.CmdLineArg.QUIET.toString()))
			{
				this.quiet = true;
				Logger parent = Logger.getLogger("com.googlecode.jgenhtml");
				parent.setLevel(Level.WARNING);
			}
			if(cmd.hasOption(CmdLineArg.SHOW_DETAILS.toString()))
			{
				this.setShowDetails(true);
			}
			if(cmd.hasOption(CmdLineArg.OUTPUT.toString()))
			{
				this.setOutRootDir(cmd.getOptionValue(CmdLineArg.OUTPUT.toString()));
			}
			if(cmd.hasOption(CmdLineArg.TITLE.toString()))
			{
				this.setTitle(cmd.getOptionValue(CmdLineArg.TITLE.toString()));
			}
			if(cmd.hasOption(CmdLineArg.CSS.toString()))
			{
				this.setCssFile(cmd.getOptionValue(CmdLineArg.CSS.toString()));
			}
			if(cmd.hasOption(CmdLineArg.DESCFILE.toString()))
			{
				this.setDescFile(cmd.getOptionValue(CmdLineArg.DESCFILE.toString()));
			}
			if(cmd.hasOption(CmdLineArg.BASEFILE.toString()))
			{
				this.setBaseFile(cmd.getOptionValue(CmdLineArg.BASEFILE.toString()));
			}
			if(cmd.hasOption(CmdLineArg.KEEPDESC.toString()))
			{
				this.setKeepDescriptions(true);
			}
			if(cmd.hasOption(CmdLineArg.SPACES.toString()))
			{
				this.setNumSpaces(cmd.getOptionValue(CmdLineArg.SPACES.toString()));
			}
			if(cmd.hasOption(CmdLineArg.NOSOURCE.toString()))
			{
				this.setNoSource(true);
			}
			if(cmd.hasOption(CmdLineArg.GZIP.toString()))
			{
				this.setGzip(true);
			}
			if(cmd.hasOption(CmdLineArg.NOSORT.toString()))
			{
				this.setNoSort(true);
			}
			else if(cmd.hasOption(CmdLineArg.SORT.toString()))
			{
				this.setNoSort(false);  // wow, this is totally pointless
			}
			if(cmd.hasOption(CmdLineArg.LEGEND.toString()))
			{
				this.setLegend(true);
			}
			if(cmd.hasOption(CmdLineArg.HTML_EXT.toString()))
			{
				this.setHtmlExt(cmd.getOptionValue(CmdLineArg.HTML_EXT.toString()));
			}
			if(cmd.hasOption(CmdLineArg.NOFUNCOV.toString()))
			{
				this.setFunctionCoverage(false);
			}
			else if(cmd.hasOption(CmdLineArg.FUNCOV.toString()))
			{
				this.setFunctionCoverage(true);
			}
			if(cmd.hasOption(CmdLineArg.NOBRANCOV.toString()))
			{
				this.setBranchCoverage(false);
			}
			else if(cmd.hasOption(CmdLineArg.BRANCOV.toString()))
			{
				this.setBranchCoverage(true);
			}
			if(cmd.hasOption(CmdLineArg.NOPREFIX.toString()))
			{
				this.setNoPrefix(true);
			}
			else if(cmd.hasOption(CmdLineArg.PREFIX.toString()))
			{
				this.setPrefix(cmd.getOptionValue(CmdLineArg.PREFIX.toString()));
			}
			traceFiles = cmd.getArgs();
			if(traceFiles != null && traceFiles.length > 0)
			{
				this.loadConfigFile(cmd.getOptionValue(CmdLineArg.CONFFILE.toString()));
			}
		}
	}

		/**
	 * Loads the lcovrc config file from the home directory or the location specified on the
	 * command line and sets properties in the config object accordingly.
	 * Does not look for a system wide config file because there is no consistent directory to
	 * look in across all platforms.
	 *
	 * @param alternatePath
	 */
	private void loadConfigFile(final String alternatePath)
	{
		try
		{
			//don't use FileUtils.getUserDirectoryPath() here, it was causing issues when run from Ant
			String lcovrc = System.getProperty("user.home") + File.separatorChar + ".lcovrc";
			Properties properties = loadFileToProperties(alternatePath);
			if(properties != null || (properties = loadFileToProperties(lcovrc)) != null)
			{

				LOGGER.log(Level.INFO, "Loaded config file {0}.", lcovrc);
				if(properties.containsKey(ConfFileArg.CSS.toString()))
				{
					setCssFile(properties.getProperty(ConfFileArg.CSS.toString()));
				}
				Integer optionValue = getNumericValue(properties, ConfFileArg.FUNCOV.toString());
				if(optionValue != null)
				{
					setFunctionCoverage(optionValue != 0);
				}
				optionValue = getNumericValue(properties, ConfFileArg.BRANCOV.toString());
				if(optionValue != null)
				{
					setBranchCoverage(optionValue != 0);
				}
				optionValue = getNumericValue(properties, ConfFileArg.GZIP.toString());
				if(optionValue != null)
				{
					setGzip(optionValue != 0);
				}
				optionValue = getNumericValue(properties, ConfFileArg.KEEPDESC.toString());
				if(optionValue != null)
				{
					setKeepDescriptions(optionValue != 0);
				}
				optionValue = getNumericValue(properties, ConfFileArg.NOSOURCE.toString());
				if(optionValue != null)
				{
					setNoSource(optionValue != 0);
				}
				optionValue = getNumericValue(properties, ConfFileArg.SORT.toString());
				if(optionValue != null)
				{
					setNoSort(optionValue == 0);
				}
				optionValue = getNumericValue(properties, ConfFileArg.SPACES.toString());
				if(optionValue != null)
				{
					setNumSpaces(optionValue);
				}
				optionValue = getNumericValue(properties, ConfFileArg.HILIMIT.toString());
				if(optionValue != null)
				{
					setHiLimit(optionValue);
				}
				optionValue = getNumericValue(properties, ConfFileArg.MEDLIMIT.toString());
				if(optionValue != null)
				{
					setMedLimit(optionValue);
				}
				optionValue = getNumericValue(properties, ConfFileArg.NOPREFIX.toString());
				if(optionValue != null)
				{
					setNoPrefix(optionValue != 0);
				}
				optionValue = getNumericValue(properties, ConfFileArg.LEGEND.toString());
				if(optionValue != null)
				{
					setLegend(optionValue != 0);
				}
				if(properties.containsKey(ConfFileArg.HTML_EXT.toString()))
				{
					setHtmlExt(properties.getProperty(ConfFileArg.HTML_EXT.toString()));
				}
				optionValue = getNumericValue(properties, ConfFileArg.HTMLONLY.toString());
				if(optionValue != null)
				{
					setHtmlOnly(optionValue != 0);
				}
				optionValue = getNumericValue(properties, ConfFileArg.VERBOSE.toString());
				if(optionValue != null)
				{
					if(optionValue != 0)
					{
						Logger parent = Logger.getLogger("com.googlecode.jgenhtml");
						parent.setLevel(Level.ALL);
					}
				}
			}
		}
		catch(IOException ex)
		{
			LOGGER.log(Level.WARNING, ex.getLocalizedMessage());
		}
	}

	/**
	 * Displays help message to user.
	 * @param options CommandLine options.
	 */
	public void showCmdLineHelp()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("jgenhtml [option] tracefile", options);
	}

	/**
	 * @param path The path to the file.
	 * @return An instance of File if the the path points to an existing file, otherwise null.
	 */
	private File getFileIfExists(final String path)
	{
		File result = null;
		String newPath = path.trim();
		if(newPath.length() > 0)
		{
			result = new File(newPath);
			if(!result.exists())
			{
				result = null;
			}
		}
		return result;
	}

	/**
	 * Get the path/s to the traceFile/s passed in on the command line.
	 * @return An array of paths.
	 */
	public String[] getTraceFiles()
	{
		return traceFiles;
	}

	/**
	 * Path to override CSS file.
	 * If this is set then this CSS file will be used instead of the default.
	 * Calling this function will only change the state of the config if the CSS
	 * file actually exists.
	 * @param cssPath The full path to the override CSS file.
	 */
	private void setCssFile(final String cssPath) {
		File file = getFileIfExists(cssPath);
		if(file != null)
		{
			this.cssFile = file;
		}
	}

	/**
	 * Determine if the user has turned "keep descriptions" on.
	 * @return true if keeping descriptions.
	 */
	public boolean isKeepDescriptions()
	{
		return keepDescriptions;
	}

	/**
	 * Set the user preference for keeping descriptions.
	 * @param keepDescriptions Set to true to keep descriptions.
	 */
	private void setKeepDescriptions(boolean keepDescriptions)
	{
		this.keepDescriptions = keepDescriptions;
	}

	public boolean isShowDetails()
	{
		return showDetails;
	}

	public void setShowDetails(boolean showDetails)
	{
		this.showDetails = showDetails;
	}

	/**
	 * Get the extension given to HTML files.
	 * @return The file name extension (including leading dot).
	 */
	public String getHtmlExt()
	{
		return htmlExt;
	}

	/**
	 * Set the extension to give to HTML files.
	 * @param htmlExt The file name extension with or without the dot.
	 */
	public void setHtmlExt(final String htmlExt)
	{
		if(htmlExt != null)
		{
			String ext = htmlExt.trim();
			if(ext.length() > 0)
			{
				if(!ext.startsWith("."))
				{
					ext = '.' + ext;
				}
				if(ext.length() > 1)
				{
					this.htmlExt = ext;
					LOGGER.log(Level.FINE, "setting html extension to {0}", ext);
				}
				else
				{
					LOGGER.log(Level.WARNING, "html extension can not be just a dot");
				}
			}
			else
			{
				LOGGER.log(Level.WARNING, "html extension can not be empty");
			}
		}
		else
		{
			LOGGER.log(Level.WARNING, "html extension can not be null");
		}
	}

	/**
	 * Path to baseline file.
	 * @param baseFile The full path to the baseline file.
	 */
	private void setBaseFile(final String baseFile) {
		File file = getFileIfExists(baseFile);
		if(file != null)
		{
			this.baseFile = file;
		}
	}

	/**
	 * The baseline file if the user has specified it.
	 * @return The the baseline file or null.
	 */
	public File getBaseFile()
	{
		return baseFile;
	}

	/**
	 * Path to description file.
	 * @param descFile The full path to description file.
	 */
	private void setDescFile(final String descFile) {
		File file = getFileIfExists(descFile);
		if(file != null)
		{
			this.descFile = file;
		}
	}

	/**
	 * The description file (produced by gendesc) if the user has specified it.
	 * @return The .desc file or null.
	 */
	public File getDescFile()
	{
		return descFile;
	}

	/**
	 * Determine if the user has requested gzip compression on output files.
	 * @return true if html-gzip is on.
	 */
	public boolean isGzip()
	{
		return gzip;
	}

	/**
	 * Set the user preference gzip compression on output files.
	 * @param gzip true to turn on html-gzip.
	 */
	private void setGzip(boolean gzip)
	{
		this.gzip = gzip;
	}

	/**
	 * Determine source code page generation is on or off.
	 * @return true if source code page generation turned off
	 */
	public boolean isNoSource()
	{
		return noSource;
	}

	/**
	 * Determine table sorting is on or off.
	 * @return true if table sorting is turned off
	 */
	public boolean isNoSort()
	{
		return noSort;
	}

	/**
	 * Turn source code page generation on or off.
	 * @param noSource
	 */
	private void setNoSource(final boolean noSource)
	{
		this.noSource = noSource;
	}

	/**
	 * Turn table sorting or off.
	 * @param noSort
	 */
	private void setNoSort(final boolean noSort)
	{
		this.noSort = noSort;
	}

	/**
	 * Determine if function coverage reporting is on or off.
	 * @return true if function coverage is turned on
	 */
	public boolean isFunctionCoverage()
	{
		return functionCoverage;
	}

	/**
	 * Turn function coverage reporting on or off.
	 * @param functionCoverage
	 */
	private void setFunctionCoverage(final boolean functionCoverage)
	{
		this.functionCoverage = functionCoverage;
	}

	/**
	 * Determine if branch coverage reporting is on or off.
	 * @return true if branch coverage is turned on
	 */
	public boolean isBranchCoverage()
	{
		return branchCoverage;
	}

	/**
	 * Turn branch coverage reporting on or off.
	 * @param branchCoverage
	 */
	private void setBranchCoverage(final boolean branchCoverage)
	{
		this.branchCoverage = branchCoverage;
	}

	/**
	 * @return A File instance representing the output root directory.
	 */
	public File getOutRootDir()
	{
		return outRootDir;
	}

	/**
	 * Sets the output directory where all the output will be generated.
	 * @param outputDir The path to the desired output directory.
	 */
	private void setOutRootDir(final String outRootDir)
	{
		File file = new File(outRootDir);
		if(!file.exists())
		{
			file.mkdirs();
		}
		this.outRootDir = file;
	}

	/**
	 * Get the user defined title.
	 * @return The title if it has been set by the user otherwise null.
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Set the user defined title if allowed.
	 * @param title The title, as set by the user.
	 */
	private void setTitle(final String title)
	{
		String newTitle = title.trim();
		if(newTitle.length() > 0)
		{
			this.title = title;
		}
	}

	/**
	 * Determine if the user has asked to show the legend.
	 * @return True if the legend is to be displayed.
	 */
	public boolean isLegend()
	{
		return this.legend;
	}

	/**
	 * Set the user preference for displaying the legend in the coverage reports.
	 * @param legend
	 */
	private void setLegend(boolean legend)
	{
		this.legend = legend;
	}

	/**
	 * Get the override CSS file.
	 */
	public File getCssFile()
	{
		return this.cssFile;
	}

	/**
	 * Determine the user preference for shortening paths in the index pages.
	 * @return true if the user want to preserve full paths.
	 */
	public boolean isNoPrefix()
	{
		return noPrefix;
	}

	/**
	 * Set the user preference for shortening paths in the index pages.
	 * Set to true to preserve full paths.
	 */
	private void setNoPrefix(final boolean noPrefix)
	{
		this.noPrefix = noPrefix;
	}

	/**
	 * Determine the user defined prefix used to shorten paths in the index pages.
	 * @return The prefix as defined by the user or null if no user prefix is set.
	 */
	public String getPrefix()
	{
		String result = prefix;
		if(this.noPrefix)
		{
			result = null;
		}
		return result;
	}

	/**
	 * Set the user defined prefix used to shorten paths in the index pages.
	 * This should originate from the command line or config file.
	 */
	private void setPrefix(String prefix)
	{
		if(!this.noPrefix)
		{
			this.prefix = prefix;
		}
	}

	/**
	 * Get the user preference for expanding tab characters to spaces in source code view.
	 * @return The number of spaces as defined by the user otherwise -1 which means leave tabs alone.
	 */
	public int getNumSpaces()
	{
		return numSpaces;
	}

	/**
	 * Set the user preference for expanding tab characters to spaces in source code view.
	 * @param numSpaces The number of spaces tabs will be expanded to.
	 */
	private void setNumSpaces(final String numSpaces)
	{
		try
		{
			int spaces = Integer.parseInt(numSpaces);
			setNumSpaces(spaces);
		}
		catch(NumberFormatException ex)
		{
			LOGGER.log(Level.WARNING, ex.getLocalizedMessage());
		}
	}

	/**
	 * Set the user preference for expanding tab characters to spaces in source code view.
	 * @param numSpaces The number of spaces tabs will be expanded to.
	 */
	private void setNumSpaces(final int numspaces)
	{
		if(numspaces >= 0)//is zero too low?
		{
			this.numSpaces = numspaces;
		}
	}

	/**
	 * Gets the "high pass mark" for percentage of lines covered.
	 * @return The user defined high pass mark otherwise the default value.
	 */
	public byte getHiLimit()
	{
		return hiLimit;
	}

	/**
	 * Sets the "high pass mark" for percentage of lines covered.
	 */
	private void setHiLimit(final int hiLimit)
	{
		if(hiLimit > 0 && hiLimit <= 100)//genhtml just accepts whatever
		{
			this.hiLimit = (byte)hiLimit;
		}
	}

	/**
	 * Gets the "medium pass mark" for percentage of lines covered.
	 * @return The user defined medium pass mark otherwise the default value.
	 */
	public byte getMedLimit()
	{
		return medLimit;
	}


	/**
	 * Sets the "medium pass mark" for percentage of lines covered.
	 */
	private void setMedLimit(final int medLimit)
	{
		if(medLimit > 0 && medLimit <= 100)//genhtml just accepts whatever
		{
			this.medLimit = (byte)medLimit;
		}
	}

	/**
	 * Determine if the user has asked for "help" on the command line.
	 * @return true if the user wants us to display help text.
	 */
	public boolean isHelp()
	{
		return help;
	}


	/**
	 * Determine if the user has asked for version info on the command line.
	 * @return true if the user wants us to display version info.
	 */
	public boolean isVersion()
	{
		return version;
	}

	/**
	 * Determine if the user has requested quiet logging.
	 * @return true if "quiet" has been specified.
	 */
	public boolean isQuiet() {
		return quiet;
	}

	/**
	 * Determine if we are producing HTML report only (ie exclude XML version).
	 * @return true if producing html version only.
	 */
	public boolean isHtmlOnly()
	{
		return htmlOnly;
	}

	/**
	 * Set user preference for producing HTML report only (ie exclude XML version).
	 * @param htmlOnly true to produce html version only.
	 */
	private void setHtmlOnly(boolean htmlOnly)
	{
		this.htmlOnly = htmlOnly;
	}


	/**
	 * Gets the value of a numeric option.
	 * @param properties The properties in which to search for the option
	 * @param optName the name of the option to fetch
	 * @return The value of the option if it is explicitly set
	 * null if the option is not set
	 */
	private Integer getNumericValue(final Properties properties, final String optName)
	{
		Integer result = null;
		if(properties.containsKey(optName))
		{
			try
			{
				int propval = Integer.parseInt(properties.getProperty(optName));
				result = propval;
			}
			catch(NumberFormatException ex)
			{
				LOGGER.log(Level.WARNING, "{0} {1}", new Object[]{optName, ex.getLocalizedMessage()});
			}
		}
		return result;
	}

	/**
	 * Loads a config file (name value pair) to Properties.
	 * Any backslashes in the file will be escaped before being loaded to properties.
	 * @param path The path to the config file
	 * @return An instance of Properties if successfully loaded, otherwise null
	 * @throws IOException
	 */
	private static Properties loadFileToProperties(final String path) throws IOException
	{
		Properties result = null;
		if(path != null)
		{
			File configFile = new File(path);
			if(configFile.exists())
			{
				result = new Properties();
				String propFileContent = FileUtils.readFileToString(configFile);
				propFileContent = propFileContent.replace("\\", "\\\\");//can't expect the lcov config file to escape backslashes
				result.load(new StringReader(propFileContent));
			}
		}
		return result;
	}
}
