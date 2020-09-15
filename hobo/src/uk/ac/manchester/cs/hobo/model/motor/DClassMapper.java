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
 * The generated external-identifier for a mapped class will be
 * the simple class-name, optionally with a specified general
 * prefix string attached. The generated external-identifier for
 * a mapped field will consist of the field-name, optionally
 * preceded by either or both of (1) a specified general prefix
 * string, and (2) the simple class-name of the class on which
 * the field is located followed by a suitable separator string.
 *
 * @author Colin Puleston
 */
public class DClassMapper {

	private Set<String> mappingPackages = new HashSet<String>();

	private String classIdsPrefix = "";
	private String fieldIdsPrefix = "";

	private String compoundFieldIdSeparator = null;

	private class MapGenerator {

		private Class<? extends DObject> dClass;

		MapGenerator(Class<? extends DObject> dClass) {

			this.dClass = dClass;
		}

		DClassMap generate() {

			DClassMap classMap = new DClassMap(dClass, createClassId());

			for (Field field : dClass.getDeclaredFields()) {

				if (mappableField(field)) {

					addFieldMap(classMap, field.getName());
				}
			}

			return classMap;
		}

		private void addFieldMap(DClassMap classMap, String fieldName) {

			classMap.addFieldMap(fieldName, createFieldId(fieldName));
		}

		private String createClassId() {

			return classIdsPrefix + dClass.getSimpleName();
		}

		private String createFieldId(String fieldName) {

			StringBuilder id = new StringBuilder();

			id.append(fieldIdsPrefix);

			if (compoundFieldIdSeparator != null) {

				id.append(dClass.getSimpleName() + compoundFieldIdSeparator);
			}

			id.append(fieldName);

			return id.toString();
		}
	}

	/**
	 * Sets a prefix to form part of the external-identififiers
	 * for both the mapped classes and mapped fields.
	 *
	 * @param prefix Prefix for external-identifiers of mapped
	 * classes and fields
	 */
	public void setIdsPrefix(String prefix) {

		classIdsPrefix = prefix;
		fieldIdsPrefix = prefix;
	}

	/**
	 * Sets a prefix to form part of the external-identififiers
	 * for the mapped classes.
	 *
	 * @param prefix Prefix for external-identifiers of mapped
	 * classes
	 */
	public void setClassIdsPrefix(String prefix) {

		classIdsPrefix = prefix;
	}

	/**
	 * Sets a prefix to form part of the external-identififiers
	 * for the mapped fields.
	 *
	 * @param prefix Prefix for external-identifiers of mapped
	 * fields
	 */
	public void setFieldIdsPrefix(String prefix) {

		fieldIdsPrefix = prefix;
	}

	/**
	 * Used to specify that the main body of the generated
	 * external-identifiers for mapped fields will be compound,
	 * consisting of the field-name, preceded by the relevant
	 * simple class-name, plus the specified separater string.
	 *
	 * @param separator Separater string to use in creating
	 * compound external-identifiers for mapped fields
	 */
	public void setCompoundFieldIds(String separator) {

		compoundFieldIdSeparator = separator;
	}

	DClassMapper(Collection<String> mappingPackages) {

		this.mappingPackages.addAll(mappingPackages);
	}

	DClassMap checkGenerateMap(Class<? extends DObject> dClass) {

		return mappingSubject(dClass) ? new MapGenerator(dClass).generate() : null;
	}

	private boolean mappingSubject(Class<? extends DObject> dClass) {

		return mappingPackages.contains(dClass.getPackage().getName());
	}

	private boolean mappableField(Field field) {

		int m = field.getModifiers();

		return !Modifier.isStatic(m) && Modifier.isPublic(m) && Modifier.isFinal(m);
	}
}
