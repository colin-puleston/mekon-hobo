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

package uk.ac.manchester.cs.rekon;

import java.util.*;

import org.semanticweb.owlapi.model.*;

/**
 * @author Colin Puleston
 */
class Name {

	private OWLEntity entity;
	private int ref;

	private NameSet equivalents = new NameSet();

	private NameSet supers = new NameSet();
	private NameSet ancestors = new NameSet();

	private NameSet subs = new NameSet();
	private NameSet descendants = new NameSet();

	public String toString() {

		return getClass().getSimpleName() + "(" + entity + ")";
	}

	Name(OWLEntity entity, int ref) {

		this.entity = entity;
		this.ref = ref;
	}

	void addEquivalent(Name name) {

		if (name != this) {

			equivalents.add(name);
		}
	}

	void addSuper(Name name) {

		if (name != this) {

			supers.add(name);
		}
	}

	NameSet resolveUpwardLinks() {

		if (ancestors.isEmpty()) {

			for (Name equ : equivalents.getSet()) {

				supers.addAll(equ.supers);
			}

			ancestors.addAll(supers);

			for (Name sup : supers.getSet()) {

				ancestors.addAll(sup.resolveUpwardLinks());

				checkForAncestorCycle();
			}
		}

		return ancestors;
	}

	NameSet resolveDownwardLinks() {

		if (descendants.isEmpty()) {

			for (Name equ : equivalents.getSet()) {

				subs.addAll(equ.subs);
			}

			descendants.addAll(subs);

			for (Name sub : subs.getSet()) {

				descendants.addAll(sub.resolveDownwardLinks());
			}
		}

		return descendants;
	}

	void purgeSupers() {

		for (Name sup : supers.copySet()) {

			supers.removeAll(sup.ancestors);
		}
	}

	void setSubs() {

		for (Name sup : supers.getSet()) {

			sup.subs.add(this);
		}
	}

	void clearDerivedLinks() {

		subs.clear();
		ancestors.clear();
		descendants.clear();
	}

	OWLEntity getEntity() {

		return entity;
	}

	int getRef() {

		return ref;
	}

	Set<Name> getAncestors() {

		return ancestors.getSet();
	}

	<E extends OWLEntity>Set<E> getEquivalentEntities(Class<E> type) {

		return getEntities(type, equivalents);
	}

	<E extends OWLEntity>Set<E> getSuperEntities(Class<E> type, boolean directOnly) {

		return getEntities(type, directOnly ? supers : ancestors);
	}

	<E extends OWLEntity>Set<E> getSubEntities(Class<E> type, boolean directOnly) {

		return getEntities(type, directOnly ? subs : descendants);
	}

	boolean subsumes(Name name) {

		return name.subsumedBy(this);
	}

	boolean subsumedBy(Name name) {

		return name == this || equivalents.contains(name) || ancestors.contains(name);
	}

	private <E extends OWLEntity>Set<E> getEntities(Class<E> type, NameSet sources) {

		Set<E> entities = new HashSet<E>();

		for (Name source : sources.getSet()) {

			entities.add(type.cast(source.entity));
		}

		return entities;
	}

	private void checkForAncestorCycle() {

		if (ancestors.contains(this)) {

			throw new RuntimeException(
						"Inference cycle involving entity: "
						+ entity);
		}
	}
}
