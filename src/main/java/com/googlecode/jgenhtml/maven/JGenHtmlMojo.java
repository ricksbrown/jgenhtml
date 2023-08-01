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

import com.googlecode.jgenhtml.plugin.JGenHtmlExecuter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * This class is a Maven plugin that runs jgenhtml against specified tracefiles.
 * @goal test
 * @phase test
 */
public class JGenHtmlMojo extends AbstractMojo
{
	/**
	 * An array of paths to the tracefiles to process.
	 * @parameter property="jgenhtml.tracefiles"
	 */
	private String[] tracefiles = null;

	/**
	 * A
	 * @parameter property="jgenhtml.in"
	 */
	private String in;

	/**
	 * @parameter property="jgenhtml.outdir"
	 */
	private String outdir = null;

	/**
	 * @parameter property="jgenhtml.config"
	 */
	private String config = null;

	public void setTracefiles(String[] tracefiles)
	{
		this.tracefiles = tracefiles;
	}

	@Override
	public void execute() throws MojoExecutionException
	{
		try
		{
			JGenHtmlExecuter executer = new JGenHtmlExecuter();
			executer.addTracefile(tracefiles);
			executer.addTracefile(in);
			executer.setOutdir(outdir);
			executer.setConfig(config);
			executer.execute();
		}
		catch(IllegalStateException ex)
		{
			throw new MojoExecutionException(ex.getMessage(), ex);
		}
	}
}
