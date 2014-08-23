/*
	Copyright (C) 2012  Rick Brown

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.jgenhtml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Note: expected values are determined by running native genhtml on my ubuntu system.
 * @author Rick Brown
 */
public class JGenHtmlTest extends TestCase
{
	//private final static Logger LOGGER = Logger.getLogger(JGenHtmlTest.class.getName());
	//private String traceFileName = null;
	private File jstdTestDir;//output results from processing a tracefile as produced by JSTD (i.e. nothing but line data)
	private File cCodeTestDir;//output results from processing a tracefile with function and branch coverage as well as line data
	private File baselinedTestDir;//output results from processing a tracefile with a baseline file
	private File noSourceTestDir;//test with --no-source flag

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public JGenHtmlTest(String testName)
	{
		super(testName);
		System.out.println("oneTimeSetUp");
		runJgenhtml();
	}

	private void runJgenhtml()
	{
		try
		{
			File outputDir = JGenHtmlTestUtils.getTestDir();
			FileUtils.cleanDirectory(outputDir);//START WITH A CLEAN DIRECTORY!
			jstdTestDir = new File(outputDir, "jstd");
			cCodeTestDir = new File(outputDir, "code");
			baselinedTestDir = new File(outputDir, "baselined");
			noSourceTestDir = new File(outputDir, "nosource");
			String[] argv = new String[]{"-o", jstdTestDir.getAbsolutePath(), JGenHtmlTestUtils.getJstdTraceFiles(false, false)[0]};
			JGenHtml.main(argv);
			argv = new String[]{"-o", cCodeTestDir.getAbsolutePath(), JGenHtmlTestUtils.getTraceFilesWithBranchAndFuncData()[0]};
			JGenHtml.main(argv);
			argv = new String[]{"-o", baselinedTestDir.getAbsolutePath(), "-b", JGenHtmlTestUtils.getBaselineFile(), JGenHtmlTestUtils.getTraceFilesWithBranchAndFuncData()[0]};
			JGenHtml.main(argv);
			argv = new String[]{"-o", noSourceTestDir.getAbsolutePath(), "--no-source",JGenHtmlTestUtils.getTraceFilesWithBranchAndFuncData()[0]};
			JGenHtml.main(argv);
		}
		catch (IOException ex)
		{
			fail(ex.getLocalizedMessage());
		}
	}

