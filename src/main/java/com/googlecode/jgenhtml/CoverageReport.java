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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * Represents the entire coverage report.
 * Knows about the generation lifecycle of the whole report.
 * @author Rick Brown
 */
public final class CoverageReport
{
	private static final Logger LOGGER = Logger.getLogger(CoverageReport.class.getName());
	private static Config config = null;
	public static final String DEFAULT_TEST_NAME = "<unnamed>";
	private String testTitle;
	private String[] traceFiles;
	private ParsedFiles parsedFiles;
	private DescriptionsPage descriptionsPage;
	private Collection<TestCaseIndexPage> indexPages;
	private Set<String> runTestNames;

	static{
		JGenHtmlUtils.setLogFormatter(LOGGER);
	}

	/**
	 * Create a new report based off these tracefiles.
	 * @param traceFiles
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public CoverageReport(final String[] traceFiles) throws IOException, ParserConfigurationException
	{
		if(config == null)
		{
			config = new Config();
		}

		this.traceFiles = traceFiles;
		this.descriptionsPage = null;
		this.indexPages = null;
		this.runTestNames = null;
		this.testTitle = null;
		this.parsedFiles = new ParsedFiles();
		processTraceFiles();
		checkProcessBaselineFile(config.getBaseFile());
		checkGenerateDescriptions(config.getDescFile());
		removePrefix();//check if there is a common prefix and strip it
	}

	/**
	 * Stores information about all the source code files parsed from the tracefile.
	 * Basically a registry to manage parsed source file data.
	 * Should be a singleton.
	 */
	private class ParsedFiles
	{
		private Map<String, TestCaseSourceFile> parsedFiles;

		public ParsedFiles()
		{
			this.parsedFiles = new HashMap<String, TestCaseSourceFile>();
		}

		public TestCaseSourceFile get(final String filePath)
		{
			TestCaseSourceFile result = null;
			if(parsedFiles.containsKey(filePath))
			{
				result = parsedFiles.get(filePath);
			}
			return result;
		}

		public TestCaseSourceFile put(final String filePath, final TestCaseSourceFile parsedFile)
		{
			return parsedFiles.put(filePath, parsedFile);
		}

		public Collection<TestCaseSourceFile> getAll()
		{
			return this.parsedFiles.values();
		}

		public int getCount()
		{
			return this.parsedFiles.size();
		}

	}

	public int getPageCount()
	{
		return this.parsedFiles.getCount();
	}

	public void setDescriptionsPage(final DescriptionsPage descriptionsPage)
	{
		this.descriptionsPage = descriptionsPage;
	}

	/**
	 * Process LCOV tracefiles.
	 */
	public void processTraceFiles() throws IOException, ParserConfigurationException
	{
		for(int i=0, len=traceFiles.length; i<len; i++)
		{
			File traceFile = new File(traceFiles[i]);
			if(traceFile.exists())
			{
				if(testTitle == null && (config == null || (testTitle = config.getTitle()) == null))
				{
					testTitle = traceFiles.length == 1? traceFile.getName() : "unnamed";
				}
				LOGGER.log(Level.INFO, "Reading data file: {0}", traceFile.getName());
				parseDatFile(traceFile, false, false);
			}
			else
			{
				LOGGER.log(Level.WARNING, "Can not find file: {0}", traceFile.getAbsolutePath());
			}
		}
	}

	private void checkProcessBaselineFile(final File baseFile) throws IOException, ParserConfigurationException
	{
		if(baseFile != null)
		{
			parseDatFile(baseFile, false, true);
		}
	}

	private void checkGenerateDescriptions(final File descFile) throws IOException, ParserConfigurationException
	{
		if(descFile != null)
		{
			setDescriptionsPage(new DescriptionsPage(testTitle, runTestNames));
			parseDatFile(descFile, true, false);
		}
	}

