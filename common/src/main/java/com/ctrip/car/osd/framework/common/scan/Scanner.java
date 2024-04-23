package com.ctrip.car.osd.framework.common.scan;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.AbstractScanner;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

public class Scanner {

	private String basePackage;

	public Scanner(String basePackage) {
		super();
		this.basePackage = basePackage;
	}

	public Set<Class<?>> scan(Predicate<String> predicate) {
		return doScan(predicate);
	}

	public Set<Class<?>> scanWith(final String packageNameSuffix, final String classNameSuffix) {
		return scan(new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input != null && !input.isEmpty() && input.toLowerCase().endsWith(classNameSuffix)
						&& input.substring(0, input.lastIndexOf(".")).endsWith(packageNameSuffix);
			}
		});
	}

	public Set<Class<?>> scan(Class superClass) {
		Reflections reflections = new Reflections(this.basePackage);
		return reflections.getSubTypesOf(superClass);
	}

	public Reflections getReflections() {
		return new Reflections(this.basePackage);
	}

	public Set<Class<?>> scanWithAnnotated(final Class<? extends Annotation> annotation) {
		Reflections reflections = new Reflections(this.basePackage);
		return reflections.getTypesAnnotatedWith(annotation);
	}

	private Set<Class<?>> doScan(Predicate<String> predicate) {
		Reflections reflections = new Reflections(this.basePackage, new ClassPredicateScanner(predicate));
		Iterable<String> types = reflections.getStore().getAll(ClassPredicateScanner.class.getSimpleName(),
				Arrays.asList(predicate.getClass().getName()));
		return Sets.newHashSet(ReflectionUtils.forNames(types, reflections.getConfiguration().getClassLoaders()));
	}

	class ClassPredicateScanner extends AbstractScanner {

		private Predicate<String> predicate;

		public ClassPredicateScanner(Predicate<String> predicate) {
			super();
			this.predicate = predicate;
		}

		@Override
		public void scan(Object cls) {
			String className = getMetadataAdapter().getClassName(cls);
			if (predicate.apply(className)) {
				getStore().put(predicate.getClass().getName(), className);
			}
		}
	}

}
