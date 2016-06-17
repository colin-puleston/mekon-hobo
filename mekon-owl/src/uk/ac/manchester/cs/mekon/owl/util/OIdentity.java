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

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Represents the identity for a frames-based object derived
 * from an OWL construct of some type. When the source is a
 * <code>OWLNamedObject</code> of some kind then the IRI can be
 * taken as the identifier, with the label being either manually
 * specified or heuristically-generated from the IRI fragment,
 * via the {@link KLabel}-mechanism. Otherwise both identifier
 * and label must be provided.
 * <p>
 * Provides a {@link #compareTo} method based on the lexical
 * values of the labels or, if the labels are identical, on
 * the lexical values of the identifiers.
 *
 * @author Colin Puleston
 */
public class OIdentity extends CIdentity implements Comparable<OIdentity> {

	static private Map<String, String> namespaces = new HashMap<String, String>();

	/**
	 * Creates a set of <code>OIdentity</code> objects with
	 * ordering based on the identifiers {@link #compareTo}
	 * method, and label generated via the
	 * {@link #createDefaultLabel} method.
	 *
	 * @param objects Named OWL objects from which identities
	 * are to be derived
	 * @return Ordered set of created identities
	 */
	static public SortedSet<OIdentity>
						createSortedSet(
							Set<? extends OWLNamedObject> objects) {

		SortedSet<OIdentity> ids = new TreeSet<OIdentity>();

		for (OWLNamedObject object : objects) {

			ids.add(new OIdentity(object));
		}

		return ids;
	}

	/**
	 * Heuristically-generates a label, using the
	 * {@link KLabel}-mechanism, and based on the IRI fragment
	 * for the specified named OWL object.
	 *
	 * @param object Object for which label is required
	 * @return generated label
	 */
	static public String createDefaultLabel(OWLNamedObject object) {

		IRI iri = object.getIRI();
		String frag = iri.toURI().getFragment();

		return frag != null
				? KLabel.create(frag, object instanceof OWLClass)
				: iri.toString();
	}

	static private List<String> getIdentifierComponents(OWLNamedObject object) {

		List<String> components = new ArrayList<String>();

		IRI iriObj = object.getIRI();

		String iri = iriObj.toString();
		String fragment = iriObj.toURI().getFragment();

		if (fragment != null) {

			components.add(getNamespace(iri, fragment));
			components.add(fragment);
		}
		else {

			components.add(iri);
		}

		return components;
	}

	static private String getNamespace(String iri, String fragment) {

		int length = iri.length() - fragment.length();

		return resolveNamespace(iri.substring(0, length));
	}

	static private String resolveNamespace(String namespace) {

		String found = namespaces.get(namespace);

		if (found == null) {

			namespaces.put(namespace, namespace);

			return namespace;
		}

		return found;
	}

	/**
	 * Constructs identity based on the specified named OWL object,
	 * with the identifier coming directly from the IRI, and the
	 * label generated via the {@link #createDefaultLabel} method.
	 *
	 * @param object Object for which identity is being constructed
	 */
	public OIdentity(OWLNamedObject object) {

		this(object, createDefaultLabel(object));
	}

	/**
	 * Constructs identity based on the specified named OWL object,
	 * with the identifier coming directly from the IRI, and with
	 * the specified label.
	 *
	 * @param object Object for which identity is being constructed
	 * @param label Label for constructed identity
	 */
	public OIdentity(OWLNamedObject object, String label) {

		super(getIdentifierComponents(object), label);
	}

	/**
	 * Constructs identity with the specified identifier and label.
	 *
	 * @param identifier Identifier for identity of constructed object
	 * @param label Label for identity of constructed object
	 */
	public OIdentity(String identifier, String label) {

		super(identifier, label);
	}

	/**
	 * Comparator method based on the lexical values of the labels or,
	 * if the labels are identical, on the lexical values of the
	 * identifiers.
	 *
	 * @param other Other identity to compare this one to
	 * @return Resulting comparison value
	 */
	public int compareTo(OIdentity other) {

		int comp = getLabel().compareTo(other.getLabel());

		if (comp != 0) {

			return comp;
		}

		return getIdentifier().compareTo(other.getIdentifier());
	}
}