	/**
	 * Parses a gcov tracefile.
	 * @param traceFile A gcov tracefile.
	 * @param isDescFile true if this is a descriptions (.desc) file.
	 * @param isBaseFile true if this is a baseline file.
	 */
	private void parseDatFile(final File traceFile, final boolean isDescFile, final boolean isBaseFile) throws IOException, ParserConfigurationException
	{
		//I used the info from here: http://manpages.ubuntu.com/manpages/precise/man1/geninfo.1.html
		File fileToProcess;
		if(traceFile.getName().endsWith(".gz"))
		{
			LOGGER.log(Level.FINE, "File {0} ends with .gz, going to gunzip it.", traceFile.getName());
			fileToProcess = JGenHtmlUtils.gunzip(traceFile);
		}
		else
		{
			fileToProcess = traceFile;
		}
		LineIterator iterator = FileUtils.lineIterator(fileToProcess);
		try
		{
			TestCaseSourceFile testCaseSourceFile = null;
			String testCaseName = DEFAULT_TEST_NAME;
			while(iterator.hasNext())
			{
				String line = iterator.nextLine();
				int tokenIdx = line.indexOf("SF:");
				if(tokenIdx >= 0 || (tokenIdx = line.indexOf("KF:")) >= 0)
				{
					String fullPath = line.substring(line.indexOf(tokenIdx) + 4);
					File sourceFile = new File(fullPath);
					fullPath = sourceFile.getCanonicalPath();
					testCaseSourceFile = parsedFiles.get(fullPath);
					if(!isBaseFile && testCaseSourceFile == null)
					{
						testCaseSourceFile = new TestCaseSourceFile(testTitle, sourceFile.getName());
						testCaseSourceFile.setSourceFile(sourceFile);
						parsedFiles.put(fullPath, testCaseSourceFile);
					}
				}
				else if(line.indexOf("end_of_record") >= 0)
				{
					if(testCaseSourceFile != null)
					{
						testCaseName = DEFAULT_TEST_NAME;
						testCaseSourceFile = null;
					}
					else
					{
						LOGGER.log(Level.FINE, "Unexpected end of record");
					}
				}
				else if(testCaseSourceFile != null)
				{
					testCaseSourceFile.processLine(testCaseName, line, isBaseFile);
				}
				else
				{
					if(isDescFile)
					{
						descriptionsPage.addLine(line);
					}
					else if(line.startsWith("TN:"))
					{
						String[] data = JGenHtmlUtils.extractLineValues(line);
						testCaseName = data[0].trim();
						if(testCaseName.length() > 0)
						{
							if(runTestNames == null)
							{
								runTestNames = new HashSet<String>();
							}
							runTestNames.add(testCaseName);
						}
					}
					else
					{
						LOGGER.log(Level.FINE, "Unexpected line: {0}", line);
					}
				}
			}
		}
		finally
		{
			LineIterator.closeQuietly(iterator);
		}
	}

	/**
	 * Write the coverage reports to the file system.
	 * @param testCaseSourceFiles  The source files we are processing.
	 */
	public void generateReports() throws IOException, ParserConfigurationException
	{
		try
		{
			LOGGER.log(Level.INFO, "Generating output at {0}", config.getOutRootDir().getAbsolutePath());
			Line.setTabExpand(config.getNumSpaces());
			generateCoverageReports();
			generateIndexFiles();
			generateResources();
			generateDescriptionPage();
			TopLevelIndexPage index = new TopLevelIndexPage(testTitle, indexPages);
			LOGGER.log(Level.INFO, "Writing directory view page.");
			try
			{

				LOGGER.log(Level.INFO, "Overall coverage rate:");
				logSummary("lines", index.getLineRate(), index.getLineHit(), index.getLineCount());
				logSummary("functions", index.getFunctionRate(), index.getFuncHit(), index.getFuncCount());
				logSummary("branches", index.getBranchRate(), index.getBranchHit(), index.getBranchCount());
			}
			catch(Throwable t)
			{
				//don't die if there is an exception in logging
				LOGGER.log(Level.WARNING, t.getLocalizedMessage());
			}
			index.writeToFileSystem();
		}
		catch (TransformerConfigurationException ex)
		{
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
		}
		catch (TransformerException ex)
		{
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
		}
	}

	/**
	 * Log a summary of the coverage information.
	 * @param prefix The type of coverage: "lines", "functions" or "branches".
	 * @param rate The coverage rate.
	 * @param hit The execution count.
	 * @param count The executable count.
	 */
	private static void logSummary(final String type, final float rate, final int hit, final int count)
	{
		String prefix = String.format("%1$-11s", type);
		prefix = prefix.replace(" ", ".");
		if(count > 0)
		{
			String[] info = new String[]{prefix, String.valueOf(rate * 100), String.valueOf(hit), String.valueOf(count), type};
			LOGGER.log(Level.INFO, "\t{0}: {1}% ({2} of {3} {4})", info);
		}
		else
		{
			LOGGER.log(Level.INFO, "\t{0}: no data found", type);
		}
	}

	/**
	 * Generates required resources in the output directory (CSS etc).
	 */
	private static void generateResources() throws IOException
	{
		File outRootDir = config.getOutRootDir();
		File docsRootDir;

		if(config.isHtmlOnly())
		{
			generateResourcesInDocRoot(outRootDir, false);
		}
		else
		{
			docsRootDir = JGenHtmlUtils.getTargetDir(outRootDir, false);
			generateResourcesInDocRoot(docsRootDir, false);
			docsRootDir = JGenHtmlUtils.getTargetDir(outRootDir, true);
			generateResourcesInDocRoot(docsRootDir, true);
			String ext = config.getHtmlExt();
			if(Config.DEFAULT_HTML_EXT.equals(ext))
			{
				JGenHtmlUtils.writeResource("index.html", outRootDir);
			}
			else
			{
				JGenHtmlUtils.writeResource("index.html", outRootDir, Config.DEFAULT_HTML_EXT, ext);
			}
		}
	}

