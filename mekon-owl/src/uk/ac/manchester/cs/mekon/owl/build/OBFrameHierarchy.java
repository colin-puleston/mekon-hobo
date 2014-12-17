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
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class OBFrameHierarchy {

	private OModel model;
	private OWLReasoner reasoner;
	private OBFrames frames;

	OBFrameHierarchy(OModel model, OBFrames frames) {

		this.model = model;
		this.frames = frames;

		reasoner = model.getReasoner();
	}

	void createLinks() {

		for (OBFrame frame : frames.getAll()) {

			createFrameLinks(frame);
		}
	}

	private void createFrameLinks(OBFrame frame) {

		for (OWLEntity source : getSubFrameSources(frame)) {

			frame.addSubFrame(frames.get(source));
		}
	}

	private Set<? extends OWLEntity> getSubFrameSources(OBFrame frame) {

		return getAllDescendants(frame.getSourceEntity());
	}

	private Set<? extends OWLEntity> getAllDescendants(OWLEntity entity) {

		if (entity instanceof OWLClass) {

			return model.getInferredSubs((OWLClass)entity, true);
		}

		if (entity instanceof OWLObjectProperty) {

			return model.getInferredSubs((OWLObjectProperty)entity, true);
		}

		throw new Error("Entity of unexpected type: " + entity);
	}
}
