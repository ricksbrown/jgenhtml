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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents a distinct unit of source code, for example a line (executable or not), function, branch etc.
 * @author Rick Brown
 */
public abstract class SourceCode
{
	public static final int NON_EXECUTABLE = -1;
	private Map<String, Integer> hits = new HashMap<String, Integer>();
	private boolean executable;

	/**
	 * Create a new instance and sets the executable flag accordingly.
	 * @param executable true if this represents an executable source code unit.
	 */
	SourceCode(boolean executable)
	{
		this.executable = executable;
	}

	/**
	 * Gets names of all the test cases which contributed to the hit count of this instance.
	 * @return A collection of test case names.
	 */
	public Collection<String> getTestCaseNames(final boolean hitOnly)
	{
		Collection<String> result = hits.keySet();
		if(hitOnly)
		{
			Set<String> filtered = new HashSet<String>(result.size());
			for(String testName : hits.keySet())
			{
				if(hits.get(testName) > 0)
				{
					filtered.add(testName);
				}
			}
			result = filtered;
		}
		return result;
	}

	/**
	 * Flags this instance as representing an executable source code unit.
	 */
	public void setExecutable()
	{
		executable = true;
	}

	/**
	 * Determine if this instance represents an executable source code unit.
	 * @return true if this is executable.
	 */
	public boolean isExecutable()
	{
		return executable;
	}

	/**
	 * Get the number of times this code was executed/taken.
	 * @return The combined hit count, or NON_EXECUTABLE if this instance is not executable.
	 */
	public int getTotalHits()
	{
		int result;
		if(executable)
		{
			result = 0;
			for(Integer next : hits.values())
			{
				result += next;
			}
		}
		else
		{
			result = NON_EXECUTABLE;
		}
		return result;
	}

	/**
	 * Get the number of times this code was executed/taken in the specified test case.
	 * @param testCaseName The name of the test case to get the hit count for.
	 * @return The hit count for this test case, or NON_EXECUTABLE if this instance is not executable.
	 */
	public int getHits(final String testCaseName)
	{
		int result;
		if(executable)
		{
			if(hits.containsKey(testCaseName))
			{
				result = hits.get(testCaseName);
			}
			else
			{
				result = 0;
			}
		}
		else
		{
			result = NON_EXECUTABLE;
		}
		return result;

	}

	/**
	 * Set the number of times this code was executed/taken.
	 * Automatically flags this instance as executable.
	 * Must be a non-negative number.
	 * @param hits The new hit count.
	 */
	public void setHits(final String testCaseName, final int hits)
	{
		setExecutable();
		this.hits.put(testCaseName, Math.max(0, hits));
	}

	/**
	 * Create an XML representation of this object.
	 * @param document The ownerDocument for the resulting XML.
	 * @return An XML node containing the state of this object.
	 */
	public abstract Element toXml(Document document);
}
