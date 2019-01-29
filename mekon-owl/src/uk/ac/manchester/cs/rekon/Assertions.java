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
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 * @author Colin Puleston
 */
class Assertions {

	private Set<OWLOntology> allOntologies;

	Assertions(OWLOntology rootOntology) {

		allOntologies = rootOntology.getOWLOntologyManager().getOntologies();
	}

	Collection<OWLClass> getAllClasses() {

		Set<OWLClass> all = new HashSet<OWLClass>();

		for (OWLOntology ont : allOntologies) {

			all.addAll(ont.getClassesInSignature());
		}

		return all;
	}

	Collection<OWLObjectProperty> getAllObjectProperties() {

		Set<OWLObjectProperty> all = new HashSet<OWLObjectProperty>();

		for (OWLOntology ont : allOntologies) {

			all.addAll(ont.getObjectPropertiesInSignature());
		}

		return all;
	}

	Collection<OWLDataProperty> getAllDataProperties() {

		Set<OWLDataProperty> all = new HashSet<OWLDataProperty>();

		for (OWLOntology ont : allOntologies) {

			all.addAll(ont.getDataPropertiesInSignature());
		}

		return all;
	}

	Collection<OWLClassExpression> getDistictEquivalents(OWLClass entity) {

		Collection<OWLClassExpression> equivs = getAllEquivalents(entity);

		equivs.remove(entity);

		return equivs;
	}

	Collection<OWLObjectPropertyExpression> getDistictEquivalents(OWLObjectProperty entity) {

		Collection<OWLObjectPropertyExpression> equivs = getAllEquivalents(entity);

		equivs.remove(entity);

		return equivs;
	}

	Collection<OWLDataPropertyExpression> getDistictEquivalents(OWLDataProperty entity) {

		Collection<OWLDataPropertyExpression> equivs = getAllEquivalents(entity);

		equivs.remove(entity);

		return equivs;
	}

	Collection<OWLClassExpression> getSupers(OWLClass entity) {

		return EntitySearcher.getSuperClasses(entity, allOntologies);
	}

	Collection<OWLObjectPropertyExpression> getSupers(OWLObjectProperty entity) {

		return EntitySearcher.getSuperProperties(entity, allOntologies);
	}

	Collection<OWLDataPropertyExpression> getSupers(OWLDataProperty entity) {

		return EntitySearcher.getSuperProperties(entity, allOntologies);
	}

	private Collection<OWLClassExpression> getAllEquivalents(OWLClass entity) {

		return EntitySearcher.getEquivalentClasses(entity, allOntologies);
	}

	private Collection<OWLObjectPropertyExpression> getAllEquivalents(OWLObjectProperty entity) {

		return EntitySearcher.getEquivalentProperties(entity, allOntologies);
	}

	private Collection<OWLDataPropertyExpression> getAllEquivalents(OWLDataProperty entity) {

		return EntitySearcher.getEquivalentProperties(entity, allOntologies);
	}
}
