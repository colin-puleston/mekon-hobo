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

package uk.ac.manchester.cs.mekon.owl.reason.frames;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents a number-valued slot in the pre-processable frames-based
 * instance representation.
 *
 * @author Colin Puleston
 */
public class ORNumberSlot extends ORSlot<INumber> {

	/**
	 * Constructor that takes the string representation of the IRI as
	 * the slot-identifier.
	 *
	 * @param iri IRI to be used in generating the classifiable
	 * OWL expression.
	 */
	public ORNumberSlot(IRI iri) {

		super(iri);
	}

	/**
	 * Constructor.
	 *
	 * @param identifier Identifier for represented slot
	 * @param iri IRI to be used in generating the classifiable
	 * OWL expression.
	 */
	public ORNumberSlot(String identifier, IRI iri) {

		super(identifier, iri);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean frameSlot() {

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public ORNumberSlot asNumberSlot() {

		return this;
	}

	/**
	 * Provides current number-value for the slot.
	 *
	 * @return Current number-value
	 * @throws KAccessException if no current value
	 */
	public INumber getValue() {

		if (hasValues()) {

			return getValues().iterator().next();
		}

		throw new KAccessException(
					"No current value for number-slot: "
					+ getIdentifier());
	}

	ORNumberSlot(CIdentity id, ISlot iSlot, IRI iri) {

		super(id, iSlot, iri);
	}
}
