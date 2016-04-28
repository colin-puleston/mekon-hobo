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

/**
 * Responsible for creating and serialisation of {@link RValueType}
 * objects. The parameterless constructor and relevant sets of "get"
 * and "set" methods are designed to enable JSON serialisation.
 *
 * @author Colin Puleston
 */
public class RValueTypeSpec {

	private RValueCategory category;
	private Boolean editable;
	private RCardinality cardinality;
	private RConceptSpec rootConcept;
	private RNumberRangeSpec numberRange;

	/**
	 * Constructor.
	 */
	public RValueTypeSpec() {
	}

	/**
	 * Sets value of category.
	 *
	 * @param category Value to set
	 */
	public void setCategory(RValueCategory category) {

		this.category = category;
	}

	/**
	 * Sets value of editable.
	 *
	 * @param editable Value to set
	 */
	public void setEditable(Boolean editable) {

		this.editable = editable;
	}

	/**
	 * Sets value of cardinality.
	 *
	 * @param cardinality Value to set
	 */
	public void setCardinality(RCardinality cardinality) {

		this.cardinality = cardinality;
	}

	/**
	 * Sets value of root-concept.
	 *
	 * @param rootConcept Value to set
	 */
	public void setRootConcept(RConceptSpec rootConcept) {

		this.rootConcept = rootConcept;
	}

	/**
	 * Sets value of number-range.
	 *
	 * @param numberRange Value to set
	 */
	public void setNumberRange(RNumberRangeSpec numberRange) {

		this.numberRange = numberRange;
	}

	/**
	 * Gets value of category.
	 *
	 * @return Relevant value
	 */
	public RValueCategory getCategory() {

		return category;
	}

	/**
	 * Gets value of editable.
	 *
	 * @return Relevant value
	 */
	public Boolean getEditable() {

		return editable;
	}

	/**
	 * Gets value of cardinality.
	 *
	 * @return Relevant value
	 */
	public RCardinality getCardinality() {

		return cardinality;
	}

	/**
	 * Gets value of root-concept.
	 *
	 * @return Relevant value
	 */
	public RConceptSpec getRootConcept() {

		return rootConcept;
	}

	/**
	 * Gets value of number-range.
	 *
	 * @return Relevant value
	 */
	public RNumberRangeSpec getNumberRange() {

		return numberRange;
	}

	void configureConceptDefinedValueType(RValueType valueType) {

		valueType.configure(rootConcept.create(), cardinality);
	}

	void configureNumberValueType(RValueType valueType) {

		valueType.configure(numberRange.create());
	}

	RValueType create() {

		RValueType valueType = new RValueType(category, editable);

		category.configureValueType(valueType, this);

		return valueType;
	}
}
