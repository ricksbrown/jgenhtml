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

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents a single line in a source code file.
 * @author Rick Brown
 */
public class Line extends SourceCode
{
	private String code = null;
	private int lineNumber = -1;
	private final List<Branch> branches = new ArrayList<>();
	private static String expander = null;

	/**
	 * Create a new instance of Line, by default it is not executable.
	 * @param lineNumber The base one line number of this line.
	 */
	public Line(final int lineNumber)
	{
		super(false);  // by default assume the line is not executable
		setLineNo(lineNumber);
	}

	/**
	 * Set the base one line number.
	 * @param lineNumber The line number, must be greater than zero.
	 */
	private void setLineNo(final int lineNumber)
	{
		if(lineNumber > 0)
		{
			this.lineNumber = lineNumber;
		}
		else
		{
			throw new IllegalArgumentException("Line number must be greater than zero");
		}
	}

	@Override
	public Element toXml(final Document document)
	{
		Element lineElement = document.createElement("line");
		Element codeElement = document.createElement("code");
		if(code != null)
		{
			codeElement.setTextContent(code);
		}
		lineElement.setAttribute("number", String.valueOf(lineNumber));
		lineElement.appendChild(codeElement);
		if(getTotalHits() >= 0)
		{
			lineElement.setAttribute("hits", String.valueOf(getTotalHits()));
			for(String testCaseName : getTestCaseNames(true))
			{
				lineElement.appendChild(JGenHtmlUtils.getHitElement(document, testCaseName, getHits(testCaseName)));
			}
		}
		for(Branch branch : getBranches())
		{
			lineElement.appendChild(branch.toXml(document));
		}
		return lineElement;
	}

	/**
	 * Get the base one line number.
	 * @return The line number.
	 */
	public int getLineNumber()
	{
		return lineNumber;
	}

	/**
	 * Set the base one line number.
	 * @param lineNumber The line number, must be greater than zero.
	 */
	public void setLineNumber(final int lineNumber)
	{
		setLineNo(lineNumber);
	}

	/**
	 * Get the source code in this line.
	 * @return The line source or null if there is no source.
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * Set the source code for this line.
	 * Trailing whitespace will be trimmed.
	 * Tab expansion will occur if specified by user.
	 * @param code The source code for this line.
	 */
	public void setCode(final String code)
	{
		if(code != null)
		{
			this.code = code.replaceAll("\\s+$", "");  // right trim
			if(expander != null)
			{
				this.code = this.code.replaceAll("\t", expander);
			}
		}
	}

	/**
	 * Get a branch in this line.
	 * @param block The block id of the branch to fetch.
	 * @param number The index number of the branch to fetch.
	 * @return The branch if it exists otherwise null.
	 */
	public Branch getBranch(final String block, final String number)
	{
		Branch result = null;
		for(Branch branch : branches)
		{
			if(block.equals(branch.getBlock()) && number.equals(branch.getNumber()))
			{
				result = branch;
				break;
			}
		}
		return result;
	}

	/**
	 * Get all the branches in this line.
	 * @return a collection of branches.
	 */
	public List<Branch> getBranches()
	{
		return branches;
	}

	/**
	 * Add a new branch to this line.
	 * @param branch The new branch to add to the previous ones.
	 */
	public void addBranch(final Branch branch)
	{
		branches.add(branch);
	}

	public static void setTabExpand(final int expand)
	{
		if(expand > -1)
		{
			expander = getTabExpander(expand);
		}
	}

	/**
	 * Get the String to use for expanding tabs to spaces.
	 * @param width The length of the expander (how many spaces).
	 * @return A string 'width' spaces long.
	 */
	private static String getTabExpander(final int width)
	{
		StringBuilder result = new StringBuilder(width);
		for(int i=0; i<width; i++)
		{
			result.append(' ');
		}
		return result.toString();
	}

}
