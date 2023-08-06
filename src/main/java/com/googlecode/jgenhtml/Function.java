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
