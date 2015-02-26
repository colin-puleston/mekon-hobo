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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * Represents a set of OWL entities that will be used in
 * generating entities in the Frames Model (FM).
 *
 * @author Colin Puleston
 */
public abstract class OBEntities
						<E extends OWLEntity,
						G extends OBEntityGroup,
						A extends OBAttributes<A>> {

	private OModel model;

	private Set<E> entities = new HashSet<E>();
	private Map<E, A> entitiesToAttributes = new HashMap<E, A>();

	/**
	 * Adds an entity to the set.
	 *
	 * @param entity Entity to add
	 */
	public void add(E entity) {

		add(entity, createAttributes());
	}

	/**
	 * Adds a collection of entities to the set.
	 *
	 * @param entities Entities to add
	 */
	public void addAll(Collection<E> entities) {

		for (E entity : entitiesToAttributes.keySet()) {

			add(entity);
		}
	}

	/**
	 * Adds a group of entities to the set.
	 *
	 * @param group Group of entities to add
	 */
	public void addGroup(G group) {

		E root = getRoot(group.getRootEntityIRI());

		if (group.getInclusion().includesRoot()) {

			addGroupEntity(group, root, true);
		}

		if (group.getInclusion().includesNonRoots()) {

			for (E nonRoot : getDescendants(root)) {

				addGroupEntity(group, nonRoot, false);
			}
		}
	}

	/**
	 * Adds a set of groups of entities to the set.
	 *
	 * @param groups Groups of entities to add
	 */
	public void addGroups(Set<G> groups) {

		for (G group : groups) {

			addGroup(group);
		}
	}

	/**
	 * Provides the FM entity-generation attributes associated with
	 * the specified OWL entity (which should have previously been
	 * registered via the {@link #add} method).
	 *
	 * @param entity Relevant entity
	 * @return Attributes for specified entity
	 * @throws KModelException if entity has not been registered
	 */
	public A getAttributes(E entity) {

		A attrs = entitiesToAttributes.get(entity);

		if (attrs == null) {

			throw new KModelException(
						"Entity has not been registered: "
						+ entity);
		}

		return attrs;
	}

	OBEntities(OModel model) {

		this.model = model;
	}

	void add(E entity, A attributes) {

		if (entitiesToAttributes.containsKey(entity)) {

			entitiesToAttributes.get(entity).absorb(attributes);
		}
		else {

			entitiesToAttributes.put(entity, attributes);
		}
	}

	abstract void addGroupEntity(G group, E entity, boolean isRoot);

	abstract A createAttributes();

	Set<E> getAll() {

		if (entitiesToAttributes.isEmpty()) {

			addAll(getAllInModel());
		}

		return entitiesToAttributes.keySet();
	}

	boolean containsAllInSignature(OWLClassExpression expression) {

		for (E entity : extractAll(expression)) {

			if (!getAll().contains(entity)) {

				return false;
			}
		}

		return true;
	}

	abstract String getTypeName();

	abstract boolean isValidEntity(IRI iri);

	abstract E get(IRI iri);

	abstract Set<E> getAllInModel();

	abstract Set<E> getDescendants(E entity);

	abstract Set<E> extractAll(OWLClassExpression expression);

	OModel getModel() {

		return model;
	}

	OWLOntology getMainOntology() {

		return model.getMainOntology();
	}

	OWLDataFactory getDataFactory() {

		return model.getDataFactory();
	}

	private E getRoot(IRI iri) {

		if (isValidEntity(iri)) {

			return get(iri);
		}

		throw new KModelException(
					"Cannot find OWL-"
					+ getTypeName() + ": " + iri);
	}
}
