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

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Stuff common to both types of index page in the coverage reports.
 * @author Rick Brown
 */
public abstract class CoverageIndexPage extends CoveragePage
{
	private final Element sources;

	/**
	 * Create a new instance.
	 * @param testName The name to display as the test name.
	 * @throws ParserConfigurationException Ummm, if the parser configuration is bad?
	 */
	protected CoverageIndexPage(final String testName) throws ParserConfigurationException
	{
		super(testName, "index");
		Document doc = this.getDoc();
		this.sources = doc.createElement("sources");
		Element root = doc.getDocumentElement();
		root.appendChild(this.sources);
	}

	/**
	 * Add a page which this page will index.
	 * @param page The page to add.
	 * @param usePath If true "pathname" will be referenced, otherwise "filename".
	 */
	void addSourceFile(final CoveragePage page, final boolean usePath)
	{
		this.addValuesFromPage(page);  // add the values from the page to this index page
		Document document = this.getDoc();
		Element source = document.createElement("source");
		sources.appendChild(source);
		if(usePath)
		{
			source.setAttribute("pathname", page.getPath());
		}
		else
		{
			source.setAttribute("filename", page.getPageName());
		}
		String prefix = page.getPrefix();
		if(prefix != null)
		{
			source.setAttribute("prefix", prefix);
		}
		source.setAttribute("lines-hit", String.valueOf(page.getLineHit()));
		source.setAttribute("lines-valid", String.valueOf(page.getLineCount()));
		source.setAttribute("functions-hit", String.valueOf(page.getFuncHit()));
		source.setAttribute("functions-valid", String.valueOf(page.getFuncCount()));
		source.setAttribute("branches-hit", String.valueOf(page.getBranchHit()));
		source.setAttribute("branches-valid", String.valueOf(page.getBranchCount()));
		if(page instanceof TestCaseSourceFile)
		{
			TestCaseSourceFile sourceFile = (TestCaseSourceFile)page;
			for(String testCaseName : sourceFile.getTestCaseNames())
			{
				Element testCase = document.createElement("testCase");
				testCase.setAttribute("name", testCaseName);
				testCase.setAttribute("lines-hit", String.valueOf(sourceFile.getLineHit(testCaseName)));
				testCase.setAttribute("branches-hit", String.valueOf(sourceFile.getBranchHit(testCaseName)));
				testCase.setAttribute("functions-hit", String.valueOf(sourceFile.getFuncHit(testCaseName)));
				source.appendChild(testCase);
			}
		}
	}
}
