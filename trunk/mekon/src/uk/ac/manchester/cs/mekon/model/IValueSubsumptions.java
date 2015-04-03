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

/**
 * @author Colin Puleston
 */
class IValueSubsumptions {

	static boolean allSubsumptions(
						Collection<? extends IValue> testSubsumers,
						Collection<? extends IValue> testSubsumeds) {

		for (IValue testSubsumer : testSubsumers) {

			if (!anySubsumptions(testSubsumer, testSubsumeds)) {

				return false;
			}
		}

		return true;
	}

	static boolean subsumption(IValue testSubsumer, IValue testSubsumed) {

		CValue<?> type = testSubsumer.getType();

		return type.valueSubsumption(testSubsumer, testSubsumed);
	}

	static List<IValue> getMostSpecifics(
							Collection<? extends IValue> values,
							CValue<?> valueType) {

		if (CValue.class.isAssignableFrom(valueType.getValueType())) {

			return getMostSpecificCValueValues(asCValues(values));
		}

		return removeDuplicates(values);
	}

	static private boolean anySubsumptions(
							IValue testSubsumer,
							Collection<? extends IValue> testSubsumeds) {

		for (IValue testSubsumed : testSubsumeds) {

			if (subsumption(testSubsumer, testSubsumed)) {

				return true;
			}
		}

		return false;
	}

	static private List<IValue> getMostSpecificCValueValues(List<CValue<?>> valuesAsCValues) {

		List<IValue> mostSpecifics = new ArrayList<IValue>();

		for (CValue<?> valueAsC : getMostSpecificCValues(valuesAsCValues)) {

			mostSpecifics.add((IValue)valueAsC);
		}

		return mostSpecifics;
	}

	static private List<CValue<?>> getMostSpecificCValues(List<CValue<?>> cValues) {

		return new MostSpecificCValues(cValues).getMostSpecifics();
	}

	static private List<IValue> removeDuplicates(Collection<? extends IValue> values) {

		List<IValue> uniqueValues = new ArrayList<IValue>();

		for (IValue value : values) {

			if (!uniqueValues.contains(value)) {

				uniqueValues.add(value);
			}
		}

		return uniqueValues;
	}

	static private List<CValue<?>> asCValues(Collection<? extends IValue> values) {

		List<CValue<?>> cValues = new ArrayList<CValue<?>>();

		for (IValue value : values) {

			cValues.add((CValue<?>)value);
		}

		return cValues;
	}
}
