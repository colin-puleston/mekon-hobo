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

package uk.ac.manchester.cs.mekon.owl.util;

import org.semanticweb.owlapi.model.*;

/**
 * Represents an entity derived from an <code>OWLNamedObject</code>
 * that will be used to generate a Frames Model (FM) entity.
 *
 * @author Colin Puleston
 */
public class OIdentified implements Comparable<OIdentified> {

	private OIdentity identity;

	/**
	 * Constructs object with an identity based on the specified
	 * named OWL object, with the identifier coming directly from
	 * the IRI, and with the specified label.
	 *
	 * @param object Object for which this object is being constructed
	 * @param label Label for identity of constructed object
	 */
	public OIdentified(OWLNamedObject object, String label) {

		identity = new OIdentity(object, label);
	}

	/**
	 * Comparator method based on the comparator for the associated
	 * {@link OIdentity} objects.
	 *
	 * @param other Other identified to compare this one to
	 * @return Resulting comparison value
	 */
	public int compareTo(OIdentified other) {

		return identity.compareTo(other.identity);
	}

	/**
	 * Provides the identity for the entity.
	 *
	 * @return Identity for entity
	 */
	public OIdentity getIdentity() {

		return identity;
	}
}
