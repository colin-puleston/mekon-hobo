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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Represents a set of annotations on some concept-level model-entity
 * or on the model itself.
 *
 * @author Colin Puleston
 */
public class CAnnotations {

	static CAnnotations combineAll(
							CAnnotatable target,
							Collection<CAnnotations> sources) {

		CAnnotations combined = new CAnnotations(target);

		for (CAnnotations source : sources) {

			combined.absorb(source);
		}

		return combined;
	}

	private CAnnotatable target;
	private KListMap<Object, Object> annotations = new KListMap<Object, Object>();

	private class Editor implements CAnnotationsEditor {

		public void add(Object key, Object value) {

			annotations.add(key, value);
		}

		public void addAll(Object key, Collection<?> values) {

			annotations.addAll(key, values);
		}

		public void remove(Object key, Object value) {

			annotations.remove(key, value);
		}

		public void removeAll(Object key, Collection<?> values) {

			annotations.removeAll(key, values);
		}

		public void removeAll(Object key) {

			annotations.removeAll(key);
		}

		public void clear() {

			annotations.clear();
		}
	}

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>CAnnotations</code>
	 * containing same set of annotation-values as this one
	 */
	public boolean equals(Object other) {

		if (other instanceof CAnnotations) {

			return annotations.equals(((CAnnotations)other).annotations);
		}

		return false;
	}

	/**
	 * Provides hash-code based on contained set of annotation-values.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return annotations.hashCode();
	}

	/**
	 * Provides the set of keys for current set of annotations.
	 *
	 * @return keys for current annotations
	 */
	public Set<Object> getKeys() {

		return annotations.keySet();
	}

	/**
	 * Retrieves exactly one annotation-value for the specified key.
	 *
	 * @param key Key for which annotation-value is required
	 * @return Relevant annotation-value
	 * @throws KAccessException if not exactly one annotation-value
	 * for specified key
	 */
	public Object getOne(Object key) {

		return getOne(key, false);
	}

	/**
	 * Retrieves exactly one annotation-value for the specified key,
	 * casting it to the specified type.
	 *
	 * @param <T> Generic version of valueType
	 * @param key Key for which annotation-value is required
	 * @param valueType Type to which value should be cast
	 * @return Relevant annotation-value
	 * @throws KAccessException if not exactly one annotation-value
	 * for specified key, or if retrieved annotation-value not of
	 * required type
	 */
	public <T>T getOne(Object key, Class<T> valueType) {

		return castValue(key, getOne(key), valueType);
	}

	/**
	 * Retrieves one or zero annotation-values for the specified key.
	 *
	 * @param key Key for which annotation-value is required
	 * @return Relevant annotation-value, or null if none
	 * @throws KAccessException if more than one annotation-value
	 * for specified key
	 */
	public Object getOneOrNone(Object key) {

		return getOne(key, true);
	}

	/**
	 * Retrieves one or zero annotation-values for the specified key,
	 * casting any retrieved value to the specified type.
	 *
	 * @param <T> Generic version of valueType
	 * @param key Key for which annotation-value is required
	 * @param valueType Type to which value should be cast
	 * @return Relevant annotation-value, or null if none
	 * @throws KAccessException if more than one annotation-value
	 * for specified key, or if retrieved annotation-value not of
	 * required type
	 */
	public <T>T getOneOrNone(Object key, Class<T> valueType) {

		Object value = getOne(key, true);

		return value != null ? castValue(key, value, valueType) : null;
	}

	/**
	 * Retrieves all annotation-values for the specified key.
	 *
	 * @param key Key for which annotation-values are required
	 * @return Relevant set of annotation-values
	 */
	public List<Object> getAll(Object key) {

		return annotations.getList(key);
	}

	/**
	 * Retrieves all annotation-values for the specified key, casting
	 * each retrieved value to the specified type.
	 *
	 * @param <T> Generic version of valueType
	 * @param key Key for which annotation-values are required
	 * @param valueType Type to which values should be cast
	 * @return Relevant set of annotation-values
	 * @throws KAccessException if any retrieved annotation-values not
	 * of required type
	 */
	public <T>List<T> getAll(Object key, Class<T> valueType) {

		List<T> values = new ArrayList<T>();

		for (Object value : getAll(key)) {

			values.add(castValue(key, value, valueType));
		}

		return values;
	}

	CAnnotations(CAnnotatable target) {

		this.target = target;
	}

	CAnnotationsEditor createEditor() {

		return new Editor();
	}

	private void absorb(CAnnotations source) {

		for (Object key : source.getKeys()) {

			List<Object> startValues = getAll(key);

			for (Object value : source.getAll(key)) {

				if (!startValues.contains(value)) {

					annotations.add(key, value);
				}
			}
		}
	}

	private Object getOne(Object key, boolean nullIfNone) {

		List<Object> values = getAll(key);

		if (values.size() == 1) {

			return values.iterator().next();
		}

		if (nullIfNone && values.isEmpty()) {

			return null;
		}

		throw new KAccessException(
					"Expected exactly one annotation-value for key: "
					+ key + " on entity: " + target
					+ ", found: " + values.size());
	}

	private <T>T castValue(Object key, Object value, Class<T> valueType) {

		if (valueType.isAssignableFrom(value.getClass())) {

			return valueType.cast(value);
		}

		throw new KAccessException(
					"Annotation-value not of expected type for key: "
					+ key + " on entity: " + target
					+ " (expected type: " + valueType
					+ " , found type: " + value.getClass() + ")");
	}
}
