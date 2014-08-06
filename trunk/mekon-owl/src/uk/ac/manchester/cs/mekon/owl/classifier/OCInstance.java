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

package uk.ac.manchester.cs.mekon.owl.classifier;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;
import uk.ac.manchester.cs.mekon.owl.frames.*;

/**
 * @author Colin Puleston
 */
class OCInstance {

	private OModel model;

	private OFFrame frame;
	private OWLClassExpression frameExpr;

	OCInstance(OModel model, OFFrame frame) {

		this.model = model;
		this.frame = frame;

		frameExpr = frameToExpression();
	}

	IClassification classify(IClassifierOps ops) {

		List<CIdentity> inferredIds = new ArrayList<CIdentity>();
		List<CIdentity> suggestedIds = new ArrayList<CIdentity>();

		OCMonitor.pollForRequestReceived(model, frameExpr);

		if (ops.inferreds()) {

			Set<OWLClass> inferreds = getInferredTypes();

			OCMonitor.pollForTypesInferred(model, inferreds);
			inferredIds.addAll(toIdentityList(inferreds));
		}

		if (ops.suggesteds()) {

			Set<OWLClass> suggesteds = getSuggestedTypes();

			OCMonitor.pollForTypesSuggested(model, suggesteds);
			suggestedIds.addAll(toIdentityList(suggesteds));
		}

		OCMonitor.pollForRequestCompleted(model, frameExpr);

		return new IClassification(inferredIds, suggestedIds);
	}

	private OWLClassExpression frameToExpression() {

		return new OFFrameToExpressionRenderer(model).render(frame);
	}

	private Set<OWLClass> getInferredTypes() {

		if (frameExpr instanceof OWLClass) {

			return Collections.singleton((OWLClass)frameExpr);
		}

		return checkRemoveRootFrameConcept(inferEquivalentsOrSupers());
	}

	private Set<OWLClass> getSuggestedTypes() {

		return model.getInferredSubs(frameExpr, true);
	}

	private Set<OWLClass> inferEquivalentsOrSupers() {

		Set<OWLClass> types = model.getInferredEquivalents(frameExpr);

		return types.isEmpty() ? model.getInferredSupers(frameExpr, true) : types;
	}

	private Set<OWLClass> checkRemoveRootFrameConcept(Set<OWLClass> allConcepts) {

		OWLClass concept = getFrameConcept();

		if (allConcepts.contains(concept)) {

			allConcepts = new HashSet<OWLClass>(allConcepts);
			allConcepts.remove(concept);
		}

		return allConcepts;
	}

	private List<CIdentity> toIdentityList(Set<OWLClass> classes) {

		return new ArrayList<CIdentity>(OIdentity.createSortedSet(classes));
	}

	private OWLClass getFrameConcept() {

		return model.getConcepts().get(frame.getIRI());
	}
}
