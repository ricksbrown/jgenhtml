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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Functionality common to any of the pages with coverage information.
 * @author Rick Brown
 */
public abstract class CoveragePage
{
	private static final Logger LOGGER = Logger.getLogger(CoveragePage.class.getName());
	private int lineCount;
	private int lineHit;
	private String path;
	private String testName;
	private String pageName;
	private Document doc;
	private String prefix;
	private int funcCount;
	private int funcHit;
	private int branchCount;
	private int branchHit;

	CoveragePage(final String testName, final String pageName) throws ParserConfigurationException
	{
		this.lineCount = 0;
		this.lineHit = 0;
		this.funcCount = 0;
		this.funcHit = 0;
		this.branchCount = 0;
		this.branchHit = 0;
		this.path = null;
		this.testName = testName;
		this.pageName = pageName;
		this.prefix = null;
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		docBuilder = dbfac.newDocumentBuilder();
		this.doc = docBuilder.newDocument();
		Element root = this.doc.createElement("coverage");
		this.doc.appendChild(root);
	}

	/**
	 * Increase the line count by this amount.
	 * The line count is the number of lines that are executable whether they were
	 * actually executed or not.
	 * @param lineCount The amount to increase the line count.
	 */
	void addToLineCount(final int lineCount)
	{
		if(lineCount > 0)
		{
			this.lineCount += lineCount;
		}
	}

	/**
	 * Increase the covered count by this amount.
	 * The covered count is the number of executable lines that were executed.
	 * @param covered The amount to increase the covered count.
	 */
	void addToCovered(final int covered)
	{
		if(covered > 0)
		{
			this.lineHit += covered;
		}
	}

	/**
	 * Increase the total function count by this amount.
	 * The total function count is the number of functions found.
	 * @param funcCount The amount to increase the function count.
	 */
	void addToFuncCount(final int funcCount)
	{
		if(funcCount > 0)
		{
			this.funcCount += funcCount;
		}
	}

	void addToFuncHit(final int funcHit)
	{
		if(funcHit > 0)
		{
			this.funcHit += funcHit;
		}
	}

	/**
	 * Increase the total branch count by this amount.
	 * The total branch count is the number of branches found.
	 * @param branchCount The amount to increase the branch count.
	 */
	void addToBranchCount(final int branchCount)
	{
		if(branchCount > 0)
		{
			this.branchCount += branchCount;
		}
	}

	void addToBranchHit(final int branchHit)
	{
		if(branchHit > 0)
		{
			this.branchHit += branchHit;
		}
	}

	/**
	 *
	 * @param path
	 */
	public void setPath(final String path)
	{
		String newPath = path;
		newPath = newPath.replace(File.separator, "/");//for web stuff we want fwd slashes
		newPath = newPath.replaceAll("/+", "/");
		int idx = newPath.indexOf('/');
		if(idx > 0)//basically strip drive letters
		{
			newPath = newPath.substring(idx);
		}

		this.path = newPath;
	}

	public String getTestName()
	{
		return this.testName;
	}

	public int getLineCount()
	{
		return this.lineCount;
	}

	public int getLineHit()
	{
		return this.lineHit;
	}

	public int getFuncCount()
	{
		return funcCount;
	}

	public int getFuncHit()
	{
		return funcHit;
	}

	public int getBranchCount()
	{
		return branchCount;
	}

	public int getBranchHit()
	{
		return branchHit;
	}

	public String getPath()
	{
		return this.path;
	}

	public String getPageName()
	{
		return this.pageName;
	}

//	public void setLineHit(int lineHit)
//	{
//		this.lineHit = Math.max(0, lineHit);
//	}
//
//	public void setFuncHit(int funcHit)
//	{
//		this.funcHit = Math.max(0, funcHit);
//	}
//
//	public void setBranchHit(int branchHit)
//	{
//		this.branchHit = Math.max(0, branchHit);
//	}


