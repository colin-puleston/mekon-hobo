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

package uk.ac.manchester.cs.mekon.owl;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Represents set of {@link OWLEntity} objects of a specific type.
 *
 * @author Colin Puleston
 */
public abstract class OEntities<E extends OWLEntity> {

	private OModel model;

	/**
	 * Tests for the presence of the entity with the specified IRI.
	 *
	 * @param iri IRI of required entity
	 * @return True if entity found
	 */
	public abstract boolean contains(IRI iri);

	/**
	 * Retrieves the entity with the specified IRI.
	 *
	 * @param iri IRI of required entity
	 * @return Required entity
	 * @throws KModelException if entity not found
	 */
	public E get(IRI iri) {

		E entity = getOrNull(iri);

		if (entity != null) {

			return entity;
		}

		throw new KModelException(
					"Cannot find OWL " + getEntityTypeName() + ": "
					+ iri);
	}

	/**
	 * Retrieves the entity with the specified IRI, if present.
	 *
	 * @param iri IRI of required entity
	 * @return Required entity, or null if entity not found
	 */
	public E getOrNull(IRI iri) {

		return contains(iri) ? getContained(iri) : null;
	}

	/**
	 * Provides all entities in set.
	 *
	 * @return All entities
	 */
	public Set<E> getAll() {

		return normalise(getAllPreNormalise());
	}

	/**
	 * Provides IRIs of all entities in set.
	 *
	 * @return IRIs of all entities
	 */
	public Set<IRI> getAllIRIs() {

		return getIRIs(getAll());
	}

	OEntities(OModel model) {

		this.model = model;
	}

	abstract String getEntityTypeName();

	abstract E getTop();

	abstract E getBottom();

	abstract E getContained(IRI iri);

	abstract Set<E> getAllPreNormalise();

	OModel getModel() {

		return model;
	}

	OWLOntology getModelOntology() {

		return model.getModelOntology();
	}

	Set<OWLOntology> getAllOntologies() {

		return model.getAllOntologies();
	}

	OWLDataFactory getDataFactory() {

		return model.getDataFactory();
	}

	OWLReasoner getReasoner() {

		return model.getReasoner();
	}

	Set<E> normalise(NodeSet<E> entities) {

		return normalise(OWLAPIVersion.getEntities(entities));
	}

	Set<E> normalise(Set<E> entities) {

		entities.remove(getTop());
		entities.remove(getBottom());

		return entities;
	}

	private Set<IRI> getIRIs(Set<E> entities) {

		Set<IRI> iris = new HashSet<IRI>();

		for (E entity : entities) {

			iris.add(entity.getIRI());
		}

		return iris;
	}
}
