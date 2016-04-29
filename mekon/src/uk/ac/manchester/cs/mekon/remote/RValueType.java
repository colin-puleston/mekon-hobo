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

import uk.ac.manchester.cs.mekon.*;

/**
 * Represents a value-type for a slot in the remote frames-based
 * representation.
 *
 * @author Colin Puleston
 */
public class RValueType {

	static private final CategoryConfig DEFAULT_CONFIG = new CategoryConfig();

	static private class CategoryConfig {

		RCardinality getCardinality() {

			return RCardinality.SINGLE_VALUE;
		}

		RConcept getRootConcept(RValueCategory category) {

			return checkForIllegalMethodInvocation(category, getRootConceptOrNull());
		}

		RNumber getNumberRange(RValueCategory category) {

			return checkForIllegalMethodInvocation(category, getNumberRangeOrNull());
		}

		RConcept getRootConceptOrNull() {

			return null;
		}

		RNumber getNumberRangeOrNull() {

			return null;
		}

		private <R>R checkForIllegalMethodInvocation(
						RValueCategory category,
						R returnedValue) {

			if (returnedValue != null) {

				return returnedValue;
			}

			throw new KAccessException(
						"Cannot invoke method on value-types of category: "
						+ category);
		}
	}

	private RValueCategory category;
	private boolean editable;
	private CategoryConfig config = DEFAULT_CONFIG;

	private class ConceptDefinedValuesConfig extends CategoryConfig {

		private RConcept rootConcept;
		private RCardinality cardinality;

		ConceptDefinedValuesConfig(RConcept rootConcept, RCardinality cardinality) {

			this.rootConcept = rootConcept;
			this.cardinality = cardinality;
		}

		RCardinality getCardinality() {

			return cardinality;
		}

		RConcept getRootConceptOrNull() {

			return rootConcept;
		}
	}

	private class NumberValuesConfig extends CategoryConfig {

		private RNumber range;

		NumberValuesConfig(RNumber range) {

			this.range = range;
		}

		RNumber getNumberRangeOrNull() {

			return range;
		}
	}

	/**
	 * Specifies the general category of value for the slot.
	 *
	 * @return General category of slot values
	 */
	public RValueCategory getCategory() {

		return category;
	}

	/**
	 * Specifies the client editability of the slot.
	 *
	 * @return True if slot is editable by client
	 */
	public boolean editable() {

		return editable;
	}

	/**
	 * Specifies the cardinality of the slot.
	 *
	 * @return Cardinality of slot
	 */
	public RCardinality getCardinality() {

		return config.getCardinality();
	}

	/**
	 * Specifies the root-concept for slot, for slots of category {@link
	 * RValueCategory.CONCEPT} or {@link RValueCategory.FRAME}.
	 *
	 * @return Root-concept for slot
	 * @throws KAccessException if slot not of applicable category
	 */
	public RConcept getRootConcept() {

		return config.getRootConcept(category);
	}

	/**
	 * Specifies the permitted value-range for slot, for slots of category
	 * {@link RValueCategory.NUMBER}.
	 *
	 * @return Value-range for slot
	 * @throws KAccessException if slot not of applicable category
	 */
	public RNumber getNumberRange() {

		return config.getNumberRange(category);
	}

	RValueType(RValueCategory category, boolean editable) {

		this.category = category;
		this.editable = editable;
	}

	void configure(RConcept rootConcept, RCardinality cardinality) {

		config = new ConceptDefinedValuesConfig(rootConcept, cardinality);
	}

	void configure(RNumber numberRange) {

		config = new NumberValuesConfig(numberRange);
	}

	RValueTypeSpec toSpec() {

		RValueTypeSpec spec = new RValueTypeSpec();

		spec.setCategory(category);
		spec.setEditable(editable);
		spec.setCardinality(getCardinality());
		spec.setRootConcept(getRootConcept().toSpec());
		spec.setNumberRange(getNumberRange().toSpec());

		return spec;
	}
}
