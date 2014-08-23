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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents a source file referenced in an lcov tracefile.
 * @author Rick Brown
 */
public class TestCaseSourceFile extends CoveragePage
{
	private static final Logger LOGGER = Logger.getLogger(TestCaseSourceFile.class.getName());
	private static final String NO_SOURCE_CODE = "/* EOF */";
	private boolean hasSource = false;
	private FunctionPage functionPage = null;
	private Map<Integer, Line> lineItems;


	public TestCaseSourceFile(final String testName, final String pageName) throws ParserConfigurationException
	{
		super(testName, pageName);
		this.lineItems = new TreeMap<Integer, Line>();
		this.functionPage = new FunctionPage(this);
	}

	/**
	 * Processes a single "BR" line (any line starting with BR).
	 * @param line The line with any leading whitespace removed.
	 */
	private void addBranchData(final String testCaseName, final String line, final boolean isBaseline)
	{
		if(line.startsWith("BRDA:"))
		{
			// BRDA:<line number>,<block number>,<branch number>,<taken>
			String[] data = JGenHtmlUtils.extractLineValues(line);//can't get as ints because "taken" can be '-' if it was not taken, anywho we need strings for attributes
			if(data != null && data.length == 4)
			{
				String block = data[1];
				String number = data[2];
				int taken = Integer.parseInt(data[3]);

				Line lineItem = getLineAt(data[0]);

				Branch branch = lineItem.getBranch(block, number);
				if(isBaseline)
				{
					branch.setHits(testCaseName, branch.getHits(testCaseName) - taken);
				}
				else
				{
					if(branch == null)
					{
						branch = new Branch();
						branch.setBlock(block);
						branch.setNumber(number);
						lineItem.addBranch(branch);
					}
					branch.setHits(testCaseName, branch.getHits(testCaseName) + taken);
				}
			}
			else
			{
				LOGGER.log(Level.FINE, "Could not parse line: {0}", line);
			}
		}
	}

	/**
	 * Processes a single "DA" line.
	 * @param line The line with any leading whitespace removed.
	 */
	private void addLineData(final String testCaseName, final String line, final boolean isBaseline)
	{
		int[] counters = JGenHtmlUtils.getLineValues(line);
		if(counters != null)
		{
			int hitCount = counters[1];
			if(hitCount >= 0)
			{
				Line lineItem = getLineAt(counters[0]);
				lineItem.setExecutable();
				if(isBaseline)
				{
					lineItem.setHits(testCaseName, lineItem.getHits(testCaseName) - hitCount);
				}
				else
				{
					lineItem.setHits(testCaseName, lineItem.getHits(testCaseName) + hitCount);
				}
			}
		}
		else
		{
			LOGGER.log(Level.FINE, "Could not parse line: {0}", line);
		}
	}

	/**
	 * Set the source line.
	 * Will replace any existing line at this position.
	 * @param lineNo The BASE ONE index, 1 is line 1, 2 is line 2 etc.
	 * @param line The line to set at this line number.
	 */
	private void setLineAt(final int lineNo, final Line line)
	{
		lineItems.put(lineNo, line);
	}

	/**
	 * Retrieve the source line.
	 * @param lineNo The BASE ONE index, 1 is line 1, 2 is line 2 etc.
	 * @return The line item at the given line number.
	 */
	private Line getLineAt(final int lineNo)
	{
		Line result;
		if(lineItems.containsKey(lineNo))
		{
			result = lineItems.get(lineNo);
		}
		else
		{
			result = new Line(lineNo);
			lineItems.put(lineNo, result);
		}
		return result;
	}

	/**
	 * Retrieve the source line.
	 * @param lineNo The BASE ONE index, 1 is line 1, 2 is line 2 etc.
	 * @return The line item at the given line number.
	 */
	private Line getLineAt(final String lineNo)
	{
		return getLineAt(Integer.parseInt(lineNo));
	}


	/**
	 * Add more information about this source file.
	 * @param line a line (from the tracefile) containing data about this source file.
	 * @param isBaseline true if this line comes from a baseline file
	 */
	public void processLine(final String testCaseName, final String line, final boolean isBaseline) throws ParserConfigurationException
	{
		String dataLine = line.trim();
		if(dataLine.startsWith("BR"))
		{
			addBranchData(testCaseName, dataLine, isBaseline);
		}
		else if(dataLine.startsWith("FN"))
		{
			functionPage.addFunctionData(testCaseName, dataLine, isBaseline);
		}
		else if(dataLine.startsWith("DA:"))
		{
			addLineData(testCaseName, dataLine, isBaseline);
		}
	}

