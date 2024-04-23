package com.ctrip.car.osd.framework.dal.mapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.platform.dal.dao.DalRowMapper;


/**
 * 对象自动映射
 * 
 * @author xiayx@Ctrip.com
 *
 * @param <T>
 */
public class ObjectRowMapper<T> implements DalRowMapper<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectRowMapperContext.class);
	private Class<T> entityClass;
	private List<Pair<String, String>> fields;

	public ObjectRowMapper(Class<T> entityClass) {
		this.fields = null;
		this.entityClass = entityClass;
	}

	@Override
	public T map(ResultSet rs, int rowNum) throws SQLException {
		Collection<Pair<String, String>> fields = getFields(rs);
		T object = org.springframework.beans.BeanUtils.instantiate(entityClass);

		for (Pair<String, String> field : fields) {
			try {
				BeanUtils.setProperty(object, field.getRight(), rs.getObject(field.getLeft()));
			} catch (IllegalAccessException e) {
				LOGGER.error("set props error :" + field, e);
			} catch (InvocationTargetException e) {
				LOGGER.error("set props error :" + field, e);
			}
		}
		return object;
	}

	private synchronized Collection<Pair<String, String>> getFields(ResultSet rs) throws SQLException {

		if (this.fields != null) {
			return this.fields;
		}

		this.fields = new ArrayList<>();
		
		List<String> properties = new ArrayList<>();
		for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(entityClass)) {
			properties.add(propertyDescriptor.getName());
		}

		Map<String, String> columnMap = new HashMap<>();
		for (Field field : entityClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(Column.class)) {
				columnMap.put(field.getAnnotation(Column.class).name(), field.getName());
			}
		}
		
		int columnCount = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String name = rs.getMetaData().getColumnLabel(i);
			if (columnMap.containsKey(name)) {
				fields.add(Pair.of(name, columnMap.get(name)));
			} else {
				for (String property : properties) {
					if (name.equalsIgnoreCase(property)) {
						fields.add(Pair.of(name, property));
						break;
					}
				}
			}
		}
		return this.fields;
	}

}
