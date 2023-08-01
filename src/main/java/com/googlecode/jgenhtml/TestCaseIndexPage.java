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

/**
 * Represents an index page for a subdirectory of the coverage report.
 * @author Rick Brown
 */
public class TestCaseIndexPage extends CoverageIndexPage
{
	public TestCaseIndexPage(final String testName, final String path) throws ParserConfigurationException
	{
		super(testName);
		this.setPath(path);
	}

	/**
	 * Add a page which this page will index.
	 * @param page The page to add.
	 */
	public void addSourceFile(final CoveragePage page)
	{
		super.addSourceFile(page, false);
	}
}