	void setPageName(final String pageName)
	{
		String newPageName = (pageName != null)? pageName.trim() : null;
		if(newPageName != null && newPageName.length() > 0)
		{
			this.pageName = pageName;
		}
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(final String prefix)
	{
		this.prefix = prefix;
	}

	Document getDoc()
	{
		return this.doc;
	}

	/**
	 * Writes this coverage page to the file system as XML/HTML.
	 * @param rootDir The output directory.
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	public void writeToFileSystem() throws TransformerConfigurationException, TransformerException, IOException
	{
		updateCoverageAttributes();
		Config config = CoverageReport.getConfig();
		doc.getDocumentElement().appendChild(config.toXml(doc));
		writeToFileSystem(config.getOutRootDir(), false);
		if(!config.isHtmlOnly())
		{
			writeToFileSystem(config.getOutRootDir(), true);
		}
	}

	public float getLineRate()
	{
		return getCoveredRate(this.getLineHit(), this.getLineCount());
	}

	public float getBranchRate()
	{
		return getCoveredRate(this.getBranchHit(), this.getBranchCount());
	}

	public float getFunctionRate()
	{
		return getCoveredRate(this.getFuncHit(), this.getFuncCount());
	}

	/**
	 * When the root element is first created not all attributes are set, call this to set them.
	 */
	private void updateCoverageAttributes()
	{
		Element coverage = this.doc.getDocumentElement();
		JGenHtmlUtils.setGlobalRootAttributes(coverage, testName);
		coverage.setAttribute("line-rate", String.valueOf(getLineRate()));
		coverage.setAttribute("lines-valid", String.valueOf(this.getLineCount()));
		coverage.setAttribute("lines-covered", String.valueOf(this.getLineHit()));
		coverage.setAttribute("branch-rate", String.valueOf(getBranchRate()));
		coverage.setAttribute("branches-valid", String.valueOf(this.getBranchCount()));
		coverage.setAttribute("branches-covered", String.valueOf(this.getBranchHit()));
		coverage.setAttribute("function-rate", String.valueOf(getFunctionRate()));
		coverage.setAttribute("functions-valid", String.valueOf(this.getFuncCount()));
		coverage.setAttribute("functions-covered", String.valueOf(this.getFuncHit()));
		coverage.setAttribute("complexity", "0");

		if(path != null && path.length() > 0)
		{
			coverage.setAttribute("package", path);
		}
		if(pageName != null && pageName.length() > 0)
		{
			coverage.setAttribute("filename", pageName);
		}
	}

	/**
	 * Writes this instance to the filesystem as a report page (xml/html).
	 * @param rootDir The root output directory.
	 * @param asXml If true, will write XML files (for client side transform) instead of HTML.
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	private void writeToFileSystem(final File rootDir, final boolean asXml) throws TransformerConfigurationException, TransformerException, IOException
	{
		Config config = CoverageReport.getConfig();
		String tagetFileName = pageName;
		if(this instanceof FunctionPage)
		{
			tagetFileName += ".func";
		}
		else if(this instanceof TestCaseSourceFile)
		{
			tagetFileName += ".gcov";
		}
		String basePath = calculateRelativePathToRoot(path);
		if(basePath.length() > 0)
		{
			Element baseElement = doc.createElement("base");
			baseElement.setAttribute("href", basePath);
			doc.getDocumentElement().appendChild(baseElement);
		}
		File outDir = config.isHtmlOnly()? JGenHtmlUtils.getTargetDir(rootDir, path) : JGenHtmlUtils.getTargetDir(rootDir, asXml, path);
		File out;
		if(asXml)
		{
			JGenHtmlUtils.linkToXsl(doc, basePath + JGenHtmlUtils.XSLT_NAME);
			out = new File(outDir, tagetFileName + ".xml");
		}
		else
		{
			out = new File(outDir, tagetFileName + config.getHtmlExt());
		}
		LOGGER.log(Level.FINE, "Writing file: {0}", out.getAbsolutePath());
		JGenHtmlUtils.transformToFile(out, asXml, doc);
	}

	private static String calculateRelativePathToRoot(final String path)
	{
		StringBuilder sb = new StringBuilder();
		if(path != null)
		{
			String safePath = path.replace(File.separator, "/");
			int nextSlash = 0;
			do
			{
				sb.append("../");
			}
			while((nextSlash = safePath.indexOf("/", nextSlash + 1)) > 0);
		}
		return sb.toString();
	}

	private void addLineValuesFromPage(final CoveragePage page)
	{
		this.addToCovered(page.getLineHit());
		this.addToLineCount(page.getLineCount());
	}

	private void addBranchValuesFromPage(final CoveragePage page)
	{
		this.addToBranchHit(page.getBranchHit());
		this.addToBranchCount(page.getBranchCount());
	}

	private void addFunctionValuesFromPage(final CoveragePage page)
	{
		this.addToFuncHit(page.getFuncHit());
		this.addToFuncCount(page.getFuncCount());
	}

	void addValuesFromPage(final CoveragePage page)
	{
		addLineValuesFromPage(page);
		addBranchValuesFromPage(page);
		addFunctionValuesFromPage(page);
	}

	private static float getCoveredRate(final int hit, final int total)
	{
		return (float) hit / (float) total;
	}
}
