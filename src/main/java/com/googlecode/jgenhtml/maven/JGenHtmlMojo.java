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
package com.googlecode.jgenhtml.maven;

import com.googlecode.jgenhtml.plugin.JGenHtmlExecutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * This class is a Maven plugin that runs jgenhtml against specified tracefiles.
 */
@Mojo(name = "genthml", defaultPhase = LifecyclePhase.PACKAGE)
public class JGenHtmlMojo extends AbstractMojo
{
	/**
	 * An array of paths to the tracefiles to process.
	 */
	@Parameter(property = "jgenhtml.tracefiles")
	private String[] tracefiles = null;

	/**
	 * A single tracefile to process.
	 */
	@Parameter(property = "jgenhtml.in")
	private String in;

	/**
	 * The output directory.
	 */
	@Parameter(property = "jgenhtml.outdir")
	private String outdir = null;

	/**
	 * Path to a config file.
	 */
	@Parameter(property = "jgenhtml.config")
	private String config = null;

	/**
	 * Whether to skip execution of Mojo
	 */
	@Parameter(property = "jgenhtml.skip", defaultValue = "false")
	private boolean skip;

	public void setTracefiles(String[] tracefiles)
	{
		this.tracefiles = tracefiles;
	}

	@Override
	public void execute() throws MojoExecutionException
	{
		if (this.skip)
		{
			getLog().info("jgenhtml:genthml plugin skipped");
			return;
		}
		try
		{
			JGenHtmlExecutor executor = new JGenHtmlExecutor();
			executor.addTracefile(tracefiles);
			executor.addTracefile(in);
			executor.setOutdir(outdir);
			executor.setConfig(config);
			executor.execute();
		}
		catch(IllegalStateException ex)
		{
			throw new MojoExecutionException(ex.getMessage(), ex);
		}
	}
}
