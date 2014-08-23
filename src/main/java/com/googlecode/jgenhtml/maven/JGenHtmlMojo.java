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
