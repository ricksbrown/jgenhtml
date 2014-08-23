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

import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.w3c.dom.Document;

/**
 * @author Rick Brown
 */
public class CoveragePageTest extends TestCase
{
	private static final String TEST_NAME = "jUnitTest";
	private static final String PAGE_NAME = "/test/junit";

	public CoveragePageTest(String testName)
	{
		super(testName);
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
	 * Test of addToLineCount method, of class CoveragePage.
	 */
	public void testAddToLineCount()
	{
		System.out.println("addToLineCount");
		CoveragePage instance = getDummyCoveragePage();
		assertEquals(0, instance.getLineCount());
		instance.addToLineCount(1);
		assertEquals(1, instance.getLineCount());
		instance.addToLineCount(4);
		assertEquals(5, instance.getLineCount());
	}

	/**
	 * Test of addToCovered method, of class CoveragePage.
	 */
	public void testAddToCovered()
	{
		System.out.println("addToCovered");
		CoveragePage instance = getDummyCoveragePage();
		assertEquals(0, instance.getLineHit());
		instance.addToCovered(2);
		assertEquals(2, instance.getLineHit());
		instance.addToCovered(4);
		assertEquals(6, instance.getLineHit());
	}

	/**
	 * Test of setPath method, of class CoveragePage.
	 */
	public void testSetPath()
	{
		System.out.println("setPath");
		CoveragePage instance = getDummyCoveragePage();
		String path = "/foo/bar/";
		instance.setPath(path);
		assertEquals(path, instance.getPath());
	}

	/**
	 * Test of setPath method, of class CoveragePage.
	 */
	public void testSetPathWithDriveLetter()
	{
		System.out.println("testSetPathWithDriveLetter");
		CoveragePage instance = getDummyCoveragePage();
		String path = "d:/foo/bar/";
		instance.setPath(path);
		assertEquals("/foo/bar/", instance.getPath());
	}

	/**
	 * Test of setPath method, of class CoveragePage.
	 */
	public void testSetPathWithDriveLetterAndDoubleSlashes()
	{
		System.out.println("testSetPathWithDriveLetterAndDoubleSlashes");
		CoveragePage instance = getDummyCoveragePage();
		String path = "d://foo/bar/";
		instance.setPath(path);
		assertEquals("/foo/bar/", instance.getPath());
	}

	/**
	 * Test of getTestName method, of class CoveragePage.
	 */
	public void testGetTestName()
	{
		System.out.println("getTestName");
		CoveragePage instance = getDummyCoveragePage();
		String expResult = TEST_NAME;
		String result = instance.getTestName();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getLineCount method, of class CoveragePage.
	 * Tested more in testAddToLineCount
	 */
	public void testGetLineCount()
	{
		System.out.println("getLineCount");
		CoveragePage instance = getDummyCoveragePage();
		int expResult = 0;
		int result = instance.getLineCount();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getCovered method, of class CoveragePage.
	 * Tested more in testAddToCovered.
	 */
	public void testGetCovered()
	{
		System.out.println("getCovered");
		CoveragePage instance = getDummyCoveragePage();
		int expResult = 0;
		int result = instance.getLineHit();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getPath method, of class CoveragePage.
	 * Tested more in testSetPath.
	 */
	public void testGetPath()
	{
		System.out.println("getPath");
		CoveragePage instance = getDummyCoveragePage();
		String result = instance.getPath();
		assertNull(result);
	}

	/**
	 * Test of getPageName method, of class CoveragePage.
	 */
	public void testGetPageName()
	{
		System.out.println("getPageName");
		CoveragePage instance = getDummyCoveragePage();
		String expResult = PAGE_NAME;
		String result = instance.getPageName();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getConfig method, of class CoveragePage.
	 */
	public void testGetConfig()
	{
		System.out.println("getConfig");
		Config expResult = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		CoverageReport.setConfig(expResult);
		Config result = CoverageReport.getConfig();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setConfig method, of class CoveragePage.
	 */
	public void testSetConfig()
	{
		System.out.println("setConfig");
		Config expResult = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		CoverageReport.setConfig(expResult);
		Config result = CoverageReport.getConfig();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getPrefix method, of class CoveragePage.
	 */
	public void testGetPrefix()
	{
		System.out.println("getPrefix");
		CoveragePage instance = getDummyCoveragePage();
		String result = instance.getPrefix();
		assertNull(result);
	}

	/**
	 * Test of setPrefix method, of class CoveragePage.
	 */
	public void testSetPrefix()
	{
		System.out.println("setPrefix");
		String prefix = "/foo";
		CoveragePage instance = getDummyCoveragePage();
		instance.setPrefix(prefix);
		assertEquals(prefix, instance.getPrefix());
	}

	/**
	 * Test of getDoc method, of class CoveragePage.
	 */
	public void testGetDoc()
	{
		System.out.println("getDoc");
		CoveragePage instance = getDummyCoveragePage();
		String expResult = "coverage";
		Document result = instance.getDoc();
		assertEquals(expResult, result.getDocumentElement().getTagName());
	}

	/**
	 * Test of writeToFileSystem method, of class CoveragePage.
	 */
	/*
	public void testWriteToFileSystem() throws Exception
	{
		System.out.println("writeToFileSystem");
		CoveragePage instance = getDummyCoveragePage();
		instance.writeToFileSystem();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}*/

	public CoveragePage getDummyCoveragePage()
	{
		CoveragePage instance = null;
		try {
			instance = new CoveragePageImpl();
		} catch (ParserConfigurationException ex) {
			fail(ex.getMessage());
		}
		return instance;
	}

	public class CoveragePageImpl extends CoveragePage
	{
		public CoveragePageImpl() throws ParserConfigurationException
		{
			super(TEST_NAME, PAGE_NAME);
		}
	}
}
