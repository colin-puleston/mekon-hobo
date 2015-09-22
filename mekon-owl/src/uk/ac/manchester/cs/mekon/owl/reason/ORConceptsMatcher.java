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

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * Extension of {@link ORMatcher} that represents the instances
 * as appropriately-defined OWL classes, which are added to an
 * in-memory version of the ontology, and that represents queries
 * as anonymous class-expressions.
 *
 * @author Colin Puleston
 */
public class ORConceptsMatcher extends OROntologyBasedMatcher {

	/**
	 * Constructs matcher for specified model with the default
	 * reasoning-type, which is {@link ORReasoningType#DL}.
	 *
	 * @param model Model over which matcher is to operate
	 */
	public ORConceptsMatcher(OModel model) {

		super(model);
	}

	/**
	 * Constructs matcher for specified model and reasoning-type.
	 *
	 * @param model Model over which matcher is to operate
	 * @param reasoningType Required reasoning-type for matching
	 */
	public ORConceptsMatcher(OModel model, ORReasoningType reasoningType) {

		super(model, reasoningType);
	}

	/**
	 * Constructs matcher, with the configuration for both the
	 * matcher itself, and the model over which it is to operate,
	 * defined via the appropriately-tagged child of the specified
	 * parent configuration-node.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	public ORConceptsMatcher(KConfigNode parentConfigNode) {

		super(parentConfigNode);
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
	 */
	protected void add(ORFrame instance, IRI iri) {

		addConceptDefinition(addConcept(iri), createConceptDefinition(instance));
	}

	/**
	 */
	protected void remove(IRI iri) {

		removeAxioms(getConceptAxioms(iri));
	}

	boolean matcherModifiesOntology() {

		return true;
	}

	List<IRI> match(ConceptExpression queryExpr) {

		return queryExpr.getMatchingConcepts();
	}

	boolean matches(ConceptExpression queryExpr, ORFrame instance) {

		return queryExpr.subsumes(createConceptExpression(instance));
	}

	private OWLClassExpression createConceptDefinition(ORFrame frame) {

		return createConceptExpression(frame).getOWLConstruct();
	}

	private ConceptExpression createConceptExpression(ORFrame frame) {

		return new ConceptExpression(getMatcherModel(), frame);
	}

	private OWLClass addConcept(IRI iri) {

		OWLClass concept = getConcept(iri);

		addAxiom(getDataFactory().getOWLDeclarationAxiom(concept));

		return concept;
	}

	private void addConceptDefinition(
					OWLClass concept,
					OWLClassExpression definiton) {

		addAxiom(getDataFactory().getOWLEquivalentClassesAxiom(concept, definiton));
	}

	private void addAxiom(OWLAxiom axiom) {

		getMatcherModel().addAxiom(axiom);
	}

	private void removeAxioms(Set<? extends OWLAxiom> axioms) {

		getMatcherModel().removeAxioms(axioms);
	}

	private OWLClass getConcept(IRI iri) {

		return getDataFactory().getOWLClass(iri);
	}

	private Set<? extends OWLAxiom> getConceptAxioms(IRI iri) {

		return getOntology().getAxioms(getConcept(iri));
	}

	private OWLOntology getOntology() {

		return getMatcherModel().getMainOntology();
	}

	private OWLDataFactory getDataFactory() {

		return getMatcherModel().getDataFactory();
	}
}
