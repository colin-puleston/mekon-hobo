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

package uk.ac.manchester.cs.rekon;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.*;

/**
 * @author Colin Puleston
 */
class DataTypes {

	private Set<TypeHandler> typeHandlers = new HashSet<TypeHandler>();

	private boolean cacheAdditionsEnabled = true;

	private abstract class TypeHandler {

		TypeHandler() {

			typeHandlers.add(this);
		}

		boolean handlesType(OWLDatatype type) {

			return getBuiltInTypes().contains(type.getBuiltInDatatype());
		}

		abstract List<OWL2Datatype> getBuiltInTypes();

		abstract Expression getUnconstrained();

		abstract Expression getFor(OWLDatatypeRestriction source);
	}

	private class Booleans extends TypeHandler {

		List<OWL2Datatype> getBuiltInTypes() {

			return Arrays.asList(OWL2Datatype.XSD_BOOLEAN);
		}

		Expression getUnconstrained() {

			return BooleanValue.BOOLEAN;
		}

		Expression getFor(OWLDatatypeRestriction source) {

			return null;
		}
	}

	private abstract class NumberRangeHandler<N extends Number> extends TypeHandler {

		private Map<OWLDatatypeRestriction, NumberRange> cache
					= new HashMap<OWLDatatypeRestriction, NumberRange>();

		Expression getFor(OWLDatatypeRestriction source) {

			NumberRange r = cache.get(source);

			if (r == null) {

				r = create(source);

				if (cacheAdditionsEnabled) {

					cache.put(source, r);
				}
			}

			return r;
		}

		abstract NumberRange create(N min, N max);

		abstract N parseValue(String value);

		private NumberRange create(OWLDatatypeRestriction source) {

			N min = getLimit(source, OWLFacet.MIN_INCLUSIVE);
			N max = getLimit(source, OWLFacet.MAX_INCLUSIVE);

			return create(min, max);
		}

		private N getLimit(OWLDatatypeRestriction source, OWLFacet facet) {

			for (OWLFacetRestriction fr : source.getFacetRestrictions()) {

				if (fr.getFacet() == facet) {

					return parseValue(fr.getFacetValue().getLiteral());
				}
			}

			return null;
		}
	}

	private class IntegerRanges extends NumberRangeHandler<Integer> {

		List<OWL2Datatype> getBuiltInTypes() {

			return Arrays.asList(OWL2Datatype.XSD_INTEGER, OWL2Datatype.XSD_INT);
		}

		NumberRange getUnconstrained() {

			return IntegerRange.UNCONSTRAINED;
		}

		NumberRange create(Integer min, Integer max) {

			return new IntegerRange(min, max);
		}

		Integer parseValue(String value) {

			return Integer.parseInt(value);
		}
	}

	private class FloatRanges extends NumberRangeHandler<Float> {

		List<OWL2Datatype> getBuiltInTypes() {

			return Arrays.asList(OWL2Datatype.XSD_FLOAT);
		}

		NumberRange getUnconstrained() {

			return FloatRange.UNCONSTRAINED;
		}

		NumberRange create(Float min, Float max) {

			return new FloatRange(min, max);
		}

		Float parseValue(String value) {

			return Float.parseFloat(value);
		}
	}

	private class DoubleRanges extends NumberRangeHandler<Double> {

		List<OWL2Datatype> getBuiltInTypes() {

			return Arrays.asList(OWL2Datatype.XSD_DOUBLE);
		}

		NumberRange getUnconstrained() {

			return DoubleRange.UNCONSTRAINED;
		}

		NumberRange create(Double min, Double max) {

			return new DoubleRange(min, max);
		}

		Double parseValue(String value) {

			return Double.parseDouble(value);
		}
	}

	DataTypes() {

		new Booleans();
		new IntegerRanges();
		new FloatRanges();
		new DoubleRanges();
	}

	void setCacheAdditionsEnabled(boolean value) {

		cacheAdditionsEnabled = value;
	}

	Expression getFor(OWLDataRange source) {

		if (source instanceof OWLDatatype) {

			return getFor(source.asOWLDatatype());
		}

		if (source instanceof OWLDatatypeRestriction) {

			return getFor((OWLDatatypeRestriction)source);
		}

		return null;
	}

	private Expression getFor(OWLDatatypeRestriction source) {

		OWLDatatype type = source.getDatatype();
		TypeHandler handler = lookForHandler(type);

		return handler != null ? handler.getFor(source) : null;
	}

	private Expression getFor(OWLDatatype source) {

		TypeHandler handler = lookForHandler(source);

		return handler != null ? handler.getUnconstrained() : null;
	}

	private TypeHandler lookForHandler(OWLDatatype type) {

		for (TypeHandler handler : typeHandlers) {

			if (handler.handlesType(type)) {

				return handler;
			}
		}

		return null;
	}
}
