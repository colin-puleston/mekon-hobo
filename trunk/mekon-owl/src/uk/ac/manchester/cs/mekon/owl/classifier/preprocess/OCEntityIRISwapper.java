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

package uk.ac.manchester.cs.mekon.owl.classifier.preprocess;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.classifier.frames.*;

/**
 * Pre-processer that modifies the representations of instances
 * that are about to be classified, by setting a specific IRI value
 * for any entities with a particular identifier.
 *
 * @author Colin Puleston
 */
public class OCEntityIRISwapper implements OCPreProcessor {

	private CIdentity identity;
	private IRI iri;

	/**
	 * Constructor.
	 *
	 * @param identity Identifier of entities whose IRI is to be set
	 * @param iri IRI that is to be set
	 */
	public OCEntityIRISwapper(CIdentity identity, IRI iri) {

		this.identity = identity;
		this.iri = iri;
	}

	/**
	 * {@inheritDoc}
	 */
	public void process(OModel model, OCFrame rootFrame) {

		process(rootFrame);
	}

	private void process(OCFrame frame) {

		checkSwapIRI(frame);

		for (OCConceptSlot slot : frame.getConceptSlots()) {

			process(slot);
		}
	}

	private void process(OCConceptSlot slot) {

		checkSwapIRI(slot);

		for (OCFrame value : slot.getValues()) {

			process(value);
		}
	}

	private void checkSwapIRI(OCFramesEntity entity) {

		if (entity.getIdentifier().equals(identity.getIdentifier())) {

			entity.setIRI(iri);
		}
	}
}
