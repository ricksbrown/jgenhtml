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
package com.googlecode.jgenhtml.plugin;

import com.googlecode.jgenhtml.Config;
import com.googlecode.jgenhtml.JGenHtmlTestUtils;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;

/**
 * @author Rick Brown
 */
public class JGenHtmlExecuterTest extends TestCase
{

	public JGenHtmlExecuterTest(String testName)
	{
		super(testName);
	}

	/**
	 * Test of addTracefile method, of class JGenHtmlExecuter.
	 */
	public void testAddTracefileWithStringArr()
	{
		System.out.println("testAddTracefileWithStringArr");
		String[] tracefiles = {"/foo/bar/test.info", "/bar/foo/test.info"};
		JGenHtmlExecuter instance = new JGenHtmlExecuter();
		instance.addTracefile(tracefiles);
		String[] tracefilesActual = getTracefiles(instance.buildArgs());
		Assert.assertArrayEquals(tracefiles, tracefilesActual);
	}

	public void testAddTracefileWithStringArrAndString()
	{
		System.out.println("testAddTracefileWithStringArrAndString");
		String tracefile = "/foo/bar/test2.info";
		String[] tracefiles = {"/foo/bar/test.info", "/bar/foo/test.info"};
		JGenHtmlExecuter instance = new JGenHtmlExecuter();
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefile);
		String[] tracefilesActual = getTracefiles(instance.buildArgs());
		assertEquals(3, tracefilesActual.length);
		List<String> actualAsList = Arrays.asList(tracefilesActual);
		assertTrue(actualAsList.contains(tracefile));
		assertTrue(actualAsList.contains(tracefiles[0]));
		assertTrue(actualAsList.contains(tracefiles[1]));
	}

	public void testAddTracefileWithMultipleStringArrs()
	{
		System.out.println("testAddTracefileWithMultipleStringArrs");
		String[] tracefiles1 = {"/foo/bar/test.info", "/bar/foo/test.info"};
		String[] tracefiles2 = {"/foo/bar/test1.info", "/bar/foo/test1.info"};
		JGenHtmlExecuter instance = new JGenHtmlExecuter();
		instance.addTracefile(tracefiles1);
		instance.addTracefile(tracefiles2);
		String[] tracefilesActual = getTracefiles(instance.buildArgs());
		assertEquals(4, tracefilesActual.length);
		List<String> actualAsList = Arrays.asList(tracefilesActual);
		assertTrue(actualAsList.containsAll(Arrays.asList(tracefiles1)));
		assertTrue(actualAsList.containsAll(Arrays.asList(tracefiles2)));
	}

	public void testAddTracefileWithMultipleStrings()
	{
		System.out.println("testAddTracefileWithMultipleStrings");
		String tracefile1 = "/foo/bar/test.info";
		String tracefile2 = "/foo/bar/test2.info";
		String tracefile3 = "/foo/bar/test3.info";
		JGenHtmlExecuter instance = new JGenHtmlExecuter();
		instance.addTracefile(tracefile1);
		instance.addTracefile(tracefile2);
		instance.addTracefile(tracefile3);
		String[] tracefilesActual = getTracefiles(instance.buildArgs());
		assertEquals(3, tracefilesActual.length);
		List<String> actualAsList = Arrays.asList(tracefilesActual);
		assertTrue(actualAsList.contains(tracefile1));
		assertTrue(actualAsList.contains(tracefile2));
		assertTrue(actualAsList.contains(tracefile3));
	}

	/**
	 * Test of addTracefile method, of class JGenHtmlExecuter.
	 */
	public void testAddTracefile_String()
	{
		System.out.println("addTracefile");
		String tracefile = "/foo/bar/test.info";
		JGenHtmlExecuter instance = new JGenHtmlExecuter();
		instance.addTracefile(tracefile);
		String[] tracefilesActual = getTracefiles(instance.buildArgs());
		assertEquals(1, tracefilesActual.length);
		assertEquals(tracefile, tracefilesActual[0]);
	}

	/**
	 * Test of addTracefile method, of class JGenHtmlExecuter.
	 */
	public void testAddTracefileDuplicateStrings()
	{
		System.out.println("testAddTracefileDuplicateStrings");
		String tracefile = "/foo/bar/test.info";
		JGenHtmlExecuter instance = new JGenHtmlExecuter();
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefile);
		String[] tracefilesActual = getTracefiles(instance.buildArgs());
		assertEquals(1, tracefilesActual.length);
		assertEquals(tracefile, tracefilesActual[0]);
	}

	/**
	 * Test of addTracefile method, of class JGenHtmlExecuter.
	 */
	public void testAddTracefileWithDuplicateStringArrs()
	{
		System.out.println("testAddTracefileWithDuplicateStringArrs");
		String[] tracefiles = {"/foo/bar/test.info", "/bar/foo/test.info"};
		JGenHtmlExecuter instance = new JGenHtmlExecuter();
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefiles);
		String[] tracefilesActual = getTracefiles(instance.buildArgs());
		Assert.assertArrayEquals(tracefiles, tracefilesActual);
	}

	public void testAddTracefileWithDuplicateStringArrAndString()
	{
		System.out.println("testAddTracefileWithDuplicateStringArrAndString");
		String tracefile = "/foo/bar/test.info";
		String[] tracefiles = {"/foo/bar/test.info", "/bar/foo/test.info"};
		JGenHtmlExecuter instance = new JGenHtmlExecuter();
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefile);
		instance.addTracefile(tracefiles);
		instance.addTracefile(tracefile);
		String[] tracefilesActual = getTracefiles(instance.buildArgs());
		assertEquals(2, tracefilesActual.length);
		Assert.assertArrayEquals(tracefiles, tracefilesActual);
	}

	/**
	 * Test of setOutdir method, of class JGenHtmlExecuter.
	 */
	public void testSetOutdir()
	{
		System.out.println("setOutdir");
		String outdir = "/foo/bar/outdir";
		JGenHtmlExecuter instance = new JGenHtmlExecuter();
		instance.setOutdir(outdir);
		String actual = getOutdir(instance.buildArgs());
		assertEquals(outdir, actual);
	}

	/**
	 * Test of setConfig method, of class JGenHtmlExecuter.
	 */
	public void testSetConfig()
	{
		System.out.println("setConfig");
		File cssFile = JGenHtmlTestUtils.createCssFile();//this file must exist for the test to work
		File lcovrc = JGenHtmlTestUtils.createLcovrcFile(cssFile.getAbsolutePath(), null, null, null, null, null, null, null);//create an lcovrc file which sets the css file
		String config = lcovrc.getAbsolutePath();
		JGenHtmlExecuter instance = new JGenHtmlExecuter();
		instance.setConfig(config);//point to the lcovrc file created above
		Config configInstance = new Config();
		try
		{
			configInstance.initializeUserPrefs(instance.buildArgs());
		}
		catch (ParseException ex)
		{
			fail(ex.getMessage());
		}
		File actualCssFile = configInstance.getCssFile();
		if(actualCssFile != null)
		{
			assertEquals(cssFile.getAbsolutePath(), actualCssFile.getAbsolutePath());//if the lcovrc was correctly set then this instance will point to our css file
		}
		else
		{
			fail("config file not correctly loaded");
		}
	}

	private String getOutdir(String[] args)
	{
		String result = null;
		Config config = new Config();
		try
		{
			config.initializeUserPrefs(args);
			File actual = config.getOutRootDir();
			if(actual != null)
			{
				result = actual.getCanonicalPath();
			}
		}
		catch(IOException ex)
		{
			fail(ex.getMessage());
		}
		catch(ParseException ex)
		{
			fail(ex.getMessage());
		}
		return result;
	}

	private String[] getTracefiles(String[] args)
	{
		String[] result = null;
		Config config = new Config();
		try
		{
			config.initializeUserPrefs(args);
			result = config.getTraceFiles();
		}
		catch (ParseException ex)
		{
			fail(ex.getMessage());
		}
		return result;
	}
}
