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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Represents a function page in an lcov report.
 * @author Rick Brown
 */
public class FunctionPage extends CoveragePage
{
	private static final Logger LOGGER = Logger.getLogger(FunctionPage.class.getName());
	private final TestCaseSourceFile testCaseSourceFile;
	private final Map<String, Function> functions;

	public FunctionPage(final TestCaseSourceFile testCaseSourceFile) throws ParserConfigurationException
	{
		super(testCaseSourceFile.getTestName(), testCaseSourceFile.getPageName());
		this.testCaseSourceFile = testCaseSourceFile;
		functions = new HashMap<>();
	}

	@Override
	public void writeToFileSystem() throws TransformerException, IOException
	{
		Document document = this.getDoc();
		Element functionsRoot = document.createElement("functions");
		Element root = document.getDocumentElement();
		root.appendChild(functionsRoot);
		for(Function func : functions.values())
		{
			functionsRoot.appendChild(func.toXml(document));
		}
		super.writeToFileSystem();
	}

	Collection<Function> getFunctions()
	{
		return functions.values();
	}

	@Override
	public int getLineCount()
	{
		return testCaseSourceFile.getLineCount();
	}

	@Override
	public int getLineHit()
	{
		return testCaseSourceFile.getLineHit();
	}

	@Override
	public int getBranchCount()
	{
		return testCaseSourceFile.getBranchCount();
	}

	@Override
	public int getBranchHit()
	{
		return testCaseSourceFile.getBranchHit();
	}

	@Override
	public int getFuncCount()
	{
		return functions.size();
	}

	@Override
	public int getFuncHit()
	{
		int result = 0;
		for(Function function : functions.values())
		{
			if(function.getTotalHits() > 0)
			{
				result++;
			}
		}
		return result;
	}

	/**
	 * Get a function instance by name.
	 * @param name The name of the function to get.
	 * @param dontCreate If true a new function instance will NOT be created if it does not already exist.
	 * @return An instance of function representing this named function.
	 */
	private Function getFunction(final String name, boolean dontCreate)
	{
		Function result;
		if(functions.containsKey(name))
		{
			result = functions.get(name);
		}
		else if(!dontCreate)
		{
			result = new Function(name);
			functions.put(name, result);
		}
		else
		{
			result = null;
		}
		return result;
	}

	/**
	 *
	 * @param line The line with any leading whitespace removed.
	 */
	public void addFunctionData(final String testCaseName, final String line, final boolean isBaseline)
	{
		boolean isFn = line.startsWith("FN:");
		if(line.startsWith("FNDA:") || (!isBaseline && isFn))
		{
			// FN:<line number of function start>, <function name>
			// FNDA:<execution count>, <function name>
			String[] data = JGenHtmlUtils.extractLineValues(line);
			if(data != null && data.length == 2)
			{
				String name = data[1];
				Function function = getFunction(name, isBaseline);
				if(function != null)
				{
					if(isFn)
					{
						function.setLineNo(data[0]);
					}
					else
					{
						int hits = Integer.parseInt(data[0]);
						if(isBaseline)
						{
							function.setHits(testCaseName, function.getHits(testCaseName) - hits);
						}
						else
						{
							function.setHits(testCaseName, function.getHits(testCaseName) + hits);
						}
					}
				}
			}
			else
			{
				LOGGER.log(Level.FINE, "Could not parse line: {0}", line);
			}
		}
	}
}
