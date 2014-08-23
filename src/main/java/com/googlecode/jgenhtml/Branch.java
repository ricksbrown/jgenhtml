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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents a single branch in a source code file.
 * @author Rick Brown
 */
public class Branch extends SourceCode
{
	private String block = null;
	private String number = null;

	public Branch()
	{
		super(true);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Element toXml(Document document)
	{
		Element branchElement = document.createElement("branch");
		branchElement.setAttribute("block", getBlock());
		branchElement.setAttribute("number", getNumber());
		branchElement.setAttribute("taken", String.valueOf(getTotalHits()));
		for(String testCaseName : getTestCaseNames(true))
		{
			branchElement.appendChild(JGenHtmlUtils.getHitElement(document, testCaseName, getHits(testCaseName)));
		}
		return branchElement;
	}

	public String getBlock()
	{
		return block;
	}

	public void setBlock(final String block)
	{
		this.block = block;
	}

	public String getNumber()
	{
		return number;
	}

	public void setNumber(final String number)
	{
		this.number = number;
	}
}