package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.OneToMany;
import com._7aske.grain.orm.model.Model;
import com._7aske.grain.orm.querybuilder.helper.ModelClass;
import com._7aske.grain.orm.querybuilder.helper.ModelField;
import com._7aske.grain.util.formatter.StringFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractQueryBuilder implements QueryBuilder {
	public final static String DATE_TIME_FORMAT_STRING = "dd-MM-yyyy HH:mm:ss";
	// @Refactor ugh
	public final static String DATE_TIME_FORMAT_STRING2 = "yyyy-MM-dd HH:mm:ss";
	public final static String DATE_FORMAT_STRING = "dd-MM-yyyy";
	public final static String DATE_FORMAT_STRING2 = "yyyy-MM-dd";
	public final static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_STRING);
	public final static DateTimeFormatter DATE_TIME_FORMAT2 = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_STRING2);
	public final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING);
	public final static DateTimeFormatter DATE_FORMAT2 = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING2);
	public final static SimpleDateFormat SIMPLE_DATE_TIME_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT_STRING);
	public final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
	private final Logger logger = LoggerFactory.getLogger(AbstractQueryBuilder.class);
	private final ModelClass modelClass;
	private final Model model;


	protected AbstractQueryBuilder(Model model) {
		this.model = model;
		this.modelClass = new ModelClass(model.getClass());
	}

	protected <T extends Model> ModelClass getModelClass() {
		return (ModelClass) modelClass;
	}

	protected <T extends Model> T getModel() {
		return (T) model;
	}

	protected Object getFieldValue(ModelField field) {
		Object value = null;
		// Handle the case where the formatted value is a relationship object
		if (field.isAnnotationPresent(OneToMany.class)) {
			// @Incomplete
		} else if (field.isAnnotationPresent(ManyToOne.class)) {
			Model m = (Model) field.get(model);
			List<ModelField> ids = new ModelClass(m.getClass()).getIdColumnFields();
			if (ids.size() > 1) {
				// @Temporary probably should throw
				logger.warn("Unsupported update of ManyToOne relationship with composite foreign key");
			} else {
				ModelField idField = ids.get(0);
				value = idField.get(m);
			}
		} else {
			value = field.get(model);
		}
		return value;
	}

	// @Incomplete probably doesn't cover all of the cases but for the time
	// being it works for basic values
	// @Incomplete check validity of date formats
	protected String getFormattedFieldValue(ModelField field) {
		return getFormattedFieldValue(field, getFieldValue(field));
	}

	protected String getFormattedFieldValue(Map.Entry<String, Object> kv) {
		return StringFormat.format("{} = {}", kv.getKey(), kv.getValue());
	}

	// Gets hopefully valid SQL formatted value for the passed Field object.
	protected String getFormattedFieldValue(ModelField field, Object value) {
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
		return modelClass.getIdColumnFields()
				.stream()
				.collect(Collectors.toMap(ModelField::getColumnName, this::getFormattedFieldValue));
	}

	protected Map<String, Object> getValuePairs() {
		Map<String, Object> columns = modelClass.getColumnFields()
				.stream()
				.collect(Collectors.toMap(ModelField::getColumnName, this::getFormattedFieldValue));
		Map<String, Object> manyToOne = getModelClass().getManyToOne()
				.stream()
				.collect(Collectors.toMap(ModelField::getColumnName, this::getFormattedFieldValue));
		columns.putAll(manyToOne);
		return columns;
	}

	protected Map<String, Object> getValuePairsFor(String... columns) {
		return Arrays.stream(columns)
				// @Refactor this can be better
				.map(col -> modelClass.getColumnFields().stream().filter(f -> f.getColumnName().equals(col)).findFirst())
				.flatMap(Optional::stream)
				.collect(Collectors.toMap(ModelField::getColumnName, this::getFormattedFieldValue));
	}
}
