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

package uk.ac.manchester.cs.mekon.owl.build;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.*;
import org.semanticweb.owlapi.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class OBNumbers {

	private OModel model;

	private Set<TypeNumberCreator<?>> creators = new HashSet<TypeNumberCreator<?>>();

	private class NumberExtractor {

		private Set<OWLClass> visited = new HashSet<OWLClass>();

		OBNumber checkExtract(OWLClass concept) {

			Set<OWLClass> supers = new HashSet<OWLClass>();

			for (OWLClassExpression sup : model.getAssertedSupers(concept)) {

				if (sup instanceof OWLDataSomeValuesFrom) {

					return checkCreateNumber((OWLDataSomeValuesFrom)sup);
				}

				if (sup instanceof OWLClass) {

					supers.add((OWLClass)sup);
				}
			}

			return checkExtractFromAny(supers);
		}

		private OBNumber checkExtractFromAny(Set<OWLClass> concepts) {

			for (OWLClass concept : concepts) {

				if (visited.add(concept)) {

					OBNumber num = checkExtract(concept);

					if (num != null) {

						return num;
					}
				}
			}

			return null;
		}
	}

	private abstract class TypeNumberCreator<N extends Number> {

		TypeNumberCreator() {

			creators.add(this);
		}

		boolean createsFor(OWLDatatype datatype) {

			OWL2Datatype builtIn = datatype.getBuiltInDatatype();

			return getBuiltInDatatypes().contains(builtIn);
		}

		abstract List<OWL2Datatype> getBuiltInDatatypes();

		OBNumber create(OWLDatatypeRestriction restriction) {

			return new OBNumber(createDef(restriction));
		}

		OBNumber createUnconstrained() {

			return new OBNumber(getUnconstrainedRange());
		}

		abstract N parseValue(String value);

		abstract CNumber getUnconstrainedRange();

		abstract CNumber createRange(N min, N max);

		private CNumber createDef(OWLDatatypeRestriction restriction) {

			N min = getValueOrNull(restriction, OWLFacet.MIN_INCLUSIVE);
			N max = getValueOrNull(restriction, OWLFacet.MAX_INCLUSIVE);

			return createRange(min, max);
		}

		private N getValueOrNull(OWLDatatypeRestriction restriction, OWLFacet facet) {

			for (OWLFacetRestriction fRes : OWLAPIVersion.getFacetRestrictions(restriction)) {

				if (fRes.getFacet() == facet) {

					return parseValue(fRes.getFacetValue().getLiteral());
				}
			}

			return null;
		}
	}

	private class IntegerCreator extends TypeNumberCreator<Integer> {

		List<OWL2Datatype> getBuiltInDatatypes() {

			return Arrays.asList(OWL2Datatype.XSD_INTEGER, OWL2Datatype.XSD_INT);
		}

		Integer parseValue(String value) {

			return Integer.parseInt(value);
		}

		CNumber getUnconstrainedRange() {

			return CNumber.INTEGER;
		}

		CNumber createRange(Integer min, Integer max) {

			return CNumber.range(min, max);
		}
	}

	private class LongCreator extends TypeNumberCreator<Long> {

		List<OWL2Datatype> getBuiltInDatatypes() {

			return Arrays.asList(OWL2Datatype.XSD_LONG);
		}

		Long parseValue(String value) {

			return Long.parseLong(value);
		}

		CNumber getUnconstrainedRange() {

			return CNumber.LONG;
		}

		CNumber createRange(Long min, Long max) {

			return CNumber.range(min, max);
		}
	}

	private class FloatCreator extends TypeNumberCreator<Float> {

		List<OWL2Datatype> getBuiltInDatatypes() {

			return Arrays.asList(OWL2Datatype.XSD_FLOAT);
		}

		Float parseValue(String value) {

			return Float.parseFloat(value);
		}

		CNumber getUnconstrainedRange() {

			return CNumber.FLOAT;
		}

		CNumber createRange(Float min, Float max) {

			return CNumber.range(min, max);
		}
	}

	private class DoubleCreator extends TypeNumberCreator<Double> {

		List<OWL2Datatype> getBuiltInDatatypes() {

			return Arrays.asList(OWL2Datatype.XSD_DOUBLE);
		}

		Double parseValue(String value) {

			return Double.parseDouble(value);
		}

		CNumber getUnconstrainedRange() {

			return CNumber.DOUBLE;
		}

		CNumber createRange(Double min, Double max) {

			return CNumber.range(min, max);
		}
	}

	OBNumbers(OModel model) {

		this.model = model;

		new IntegerCreator();
		new LongCreator();
		new FloatCreator();
		new DoubleCreator();
	}

	OBNumber checkExtractNumber(OWLClass concept) {

		return new NumberExtractor().checkExtract(concept);
	}

	OBNumber checkCreateNumber(OWLDataRange range) {

		if (range instanceof OWLDatatype) {

			return checkCreateNumber(range.asOWLDatatype());
		}

		if (range instanceof OWLDatatypeRestriction) {

			return checkCreateNumber((OWLDatatypeRestriction)range);
		}

		return null;
	}

	private OBNumber checkCreateNumber(OWLDataSomeValuesFrom restriction) {

		if (model.indirectNumericProperty(restriction.getProperty())) {

			return checkCreateNumber(restriction.getFiller());
		}

		return null;
	}

	private OBNumber checkCreateNumber(OWLDatatypeRestriction restriction) {

		OWLDatatype datatype = restriction.getDatatype();
		TypeNumberCreator<?> creator = lookForCreator(datatype);

		return creator != null ? creator.create(restriction) : null;
	}

	private OBNumber checkCreateNumber(OWLDatatype datatype) {

		TypeNumberCreator<?> creator = lookForCreator(datatype);

		return creator != null ? creator.createUnconstrained() : null;
	}

	private TypeNumberCreator<?> lookForCreator(OWLDatatype datatype) {

		for (TypeNumberCreator<?> creator : creators) {

			if (creator.createsFor(datatype)) {

				return creator;
			}
		}

		return null;
	}
}
