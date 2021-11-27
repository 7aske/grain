package com._7aske.grain.logging;

import com._7aske.grain.util.formatter.StringFormat;

import java.time.LocalDateTime;

import static com._7aske.grain.logging.Color.*;
import static com._7aske.grain.logging.Util.shortenClassName;

class ConsoleLogger extends Logger {
	//2021-11-26 00:36:32.144  INFO 4063739 --- [           main] o.apache.catalina.core.StandardService   : Stopping service [Tomcat]
	public ConsoleLogger(String name) {
		super(shortenClassName(name, 41));
	}

	private void doLog(Level level, String s, Object... params) {
		// @Incomplete allow to change format
		String FORMAT = "{0} {1:5} - [{2:15}] {3:-41}: {4}";
		String date = DATE_TIME_FORMATTER.format(LocalDateTime.now());
		String message = StringFormat.format(s, params);
		String threadName = Thread.currentThread().getName();
		if (threadName.length() > 15) {
			threadName = threadName.substring(0, 15);
		}
		String levelStr;
		switch (level) {
			case TRACE:
				levelStr = purple(level.getName());
				break;
			case INFO:
			case DEBUG:
				levelStr = blue(level.getName());
				break;
			case WARN:
				levelStr = yellow(level.getName());
				break;
			case ERROR:
				levelStr = red(level.getName());
				break;
			default:
				levelStr = level.getName();
		}
		System.out.println(StringFormat.format(FORMAT, date, levelStr, threadName, cyan(name), message));
	}

	@Override
	public void trace(String s, Object... params) {
		doLog(Level.TRACE, s, params);
	}

	@Override
	public void info(String s, Object... params) {
		doLog(Level.INFO, s, params);
	}

	@Override
	public void warn(String s, Object... params) {
		doLog(Level.WARN, s, params);
	}

	@Override
	public void debug(String s, Object... params) {
		doLog(Level.DEBUG, s, params);
	}

	@Override
	public void error(String s, Object... params) {
		doLog(Level.ERROR, s, params);
	}
}
