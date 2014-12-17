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

import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * @author Colin Puleston
 */
class OBFrames {

	private IReasoner iReasoner = null;
	private OBConcepts concepts;
	private OBProperties properties;
	private OBEntityLabels labels;

	private Map<OWLEntity, OBFrame> frames = new HashMap<OWLEntity, OBFrame>();

	OBFrames(OBConcepts concepts, OBProperties properties, OBEntityLabels labels) {

		this.concepts = concepts;
		this.properties = properties;
		this.labels = labels;
	}

	void setIReasoner(IReasoner iReasoner) {

		this.iReasoner = iReasoner;
	}

	void createAll() {

		createAllForConcepts();
		createAllForProperties();
	}

	Collection<OBFrame> getAll() {

		return frames.values();
	}

	OBFrame get(OWLEntity sourceEntity) {

		OBFrame frame = frames.get(sourceEntity);

		if (frame == null) {

			throw new Error("Cannot find frame for: " + sourceEntity);
		}

		return frame;
	}

	private void createAllForConcepts() {

		for (OWLClass concept : concepts.getAll()) {

			createFrame(concept, iReasoner, concepts.isHidden(concept));
		}
	}

	private void createAllForProperties() {

		for (OWLObjectProperty property : properties.getAll()) {

			if (properties.frameSource(property)) {

				createFrame(property, null, false);
			}
		}
	}

	private OBFrame createFrame(
						OWLEntity sourceEntity,
						IReasoner iReasoner,
						boolean hidden) {

		String label = labels.getLabel(sourceEntity);
		OBFrame frame = new OBFrame(sourceEntity, label, hidden, iReasoner);

		frames.put(frame.getSourceEntity(), frame);

		return frame;
	}
}
