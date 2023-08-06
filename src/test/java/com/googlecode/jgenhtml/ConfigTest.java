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
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Rick Brown
 */
public class ConfigTest extends TestCase
{
	private static final int DEFAULT_NUMSPACES = -1;

	public ConfigTest(String testName)
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
	 * Test of setCssFile method, of class Config.
	 */
	public void testSetAndGetCssFile()
	{
		System.out.println("testSetAndGetCssFile");
		File cssFile = JGenHtmlTestUtils.createCssFile();
		String cssPath = cssFile.getAbsolutePath();
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"-c", cssPath});
		// instance.setCssFile(cssPath);
		assertEquals(cssPath, instance.getCssFile().getAbsolutePath());
	}

	/**
	 * Test of setCssFile method, of class Config.
	 */
	public void testSetAndGetCssFileNonExistant()
	{
		System.out.println("testSetAndGetCssFileNonExistant");
		String cssPath = "/foo/bar/moo/baa.css";
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"-c", cssPath});
		// instance.setCssFile(cssPath);
		assertNull(instance.getCssFile());
	}

	/**
	 * Test of isFunctionCoverage method, of class Config.
	 */
	public void testIsFunctionCoverage()
	{
		System.out.println("isFunctionCoverage");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		boolean result = instance.isFunctionCoverage();
		assertTrue("Function coverage should be on by default", result);
	}

	/**
	 * Test of setFunctionCoverage method, of class Config.
	 */
	public void testSetFunctionCoverage()
	{
		System.out.println("setFunctionCoverage");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--no-function-coverage"});
		// instance.setFunctionCoverage(false);
		assertFalse(instance.isFunctionCoverage());
	}

	/**
	 * Test of isBranchCoverage method, of class Config.
	 */
	public void testIsBranchCoverage()
	{
		System.out.println("isBranchCoverage");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		boolean result = instance.isBranchCoverage();
		assertTrue("Branch coverage should be on by default", result);
	}

	/**
	 * Test of setBranchCoverage method, of class Config.
	 */
	public void testSetBranchCoverage()
	{
		System.out.println("setBranchCoverage");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--no-branch-coverage"});
		// instance.setBranchCoverage(false);
		assertFalse(instance.isBranchCoverage());
	}

	/**
	 * Test of getOutRootDir method, of class Config.
	 */
	public void testGetOutRootDir()
	{
		System.out.println("getOutRootDir");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		String expResult = new File(".").getAbsolutePath();
		String result = instance.getOutRootDir().getAbsolutePath();
		assertEquals("Should default to current working directory", expResult, result);
	}

	/**
	 * Test of setOutRootDir method, of class Config.
	 */
	public void testSetOutRootDir()
	{
		System.out.println("setOutRootDir");
		String outRootDir = FileUtils.getUserDirectoryPath();
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"-o", outRootDir});
		// instance.setOutRootDir(outRootDir);
		String result = instance.getOutRootDir().getAbsolutePath();
		assertEquals(FileUtils.getUserDirectoryPath(), result);
	}

	/**
	 * Test of getTitle method, of class Config.
	 */
	public void testGetTitle()
	{
		System.out.println("getTitle");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		String result = instance.getTitle();
		assertNull(result);
	}

	/**
	 * Test of setTitle method, of class Config.
	 */
	public void testSetTitle()
	{
		System.out.println("setTitle");
		String title = "/foo/bar/some.js";
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--title", title});
		// instance.setTitle(title);
		assertEquals(title, instance.getTitle());
	}

	/**
	 * Test of getCssFile method, of class Config.
	 * Testing the default value if none has been set.
	 */
	public void testGetCssFile()
	{
		System.out.println("getCssFile");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		File result = instance.getCssFile();
		assertNull(result);
	}

	/**
	 * Test of isNoPrefix method, of class Config.
	 */
	public void testIsNoPrefix()
	{
		System.out.println("isNoPrefix");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		boolean result = instance.isNoPrefix();
		assertFalse("no-prefix should default to false", result);
	}

	/**
	 * Test of setNoPrefix method, of class Config.
	 */
	public void testSetNoPrefix()
	{
		System.out.println("setNoPrefix");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--no-prefix"});
		assertTrue(instance.isNoPrefix());
	}

	/**
	 * Test of getPrefix method, of class Config.
	 */
	public void testGetPrefix()
	{
		System.out.println("getPrefix");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		String result = instance.getPrefix();
		assertNull("prefix should default to null", result);
	}

	/**
	 * Test of getPrefix method, of class Config.
	 */
	public void testGetPrefixWithNoPrefix()
	{
		System.out.println("testGetPrefixWithNoPrefix");
		String prefix = "/foo/bar/moo";
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--prefix", prefix, "--no-prefix"});
		// instance.setPrefix(prefix);
		// instance.setNoPrefix(true);
		String result = instance.getPrefix();
		assertNull("prefix should be null when no-prefix is true", result);
	}


	/**
	 * Test of setPrefix method, of class Config.
	 */
	public void testSetPrefix()
	{
		System.out.println("setPrefix");
		String prefix = "/foo/bar/moo";
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--prefix", prefix});
		// instance.setPrefix(prefix);
		assertEquals(prefix, instance.getPrefix());
	}

	/**
	 * Test of setPrefix method, of class Config.
	 */
	public void testSetPrefixWithNoPrefix()
	{
		System.out.println("testSetPrefixWithNoPrefix");
		String prefix = "/foo/bar/moo";
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--no-prefix", "--prefix", prefix});
		// instance.setNoPrefix(true);
		// instance.setPrefix(prefix);
		assertNull(instance.getPrefix());
	}

	/**
	 * Test of getNumSpaces method, of class Config.
	 */
	public void testGetNumSpaces()
	{
		System.out.println("getNumSpaces");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		int expResult = DEFAULT_NUMSPACES;
		int result = instance.getNumSpaces();
		assertEquals("num-spaces should default to -1", expResult, result);
	}

	/**
	 * Test of setNumSpaces method, of class Config.
	 */
	public void testSetNumSpaces()
	{
		System.out.println("setNumSpaces");
		String numSpaces = "2";
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--num-spaces", numSpaces});
		// instance.setNumSpaces(numSpaces);
		int result = instance.getNumSpaces();
		assertEquals(Integer.parseInt(numSpaces), result);
	}

	/**
	 * Test of isNoSort method, of class Config.
	 */
	public void testIsNoSort()
	{
		System.out.println("isNoSort");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		assertFalse("no-sort should default to false", instance.isNoSort());
	}

	/**
	 * Test of setNoSort method, of class Config.
	 */
	public void testSetNoSort()
	{
		System.out.println("setNoSort");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--no-sort"});
		assertTrue("no-sort should now be true", instance.isNoSort());
	}

	/**
	 * Test of isNoSource method, of class Config.
	 */
	public void testIsNoSource()
	{
		System.out.println("isNoSource");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		assertFalse("no-source should default to false", instance.isNoSource());
	}

	/**
	 * Test of setGzip method, of class Config.
	 */
	public void testSetGzip()
	{
		System.out.println("setGzip");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--html-gzip"});
		assertTrue("html-gzip should now be true", instance.isGzip());
	}

	/**
	 * Test of isNoSource method, of class Config.
	 */
	public void testIsGzip()
	{
		System.out.println("isGzip");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{});
		assertFalse("html-gzip should default to false", instance.isGzip());
	}

	/**
	 * Test of setNoSource method, of class Config.
	 */
	public void testSetNoSource()
	{
		System.out.println("setNoSource");
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--no-source"});
		assertTrue("no-source should now be true", instance.isNoSource());
	}

	/**
	 * Test of setNumSpaces method, of class Config.
	 */
	public void testSetNumSpacesInvalid()
	{
		System.out.println("testSetNumSpacesInvalid");
		String numSpaces = "X";
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--num-spaces", numSpaces});
		// instance.setNumSpaces(numSpaces);
		int result = instance.getNumSpaces();
		assertEquals("invalid number should have no side effects", DEFAULT_NUMSPACES, result);
	}

	/**
	 * Test of loadConfigFile method, of class Config.
	 */
	public void testLoadConfigFile()
	{
		System.out.println("loadConfigFile");
		String cssFile = JGenHtmlTestUtils.createCssFile().getAbsolutePath(),
				prefixes = "0", spaces = "2", functions = "0", branches = "0", sort="0", noSource = "1", gzipHtml = "1";
		File lcovrc = JGenHtmlTestUtils.createLcovrcFile(cssFile, prefixes, spaces, functions, branches, sort, noSource, gzipHtml);
		String alternatePath = lcovrc.getAbsolutePath();
		Config instance = JGenHtmlTestUtils.getDummyConfig(new String[]{"--config-file", alternatePath, "/some/tracefile"});
		// instance.loadConfigFile(alternatePath);
		assertEquals(cssFile, instance.getCssFile().getAbsolutePath());
		assertFalse(instance.isNoPrefix());
		assertEquals(Integer.parseInt(spaces), instance.getNumSpaces());
		assertFalse(instance.isFunctionCoverage());
		assertFalse(instance.isBranchCoverage());
		assertTrue(instance.isNoSort());
		assertTrue(instance.isNoSource());
		assertTrue(instance.isGzip());
	}
}
