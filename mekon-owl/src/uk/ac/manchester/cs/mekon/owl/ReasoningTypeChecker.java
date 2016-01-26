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

package uk.ac.manchester.cs.mekon.owl;

import java.util.*;

import org.semanticweb.owlapi.model.*;

/**
 * @author Colin Puleston
 */
abstract class ReasoningTypeChecker {

	private Set<Class<? extends OWLAxiom>> axiomTypes
					= new HashSet<Class<? extends OWLAxiom>>();

	private Set<Class<? extends OWLClassExpression>> classExprTypes
					= new HashSet<Class<? extends OWLClassExpression>>();

	private Set<AxiomFilter<?>> axiomFilters = new HashSet<AxiomFilter<?>>();

	private abstract class AxiomFilter<A extends OWLAxiom> {

		boolean filterFor(OWLAxiom axiom) {

			return getAxiomType().isAssignableFrom(axiom.getClass());
		}

		boolean pass(OWLAxiom axiom) {

			return !filterOut(getAxiomType().cast(axiom));
		}

		abstract Class<A> getAxiomType();

		abstract boolean filterOut(A axiom);
	}

	private class SubClassAxiomFilter
					extends
						AxiomFilter<OWLSubClassOfAxiom> {

		Class<OWLSubClassOfAxiom> getAxiomType() {

			return OWLSubClassOfAxiom.class;
		}

		boolean filterOut(OWLSubClassOfAxiom axiom) {

			return axiom.getSubClass().isBottomEntity()
					|| axiom.getSuperClass().isTopEntity();
		}
	}

	private abstract class SubPropertyAxiomFilter
								<A extends OWLSubPropertyAxiom<?>>
								extends AxiomFilter<A> {

		boolean filterOut(A axiom) {

			return axiom.getSubProperty().isBottomEntity()
					|| axiom.getSuperProperty().isTopEntity();
		}
	}

	private class SubObjectPropertyAxiomFilter
					extends
						SubPropertyAxiomFilter<OWLSubObjectPropertyOfAxiom> {

		Class<OWLSubObjectPropertyOfAxiom> getAxiomType() {

			return OWLSubObjectPropertyOfAxiom.class;
		}
	}

	private class SubDataPropertyAxiomFilter
					extends
						SubPropertyAxiomFilter<OWLSubDataPropertyOfAxiom> {

		Class<OWLSubDataPropertyOfAxiom> getAxiomType() {

			return OWLSubDataPropertyOfAxiom.class;
		}
	}

	ReasoningTypeChecker() {

		addBasicValidTypes();
		addAxiomFilters();
	}

	void addValidAxiomType(Class<? extends OWLAxiom> type) {

		axiomTypes.add(type);
	}

	void addValidClassExprType(Class<? extends OWLClassExpression> type) {

		classExprTypes.add(type);
	}

	boolean valid(OWLAxiom axiom) {

		return validAxiomType(axiom)
				&& passesAxiomFilters(axiom)
				&& validClassExprTypes(axiom);
	}

	private void addBasicValidTypes() {

		addValidAxiomType(OWLDeclarationAxiom.class);
		addValidAxiomType(OWLSubClassOfAxiom.class);
		addValidAxiomType(OWLClassAssertionAxiom.class);
		addValidAxiomType(OWLObjectPropertyAssertionAxiom.class);
		addValidAxiomType(OWLDataPropertyAssertionAxiom.class);

		addValidClassExprType(OWLClass.class);
	}

	private void addAxiomFilters() {

		axiomFilters.add(new SubClassAxiomFilter());
		axiomFilters.add(new SubObjectPropertyAxiomFilter());
		axiomFilters.add(new SubDataPropertyAxiomFilter());
	}

	private boolean validAxiomType(OWLAxiom axiom) {

		return validType(axiomTypes, axiom);
	}

	private boolean validClassExprTypes(OWLAxiom axiom) {

		return validTypes(classExprTypes, axiom.getNestedClassExpressions());
	}

	private <T>boolean validTypes(Set<Class<? extends T>> valids, Set<T> tests) {

		for (T test : tests) {

			if (!validType(valids, test)) {

				return false;
			}
		}

		return true;
	}

	private <T>boolean validType(Set<Class<? extends T>> valids, T test) {

		for (Class<? extends T> valid : valids) {

			if (valid.isAssignableFrom(test.getClass())) {

				return true;
			}
		}

		return false;
	}

	private boolean passesAxiomFilters(OWLAxiom axiom) {

		for (AxiomFilter<?> filter : axiomFilters) {

			if (filter.filterFor(axiom)) {

				return filter.pass(axiom);
			}
		}

		return true;
	}
}
