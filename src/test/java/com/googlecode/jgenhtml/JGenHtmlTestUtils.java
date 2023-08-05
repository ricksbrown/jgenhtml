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
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import junit.framework.TestCase;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 *
 * @author Rick Brown
 */
public class JGenHtmlTestUtils
{

	/* We could get expected values for tests by scraping the genhtml coverage reports but beause tables are used for layout and the html is anything but semantic it's a royal pita*/
//	public void getExpectedFromReport()
//	{
//		Document document = (Document)JGenHtmlTestUtils.parse("/home/rick/projects/test/genhtml/index.html");
//		NodeList tables = document.getElementsByTagName("table");
//		//extractTableData(tables, 1);
//	}
//
//	public class TableCoords
//	{
//		public int index;
//		public List<RowCoords> rows = new ArrayList<RowCoords>();
//	}
//
//	public class RowCoords
//	{
//		public int index;
//		public List<Cell> cells = new ArrayList<Cell>();
//	}
//
//	public class Cell
//	{
//		public int index;
//	}
//
//	private void extractTableData(NodeList tables, TableCoords tableCoords)
//	{
//		Element table = (Element)tables.item(tableCoords.index);
//		NodeList rows = table.getElementsByTagName("tr");
//		for(RowCoords rowCoord : tableCoords.rows)
//		{
//			Element row = (Element)rows.item(rowCoord.index);
//			NodeList cells = row.getElementsByTagName("td");
//		}
//		Element row = (Element)rows.item(rowIdx);
//		NodeList cells = row.getElementsByTagName("td");
//		for(int k=0; k<cells.getLength(); k++)
//		{
//			Element cell = (Element)cells.item(k);
//			System.out.print(" | " + cell.getTextContent() + " | ");
//		}
//	}


	public static File getTestDir()
	{
		String tmpDir = FileUtils.getTempDirectoryPath();
		File testDir = new File(tmpDir + File.separatorChar + "jgenhtml");
		if(!testDir.exists())
		{
			testDir.mkdirs();
		}
		return testDir;
	}

