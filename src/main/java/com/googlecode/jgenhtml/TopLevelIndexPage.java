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
