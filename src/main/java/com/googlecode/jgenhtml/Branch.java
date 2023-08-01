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