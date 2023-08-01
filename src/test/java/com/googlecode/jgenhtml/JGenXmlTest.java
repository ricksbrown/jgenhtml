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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This tests the XML output version of the reports.
 * Kind of abandoned this before it got far because it is not a complete end to end test (it does not test the XSL layer).
 * Switched to testing the HTML instead using tag-soup but thought I'd leave this in place anyway.
 * The more tests the merrier.
 * @author Rick Brown
 */
public class JGenXmlTest extends TestCase
{
	private String traceFileName = null;

	public JGenXmlTest(String testName)
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
			String outputDirPath = outputDir.getAbsolutePath();
			String traceFile = JGenHtmlTestUtils.getJstdTraceFiles(false, false)[0];
			String[] argv = new String[]{"-o", outputDirPath, traceFile};
			traceFileName = new File(traceFile).getName();
			JGenHtml.main(argv);
		}
		catch (IOException ex)
		{
			fail(ex.getLocalizedMessage());
		}
	}

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

	/**
	 * Test of main method, of class JGenHtml.
	 */
	public void testMain()
	{
		System.out.println("main");
		Document doc = getXmlPage("xml/index.xml");
		Element root = doc.getDocumentElement();
		assertEquals(root.getTagName(), "coverage");
		String[] lines = JGenHtmlTestUtils.TOP_IDX_STATS_EXPECTED[JGenHtmlTestUtils.LINES];
		Map<String, String> expected = getExpectedCoverage(lines[JGenHtmlTestUtils.TOTAL], lines[JGenHtmlTestUtils.HIT], traceFileName, "index");
		checkAttributes(expected, root);
	}

	/**
	 * Coverage Element checks
	 * @param lv lines valid
	 * @param lc lines covered
	 * @param tn test name
	 * @param fn file name
	 * @return
	 */
	private Map<String, String> getExpectedCoverage(String lv, String lc, String tn, String fn)
	{
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("lines-valid", lv);
		expected.put("lines-covered", lc);
		expected.put("line-rate", String.valueOf(JGenHtmlTestUtils.getCoveredRate(lc, lv)));
		expected.put("testname", tn);
		expected.put("filename", fn);
		expected.put("date", JGenHtmlTestUtils.getDate());
		return expected;
	}

	private void checkAttributes(Map<String, String> attributes, Element element)
	{
		for (String name : attributes.keySet())
		{
			assertTrue(element.hasAttribute(name));
			assertEquals(attributes.get(name), element.getAttribute(name));
		}
	}

	private Document getXmlPage(final String relativePathToFile)
	{
		Document result = null;
		File page = getReportFile(relativePathToFile);
		if(page.exists())
		{
			try
			{
				result = JGenHtmlUtils.loadXmlDoc(new FileInputStream(page));
			}
			catch (FileNotFoundException ex)
			{
				fail("Could not parse " + page.getAbsolutePath() + " " + ex.getLocalizedMessage());
			}
		}
		else
		{
			fail("Could not find " + page.getAbsolutePath());
		}
		return result;
	}

	/**
	 *
	 * @param relativePathToFile The path to the file relative to the output directory (i.e. no leading file separator).
	 * @return The file you want
	 */
	private File getReportFile(final String relativePathToFile)
	{
		File outputDir = JGenHtmlTestUtils.getTestDir();
		String outputDirPath = outputDir.getAbsolutePath();
		String absPathToFile = outputDirPath + File.separatorChar + relativePathToFile;
		File result = new File(absPathToFile);
		return result;
	}

}
