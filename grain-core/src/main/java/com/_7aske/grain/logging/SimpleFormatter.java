package com._7aske.grain.logging;

import com._7aske.grain.util.formatter.StringFormat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static com._7aske.grain.logging.Color.*;
import static com._7aske.grain.logging.Util.shortenClassName;

/**
 * Grain specific implementation of {@link java.util.logging.Formatter} that uses
 * {@link com._7aske.grain.util.formatter.StringFormat} to format log messages.
 */
public class SimpleFormatter extends Formatter {
	// @Todo create a way of changing this through logging.properties
	public static final String LOG_FORMAT = "{0} {1:7} - [{2:15}] {3:-41}: {4}{5}\n";
	public static final String DATE_TIME_FORMAT_STRING = "dd-MM-yyyy hh:mm:ss.SSS";
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_STRING);
	public static final SimpleDateFormat SIMPLE_DATE_TIME_FORMATTER = new SimpleDateFormat(DATE_TIME_FORMAT_STRING);
	private final Date date = new Date();

	@Override
	public String format(LogRecord logRecord) {
		date.setTime(logRecord.getMillis());

		String source = shortenClassName(logRecord.getLoggerName(), 41);
		String message = StringFormat.format(logRecord.getMessage(), logRecord.getParameters());
		String throwable = getThrowable(logRecord);
		String thread = getThreadName();
		Level level = logRecord.getLevel();

		String levelStr;
		if (Level.SEVERE.equals(level)) {
			levelStr = red(level.getName());
		} else if (Level.WARNING.equals(level)) {
			levelStr = yellow(level.getName());
		} else if (Level.INFO.equals(level)) {
			levelStr = blue(level.getName());
		} else if (Level.CONFIG.equals(level)) {
			levelStr = green(level.getName());
		} else if (Level.FINE.equals(level) || Level.FINER.equals(level) || Level.FINEST.equals(level)) {
			levelStr = purple(level.getName());
		} else {
			levelStr = level.getName();
		}

		return StringFormat.format(LOG_FORMAT, SIMPLE_DATE_TIME_FORMATTER.format(date), levelStr, thread, cyan(source), message, throwable);
	}
	private String getThrowable(LogRecord logRecord) {
		if (logRecord.getThrown() == null) {
			return "";
		}
		StringWriter stringWriter = new StringWriter();
		try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
			printWriter.println();
			logRecord.getThrown().printStackTrace(printWriter);
		}
		return stringWriter.toString();
	}

	private String getThreadName() {
		String name = Thread.currentThread().getName();
		return (name != null) ? name : "";
	}
}
