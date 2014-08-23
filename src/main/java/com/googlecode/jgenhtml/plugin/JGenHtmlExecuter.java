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
package com.googlecode.jgenhtml.plugin;

import com.googlecode.jgenhtml.JGenHtml;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is a helper for the jgenhtml Ant task and Maven mojo.
 * It deals with validation and execution.
 */
public class JGenHtmlExecuter
{
	private Set<String> tracefiles;
	private String outdir = null;
	private String config = null;

	public JGenHtmlExecuter()
	{
		tracefiles = new HashSet<String>();
	}

	/**
	 * Add more paths to tracefiles.
	 * Duplicates are ignored.
	 * @param tracefiles Paths to tracefiles we want to process
	 */
	public void addTracefile(final String[] tracefiles)
	{
		if(tracefiles != null && tracefiles.length > 0)
		{
			this.tracefiles.addAll(Arrays.asList(tracefiles));
		}
	}

	/**
	 * Add another path to tracefiles.
	 * Duplicates are ignored.
	 * @param tracefile Path to a tracefile we want to process
	 */
	public void addTracefile(final String tracefile)
	{
		if(tracefile != null)
		{
			this.tracefiles.add(tracefile);
		}
	}

	/**
	 * @param outdir The path to the output directory in which output will be generated.
	 */
	public void setOutdir(String outdir)
	{
		this.outdir = outdir;
	}

	/**
	 *
	 * @param in The path to an lcovrc config file.
	 * If not specified will check in user home directory.
	 */
	public void setConfig(String config)
	{
		this.config = config;
	}

	/**
	 * Check that the mandatory properties have been set.
	 */
	private void validate() throws IllegalStateException
	{
		if(tracefiles.size() < 1)
		{
			throw new IllegalStateException("No tracefiles specified");
		}
	}

	/**
	 * Build an args array that can be passed to the main method of jgenhtml.
	 * @return
	 */
	String[] buildArgs()
	{
		String result[] = new String[0];
		List<String> args = new ArrayList<String>();
		if(outdir != null && outdir.length() > 0)
		{
			args.add("--output-directory");
			args.add(outdir);
		}
		if(config != null && config.length() > 0)
		{
			args.add("--config-file");
			args.add(config);
		}
		args.addAll(tracefiles);
		return args.toArray(result);
	}

	/**
	 * Run jgenhtml with the provided properties.
	 * @throws IllegalStateException If mandatory properties have not been set.
	 */
	public void execute() throws IllegalStateException
	{
		validate();
		String[] args = buildArgs();
		JGenHtml.main(args);
	}
}