	public static Config getDummyConfig(String[] argv)
	{
		try
		{
			Config result = new Config();
			result.initializeUserPrefs(argv);
			return result;
		}
		catch (ParseException ex)
		{
			Logger.getLogger(JGenHtmlTestUtils.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * TODO - the tracefiles returned by this are all derived from the one example tracefile; it would be
	 * better (from a maintenance perspective) to programmatically split it and gzip it as required.
	 * @param gzipped
	 * @param split
	 * @return
	 */
	public static String[] getJstdTraceFiles(boolean gzipped, boolean split)
	{
		String[] result;
		if(split)
		{
			result = new String[]{"jsTestDriver.conf-coverage-part1.dat", "jsTestDriver.conf-coverage-part2.dat"};
		}
		else
		{
			result = new String[]{"jsTestDriver.conf-coverage.dat"};
		}
		if(gzipped)
		{
			for(int i=0; i< result.length; i++)
			{
				result[i] += ".gz";
			}
		}
		addPathAndEnsureExists(result);
		return result;
	}

	public static String getBaselineFile()
	{
		return getTraceFilesWithBranchAndFuncData()[0];  // Simply use the input files as the baseline, it should null everything out
	}

	/**
	 * Helper for getTraceFiles methods.
	 * Will prepend the test dir path to the names and ensure these files exist in the test dir.
	 * @param traceFiles An array of dummy traceFile names.
	 */
	private static void addPathAndEnsureExists(final String[] traceFiles)
	{
		File dir = getTestDir();
		for(int i=0; i<traceFiles.length; i++)  // append the full path to the file names and make sure they actually exist
		{
			String name = traceFiles[i];
			traceFiles[i] = dir.getAbsolutePath() + File.separatorChar + name;
			File datFile = new File(traceFiles[i]);
			if(!datFile.exists())
			{
				JGenHtmlUtils.writeResource(name, dir);
			}
		}
	}

	public static String[] getTraceFilesWithBranchAndFuncData()
	{
		String[] result = new String[]{"cov.gcda.info"};
		addPathAndEnsureExists(result);
		return result;
	}

	public static boolean arrayEqualsIgnoreOrder(final String[] a, final String[] b)
	{
		boolean result = a != null && b != null;
		if (result) {
			List<String> aList = Arrays.asList(a);
			List<String> bList = Arrays.asList(b);
			result = aList.containsAll(bList) &&  bList.containsAll(aList);
		}
		return result;
	}

	/**
	 * Get an lcovrc file, creating it if it doesn't exist.
	 * @param cssFile
	 * @param noPrefix
	 * @param numSpaces
	 * @param funcCoverage
	 * @param branchCoverage
	 * @return
	 */
	public static File createLcovrcFile(String cssFile, String noPrefix, String numSpaces, String funcCoverage, String branchCoverage, String sort, String noSource, String gzipHtml)
	{
		File lcovrc = new File(getTestDir(), "lcovrc");
		try
		{
			List<String> lines = new ArrayList<String>();
			// throw in a few comments and stuff
			lines.add("#");
			lines.add("#jgenthml test file");
			lines.add("#");
			lines.add("#genhtml_num_spaces = 2");
			lines.add("");
			if(cssFile != null)
			{
				lines.add("genhtml_css_file = " + cssFile);
			}
			lines.add("#genhtml_no_prefix = 1");
			lines.add("");
			if(noPrefix != null)
			{
				lines.add("genhtml_no_prefix = " + noPrefix);
			}
			if(numSpaces != null)
			{
				lines.add("genhtml_num_spaces = " + numSpaces);
			}
			if(funcCoverage != null)
			{
				lines.add("genhtml_function_coverage = " + funcCoverage);
			}
			if(branchCoverage != null)
			{
				lines.add("genhtml_branch_coverage = " + branchCoverage);
			}
			if(sort != null)
			{
				lines.add("genhtml_sort = " + sort);
			}
			if(noSource != null)
			{
				lines.add("genhtml_no_source=" + noSource);
			}
			lines.add("#genhtml_html_gzip=0");
			if(gzipHtml != null)
			{
				lines.add("genhtml_html_gzip=" + gzipHtml);
			}
			FileUtils.writeLines(lcovrc, lines);
		}
		catch (IOException ex)
		{
			Logger.getLogger(JGenHtmlTestUtils.class.getName()).log(Level.SEVERE, null, ex);
		}
		return lcovrc;
	}

	/**
	 * Get a css file, creating it if it doesn't exist.
	 */
	public static File createCssFile()
	{
		File css = new File(getTestDir(), "test.css");
		try
		{
			FileUtils.writeStringToFile(css, "body{ background-color:grey; }");
		}
		catch (IOException ex)
		{
			Logger.getLogger(JGenHtmlTestUtils.class.getName()).log(Level.SEVERE, null, ex);
		}
		return css;
	}

	public static float getCoveredRate(final String hit, final String total)
	{
		return getCoveredRate(Integer.parseInt(hit),Integer.parseInt(total));
	}

	public static float getCoveredRate(final int hit, final int total)
	{
		return (float) hit / (float) total;
	}

	/**
	 * @return The current date in the correct format for the coverage "date" attribute.
	 */
	public static String getDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}

	// first dimension
	static final int LINES = 0;

	// second dimension
	static final int HIT = 0;
	static final int TOTAL = 1;

	static final String[][] TOP_IDX_STATS_EXPECTED = new String[][]{
		new String[]{"31", "40", "77.5%"},
		new String[]{"0", "0", "-"},
		new String[]{"0", "0", "-"}
	};

	static final String[][] FIRST_IDX_STATS_EXPECTED = new String[][]{
		new String[]{"7", "9", "77.8%"},
		new String[]{"0", "0", "-"},
		new String[]{"0", "0", "-"}
	};

	static final String[][] SECOND_IDX_STATS_EXPECTED = new String[][]{
		// Based on results from LCOV version 1.14
		new String[]{"18", "24", "75.0%"},
		new String[]{"0", "0", "-"},
		new String[]{"0", "0", "-"}
	};

	static final String[][] THIRD_IDX_STATS_EXPECTED = new String[][]{
		new String[]{"6", "7", "85.7%"},
		new String[]{"0", "0", "-"},
		new String[]{"0", "0", "-"}
	};

	static final String[][] TEST_STATS_EXPECTED = new String[][]{
		new String[]{"7", "9", "77.8%"},
		new String[]{"0", "0", "-"},
		new String[]{"0", "0", "-"}
	};

	static final String[][] FRED_STATS_EXPECTED = new String[][]{
		new String[]{"18", "18", "100.0%"},
		new String[]{"0", "0", "-"},
		new String[]{"0", "0", "-"}
	};

	static final String[][] JIM_STATS_EXPECTED = new String[][]{
		new String[]{"0", "6", "0.0%"},
		new String[]{"0", "0", "-"},
		new String[]{"0", "0", "-"}
	};
	static final String[][] BOB_STATS_EXPECTED = new String[][]{
		new String[]{"6", "7", "85.7%"},
		new String[]{"0", "0", "-"},
		new String[]{"0", "0", "-"}
	};

	static final String[][] TOP_IDX_INDEX_EXPECTED = new String[][]{
		// Based on results from LCOV version 1.14
		new String[]{"/home/rick/tests", "77.8%", "77.8%", "7/9", "-", "0/0", "-", "0/0"},
		new String[]{"thing", "75.0%", "75.0%", "18/24", "-", "0/0", "-", "0/0"},
		new String[]{"thing/else", "85.7%", "85.7%", "6/7", "-", "0/0", "-", "0/0"}
	};

	static final String[][] FIRST_IDX_INDEX_EXPECTED = new String[][]{
		new String[]{"test.js", "77.8%", "77.8%", "7/9", "-", "0/0", "-", "0/0"}
	};

	static final String[][] SECOND_IDX_INDEX_EXPECTED = new String[][]{
		new String[]{"fred.js", "100.0%", "100.0%", "18/18", "-", "0/0", "-", "0/0"},
		new String[]{"jim.js", "0.0%", "0.0%", "0/6", "-", "0/0", "-", "0/0"}
	};

	static final String[][] THIRD_IDX_INDEX_EXPECTED = new String[][]{
		// Based on results from LCOV version 1.14
		new String[]{"bob.js", "85.7%", "85.7%", "6/7", "-", "0/0", "-", "0/0"}
	};

	/*
		Stats table for cov.gcda.info
	 */
	static final String[][] TOP_C_IDX_STATS_EXPECTED = new String[][]{
		new String[]{"24", "35", "68.6%"},
		new String[]{"4", "14", "28.6%"},
		new String[]{"18", "32", "56.2%"}
	};

	/*
		Index table for cov.gcda.info
	 */
	static final String[][] TOP_C_IDX_INDEX_EXPECTED = new String[][]{
		new String[]{"/home/fred/projects/test/gcov", "28.6%", "28.6%", "2/7", "0.0%", "0/6", "33.3%", "2/6"},
		new String[]{"gcov", "78.6%", "78.6%", "22/28", "50.0%", "4/8", "61.5%", "16/26"}
	};

	/*
		Stats table for cov.gcda.info/gcov
	 */
	static final String[][] FIRST_C_IDX_STATS_EXPECTED = new String[][]{
		new String[]{"22", "28", "78.6%"},
		new String[]{"4", "8", "50.0%"},
		new String[]{"16", "26", "61.5%"}
	};

	/*
		Index table for cov.gcda.info/gcov
	 */
	static final String[][] FIRST_C_IDX_INDEX_EXPECTED = new String[][]{
		new String[]{"cov.c", "85.7%", "85.7%", "6/7", "33.3%", "1/3", "66.7%", "4/6"},
		new String[]{"cov2.c", "85.7%", "85.7%", "6/7", "100.0%", "1/1", "62.5%", "5/8"},
		new String[]{"cov3.c", "85.7%", "85.7%", "6/7", "100.0%", "1/1", "83.3%", "5/6"},
		new String[]{"cov4.c", "57.1%", "57.1%", "4/7", "33.3%", "1/3", "33.3%", "2/6"}
	};

	static final String[][] COV_STATS_EXPECTED = new String[][]{
		new String[]{"6", "7", "85.7%"},
		new String[]{"1", "3", "33.3%"},
		new String[]{"4", "6", "66.7%"}
	};

	static final String[][] COV2_STATS_EXPECTED = new String[][]{
		new String[]{"6", "7", "85.7%"},
		new String[]{"1", "1", "100.0%"},
		new String[]{"5", "8", "62.5%"}
	};

	static final String[][] COV3_STATS_EXPECTED = new String[][]{
		new String[]{"6", "7", "85.7%"},
		new String[]{"1", "1", "100.0%"},
		new String[]{"5", "6", "83.3%"}
	};

	static final String[][] COV_FUNC_EXPECTED = new String[][]{
		new String[]{"main", "4"},
		new String[]{"mainThree", "0"},
		new String[]{"mainTwo", "0"}
	};

	static final String[][] COV2_FUNC_EXPECTED = new String[][]{
		new String[]{"main", "1"}
	};

	static final String[][] COV3_FUNC_EXPECTED = new String[][]{
		new String[]{"main", "1"}
	};


	/*
		Stats table for cov.gcda.info with baseline cov.gcda.info (itself)
	 */
	static final String[][] BASELINED_TOP_C_IDX_STATS_EXPECTED = new String[][]{
		new String[]{"0", "35", "0.0%"},
		new String[]{"0", "14", "0.0%"},
		new String[]{"0", "32", "0.0%"}  // Genhtml seems to set the branches to 0, is that a bug? I don't understand why.
	};

	/*
		Index table for cov.gcda.info with baseline cov.gcda.info (itself)
	 */
	static final String[][] BASELINED_TOP_C_IDX_INDEX_EXPECTED = new String[][]{
		new String[]{"/home/fred/projects/test/gcov", "0.0%", "0.0%", "0/7", "0.0%", "0/6", "0.0%", "0/6"},
		new String[]{"gcov", "0.0%", "0.0%", "0/28", "0.0%", "0/8", "0.0%", "0/26"}
	};

	static final String[][] BASELINED_FIRST_C_IDX_STATS_EXPECTED = new String[][]{
		new String[]{"0", "28", "0.0%"},
		new String[]{"0", "8", "0.0%"},
		new String[]{"0", "26", "0.0%"}  // Genhtml seems to set the branches to 0, is that a bug? I don't understand why.
	};

	/*
		Index table for cov.gcda.info/gcov with baseline cov.gcda.info (itself)
	 */
	static final String[][] BASELINED_FIRST_C_IDX_INDEX_EXPECTED = new String[][]{
		new String[]{"cov.c", "0.0%", "0.0%", "0/7", "0.0%", "0/3", "0.0%", "0/6"},
		new String[]{"cov2.c", "0.0%", "0.0%", "0/7", "0.0%", "0/1", "0.0%", "0/8"},
		new String[]{"cov3.c", "0.0%", "0.0%", "0/7", "0.0%", "0/1", "0.0%", "0/6"},
		new String[]{"cov4.c", "0.0%", "0.0%", "0/7", "0.0%", "0/3", "0.0%", "0/6"}
	};

	static final String[][] BASELINED_COV_STATS_EXPECTED = new String[][]{
		new String[]{"0", "7", "0.0%"},
		new String[]{"0", "3", "0.0%"},
		new String[]{"0", "6", "0.0%"}
	};

	static final String[][] BASELINED_COV2_STATS_EXPECTED = new String[][]{
		new String[]{"0", "7", "0.0%"},
		new String[]{"0", "1", "0.0%"},
		new String[]{"0", "8", "0.0%"}
	};

	static final String[][] BASELINED_COV3_STATS_EXPECTED = new String[][]{
		new String[]{"0", "7", "0.0%"},
		new String[]{"0", "1", "0.0%"},
		new String[]{"0", "6", "0.0%"}
	};

	static final String[][] BASELINED_COV_FUNC_EXPECTED = new String[][]{
		new String[]{"main", "0"},
		new String[]{"mainThree", "0"},
		new String[]{"mainTwo", "0"}
	};

	static final String[][] BASELINED_COV2_FUNC_EXPECTED = new String[][]{
		new String[]{"main", "0"}
	};

	static final String[][] BASELINED_COV3_FUNC_EXPECTED = new String[][]{
		new String[]{"main", "0"}
	};

	/**
	 * Get an HTML page as a DOM object
	 * @param location The path to the HTML file.
	 */
	public static Node parse(String location)
	{
		try
		{
			XMLReader reader = new Parser();
			reader.setFeature(Parser.namespacesFeature, false);
			reader.setFeature(Parser.namespacePrefixesFeature, false);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMResult result = new DOMResult();
			transformer.transform(new SAXSource(reader, new InputSource(new FileReader(new File(location)))), result);
			return result.getNode();
		}
		catch (TransformerException | SAXNotRecognizedException | IOException | SAXNotSupportedException ex)
		{
			TestCase.fail(ex.getLocalizedMessage());
		}
		return null;
	}

}
