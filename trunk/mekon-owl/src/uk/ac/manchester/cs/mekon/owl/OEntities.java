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

import uk.ac.manchester.cs.mekon.*;

/**
 * Represents set of {@link OWLEntity} objects of a specific type.
 *
 * @author Colin Puleston
 */
public class OEntities<E extends OWLEntity> {

	private String entityTypeName;

	private Map<IRI, E> entitiesByIRI = new HashMap<IRI, E>();

	/**
	 * Tests for the presence of the entity with the specified IRI.
	 *
	 * @param iri IRI of required entity
	 * @return True if entity found
	 */
	public boolean contains(IRI iri) {

		return entitiesByIRI.containsKey(iri);
	}

	/**
	 * Retrieves the entity with the specified IRI.
	 *
	 * @param iri IRI of required entity
	 * @return Required entity
	 * @throws KModelException if entity not found
	 */
	public E get(IRI iri) {

		E entity = entitiesByIRI.get(iri);

		if (entity == null) {

			throw new KModelException(
						"Cannot find OWL " + entityTypeName + ": "
						+ iri);
		}

		return entity;
	}

	/**
	 * Provides all entities in set.
	 *
	 * @return All entities
	 */
	public Set<E> getAll() {

		return new HashSet<E>(entitiesByIRI.values());
	}

	/**
	 * Provides IRIs of all entities in set.
	 *
	 * @return IRIs of all entities
	 */
	public Set<IRI> getAllIRIs() {

		return entitiesByIRI.keySet();
	}

	OEntities(String entityTypeName, Set<E> entities) {

		this.entityTypeName = entityTypeName;

		for (E entity : entities) {

			add(entity);
		}
	}

	void add(E entity) {

		entitiesByIRI.put(entity.getIRI(), entity);
	}

	void remove(E entity) {

		entitiesByIRI.remove(entity.getIRI());
	}
}
