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

package uk.ac.manchester.cs.mekon.owl.reason;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;
import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * Extension of {@link ORMatcher} that represents the instances
 * as appropriately-defined OWL classes, which are added to an
 * in-memory version of the ontology, and that represents queries
 * as anonymous class-expressions.
 *
 * @author Colin Puleston
 */
public class ORConceptsMatcher extends ORMatcher {

	/**
	 * Constructs matcher for specified model.
	 *
	 * @param model Model over which matcher is to operate
	 */
	public ORConceptsMatcher(OModel model) {

		super(model);
	}

	/**
	 * Constructs matcher for specified model, with the configuration
	 * defined via the appropriately-tagged child of the specified parent
	 * configuration-node.
	 *
	 * @param model Model over which matcher is to operate
	 * @param parentConfigNode Parent configuration-node
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	public ORConceptsMatcher(OModel model, KConfigNode parentConfigNode) {

		super(model, parentConfigNode);
	}

	/**
	 * Specifies that referenced instances are to be expanded.
	 *
	 * @return True since referenced instances are to be expanded
	 */
	protected boolean expandInstanceRefs() {

		return true;
	}

	/**
	 */
	protected void addToOntologyLinkedStore(NNode instance, IRI iri) {

		addConceptDescription(addConcept(iri), createConceptDescription(instance));
	}

	/**
	 */
	protected void removeFromOntologyLinkedStore(IRI iri) {

		removeAxioms(getConceptAxioms(iri));
	}

	boolean individualsMatcher() {

		return false;
	}

	List<IRI> match(ConceptExpression queryExpr) {

		return queryExpr.getMatchingConcepts();
	}

	boolean matches(ConceptExpression queryExpr, NNode instance) {

		return queryExpr.subsumes(createConceptExpression(instance));
	}

	private OWLClassExpression createConceptDescription(NNode node) {

		return createConceptExpression(node).getOWLConstruct();
	}

	private OWLClass addConcept(IRI iri) {

		OWLClass concept = getConcept(iri);

		addAxiom(getDataFactory().getOWLDeclarationAxiom(concept));

		return concept;
	}

	private void addConceptDescription(OWLClass concept, OWLClassExpression description) {

		addAxiom(getDataFactory().getOWLSubClassOfAxiom(concept, description));
	}

	private void addAxiom(OWLAxiom axiom) {

		getModel().addInstanceAxiom(axiom);
	}

	private void removeAxioms(Set<? extends OWLAxiom> axioms) {

		getModel().removeInstanceAxioms(axioms);
	}

	private OWLClass getConcept(IRI iri) {

		return getDataFactory().getOWLClass(iri);
	}

	private Set<? extends OWLAxiom> getConceptAxioms(IRI iri) {

		return OWLAPIVersion.getAxioms(getInstanceOntology(), getConcept(iri));
	}

	private OWLOntology getInstanceOntology() {

		return getModel().getInstanceOntology();
	}

	private OWLDataFactory getDataFactory() {

		return getModel().getDataFactory();
	}
}
