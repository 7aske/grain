package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.OneToMany;
import com._7aske.grain.orm.model.Model;
import com._7aske.grain.orm.model.ModelInspector;
import com._7aske.grain.util.formatter.StringFormat;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractQueryBuilder implements QueryBuilder {
	protected final static String DATE_TIME_FORMAT_STRING = "dd-MM-yyyy hh:mm:ss";
	protected final static String DATE_FORMAT_STRING = "dd-MM-yyyy";
	protected final static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_STRING);
	protected final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING);
	protected final static SimpleDateFormat SIMPLE_DATE_TIME_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT_STRING);
	protected final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
	private final Logger logger = LoggerFactory.getLogger(AbstractQueryBuilder.class);
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
			// Handle the case where the formatted value is a relationship object
			if (field.isAnnotationPresent(OneToMany.class)) {
				// @Incomplete
			} else if (field.isAnnotationPresent(ManyToOne.class))  {
				Model model = (Model) field.get(getModelInspector().getModel());
				List<Field> ids = new ModelInspector(model).getModelIds();
				if (ids.size() > 1) {
					// @Temporary probably should throw
					logger.warn("Unsupported update of ManyToOne relationship with composite foreign key");
				} else {
					Field idField = ids.get(0);
					idField.setAccessible(true);
					value = idField.get(model);
				}
			} else {
				value = field.get(getModelInspector().getModel());
			}
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

	protected String getFormattedFieldValue(Map.Entry<String,Object> kv) {
		return getFormattedFieldValue(kv.getKey(), kv.getValue());
	}
	protected String getFormattedFieldValue(String key, Object value) {
		try {
			Field field = model.getModel().getClass().getDeclaredField(key);
			return StringFormat.format("{} = {}", key, getFormattedFieldValue(field, value));
		} catch (NoSuchFieldException e) {
			return String.format("%s = %s", key, value);
		}
	}

	// Gets hopefully valid SQL formatted value for the passed Field object.
	protected String getFormattedFieldValue(Field field, Object value) {
		if (value == null) {
			return "NULL";
		} else if (String.class.isAssignableFrom(field.getType())) {
			return String.format("'%s'", value);
		} else if (Byte.class.isAssignableFrom(field.getType())) {
			return value.toString();
		} else if (Byte.class.isAssignableFrom(field.getType())) {
			return value.toString();
		} else if (Integer.class.isAssignableFrom(field.getType())) {
			return value.toString();
		} else if (Float.class.isAssignableFrom(field.getType())) {
			return value.toString();
		} else if (Double.class.isAssignableFrom(field.getType())) {
			return value.toString();
		} else if (Boolean.class.isAssignableFrom(field.getType())) {
			return value.toString();
		} else if (Long.class.isAssignableFrom(field.getType())) {
			return value.toString();
		} else if (Character.class.isAssignableFrom(field.getType())) {
			// @Note @CopyPasta this is probably bad
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
			return value.toString();
		}
	}

	protected Map<String, Object> getIdValuePairs() {
		return getModelInspector().getModelIds()
				.stream()
				.collect(Collectors.toMap(f -> f.getAnnotation(Column.class).name(), this::getFormattedFieldValue));
	}

	protected Map<String, Object> getValuePairs() {
		Map<String, Object> columns = getModelInspector().getModelFields()
				.stream()
				.collect(Collectors.toMap(f -> f.getAnnotation(Column.class).name(), this::getFormattedFieldValue));
		Map<String, Object> manyToOne = getModelInspector().getModelManyToOne()
				.stream()
				.collect(Collectors.toMap(f -> f.getAnnotation(ManyToOne.class).column(), this::getFormattedFieldValue));
		columns.putAll(manyToOne);
		return columns;
	}

	protected Map<String, Object> getValuePairsFor(String... columns) {
		return Arrays.stream(columns)
				// @Refactor this can be better
				.map(col -> getModelInspector().getModelFields().stream().filter(f -> f.getAnnotation(Column.class).name().equals(col)).findFirst())
				.flatMap(Optional::stream)
				.collect(Collectors.toMap(f -> f.getAnnotation(Column.class).name(), this::getFormattedFieldValue));
	}
}