	private static void generateResourcesInDocRoot(final File docRootDir, final boolean asXml) throws IOException
	{
		File cssFile = config.getCssFile();
		JGenHtmlUtils.writeResource(JGenHtmlUtils.JS_NAME, docRootDir);
		if(cssFile != null)
		{
			JGenHtmlUtils.writeResource(cssFile, docRootDir);
		}
		else
		{
			JGenHtmlUtils.writeResource(JGenHtmlUtils.CSS_NAME, docRootDir);
		}
		if(asXml)
		{
			JGenHtmlUtils.writeResource(JGenHtmlUtils.XSLT_NAME, docRootDir);
		}
		else if(config.isGzip())
		{
			FileUtils.writeStringToFile(new File(docRootDir,".htaccess"), "AddEncoding x-gzip " + config.getHtmlExt());
		}
	}

	private void generateDescriptionPage() throws TransformerConfigurationException, IOException, TransformerException
	{
		if(this.descriptionsPage != null)
		{
			LOGGER.log(Level.INFO, "Writing test case description file.");
			this.descriptionsPage.writeToFileSystem(config.getOutRootDir(), false);
			if(!config.isHtmlOnly())
			{
				this.descriptionsPage.writeToFileSystem(config.getOutRootDir(), true);
			}
		}
	}

	/**
	 * Generates index pages in output directory.
	 * @param indexPages The index pages to generate.
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	private void generateIndexFiles() throws TransformerConfigurationException, TransformerException, IOException
	{
		for(TestCaseIndexPage index : indexPages)
		{
			index.writeToFileSystem();
		}
	}

	/**
	 * Generate line coverage report pages in the output directory.
	 * @param testCaseSourceFiles The test case source files to generate.
	 * @return Index pages required to reference the coverage reports.
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	private void generateCoverageReports() throws TransformerConfigurationException, TransformerException, IOException, ParserConfigurationException
	{
		Map<String, TestCaseIndexPage> indeces = new HashMap<String, TestCaseIndexPage>();
		for(TestCaseSourceFile testCaseSourceFile : parsedFiles.getAll())
		{
			LOGGER.log(Level.INFO, "Writing report for {0}", testCaseSourceFile.getPageName());
			String path = testCaseSourceFile.getPath();
			if(!indeces.containsKey(path))
			{
				String testName = testCaseSourceFile.getTestName();
				TestCaseIndexPage indexPage = new TestCaseIndexPage(testName, path);
				String prefix = testCaseSourceFile.getPrefix();
				if(prefix != null)
				{
					indexPage.setPrefix(prefix);
				}
				indeces.put(path, indexPage);
			}
			TestCaseIndexPage indexPage = indeces.get(path);
			indexPage.addSourceFile(testCaseSourceFile);
			testCaseSourceFile.writeToFileSystem();
		}
		indexPages = indeces.values();
	}

	/**
	 * For the list of source files, iterate over each of them and remove the prefix if appropriate.
	 * @param testCaseSourceFiles The list of source files.
	 */
	public void removePrefix()
	{
		Collection<TestCaseSourceFile> testCaseSourceFiles = parsedFiles.getAll();
		String prefix = getPrefix(testCaseSourceFiles);
		if(prefix != null)
		{
			for(TestCaseSourceFile sourceFile : testCaseSourceFiles)
			{
				String path = sourceFile.getPath();
				int prefixLen = prefix.length();
				if(path.startsWith(prefix) && path.length() > prefixLen)
				{
					sourceFile.setPath(path.substring(prefixLen));
					sourceFile.setPrefix(prefix);
				}
			}
		}
	}

	/**
	 * Get the prefix to remove from paths to shorten them in the index pages.
	 * @param testCaseSourceFiles The source files we are processing.
	 * @return The prefix to remove or null (if the user specified not to remove prefixes).
	 */
	private static String getPrefix(final Collection<TestCaseSourceFile> testCaseSourceFiles)
	{
		String result;
		if(config.isNoPrefix())
		{
			LOGGER.log(Level.INFO, "User asked not to remove filename prefix");
			result = null;
		}
		else if((result = config.getPrefix()) == null)
		{
			result = JGenHtmlUtils.getPrefix(testCaseSourceFiles);
			if(result != null)
			{
				LOGGER.log(Level.INFO, "Found common filename prefix {0}", result);
			}
			else
			{
				LOGGER.log(Level.INFO, "No common filename prefix found!", result);
			}
		}
		else
		{
			LOGGER.log(Level.INFO, "Using user-specified filename prefix \"{0}\'", result);
		}
		return result;
	}

	public static void setConfig(Config config)
	{
		CoverageReport.config = config;
	}

	public static Config getConfig()
	{
		return config;
	}
}
