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
