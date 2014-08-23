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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.cli.ParseException;

/**
 * genhtml functionality required by JsTestDriver's coverage plugin (plus a fair but more).
 * Written in Java so it can run anywhere JsTestDriver will run.
 * @author Rick Brown
 */
public class JGenHtml
{
	private static final Logger LOGGER = Logger.getLogger(JGenHtml.class.getName());

	static{
		JGenHtmlUtils.setLogFormatter(LOGGER);
	}
	public static final String VERSION = "1.5";//todo find a sensible way to get this from the pom

	/**
	 * Run jgenhtml.
	 * @param argv Arguments (viewable by running with -h switch).
	 */
	public static void main(final String[] argv)
	{

		Config config = new Config();
		try
		{
			config.initializeUserPrefs(argv);
			if(config.isHelp())
			{
				config.showCmdLineHelp();
			}
			else if(config.isVersion())
			{
				System.out.println("jgenhtml version " + VERSION);
			}
			else
			{
				String[] traceFiles = config.getTraceFiles();
				if(traceFiles.length > 0)
				{
					CoverageReport.setConfig(config);
					CoverageReport coverageReport = new CoverageReport(traceFiles);
					if(coverageReport.getPageCount() > 0)
					{
						LOGGER.log(Level.INFO, "Found {0} entries.", coverageReport.getPageCount());
						coverageReport.generateReports();
					}
				}
				else
				{
					LOGGER.log(Level.INFO, "jgenhtml: No filename specified");
					LOGGER.log(Level.INFO, "Use jgenhtml --help to get usage information");
				}
			}
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, null, ex);
		}
		catch (ParserConfigurationException ex)
		{
			LOGGER.log(Level.SEVERE, null, ex);
		}
		catch (ParseException ex)
		{
			LOGGER.log(Level.WARNING, ex.getLocalizedMessage());
		}
	}


}
