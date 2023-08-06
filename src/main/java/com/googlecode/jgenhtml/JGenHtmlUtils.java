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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

/**
 * Helpers for doing common JGenHtml stuff...
 * @author Rick Brown
 */
public class JGenHtmlUtils
{
	private final static Logger LOGGER = Logger.getLogger(JGenHtmlUtils.class.getName());
	private static final byte MIN_DIRECTORIES = 1;
	public static final String XSLT_NAME = "jgenhtml.xsl";
	public static final String CSS_NAME = "jgenhtml.css";
	public static final String JS_NAME = "jgenhtml.js";

	public static void setGlobalRootAttributes(final Element root, final String testName)
	{
		root.setAttribute("testname", testName);
		root.setAttribute("date", getDate());
		root.setAttribute("version", JGenHtml.VERSION);
	}

	/**
	 * @return The current date in the correct format for the coverage "date" attribute.
	 */
	private static String getDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}

	public static File processFilePath(final String filePath)
	{
		Path path = Paths.get(filePath);
		path = path.normalize();
		return path.toFile();
	}

	/**
	 * Parse the numerical values out of a line.
	 * @param line The line with any leading whitespace removed.
	 * @return The numerical values from the line.
	 */
	public static int[] getLineValues(String line)
	{
		String[] values = extractLineValues(line);
		int[] result = null;
		if(values != null)
		{
			result = new int[values.length];
			try
			{
				for(int i = 0; i < values.length; i++)
				{
					result[i] = Integer.parseInt(values[i]);
				}
			}
			catch(NumberFormatException nfe)
			{
				LOGGER.log(Level.FINE, "Could not parse value from line: {0}", line);
			}
		}
		return result;
	}

	/**
	 * Parse the  values out of a line.
	 * @param line The line with any leading whitespace removed.
	 * @return The values from the line.
	 */
	public static String[] extractLineValues(String line)
	{
		String[] result = null;
		int tokenIdx = line.indexOf(":");
		if(tokenIdx > 0 && (line.length() > tokenIdx + 1))
		{
			String values = line.substring(tokenIdx + 1);
			result = values.split(",");
		}
		return result;
	}

	/**
	 * Unzips a file.
	 * @param gzippedFile A gzipped file.
	 * @return The gunzipped version of the file.
	 * @throws IOException If you screw up.
	 */
	public static File gunzip(File gzippedFile) throws IOException
	{
		OutputStream out = null;
		GZIPInputStream gzipInputStream = null;
		File gunzippedFile = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + gzippedFile.getName().replace(".gz", ""));
		try
		{
			InputStream in = new FileInputStream(gzippedFile);
			gzipInputStream = new GZIPInputStream(in);
			out = new FileOutputStream(gunzippedFile);

			byte[] buf = new byte[1024];
			int len;
			while ((len = gzipInputStream.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
		}
		finally
		{
			if(gzipInputStream != null)
			{
				gzipInputStream.close();
			}
			if(out != null)
			{
				out.close();
			}
		}
		return gunzippedFile;
	}

	public static void setLogFormatter(final Logger logger)
	{
		logger.setUseParentHandlers(false);
		JGenHtmlLogFormatter formatter = new JGenHtmlLogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}

	/**
	 * Writes a resource (e.g. CSS file) to the destination directory.
	 * @param resourceName The name of a resource (which the classloader can find).
	 * @param destDir The destination directory.
	 */
	protected static void writeResource(final String resourceName, final File destDir)
	{
		writeResource(resourceName, destDir, null, null);
	}

	/**
	 * Writes a resource (e.g. CSS file) to the destination directory.
	 * @param resourceName The name of a resource (which the classloader can find).
	 * @param destDir The destination directory.
	 * @param token A token to find and replace in the resource.
	 * @param substitute The substitution for the token.
	 */
	protected static void writeResource(final String resourceName, final File destDir, String token, String substitute)
	{
		InputStream in = JGenHtml.class.getResourceAsStream('/' + resourceName);
		writeStreamToFileAndReplace(in, new File(destDir, resourceName), token, substitute);
	}

	/**
	 * Writes a resource (e.g. CSS file) to the destination directory.
	 * @param resource A resource file.
	 * @param destDir The destination directory.
	 */
	protected static void writeResource(final File resource, final File destDir)
	{
		try
		{
			LOGGER.log(Level.FINE, "Copying resource {0}", resource.getName());
			FileUtils.copyFileToDirectory(resource, destDir);
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
		}
	}

	private static void writeStreamToFileAndReplace(final InputStream in, final File targetFile, String token, String substitute)
	{
		try
		{
			OutputStream out = null;
			try
			{
				out = new FileOutputStream(targetFile);
				if(token != null && substitute != null)
				{
					String input = IOUtils.toString(in);
					input = input.replace(token, substitute);
					IOUtils.write(input, out);
				}
				else
				{
					IOUtils.copy(in, out);
				}
			}
			finally
			{
				if(out != null)
				{
					out.close();
				}
				in.close();
			}
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
		}
	}

	/**
	 * Same algorithm as the LCOV genhtml tool so it should always yield the same result.
	 * @param indexPages Determine the shared prefix based on the test paths.
	 * @return The prefix to remove or null.
	 */
	public static String getPrefix(Collection<TestCaseSourceFile> indexPages)
	{
		Map<String, Integer> prefix = new HashMap<>();
		for(CoveragePage page : indexPages)
		{
			String current = page.getPath();
			while ((current = shortenPrefix(current)) != null)
			{
				String next = current + '/';
				if(!prefix.containsKey(next))
				{
					prefix.put(next, 0);
				}
			}
		}

		for(CoveragePage page : indexPages)
		{
			String dir = page.getPath();
			for(int i = 0; i < MIN_DIRECTORIES; i++)
			{
				prefix.remove(dir + '/');
				dir = shortenPrefix(dir);
			}
		}

		Set<String> keys = prefix.keySet();
		for(String current : keys)
		{
			for(CoveragePage page : indexPages)
			{
				String path = page.getPath();
				prefix.put(current, prefix.get(current) + path.length());
				if(path.startsWith(current))
				{
					prefix.put(current, prefix.get(current) - current.length());
				}
			}
		}

		String result = null;
		// Find and return prefix with minimal sum
		for(String current : keys)
		{
			if(result == null || prefix.get(current) < prefix.get(result))
			{
				result = current;
			}
		}
		return result;
	}

	private static String shortenPrefix(String prefix)
	{
		String result = null;
		int idx = prefix.lastIndexOf('/');
		if(idx > 0)
		{
			result = prefix.substring(0, idx);
		}
		return result;
	}

	public static void transformToFile(final File targetFile, final boolean asXml, final Document doc) throws TransformerConfigurationException, TransformerException, IOException
	{
		Transformer transformer;
		Config config = CoverageReport.getConfig();
		if(asXml)
		{
			transformer = getTransformer(null);
		}
		else
		{
			transformer = getTransformer('/' + JGenHtmlUtils.XSLT_NAME);
			transformer.setParameter("ext", config.getHtmlExt());
			File cssFile = config.getCssFile();
			if(cssFile != null)
			{
				transformer.setParameter("cssName", cssFile.getName());
			}
		}
		DOMSource src = new DOMSource(doc);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult res;
		if(config.isGzip())
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(targetFile));
			res = new StreamResult(bos);
			transformer.transform(src, res);
			IOUtils.write(bos.toByteArray(), gzos);
			IOUtils.closeQuietly(gzos);
		}
		else
		{
			res = new StreamResult(targetFile);
			transformer.transform(src, res);
		}
	}

	/**
	 * Gets an XSLT transformer instance.
	 * @param xsltPath The path to the XSLT file.
	 * @return An instance of Transformer
	 */
	private static Transformer getTransformer(final String xsltPath)
	{
		Transformer transformer;
		try
		{
			TransformerFactory tFactory = TransformerFactory.newInstance();
			if(xsltPath != null)
			{
				Document xslt = loadXmlDoc(TestCaseSourceFile.class.getResourceAsStream(xsltPath));
				transformer = tFactory.newTransformer(new DOMSource(xslt));
			}
			else
			{
				transformer = tFactory.newTransformer();
			}
		}
		catch (TransformerConfigurationException ex)
		{
			transformer = null;
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
		}
		return transformer;
	}

	public static void linkToXsl(Document doc, final String xslPath)
	{
		ProcessingInstruction xsltLink = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"" + xslPath + '"');
		Element documentElement = doc.getDocumentElement();
		doc.insertBefore(xsltLink, documentElement);
	}

	public static Document loadXmlDoc(final InputStream stream)
	{
		Document result = null;
		try
		{
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setExpandEntityReferences(false);
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			result = builder.parse(stream);

		}
		catch (SAXException | IOException | ParserConfigurationException ex)
		{
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
		}
		return result;
	}


	/**
	 * Creates (if necessary) and returns either the HTML or XML output directory.
	 * @param rootDir The output root directory.
	 * @param asXml If true will return the XML output directory, otherwise the HTML directory.
	 * @return Either the XML or HTML output directory.
	 */
	public static File getTargetDir(final File rootDir, final boolean asXml)
	{
		return getTargetDir(rootDir, asXml, null);
	}

	/**
	 * Creates and returns a descendent directory within either the HTML or XML output directory found in the given root directory.
	 * @param rootDir The output root directory (which will contain an HTML and/or XML output directory).
	 * @param asXml If true will use the XML output directory, otherwise the HTML directory.
	 * @param subPath the relative path within the XML/HTML directory.
	 * @return The directory represented by subPath. If it did not exist it is created.
	 *
	 * example getTargetDir(tmpDir, true, "foo/bar");
	 * Would return the directory "bar" within tmpDir/xml.
	 *
	 * example getTargetDir(tmpDir, false, "foo/bar");
	 * Would return the directory "bar" within tmpDir/html.
	 */
	public static File getTargetDir(final File rootDir, final boolean asXml, final String subPath)
	{
		File targetDir = new File(rootDir, asXml? "xml" : "html");
		return getTargetDir(targetDir, subPath);
	}

	/**
	 * Creates and returns a descendent directory within the given root directory.
	 * @param rootDir The directory in which to create the subdirectory.
	 * @param subPath the relative path within the directory.
	 * @return The directory represented by subPath. If it did not exist it is created.
	 *
	 * example getTargetDir(tmpDir, "foo/bar");
	 * Would return the directory "bar" within tmpDir.
	 */
	public static File getTargetDir(final File rootDir, final String subPath)
	{
		File result;
		if(subPath != null && subPath.length() > 0)
		{
			result = new File(rootDir, subPath);
		}
		else
		{
			result = rootDir;
		}
		if(!result.exists())
		{
			result.mkdirs();
		}
		return result;
	}

	public static Element getHitElement(final Document document, final String name, final int hits)
	{
		Element result = document.createElement("hit");
		result.setAttribute("name", name);
		result.setAttribute("count", String.valueOf(hits));
		return result;
	}
}
