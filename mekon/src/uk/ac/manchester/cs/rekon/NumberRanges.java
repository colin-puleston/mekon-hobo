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
class NumberRanges {

	private Set<TypeHandler<?>> typeHandlers = new HashSet<TypeHandler<?>>();

	private boolean cacheAdditionsEnabled = true;

	private abstract class TypeHandler<N extends Number> {

		private Map<OWLDatatypeRestriction, NumberRange> cache
					= new HashMap<OWLDatatypeRestriction, NumberRange>();

		TypeHandler() {

			typeHandlers.add(this);
		}

		boolean handlesType(OWLDatatype datatype) {

			return getBuiltInDatatypes().contains(datatype.getBuiltInDatatype());
		}

		abstract List<OWL2Datatype> getBuiltInDatatypes();

		NumberRange toRange(OWLDatatypeRestriction res) {

			NumberRange r = cache.get(res);

			if (r == null) {

				r = create(res);

				if (cacheAdditionsEnabled) {

					cache.put(res, r);
				}
			}

			return r;
		}

		abstract NumberRange getUnconstrained();

		abstract NumberRange create(N min, N max);

		abstract N parseValue(String value);

		private NumberRange create(OWLDatatypeRestriction res) {

			N min = getValueOrNull(res, OWLFacet.MIN_INCLUSIVE);
			N max = getValueOrNull(res, OWLFacet.MAX_INCLUSIVE);

			return create(min, max);
		}

		private N getValueOrNull(OWLDatatypeRestriction res, OWLFacet facet) {

			for (OWLFacetRestriction fres : res.getFacetRestrictions()) {

				if (fres.getFacet() == facet) {

					return parseValue(fres.getFacetValue().getLiteral());
				}
			}

			return null;
		}
	}

	private class IntegerRanges extends TypeHandler<Integer> {

		List<OWL2Datatype> getBuiltInDatatypes() {

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

	private class LongRanges extends TypeHandler<Long> {

		List<OWL2Datatype> getBuiltInDatatypes() {

			return Arrays.asList(OWL2Datatype.XSD_LONG);
		}

		NumberRange getUnconstrained() {

			return LongRange.UNCONSTRAINED;
		}

		NumberRange create(Long min, Long max) {

			return new LongRange(min, max);
		}

		Long parseValue(String value) {

			return Long.parseLong(value);
		}
	}

	private class FloatRanges extends TypeHandler<Float> {

		List<OWL2Datatype> getBuiltInDatatypes() {

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

	private class DoubleRanges extends TypeHandler<Double> {

		List<OWL2Datatype> getBuiltInDatatypes() {

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

	NumberRanges() {

		new IntegerRanges();
		new LongRanges();
		new FloatRanges();
		new DoubleRanges();
	}

	void setCacheAdditionsEnabled(boolean value) {

		cacheAdditionsEnabled = value;
	}

	NumberRange toRange(OWLDataRange range) {

		if (range instanceof OWLDatatype) {

			return toRange(range.asOWLDatatype());
		}

		if (range instanceof OWLDatatypeRestriction) {

			return toRange((OWLDatatypeRestriction)range);
		}

		return null;
	}

	private NumberRange toRange(OWLDatatypeRestriction res) {

		OWLDatatype datatype = res.getDatatype();
		TypeHandler<?> handler = lookFors(datatype);

		return handler != null ? handler.toRange(res) : null;
	}

	private NumberRange toRange(OWLDatatype datatype) {

		TypeHandler<?> handler = lookFors(datatype);

		return handler != null ? handler.getUnconstrained() : null;
	}

	private TypeHandler<?> lookFors(OWLDatatype datatype) {

		for (TypeHandler<?> handler : typeHandlers) {

			if (handler.handlesType(datatype)) {

				return handler;
			}
		}

		return null;
	}
}
