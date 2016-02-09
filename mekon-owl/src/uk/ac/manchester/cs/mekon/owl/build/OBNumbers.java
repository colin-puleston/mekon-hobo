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

/**
 * @author Colin Puleston
 */
class OBNumbers {

	private OModel model;

	private Set<TypeNumberCreator<?>> creators = new HashSet<TypeNumberCreator<?>>();

	private class SupersProcessor extends OWLObjectVisitorExAdapter<OBNumber> {

		private Set<OWLClass> concepts = new HashSet<OWLClass>();

		public OBNumber visit(OWLClass e) {

			concepts.add(e);

			return null;
		}

		public OBNumber visit(OWLDataSomeValuesFrom e) {

			return checkCreateNumber(e.getProperty(), e.getFiller());
		}

		public OBNumber visit(OWLDataExactCardinality e) {

			return checkCreateNumber(e.getProperty(), e.getFiller());
		}

		protected OBNumber getDefaultReturnValue(OWLObject e) {

			return null;
		}

		Set<OWLClass> getConcepts() {

			return concepts;
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

			for (OWLFacetRestriction facetRest : restriction.getFacetRestrictions()) {

				if (facetRest.getFacet() == facet) {

					return parseValue(facetRest.getFacetValue().getLiteral());
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

	OBNumbers(OModel model) {

		this.model = model;

		new IntegerCreator();
		new FloatCreator();
	}

	OBNumber checkExtractNumber(OWLClass concept) {

		SupersProcessor supersProcessor = new SupersProcessor();

		for (OWLClassExpression sup : model.getAssertedSupers(concept)) {

			OBNumber num = sup.accept(supersProcessor);

			if (num != null) {

				return num;
			}
		}

		return checkExtractNumber(supersProcessor.getConcepts());
	}

	OBNumber checkCreateNumber(OWLDataRange range) {

		if (range.isDatatype()) {

			return checkCreateNumber(range.asOWLDatatype());
		}

		if (range instanceof OWLDatatypeRestriction) {

			return checkCreateNumber((OWLDatatypeRestriction)range);
		}

		return null;
	}

	OBNumber checkCreateNumber(OWLDatatype datatype) {

		TypeNumberCreator<?> creator = lookForCreator(datatype);

		return creator != null ? creator.createUnconstrained() : null;
	}

	private OBNumber checkExtractNumber(Set<OWLClass> concepts) {

		for (OWLClass concept : concepts) {

			OBNumber num = checkExtractNumber(concept);

			if (num != null) {

				return num;
			}
		}

		return null;
	}

	private OBNumber checkCreateNumber(
						OWLDataPropertyExpression property,
						OWLDataRange range) {

		return model.isIndirectNumericProperty(property)
				? checkCreateNumber(range)
				: null;
	}

	private OBNumber checkCreateNumber(OWLDatatypeRestriction restriction) {

		OWLDatatype datatype = restriction.getDatatype();
		TypeNumberCreator<?> creator = lookForCreator(datatype);

		return creator != null ? creator.create(restriction) : null;
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
