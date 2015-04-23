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
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class ConceptExpression extends InstanceConstruct {

	private OModel model;
	private ORFrame frame;
	private OWLClassExpression expression;

	ConceptExpression(OModel model, ORFrame frame) {

		this.model = model;
		this.frame = frame;

		expression = frameToExpression(frame);
	}

	ORFrame getFrame() {

		return frame;
	}

	boolean subsumes(ConceptExpression testSubsumed) {

		OWLClassExpression testSubsExpr = testSubsumed.getConstruct();

		return isEntailed(getSubClassOfThisAxiom(testSubsExpr))
				|| isEntailed(getEquivalentToThisAxiom(testSubsExpr));
	}

	List<CIdentity> getMatchingIndividuals() {

		return toResolvedIdentityList(inferIndividuals());
	}

	void cleanUp() {
	}

	boolean suggestsTypes() {

		return true;
	}

	OWLClassExpression getConstruct() {

		return expression;
	}

	Set<OWLClass> getInferredTypes() {

		return expression instanceof OWLClass
				? Collections.<OWLClass>emptySet()
				: inferEquivalentsOrSupers();
	}

	Set<OWLClass> getSuggestedTypes() {

		return model.getInferredSubs(expression, true);
	}

	private OWLClassExpression frameToExpression(ORFrame frame) {

		return new ExpressionRenderer(model).render(frame);
	}

	private Set<OWLClass> inferEquivalentsOrSupers() {

		Set<OWLClass> types = model.getInferredEquivalents(expression);

		return types.isEmpty() ? model.getInferredSupers(expression, true) : types;
	}

	private Set<OWLNamedIndividual> inferIndividuals() {

		return model.getInferredIndividuals(expression, false);
	}

	private OWLAxiom getSubClassOfThisAxiom(OWLClassExpression subClass) {

		return getDataFactory().getOWLSubClassOfAxiom(subClass, expression);
	}

	private OWLAxiom getEquivalentToThisAxiom(OWLClassExpression equiv) {

		return getDataFactory().getOWLEquivalentClassesAxiom(equiv, expression);
	}

	private boolean isEntailed(OWLAxiom axiom) {

		return getReasoner().isEntailed(axiom);
	}

	private List<CIdentity> toResolvedIdentityList(Set<OWLNamedIndividual> individuals) {

		List<CIdentity> identities = new ArrayList<CIdentity>();

		for (CIdentity identity : OIdentity.createSortedSet(individuals)) {

			identities.add(resolveIndividualIdentity(identity));
		}

		return identities;
	}

	private CIdentity resolveIndividualIdentity(CIdentity identity) {

		IRI iri = IRI.create(identity.getIdentifier());

		return new CIdentity(IndividualIRIGenerator.extractName(iri));
	}

	private OWLDataFactory getDataFactory() {

		return model.getDataFactory();
	}

	private OWLReasoner getReasoner() {

		return model.getReasoner();
	}
}
