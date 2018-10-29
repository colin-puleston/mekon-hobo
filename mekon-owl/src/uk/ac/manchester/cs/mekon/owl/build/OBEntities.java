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

		for (E entity : entities) {

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

		checkAddGroupEntity(group, root, EntityLocation.ROOT);

		if (group.getInclusion().includesAnyNonRoots()) {

			addNonRootGroupEntities(group, getSubs(root));
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
	 * Tests whether the specified OWL entity has been registered.
	 *
	 * @param entity Required entity
	 * @return True if entity has been registered
	 */
	public boolean contains(E entity) {

		return entitiesToAttributes.containsKey(entity);
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

		A currentAttrs = entitiesToAttributes.get(entity);

		if (currentAttrs != null) {

			attributes = currentAttrs.update(attributes);
		}

		entitiesToAttributes.put(entity, attributes);
	}

	abstract void addGroupEntity(G group, E entity, EntityLocation location);

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

	abstract boolean validEntity(IRI iri);

	abstract E get(IRI iri);

	abstract Set<E> getAllInModel();

	abstract Set<E> getSubs(E entity);

	abstract Set<E> extractAll(OWLClassExpression expression);

	OModel getModel() {

		return model;
	}

	OWLOntology getModelOntology() {

		return model.getModelOntology();
	}

	OWLDataFactory getDataFactory() {

		return model.getDataFactory();
	}

	private void addNonRootGroupEntities(G group, Set<E> currents) {

		for (E current : currents) {

			Set<E> subs = getSubs(current);

			if (subs.isEmpty()) {

				checkAddGroupEntity(group, current, EntityLocation.LEAF);
			}
			else {

				checkAddGroupEntity(group, current, EntityLocation.INTERMEDIATE);
				addNonRootGroupEntities(group, subs);
			}
		}
	}

	private void checkAddGroupEntity(
					G group,
					E entity,
					EntityLocation location) {

		if (group.getInclusion().includes(location)) {

			addGroupEntity(group, entity, location);
		}
	}

	private E getRoot(IRI iri) {

		if (validEntity(iri)) {

			return get(iri);
		}

		throw new KModelException(
					"Cannot find OWL-"
					+ getTypeName() + ": " + iri);
	}
}
