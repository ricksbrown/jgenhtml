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
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;

/**
 *
 * @author Rick Brown
 */
public class CoverageReportTest extends TestCase
{

	public CoverageReportTest(String testName)
	{
		super(testName);
		CoverageReport.setConfig(JGenHtmlTestUtils.getDummyConfig(new String[]{}));
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
	 * Test of processTraceFile method, of class JGenHtml.
	 */
	public void testProcessTraceFile()
	{
		System.out.println("processTraceFile");
		String[] traceFiles = JGenHtmlTestUtils.getJstdTraceFiles(false, false);
		processTraceFileHelper(traceFiles);
	}

	/**
	 * Test of processTraceFile method, of class JGenHtml.
	 */
	public void testProcessTraceFileMultiple()
	{
		System.out.println("testProcessTraceFileMultiple");
		String[] traceFiles = JGenHtmlTestUtils.getJstdTraceFiles(false, true);
		processTraceFileHelper(traceFiles);
	}

	/**
	 * Test of processTraceFile method, of class JGenHtml.
	 */
	public void testProcessTraceFileGzipped()
	{
		System.out.println("testProcessTraceFileGzipped");
		String[] traceFiles = JGenHtmlTestUtils.getJstdTraceFiles(true, false);
		processTraceFileHelper(traceFiles);
	}

	/**
	 * Test of processTraceFile method, of class JGenHtml.
	 */
	public void testProcessTraceFileMultipleGzipped()
	{
		System.out.println("testProcessTraceFileMultipleGzipped");
		String[] traceFiles = JGenHtmlTestUtils.getJstdTraceFiles(true, true);
		processTraceFileHelper(traceFiles);
	}

	private void processTraceFileHelper(String[] traceFiles)
	{
		try
		{
			String fileName;
			CoverageReport report = new CoverageReport(traceFiles);
//			List<TestCaseSourceFile> result = report.getParsedFiles();
//			assertEquals(4, result.size());
//			assertNull(result.get(0).getPrefix());
//			assertEquals("/home/rick/tests", result.get(1).getPrefix());
//			assertEquals("/home/rick/tests", result.get(2).getPrefix());
//			assertEquals("/home/rick/tests", result.get(3).getPrefix());
			if(traceFiles.length == 1)
			{
				File traceFile = new File(traceFiles[0]);
				fileName = traceFile.getName();
			}
			else
			{
				fileName = "unnamed";
			}
		}
		catch (IOException ex)
		{
			fail(ex.getMessage());
		}
		catch (ParserConfigurationException ex)
		{
			fail(ex.getMessage());
		}
	}

	private String getExpectedToString(float lr, int lv, int lc, String tn, String fn)
	{
		return "{\"line-rate\":" + lr + ",\"lines-valid\":" + lv + ",\"lines-covered\":" + lc + ",\"testname\":\"" + tn + "\",\"filename\":\"" + fn + "\"}";
	}

	public float getCr(final int covered, final int lineCount)
	{
		return ((float)covered / (float) lineCount);
	}

//	/**
//	 * Test of addSourceFile method, of class CoverageReport.
//	 */
//	public void testAddSourceFile()
//	{
////		System.out.println("addSourceFile");
////		TestCaseSourceFile sourceFile = null;
////		CoverageReport instance = null;
////		instance.addSourceFile(sourceFile);
////		// TODO review the generated test code and remove the default call to fail.
////		fail("The test case is a prototype.");
////	}
////
////	/**
////	 * Test of getParsedFiles method, of class CoverageReport.
////	 */
////	public void testGetParsedFiles()
////	{
////		System.out.println("getParsedFiles");
////		CoverageReport instance = null;
////		List expResult = null;
////		List result = instance.getParsedFiles();
////		assertEquals(expResult, result);
////		// TODO review the generated test code and remove the default call to fail.
////		fail("The test case is a prototype.");
////	}
////
////	/**
////	 * Test of getDescriptionsPage method, of class CoverageReport.
////	 */
////	public void testGetDescriptionsPage()
////	{
////		System.out.println("getDescriptionsPage");
////		CoverageReport instance = null;
////		DescriptionsPage expResult = null;
////		DescriptionsPage result = instance.getDescriptionsPage();
////		assertEquals(expResult, result);
////		// TODO review the generated test code and remove the default call to fail.
////		fail("The test case is a prototype.");
////	}
////
////	/**
////	 * Test of setDescriptionsPage method, of class CoverageReport.
////	 */
////	public void testSetDescriptionsPage()
////	{
////		System.out.println("setDescriptionsPage");
////		DescriptionsPage descriptionsPage = null;
////		CoverageReport instance = null;
////		instance.setDescriptionsPage(descriptionsPage);
////		// TODO review the generated test code and remove the default call to fail.
////		fail("The test case is a prototype.");
////	}
////
////	/**
////	 * Test of checkGenerateDescriptions method, of class CoverageReport.
////	 */
////	public void testCheckGenerateDescriptions() throws Exception
////	{
////		System.out.println("checkGenerateDescriptions");
////		File descFile = null;
////		CoverageReport instance = null;
////		instance.checkGenerateDescriptions(descFile);
////		// TODO review the generated test code and remove the default call to fail.
////		fail("The test case is a prototype.");
////	}
////
////	/**
////	 * Test of generateReports method, of class CoverageReport.
////	 */
////	public void testGenerateReports() throws Exception
////	{
////		System.out.println("generateReports");
////		CoverageReport instance = null;
////		instance.generateReports();
////		// TODO review the generated test code and remove the default call to fail.
////		fail("The test case is a prototype.");
////	}
////
////	/**
////	 * Test of removePrefix method, of class CoverageReport.
////	 */
////	public void testRemovePrefix()
////	{
////		System.out.println("removePrefix");
////		CoverageReport instance = null;
////		instance.removePrefix();
////		// TODO review the generated test code and remove the default call to fail.
////		fail("The test case is a prototype.");
////	}
////
////	/**
////	 * Test of setConfig method, of class CoverageReport.
////	 */
////	public void testSetConfig()
////	{
////		System.out.println("setConfig");
////		Config config = null;
////		CoverageReport.setConfig(config);
////		// TODO review the generated test code and remove the default call to fail.
////		fail("The test case is a prototype.");
////	}
////
////	/**
////	 * Test of getConfig method, of class CoverageReport.
////	 */
////	public void testGetConfig()
////	{
////		System.out.println("getConfig");
////		Config expResult = null;
////		Config result = CoverageReport.getConfig();
////		assertEquals(expResult, result);
////		// TODO review the generated test code and remove the default call to fail.
////		fail("The test case is a prototype.");
////	}
}
