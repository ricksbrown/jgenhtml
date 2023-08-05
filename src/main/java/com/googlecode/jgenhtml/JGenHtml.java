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
	public static final String VERSION = "1.6";  // todo find a sensible way to get this from the pom

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
		catch (IOException | ParserConfigurationException ex)
		{
			LOGGER.log(Level.SEVERE, null, ex);
		}
		catch (ParseException ex)
		{
			LOGGER.log(Level.WARNING, ex.getLocalizedMessage());
		}
	}


}
