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