	/**
	 * Test the stats table on top level index page.
	 */
	public void testTopIndexPageStats()
	{
		System.out.println("testTopIndexPageStats");
		boolean result = checkTablesOnPage(jstdTestDir, "", "index.html", JGenHtmlTestUtils.TOP_IDX_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	/**
	 * Test the index table on top level index page.
	 */
	public void testTopIndexPageIndex()
	{
		System.out.println("testTopIndexPageIndex");
		boolean result = checkTablesOnPage(jstdTestDir, "", "index.html", JGenHtmlTestUtils.TOP_IDX_INDEX_EXPECTED, "index");
		assertTrue(result);
	}

	/**
	 * Test the stats table on first source index page.
	 */
	public void testFirstIndexPageStats()
	{
		System.out.println("testFirstIndexPageStats");
		boolean result = checkTablesOnPage(jstdTestDir, "home/rick/tests", "index.html", JGenHtmlTestUtils.FIRST_IDX_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	/**
	 * Test the index table on first source index page.
	 */
	public void testFirstIndexPageIndex()
	{
		System.out.println("testFirstIndexPageStats");
		boolean result = checkTablesOnPage(jstdTestDir, "home/rick/tests", "index.html", JGenHtmlTestUtils.FIRST_IDX_INDEX_EXPECTED, "index");
		assertTrue(result);
	}

	/**
	 * Test the stats table on second source index page.
	 */
	public void testSecondIndexPageStats()
	{
		System.out.println("testSecondIndexPageStats");
		boolean result = checkTablesOnPage(jstdTestDir, "some/thing", "index.html", JGenHtmlTestUtils.SECOND_IDX_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	/**
	 * Test the index table on second source index page.
	 */
	public void testSecondIndexPageIndex()
	{
		System.out.println("testSecondIndexPageIndex");
		boolean result = checkTablesOnPage(jstdTestDir, "some/thing", "index.html", JGenHtmlTestUtils.SECOND_IDX_INDEX_EXPECTED, "index");
		assertTrue(result);
	}

	/**
	 * Test the stats table on third source index page.
	 */
	public void testThirdIndexPageStats()
	{
		System.out.println("testThirdIndexPageStats");
		boolean result = checkTablesOnPage(jstdTestDir, "some/thing/else", "index.html", JGenHtmlTestUtils.THIRD_IDX_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	/**
	 * Test the index table on third source index page.
	 */
	public void testThirdIndexPageIndex()
	{
		System.out.println("testThirdIndexPageIndex");
		boolean result = checkTablesOnPage(jstdTestDir, "some/thing/else", "index.html", JGenHtmlTestUtils.THIRD_IDX_INDEX_EXPECTED, "index");
		assertTrue(result);
	}

	public void testTestJs()
	{
		System.out.println("testTestJs");
		boolean result = checkTablesOnPage(jstdTestDir, "home/rick/tests", "test.js.gcov.html", JGenHtmlTestUtils.TEST_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testFredJs()
	{
		System.out.println("testFredJs");
		boolean result = checkTablesOnPage(jstdTestDir, "some/thing", "fred.js.gcov.html", JGenHtmlTestUtils.FRED_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testJimJs()
	{
		System.out.println("testJimJs");
		boolean result = checkTablesOnPage(jstdTestDir, "some/thing", "jim.js.gcov.html", JGenHtmlTestUtils.JIM_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testBobJs()
	{
		System.out.println("testBobJs");
		boolean result = checkTablesOnPage(jstdTestDir, "some/thing/else", "bob.js.gcov.html", JGenHtmlTestUtils.BOB_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testTestJsFunc()
	{
		System.out.println("testTestJsFunc");
		boolean result = checkTablesOnPage(jstdTestDir, "home/rick/tests", "test.js.func.html", new String[][]{}, "functionCoverage");
		assertFalse("function page should not exist if no functions found", result);
	}

	public void testFredJsFunc()
	{
		System.out.println("testFredJsFunc");
		boolean result = checkTablesOnPage(jstdTestDir, "some/thing", "fred.js.func.html", new String[][]{}, "functionCoverage");
		assertFalse("function page should not exist if no functions found", result);
	}

	public void testJimJsFunc()
	{
		System.out.println("testJimJsFunc");
		boolean result = checkTablesOnPage(jstdTestDir, "some/thing", "jim.js.func.html", new String[][]{}, "functionCoverage");
		assertFalse("function page should not exist if no functions found", result);
	}

	public void testBobJsFunc()
	{
		System.out.println("testBobJsFunc");
		boolean result = checkTablesOnPage(jstdTestDir, "some/thing/else", "bob.js.func.html", new String[][]{}, "functionCoverage");
		assertFalse("function page should not exist if no functions found", result);
	}

	public void testCovC()
	{
		System.out.println("testCovC");
		boolean result = checkTablesOnPage(cCodeTestDir, "test/gcov", "cov.c.gcov.html", JGenHtmlTestUtils.COV_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testCov2C()
	{
		System.out.println("testCov2C");
		boolean result = checkTablesOnPage(cCodeTestDir, "test/gcov", "cov2.c.gcov.html", JGenHtmlTestUtils.COV2_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testCov3C()
	{
		System.out.println("testCov3C");
		boolean result = checkTablesOnPage(cCodeTestDir, "test/gcov", "cov3.c.gcov.html", JGenHtmlTestUtils.COV3_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testCovCFuncStats()
	{
		System.out.println("testCovCFuncStats");
		boolean result = checkTablesOnPage(cCodeTestDir, "test/gcov", "cov.c.func.html", JGenHtmlTestUtils.COV_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testCovCFunc()
	{
		System.out.println("testCovCFunc");
		boolean result = checkTablesOnPage(cCodeTestDir, "test/gcov", "cov.c.func.html", JGenHtmlTestUtils.COV_FUNC_EXPECTED, "functionCoverage");
		assertTrue(result);
	}

	public void testCovC2FuncStats()
	{
		System.out.println("testCovC2FuncStats");
		boolean result = checkTablesOnPage(cCodeTestDir, "test/gcov", "cov2.c.func.html", JGenHtmlTestUtils.COV2_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testCovC2Func()
	{
		System.out.println("testCovC2Func");
		boolean result = checkTablesOnPage(cCodeTestDir, "test/gcov", "cov2.c.func.html", JGenHtmlTestUtils.COV2_FUNC_EXPECTED, "functionCoverage");
		assertTrue(result);
	}

	public void testCovC3FuncStats()
	{
		System.out.println("testCovC3FuncStats");
		boolean result = checkTablesOnPage(cCodeTestDir, "test/gcov", "cov3.c.func.html", JGenHtmlTestUtils.COV3_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testCovC3Func()
	{
		System.out.println("testCovC3Func");
		boolean result = checkTablesOnPage(cCodeTestDir, "test/gcov", "cov3.c.func.html", JGenHtmlTestUtils.COV3_FUNC_EXPECTED, "functionCoverage");
		assertTrue(result);
	}

	/**
	 * Test the stats table on top level c-code index page.
	 */
	public void testTopCIndexPageStats()
	{
		System.out.println("testTopCIndexPageStats");
		boolean result = checkTablesOnPage(cCodeTestDir, "", "index.html", JGenHtmlTestUtils.TOP_C_IDX_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	/**
	 * Test the index table on top level c-code index page.
	 */
	public void testTopCIndexPageIndex()
	{
		System.out.println("testTopCIndexPageIndex");
		boolean result = checkTablesOnPage(cCodeTestDir, "", "index.html", JGenHtmlTestUtils.TOP_C_IDX_INDEX_EXPECTED, "index");
		assertTrue(result);
	}

	/**
	 * Test the stats table on first c-code source index page.
	 */
	public void testFirstCIndexPageStats()
	{
		System.out.println("testFirstCIndexPageStats");
		boolean result = checkTablesOnPage(cCodeTestDir, "test/gcov", "index.html", JGenHtmlTestUtils.FIRST_C_IDX_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	/**
	 * Test the index table on first c-code source index page.
	 */
	public void testFirstCIndexPageIndex()
	{
		System.out.println("testFirstCIndexPageIndex");
		boolean result = checkTablesOnPage(cCodeTestDir, "test/gcov", "index.html", JGenHtmlTestUtils.FIRST_C_IDX_INDEX_EXPECTED, "index");
		assertTrue(result);
	}

	public void testBaselinedCovC()
	{
		System.out.println("testBaselinedCovC");
		boolean result = checkTablesOnPage(baselinedTestDir, "test/gcov", "cov.c.gcov.html", JGenHtmlTestUtils.BASELINED_COV_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testBaselinedCov2C()
	{
		System.out.println("testBaselinedCov2C");
		boolean result = checkTablesOnPage(baselinedTestDir, "test/gcov", "cov2.c.gcov.html", JGenHtmlTestUtils.BASELINED_COV2_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testBaselinedCov3C()
	{
		System.out.println("testBaselinedCov3C");
		boolean result = checkTablesOnPage(baselinedTestDir, "test/gcov", "cov3.c.gcov.html", JGenHtmlTestUtils.BASELINED_COV3_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testBaselinedCovCFuncStats()
	{
		System.out.println("testBaselinedCovCFuncStats");
		boolean result = checkTablesOnPage(baselinedTestDir, "test/gcov", "cov.c.func.html", JGenHtmlTestUtils.BASELINED_COV_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testBaselinedCovCFunc()
	{
		System.out.println("testBaselinedCovCFunc");
		boolean result = checkTablesOnPage(baselinedTestDir, "test/gcov", "cov.c.func.html", JGenHtmlTestUtils.BASELINED_COV_FUNC_EXPECTED, "functionCoverage");
		assertTrue(result);
	}

	public void testBaselinedCovC2FuncStats()
	{
		System.out.println("testBaselinedCovC2FuncStats");
		boolean result = checkTablesOnPage(baselinedTestDir, "test/gcov", "cov2.c.func.html", JGenHtmlTestUtils.BASELINED_COV2_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testBaselinedCovC2Func()
	{
		System.out.println("testBaselinedCovC2Func");
		boolean result = checkTablesOnPage(baselinedTestDir, "test/gcov", "cov2.c.func.html", JGenHtmlTestUtils.BASELINED_COV2_FUNC_EXPECTED, "functionCoverage");
		assertTrue(result);
	}

	public void testBaselinedCovC3FuncStats()
	{
		System.out.println("testBaselinedCovC3FuncStats");
		boolean result = checkTablesOnPage(baselinedTestDir, "test/gcov", "cov3.c.func.html", JGenHtmlTestUtils.BASELINED_COV3_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	public void testBaselinedCovC3Func()
	{
		System.out.println("testBaselinedCovC3Func");
		boolean result = checkTablesOnPage(baselinedTestDir, "test/gcov", "cov3.c.func.html", JGenHtmlTestUtils.BASELINED_COV3_FUNC_EXPECTED, "functionCoverage");
		assertTrue(result);
	}

	/**
	 * Test the stats table on top level c-code index page.
	 */
	public void testBaselinedTopCIndexPageStats()
	{
		System.out.println("testBaselinedTopCIndexPageStats");
		boolean result = checkTablesOnPage(baselinedTestDir, "", "index.html", JGenHtmlTestUtils.BASELINED_TOP_C_IDX_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	/**
	 * Test the index table on top level c-code index page.
	 */
	public void testBaselinedTopCIndexPageIndex()
	{
		System.out.println("testBaselinedTopCIndexPageIndex");
		boolean result = checkTablesOnPage(baselinedTestDir, "", "index.html", JGenHtmlTestUtils.BASELINED_TOP_C_IDX_INDEX_EXPECTED, "index");
		assertTrue(result);
	}

	/**
	 * Test the stats table on first c-code source index page.
	 */
	public void testBaselinedFirstCIndexPageStats()
	{
		System.out.println("testBaselinedFirstCIndexPageStats");
		boolean result = checkTablesOnPage(baselinedTestDir, "test/gcov", "index.html", JGenHtmlTestUtils.BASELINED_FIRST_C_IDX_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	/**
	 * Test the index table on first c-code source index page.
	 */
	public void testBaselinedFirstCIndexPageIndex()
	{
		System.out.println("testBaselinedFirstCIndexPageIndex");
		boolean result = checkTablesOnPage(baselinedTestDir, "test/gcov", "index.html", JGenHtmlTestUtils.BASELINED_FIRST_C_IDX_INDEX_EXPECTED, "index");
		assertTrue(result);
	}


	public void testNoSourceCovC()
	{
		System.out.println("testNoSourceCovC");
		boolean result = checkTablesOnPage(noSourceTestDir, "test/gcov", "cov.c.gcov.html", JGenHtmlTestUtils.COV_STATS_EXPECTED, "stats");
		assertFalse("should not create source with no-source flag set", result);
	}

	public void testNoSourceCov2C()
	{
		System.out.println("testNoSourceCov2C");
		boolean result = checkTablesOnPage(noSourceTestDir, "test/gcov", "cov2.c.gcov.html", JGenHtmlTestUtils.COV2_STATS_EXPECTED, "stats");
		assertFalse("should not create source with no-source flag set", result);
	}

	public void testNoSourceCov3C()
	{
		System.out.println("testNoSourceCov3C");
		boolean result = checkTablesOnPage(noSourceTestDir, "test/gcov", "cov3.c.gcov.html", new String[][]{}, "stats");
		assertFalse("should not create source with no-source flag set", result);
	}

	public void testNoSourceCovCFunc()
	{
		System.out.println("testNoSourceCovCFunc");
		boolean result = checkTablesOnPage(noSourceTestDir, "test/gcov", "cov.c.func.html", new String[][]{}, "functionCoverage");
		assertFalse("should not create function page with no-source flag set", result);
	}


	public void testNoSourceCovC2Func()
	{
		System.out.println("testNoSourceCovC2Func");
		boolean result = checkTablesOnPage(noSourceTestDir, "test/gcov", "cov2.c.func.html", new String[][]{}, "functionCoverage");
		assertFalse("should not create function page with no-source flag set", result);
	}

	public void testNoSourceCovC3Func()
	{
		System.out.println("testNoSourceCovC3Func");
		boolean result = checkTablesOnPage(noSourceTestDir, "test/gcov", "cov3.c.func.html", new String[][]{}, "functionCoverage");
		assertFalse("should not create function page with no-source flag set", result);
	}

	/**
	 * Test the stats table on top level c-code index page.
	 */
	public void testNoSourceTopCIndexPageStats()
	{
		System.out.println("testNoSourceTopCIndexPageStats");
		boolean result = checkTablesOnPage(noSourceTestDir, "", "index.html", JGenHtmlTestUtils.TOP_C_IDX_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	/**
	 * Test the index table on top level c-code index page.
	 */
	public void testNoSourceTopCIndexPageIndex()
	{
		System.out.println("testNoSourceTopCIndexPageIndex");
		boolean result = checkTablesOnPage(noSourceTestDir, "", "index.html", JGenHtmlTestUtils.TOP_C_IDX_INDEX_EXPECTED, "index");
		assertTrue(result);
	}

	/**
	 * Test the stats table on first c-code source index page.
	 */
	public void testNoSourceFirstCIndexPageStats()
	{
		System.out.println("testNoSourceFirstCIndexPageStats");
		boolean result = checkTablesOnPage(noSourceTestDir, "test/gcov", "index.html", JGenHtmlTestUtils.FIRST_C_IDX_STATS_EXPECTED, "stats");
		assertTrue(result);
	}

	/**
	 * Test the index table on first c-code source index page.
	 */
	public void testNoSourceFirstCIndexPageIndex()
	{
		System.out.println("testNoSourceFirstCIndexPageIndex");
		boolean result = checkTablesOnPage(noSourceTestDir, "test/gcov", "index.html", JGenHtmlTestUtils.FIRST_C_IDX_INDEX_EXPECTED, "index");
		assertTrue(result);
	}

	/**
	 *
	 * @param pageName The file name, for example "index.html"
	 * @param relativePath The path, relative to the HTML output root dir, for example "some/sub/directory"
	 * @param expectedResults A 2d array containing expected results.
	 * @param tableClass The class attribute of the table to find and check.
	 * @return true if the page exists, false if it doesn't
	 */
	private boolean checkTablesOnPage(File rootDir, String relativePath, String pageName, String[][] expectedResults, String tableClass)
	{
		boolean result;
		File htmlDir = JGenHtmlUtils.getTargetDir(rootDir, false, relativePath);
		File page = new File(htmlDir, pageName);
		if((result = page.exists()))
		{
			Document document = (Document) JGenHtmlTestUtils.parse(page.getAbsolutePath());//not really caring about performance in my tests
			checkTable(document, expectedResults, tableClass);
		}
		return result;
	}

	/**
	 * Check the values in the cells of an HTML table row by row.
	 * @param document
	 * @param expectedResults
	 */
	private void checkTable(Document document, String[][] expectedResults, String tableClass)
	{
		List<Element> tables = getElementsByTagAndClass(document, "table", tableClass);
		if(tables.size() == 1)
		{
			Element table = tables.get(0);
			Element tbody = (Element) table.getElementsByTagName("tbody").item(0);//all jgenhtml tables have explicit tbody elements
			NodeList rows = tbody.getElementsByTagName("tr");
			assertTrue("Not testing anything!", rows.getLength() > 0);
			for(int i=0; i< rows.getLength(); i++)
			{
				String[] expected = expectedResults[i];
				NodeList cells = ((Element)rows.item(i)).getElementsByTagName("td");
				assertEquals("Unexpected cell count", expected.length, cells.getLength());
				for(int j=0; j<cells.getLength(); j++)
				{
					assertEquals(expected[j], cells.item(j).getTextContent().trim());
				}
			}
		}
		else
		{
			fail("Expected to find one table but got " + tables.size());
		}
	}

	/**
	 * Get elements which match the tagName and className specified.
	 * @param document The document which contains the elements we are looking for.
	 * @param tagName The tag name of the elements to match.
	 * @param className The CSS class to match. If null will return all elements that match the tagName alone.
	 * @return A collection of matching elements.
	 */
	private static List<Element> getElementsByTagAndClass(Document document, String tagName, String className)
	{
		NodeList candidates = document.getElementsByTagName(tagName);
		List<Element> result = new ArrayList<Element>();
		for(int i=0; i<candidates.getLength(); i++)
		{
			Element next = (Element)candidates.item(i);
			if(className == null || className.equals(next.getAttribute("class")))//todo find a space separated list
			{
				result.add(next);
			}
		}
		return result;
	}
}
