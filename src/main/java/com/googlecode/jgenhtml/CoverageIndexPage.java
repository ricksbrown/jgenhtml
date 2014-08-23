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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Stuff common to both types of index page in the coverage reports.
 * @author Rick Brown
 */
public abstract class CoverageIndexPage extends CoveragePage
{
	private Element sources;

	/**
	 * Create a new instance.
	 * @param testName The name to display as the test name.
	 * @throws ParserConfigurationException
	 */
	public CoverageIndexPage(final String testName) throws ParserConfigurationException
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
		this.addValuesFromPage(page);//add the values from the page to this index page
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
