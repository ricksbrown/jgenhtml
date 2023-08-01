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

import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Represents the top level index page of the coverage report.
 * Contains summary information about all the directories containing coverage reports.
 *
 * @author Rick Brown
 */
public class TopLevelIndexPage extends CoverageIndexPage
{
	//private static final Logger LOGGER = Logger.getLogger(TopLevelIndexPage.class.getName());

	/**
	 * Create a new instance of this class.
	 * @param indexPages The index pages for each of the directories containing coverage reports.
	 * @param testName The name which identifies all the tests in this report.
	 */
	public TopLevelIndexPage(final String testName, final Collection<TestCaseIndexPage> indexPages) throws ParserConfigurationException
	{
		super(testName);
		for(TestCaseIndexPage indexPage : indexPages)
		{
			super.addSourceFile(indexPage, true);
		}
	}
}
