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

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Strip down the crazy verbose default formatter.
 * @author Rick Brown
 */
public class JGenHtmlLogFormatter extends Formatter {

	@Override
	public String format(LogRecord record)
	{
		StringBuilder builder = new StringBuilder(1000);
		if(record.getLevel().intValue() >= Level.WARNING.intValue())
		{
			builder.append(record.getLevel());
			builder.append(": ");
		}
		builder.append(formatMessage(record));
		builder.append("\n");
		return builder.toString();
	}

	@Override
	public String getHead(Handler h)
	{
		return super.getHead(h);
	}

	@Override
	public String getTail(Handler h)
	{
		return super.getTail(h);
	}
}