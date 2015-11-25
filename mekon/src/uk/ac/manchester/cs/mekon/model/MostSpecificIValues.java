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

import uk.ac.manchester.cs.mekon.model.util.*;

/**
 * @author Colin Puleston
 */
class MostSpecificIValues {

	private Collection<? extends IValue> values;
	private Finder finder;

	private abstract class Finder {

		abstract List<IValue> find();
	}

	private class DefaultFinder extends Finder {

		List<IValue> find() {

			List<IValue> mostSpecifics = new ArrayList<IValue>();

			for (IValue value : values) {

				update(mostSpecifics, value);
			}

			return mostSpecifics;
		}

		private void update(List<IValue> mostSpecifics, IValue newValue) {

			for (IValue value : new ArrayList<IValue>(mostSpecifics)) {

				if (value.subsumes(newValue)) {

					return;
				}

				if (newValue.subsumes(value)) {

					mostSpecifics.remove(value);
				}
			}

			mostSpecifics.add(newValue);
		}
	}

	private class CValuesFinder extends Finder {

		List<IValue> find() {

			return asValues(findCValues());
		}

		private List<CValue<?>> findCValues() {

			return new MostSpecificCValues(valuesAsCValues()).getCurrents();
		}

		private List<CValue<?>> valuesAsCValues() {

			List<CValue<?>> cValues = new ArrayList<CValue<?>>();

			for (IValue value : values) {

				cValues.add((CValue<?>)value);
			}

			return cValues;
		}

		private List<IValue> asValues(List<CValue<?>> cValues) {

			List<IValue> values = new ArrayList<IValue>();

			for (CValue<?> cValue : cValues) {

				values.add((IValue)cValue);
			}

			return values;
		}
	}

	MostSpecificIValues(CValue<?> valueType, Collection<? extends IValue> values) {

		this.values = values;

		finder = getFinder(valueType);
	}

	List<IValue> get() {

		return finder.find();
	}

	private Finder getFinder(CValue<?> valueType) {

		return cValues(valueType) ? new CValuesFinder() : new DefaultFinder();
	}

	private boolean cValues(CValue<?> valueType) {

		return CValue.class.isAssignableFrom(valueType.getValueType());
	}
}
