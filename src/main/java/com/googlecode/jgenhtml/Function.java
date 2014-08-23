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
 * Contains information about a single function in a source file.
 * @author Rick Brown
 */
public class Function extends SourceCode
{
	private String name;
	private String lineNo = null;

	public Function(final String name)
	{
		super(true);
		this.name = name;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Element toXml(Document document)
	{
		Element functionElement = document.createElement("function");
		functionElement.setAttribute("name", getName());
		functionElement.setAttribute("line-number", getLineNo());
		functionElement.setAttribute("hits", String.valueOf(getTotalHits()));
		for(String testCaseName : getTestCaseNames(true))
		{
			functionElement.appendChild(JGenHtmlUtils.getHitElement(document, testCaseName, getHits(testCaseName)));
		}
		return functionElement;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getLineNo()
	{
		return lineNo;
	}

	public void setLineNo(String lineNo)
	{
		this.lineNo = lineNo;
	}
}
