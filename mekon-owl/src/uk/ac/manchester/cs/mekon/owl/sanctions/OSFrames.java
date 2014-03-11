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

package uk.ac.manchester.cs.mekon.owl.sanctions;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * @author Colin Puleston
 */
class OSFrames {

	private IReasoner iReasoner = null;
	private OSConcepts concepts;
	private OSEntityLabels labels;

	private Map<OWLClass, OSFrame> frames = new HashMap<OWLClass, OSFrame>();

	OSFrames(OSConcepts concepts, OSEntityLabels labels) {

		this.concepts = concepts;
		this.labels = labels;
	}

	void setIReasoner(IReasoner iReasoner) {

		this.iReasoner = iReasoner;
	}

	void createAll() {

		for (OWLClass concept : concepts.getAll()) {

			createFrame(concept);
		}
	}

	Collection<OSFrame> getAll() {

		return frames.values();
	}

	OSFrame get(OWLClass concept) {

		OSFrame frame = frames.get(concept);

		if (frame == null) {

			throw new Error("Cannot find frame for: " + concept);
		}

		return frame;
	}

	private OSFrame createFrame(OWLClass concept) {

		String label = labels.getLabel(concept);
		boolean hidden = concepts.isHidden(concept);

		return createFrame(concept, label, hidden);
	}

	private OSFrame createFrame(OWLClass concept, String label, boolean hidden) {

		return addFrame(new OSFrame(concept, label, hidden, iReasoner));
	}

	private OSFrame addFrame(OSFrame frame) {

		frames.put(frame.getConcept(), frame);

		return frame;
	}
}
