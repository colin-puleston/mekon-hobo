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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
abstract class InstanceConstruct {

	private OModel model;
	private ORFrame frame;

	InstanceConstruct(OModel model, ORFrame frame) {

		this.model = model;
		this.frame = frame;
	}

	IClassification classify(IClassifierOps ops) {

		OWLObject construct = getConstruct();

		ORMonitor.pollForClassifierRequest(model, construct);

		List<CIdentity> inferredIds = new ArrayList<CIdentity>();
		List<CIdentity> suggestedIds = new ArrayList<CIdentity>();

		if (ops.inferreds()) {

			Set<OWLClass> inferreds = getInferredTypes();

			purgeInferredTypes(inferreds);

			ORMonitor.pollForTypesInferred(model, inferreds);
			inferredIds.addAll(toIdentityList(inferreds));
		}

		if (suggestsTypes() && ops.suggesteds()) {

			Set<OWLClass> suggesteds = getSuggestedTypes();

			ORMonitor.pollForTypesSuggested(model, suggesteds);
			suggestedIds.addAll(toIdentityList(suggesteds));
		}

		ORMonitor.pollForClassifierDone(model, construct);

		cleanUp();

		return new IClassification(inferredIds, suggestedIds);
	}

	abstract void cleanUp();

	abstract boolean suggestsTypes();

	abstract OWLObject getConstruct();

	abstract Set<OWLClass> getInferredTypes();

	abstract Set<OWLClass> getSuggestedTypes();

	private void purgeInferredTypes(Set<OWLClass> types) {

		types.remove(getFrameConcept());
	}

	private OWLClass getFrameConcept() {

		return model.getConcepts().get(frame.getIRI());
	}

	private List<CIdentity> toIdentityList(Set<OWLClass> entities) {

		return new ArrayList<CIdentity>(OIdentity.createSortedSet(entities));
	}
}