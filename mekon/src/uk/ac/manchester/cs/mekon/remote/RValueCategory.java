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
 * Specifies the general category of value for a particular slot.
 *
 * @author Colin Puleston
 */
public enum RValueCategory {

	/**
	 * Slot has values of type {@link DConceptValue}
	 */
	CONCEPT {

		void configureValueType(RValueType valueType, RValueTypeSpec spec) {

			spec.configureConceptDefinedType(valueType);
		}

		void visitValueType(RValueTypeVisitor visitor, RValueType valueType) {

			visitor.visitConceptType(valueType);
		}
	},

	/**
	 * Slot has values of type {@link DFrameValue}
	 */
	FRAME {

		void configureValueType(RValueType valueType, RValueTypeSpec spec) {

			spec.configureConceptDefinedType(valueType);
		}

		void visitValueType(RValueTypeVisitor visitor, RValueType valueType) {

			visitor.visitFrameType(valueType);
		}
	},

	/**
	 * Slot has values of type {@link RNumber}
	 */
	NUMBER {

		void configureValueType(RValueType valueType, RValueTypeSpec spec) {

			spec.configureNumberType(valueType);
		}

		void visitValueType(RValueTypeVisitor visitor, RValueType valueType) {

			visitor.visitNumberType(valueType);
		}
	},

	/**
	 * Slot has values of type {@link RString}
	 */
	STRING {

		void configureValueType(RValueType valueType, RValueTypeSpec spec) {
		}

		void visitValueType(RValueTypeVisitor visitor, RValueType valueType) {

			visitor.visitStringType(valueType);
		}
	};

	abstract void configureValueType(RValueType valueType, RValueTypeSpec spec);

	abstract void visitValueType(RValueTypeVisitor visitor, RValueType valueType);
}