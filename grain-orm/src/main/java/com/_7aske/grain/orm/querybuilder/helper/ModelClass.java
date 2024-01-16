package com._7aske.grain.orm.querybuilder.helper;

import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.orm.annotation.*;
import com._7aske.grain.orm.exception.GrainDbIntrospectionException;
import com._7aske.grain.orm.model.Model;
import com._7aske.grain.core.reflect.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com._7aske.grain.core.reflect.ReflectionUtil.isAnnotationPresent;

public class ModelClass implements HasDialect {
	private final Logger logger = LoggerFactory.getLogger(ModelClass.class);
	private final Class<? extends Model> clazz;
	private final List<ModelField> columnFields;
	private final List<OneToManyField> oneToManyFields;
	private final List<ManyToOneField> manyToOneFields;
	private final List<ModelField> idFields;

	public ModelClass(Class<? extends Model> clazz) {
		this.clazz = clazz;
		Field[] fields = clazz.getDeclaredFields();
		idFields = Arrays.stream(fields)
				.filter(f -> isAnnotationPresent(f, Id.class))
				.map(ModelField::new)
				.collect(Collectors.toList());
		columnFields = Arrays.stream(fields)
				.filter(f -> isAnnotationPresent(f, Column.class))
				.map(ModelField::new)
				.collect(Collectors.toList());
		oneToManyFields = Arrays.stream(fields)
				.filter(f -> isAnnotationPresent(f, OneToMany.class))
				.map(OneToManyField::new)
				.collect(Collectors.toList());
		manyToOneFields = Arrays.stream(fields)
				.filter(f -> isAnnotationPresent(f, ManyToOne.class))
				.map(ManyToOneField::new)
				.collect(Collectors.toList());
	}

	public Model newInstance() {
		return ReflectionUtil.newInstance(clazz);
	}

	public List<ModelField> getIdColumnFields() {
		return idFields;
	}

	public ModelField getIdColumnField() {
		// @Temporary
		if (idFields.size() > 1)
			logger.warn("Called getId for class with a composite ID");

		return idFields.get(0);
	}

	public String getTableName() {
		if (!this.clazz.isAnnotationPresent(Table.class))
			throw new GrainDbIntrospectionException("Referenced class is not annotated with @Table annotation");
		Table column = this.clazz.getAnnotation(Table.class);
		return column.name() == null || column.name().isEmpty() ? applyDialect(clazz.getSimpleName()) : column.name();
	}

	public List<ModelField> getColumnAndManyToOneFields() {
		return Stream.concat(columnFields.stream(), manyToOneFields.stream())
				.collect(Collectors.toList());
	}

	public List<ModelField> getColumnFields() {
		return columnFields;
	}

	public List<ModelField> getIdFields() {
		return idFields;
	}

	public List<OneToManyField> getOneToMany() {
		return oneToManyFields;
	}

	public List<ManyToOneField> getManyToOne() {
		return manyToOneFields;
	}

	public Field getDeclaredField(String key) {
		try {
			return clazz.getDeclaredField(key);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	public ModelField getField(String name) {
		for (ModelField f : idFields) {
			if (f.getField().getName().equals(name)) return f;
		}
		for (ModelField f : columnFields) {
			if (f.getField().getName().equals(name)) return f;
		}
		for (ModelField f : manyToOneFields) {
			if (f.getField().getName().equals(name)) return f;
		}
		for (ModelField f : oneToManyFields) {
			if (f.getField().getName().equals(name)) return f;
		}
		return null;
	}
}
