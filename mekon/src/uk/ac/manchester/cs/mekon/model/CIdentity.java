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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

/**
 * Identifies a concept-level entity from the frames-based
 * model via a unique identifier, and a label. The identifier
 * must always be supplied, whereas the label is optional and
 * will be assigned a default value if not supplied.
 *
 * @author Colin Puleston
 */
public class CIdentity {

	static private final String UNSPECIFIED_LABEL = "<NO-LABEL>";

	/**
	 * Provides the unique identifiers from all of the specified
	 * identities.
	 *
	 * @param identities Identities whose identifiers are required
	 * @return All relevant identifiers (in relevant order, if
	 * applicable)
	 */
	static public List<String> getAllIdentifiers(Collection<CIdentity> identities) {

		List<String> ids = new ArrayList<String>();

		for (CIdentity identity : identities) {

			ids.add(identity.getIdentifier());
		}

		return ids;
	}

	/**
	 * Provides the labels from all of the specified identities.
	 *
	 * @param identities Identities whose labels are required
	 * @return All relevant labels (in relevant order, if applicable)
	 */
	static public List<String> getAllLabels(Collection<CIdentity> identities) {

		List<String> labels = new ArrayList<String>();

		for (CIdentity identity : identities) {

			labels.add(identity.getLabel());
		}

		return labels;
	}

	private List<String> identifierComponents = new ArrayList<String>();
	private String label;

	/**
	 * Constructs identity with specified identifier and with
	 * default label.
	 *
	 * @param identifier Unique identifier for entity
	 */
	public CIdentity(String identifier) {

		this(identifier, UNSPECIFIED_LABEL);
	}

	/**
	 * Constructs identity with specified identifier-components
	 * and with default label.
	 *
	 * @param identifierComponents Components of unique identifier
	 * for entity
	 */
	public CIdentity(List<String> identifierComponents) {

		this(identifierComponents, UNSPECIFIED_LABEL);
	}

	/**
	 * Constructs identity with specified identifier and label.
	 *
	 * @param identifier Unique identifier for entity
	 * @param label Label for entity
	 */
	public CIdentity(String identifier, String label) {

		identifierComponents.add(identifier);

		this.label = label;
	}

	/**
	 * Constructs identity with specified identifier-components
	 * and label.
	 *
	 * @param identifierComponents Components of unique identifier
	 * for entity
	 * @param label Label for entity
	 */
	public CIdentity(List<String> identifierComponents, String label) {

		this.identifierComponents.addAll(identifierComponents);

		this.label = label;
	}

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>CIdentity</code>
	 * with the same identifier-components
	 */
	public boolean equals(Object other) {

		if (other instanceof CIdentity) {

			CIdentity id = (CIdentity)other;

			return getIdentifier().equals(id.getIdentifier());
		}

		return false;
	}

	/**
	 * Provides hash-code based on identifier.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return getIdentifier().hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return CIdentity.class.getSimpleName() + "(" + toInnerString() + ")";
	}

	/**
	 * Provides the unique identifier for the entity.
	 *
	 * @return Unique identifier for entity
	 */
	public String getIdentifier() {

		return identifierComponents.size() == 1
					? identifierComponents.get(0)
					: createIdentifier();
	}

	/**
	 * Provides the label for the entity.
	 *
	 * @return Label for entity
	 */
	public String getLabel() {

		return label;
	}

	/**
	 * Constructs identity with the same identifier-components as
	 * this one and with the specified label.
	 *
	 * @param newLabel New-label for entity
	 * @return Derived identity
	 */
	public CIdentity deriveIdentity(String newLabel) {

		return new CIdentity(identifierComponents, newLabel);
	}

	String toInnerString() {

		return getIdentifier() + "(" + label + ")";
	}

	private String createIdentifier() {

		StringBuilder id = new StringBuilder();

		for (String c : identifierComponents) {

			id.append(c);
		}

		return id.toString();
	}
}
