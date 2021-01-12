/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.hobo.model.motor;

import java.util.*;
import java.lang.reflect.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * Generates a set of mappings between the Object Model (OM)
 * classes ({@link DObject}-derived classes) from a set of one
 * or more packages, plus any OM fields attached to those classes,
 * and entities in one or more external sources.
 * <p>
 * This is an abstract class whose concrete extensions will implement
 * methods to generate external-identifiers for the classes and
 * fields that are being mapped.
 *
 * @author Colin Puleston
 */
public abstract class DClassMapper {

	private Set<PackageMapper> packageMappers = new HashSet<PackageMapper>();

	private abstract class PackageMapper {

		final String basePackageName;

		PackageMapper(String basePackageName) {

			this.basePackageName = basePackageName;

			packageMappers.add(this);
		}

		abstract boolean mappingSubject(String packageName);
	}

	private class SinglePackageMapper extends PackageMapper {

		SinglePackageMapper(String packageName) {

			super(packageName);
		}

		boolean mappingSubject(String packageName) {

			return packageName.equals(basePackageName);
		}
	}

	private class PackageGroupMapper extends PackageMapper {

		PackageGroupMapper(String basePackageName) {

			super(basePackageName);
		}

		boolean mappingSubject(String packageName) {

			return packageName.startsWith(basePackageName);
		}
	}

	/**
	 * Adds an OM package for whose classes mappings are to be
	 * generated
	 *
	 * @param packageName Name of relevant OM package
	 */
	public void addPackage(String packageName) {

		new SinglePackageMapper(packageName);
	}

	/**
	 * Adds a group of OM packages for whose classes mappings are to be
	 * generated.
	 *
	 * @param basePackageName Base-name of relevant OM packages
	 */
	public void addPackageGroup(String basePackageName) {

		new PackageGroupMapper(basePackageName);
	}

	/**
	 * Provides the external-identifier that is to be mapped to a
	 * specified OM class.
	 *
	 * @param dClass OM class for which identifier is required
	 * @return external-identifier to be mapped to specified OM class
	 */
	protected abstract String getClassExternalId(Class<? extends DObject> dClass);

	/**
	 * Provides the external-identifier that is to be mapped to a
	 * specified OM field.
	 *
	 * @param dClass OM class containing field for which identifier is
	 * required
	 * @param fieldName Name of OM field for which identifier is required
	 * @return external-identifier to be mapped to specified OM field
	 */
	protected abstract String getFieldExternalId(
								Class<? extends DObject> dClass,
								String fieldName);

	DClassMap checkGenerateMap(Class<? extends DObject> dClass) {

		return mappingSubject(dClass) ? generateMap(dClass) : null;
	}

	private Class<? extends DObject> dClass;

	private DClassMap generateMap(Class<? extends DObject> dClass) {

		DClassMap classMap = new DClassMap(dClass, getClassExternalId(dClass));

		for (Field field : dClass.getDeclaredFields()) {

			if (mappableField(field)) {

				String fieldName = field.getName();

				classMap.addFieldMap(fieldName, getFieldExternalId(dClass, fieldName));
			}
		}

		return classMap;
	}

	private boolean mappingSubject(Class<? extends DObject> dClass) {

		String packageName = dClass.getPackage().getName();

		for (PackageMapper packageMapper : packageMappers) {

			if (packageMapper.mappingSubject(packageName)) {

				return true;
			}
		}

		return false;
	}

	private boolean mappableField(Field field) {

		int m = field.getModifiers();

		return !Modifier.isStatic(m) && Modifier.isPublic(m) && Modifier.isFinal(m);
	}
}
