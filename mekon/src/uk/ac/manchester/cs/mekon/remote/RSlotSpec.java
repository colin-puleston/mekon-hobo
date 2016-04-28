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

package uk.ac.manchester.cs.mekon.remote;

import java.util.*;

/**
 * Responsible for creating and serialisation of {@link RSlot}
 * objects. The parameterless constructor and relevant sets of "get"
 * and "set" methods are designed to enable JSON serialisation.
 *
 * @author Colin Puleston
 */
public class RSlotSpec {

	private RIdentitySpec type;
	private RValueTypeSpec valueType;

	private List<RValueSpec> values = new ArrayList<RValueSpec>();

	/**
	 * Constructor.
	 */
	public RSlotSpec() {
	}

	/**
	 * Sets value of type-spec.
	 *
	 * @param type Value to set
	 */
	public void setType(RIdentitySpec type) {

		this.type = type;
	}

	/**
	 * Sets value of value-type-spec.
	 *
	 * @param valueType Value to set
	 */
	public void setValueType(RValueTypeSpec valueType) {

		this.valueType = valueType;
	}

	/**
	 * Sets value of value-specs.
	 *
	 * @param values Value to set
	 */
	public void setValues(List<RValueSpec> values) {

		this.values.clear();
		this.values.addAll(values);
	}

	/**
	 * Gets value of type-spec.
	 *
	 * @return Relevant value
	 */
	public RIdentitySpec getType() {

		return type;
	}

	/**
	 * Gets value of value-type-spec.
	 *
	 * @return Relevant value
	 */
	public RValueTypeSpec getValueType() {

		return valueType;
	}

	/**
	 * Gets value of value-specs.
	 *
	 * @return Relevant value
	 */
	public List<RValueSpec> getValues() {

		return new ArrayList<RValueSpec>(values);
	}

	void addValue(RValueSpec value) {

		values.add(value);
	}

	RSlot create() {

		RSlot slot = new RSlot(type.create(), valueType.create());

		for (RValueSpec value : values) {

			slot.addValueNoChecks(value.create());
		}

		return slot;
	}
}
