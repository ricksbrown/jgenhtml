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
public class JGenHtmlExecutor
{
	private final Set<String> tracefiles;
	private String outdir = null;
	private String config = null;

	public JGenHtmlExecutor()
	{
		tracefiles = new HashSet<>();
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
	 * @param config The path to an lcovrc config file.
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
		if(tracefiles.isEmpty())
		{
			throw new IllegalStateException("No tracefiles specified");
		}
	}

	/**
	 * Build an args array that can be passed to the main method of jgenhtml.
	 * @return A set of cli args.
	 */
	String[] buildArgs()
	{
		String[] result = new String[0];
		List<String> args = new ArrayList<>();
		if(outdir != null && !outdir.isEmpty())
		{
			args.add("--output-directory");
			args.add(outdir);
		}
		if(config != null && !config.isEmpty())
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
