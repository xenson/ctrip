package com.ctrip.car.osd.framework.common.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BeanCopyUtils {

	public static <T> T copy(Object source, Class<T> target) {
		try {
			T instance =  target.newInstance();
			BeanUtils.copyProperties(instance , source);
			return instance;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> List<T> copyList(List<Object> sources, Class<T> target) {
		List<T> results = new ArrayList<>();
		for (Object source : sources) {
			results.add(copy(source, target));
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public static <T> T clone(T t) {
	    if (t == null) {
	        return null;
        }
        if (t instanceof Serializable) {
	        return (T) SerializationUtils.clone((Serializable) t);
        }

        if (t instanceof Cloneable) {
	        return ObjectUtils.clone(t);
        } else {
	        throw new UnsupportedOperationException("the parameter type(" + t.getClass() +  ")must be subclass of Serializable or Cloneable");
        }
    }

}
