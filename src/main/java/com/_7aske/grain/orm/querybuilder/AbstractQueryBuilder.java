package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.model.Model;
import com._7aske.grain.orm.model.ModelInspector;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractQueryBuilder implements QueryBuilder {
	protected final static String DATE_TIME_FORMAT_STRING = "dd-MM-yyyy hh:mm:ss";
	protected final static String DATE_FORMAT_STRING = "dd-MM-yyyy";
	protected final static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_STRING);
	protected final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING);
	protected final static SimpleDateFormat SIMPLE_DATE_TIME_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT_STRING);
	protected final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
	private final ModelInspector model;


	protected AbstractQueryBuilder(Model model) {
		this.model = new ModelInspector(model);
	}

	protected ModelInspector getModelInspector() {
		return model;
	}

	protected Object getFieldValue(Field field) {
		Object value = null;
		try {
			field.setAccessible(true);
			value = field.get(getModelInspector().getModel());
		} catch (IllegalAccessException e) {
			// ignored
		}
		return value;
	}

	// @Incomplete probably doesn't cover all of the cases but for the time
	// being it works for basic values
	// @Incomplete check validity of date formats
	protected String getFormattedFieldValue(Field field) {
		return getFormattedFieldValue(field, getFieldValue(field));
	}

	// Gets hopefully valid SQL formatted value for the passed Field object.
	protected String getFormattedFieldValue(Field field, Object value) {
		if (value == null) {
			return "NULL";
		} else if (String.class.isAssignableFrom(field.getType())) {
			return String.format("'%s'", value);
		} else if (Number.class.isAssignableFrom(field.getType())) {
			return value.toString();
		} else if (Boolean.class.isAssignableFrom(field.getType())) {
			return value.toString();
		} else if (field.getType().isPrimitive()) {
			return value.toString();
		} else if (Date.class.isAssignableFrom(field.getType())) {
			return String.format("'%s'", SIMPLE_DATE_FORMAT.format(value));
		} else if (LocalDate.class.isAssignableFrom(field.getType())) {
			return String.format("'%s'", ((LocalDate) value).format(DATE_FORMAT));
		} else if (LocalDateTime.class.isAssignableFrom(field.getType())) {
			return String.format("'%s'", ((LocalDateTime) value).format(DATE_TIME_FORMAT));
		} else {
			return String.format("'%s'", value);
		}
	}

	protected Map<String, Object> getIdValuePairs() {
		return getModelInspector().getModelIds()
				.stream()
				.collect(Collectors.toMap(f -> f.getAnnotation(Column.class).name(), this::getFormattedFieldValue));
	}

	protected Map<String, Object> getValuePairs() {
		return getModelInspector().getModelFields()
				.stream()
				.collect(Collectors.toMap(f -> f.getAnnotation(Column.class).name(), this::getFormattedFieldValue));
	}

	protected Map<String, Object> getValuePairsFor(String... columns) {
		return Arrays.stream(columns)
				// @Refactor this can be better
				.map(col -> getModelInspector().getModelFields().stream().filter(f -> f.getAnnotation(Column.class).name().equals(col)).findFirst())
				.flatMap(Optional::stream)
				.collect(Collectors.toMap(f -> f.getAnnotation(Column.class).name(), this::getFormattedFieldValue));
	}
}
