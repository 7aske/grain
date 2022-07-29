package com._7aske.grain.logging;

import com._7aske.grain.util.formatter.StringFormat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import static com._7aske.grain.logging.Color.cyan;
import static com._7aske.grain.logging.Util.shortenClassName;

public class SimpleFormatter extends Formatter {
	public static final String LOG_FORMAT = "{0} {1:5} - [{2:15}] {3:-41}: {4}{5}\n";
	public static final String DATE_TIME_FORMAT_STRING = "dd-MM-yyyy hh:mm:ss.SSS";
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_STRING);
	public static final SimpleDateFormat SIMPLE_DATE_TIME_FORMATTER = new SimpleDateFormat(DATE_TIME_FORMAT_STRING);
	private final Date date = new Date();

	@Override
	public String format(LogRecord logRecord) {
		date.setTime(logRecord.getMillis());

		String source = shortenClassName(logRecord.getLoggerName(), 41);
		String message = formatMessage(logRecord);
		String throwable = getThrowable(logRecord);
		String thread = getThreadName();
		String level = logRecord.getLevel().getName();

		return StringFormat.format(LOG_FORMAT, SIMPLE_DATE_TIME_FORMATTER.format(date), level, thread, cyan(source), message, throwable);
	}
	private String getThrowable(LogRecord logRecord) {
		if (logRecord.getThrown() == null) {
			return null;
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
