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

import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * @author Colin Puleston
 */
class ConceptBasedInstance extends ORInstance {

	private OModel model;

	private OWLClassExpression frameExpr;

	ConceptBasedInstance(OModel model, ORFrame frame) {

		super(model, frame);

		this.model = model;

		frameExpr = frameToExpression(frame);
	}

	void cleanUp() {
	}

	boolean suggestsTypes() {

		return true;
	}

	boolean infersMatchingIndividuals() {

		return true;
	}

	OWLObject getFrameRendering() {

		return frameExpr;
	}

	Set<OWLClass> getInferredTypes() {

		if (frameExpr instanceof OWLClass) {

			return Collections.singleton((OWLClass)frameExpr);
		}

		return checkRemoveRootFrameConcept(inferEquivalentsOrSupers());
	}

	Set<OWLClass> getSuggestedTypes() {

		return model.getInferredSubs(frameExpr, true);
	}

	Set<OWLNamedIndividual> getMatchingIndividuals() {

		return model.getInferredIndividuals(frameExpr, false);
	}

	private OWLClassExpression frameToExpression(ORFrame frame) {

		return new ExpressionRenderer(model).render(frame);
	}

	private Set<OWLClass> inferEquivalentsOrSupers() {

		Set<OWLClass> types = model.getInferredEquivalents(frameExpr);

		return types.isEmpty() ? model.getInferredSupers(frameExpr, true) : types;
	}
}