	@Override
	public void writeToFileSystem() throws TransformerConfigurationException, TransformerException, IOException
	{
		Document document = this.getDoc();
		Element lines = document.createElement("lines");
		Element root = document.getDocumentElement();
		root.appendChild(lines);
		if(!CoverageReport.getConfig().isNoSource())
		{
			int prevLineNo = 0;
			for(Line line : lineItems.values())
			{
				if(!hasSource)
				{
					int lineNo = line.getLineNumber();
					while(++prevLineNo < lineNo)
					{
						Line dummyLine = new Line(prevLineNo);
						dummyLine.setCode(NO_SOURCE_CODE);
						lines.appendChild(dummyLine.toXml(document));
					}
					prevLineNo = lineNo;
					line.setCode(NO_SOURCE_CODE);
				}
				Element lineElement = line.toXml(document);
				lines.appendChild(lineElement);
			}
			super.writeToFileSystem();
			if(this.functionPage.getFuncCount() > 0)
			{
				this.functionPage.writeToFileSystem();
			}
		}
	}

	@Override
	public int getLineCount()
	{
		return getExecutableLineCount(false);
	}

	@Override
	public int getLineHit()
	{
		return getExecutableLineCount(true);
	}

	@Override
	public int getBranchCount()
	{
		int result = 0;
		for(Line line : lineItems.values())
		{
			result += line.getBranches().size();
		}
		return result;
	}

	@Override
	public int getBranchHit()
	{
		int result = 0;
		for(Line line : lineItems.values())
		{
			for(Branch branch : line.getBranches())
			{
				if(branch.getTotalHits() > 0)
				{
					result++;
				}
			}
		}
		return result;
	}

	public Collection<String> getTestCaseNames()
	{
		Collection<String> result = new HashSet<String>();
		for(Line line : lineItems.values())
		{
			result.addAll(line.getTestCaseNames(true));
			for(Branch branch : line.getBranches())
			{
				result.addAll(branch.getTestCaseNames(true));
			}
		}
		for(Function function : functionPage.getFunctions())
		{
			result.addAll(function.getTestCaseNames(true));
		}
		return result;
	}

	public int getLineHit(final String testCaseName)
	{
		return getHitByTestCase(lineItems.values(), testCaseName);
	}

	public int getBranchHit(final String testCaseName)
	{
		int result = 0;
		for(Line line : lineItems.values())
		{
			result += getHitByTestCase(line.getBranches(), testCaseName);
		}
		return result;
	}

	public int getFuncHit(final String testCaseName)
	{
		return getHitByTestCase(functionPage.getFunctions(), testCaseName);
	}

	private static int getHitByTestCase(Collection<? extends SourceCode> items, String testCaseName)
	{
		int result = 0;
		for(SourceCode item : items)
		{
			if(item.isExecutable() && item.getHits(testCaseName) > 0)
			{
				result++;
			}
		}
		return result;
	}

	@Override
	public int getFuncCount()
	{
		return this.functionPage.getFuncCount();
	}

	@Override
	public int getFuncHit()
	{
		return this.functionPage.getFuncHit();
	}

	@Override
	public void setPath(final String path)
	{
		this.functionPage.setPath(path);
		super.setPath(path);
	}

	@Override
	public void setPrefix(final String prefix)
	{
		this.functionPage.setPrefix(prefix);
		super.setPrefix(prefix);
	}

	/**
	 * Gets the total number of executable functions.
	 * That is, the number of functionsRoot excluding comments etc.
	 * @param hitOnly If true will only count functionsRoot that were executed at least once.
	 * @return The count of executable or executed functionsRoot.
	 */
	private int getExecutableLineCount(final boolean hitOnly)
	{
		int result = 0;
		for(Line line : lineItems.values())
		{
			if(hitOnly)
			{
				if(line.isExecutable() && line.getTotalHits() > 0)
				{
					result++;
				}
			}
			else if(line.isExecutable())
			{
				result++;
			}
		}
		return result;
	}

	/**
	 * Provide the source code file.
	 * @param sourceFile The file on the file system.
	 * @throws IOException
	 */
	protected void setSourceFile(final File sourceFile) throws IOException
	{
		super.setPageName(sourceFile.getName());

		File parentDir = sourceFile.getParentFile();
		if(parentDir != null)
		{
			this.setPath(parentDir.getCanonicalPath());//canonical path is definitely needed here
			if(sourceFile.exists())
			{
				hasSource = true;
				loadSourceFile(sourceFile);
			}
			else
			{
				LOGGER.log(Level.FINER, "Can not find file: {0}", sourceFile.getAbsolutePath());
			}
		}
		else
		{
			throw new IOException("Something very wrong with file: " + sourceFile);
		}
	}

	/**
	 * Loads the source code for this source file.
	 * @param sourceFile The source file represented by this instance.
	 * @throws IOException
	 */
	private void loadSourceFile(final File sourceFile) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(sourceFile));
		try
		{
			String line;
			int i = 1;
			while ((line = br.readLine()) != null)
			{
				Line lineItem = new Line(i);
				lineItem.setCode(line);
				setLineAt(i++, lineItem);
			}
		}
		finally
		{
			br.close();
		}
	}

}
