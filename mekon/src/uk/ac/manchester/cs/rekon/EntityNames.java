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
abstract class EntityNames<E extends OWLEntity, S extends OWLObject, N extends Name> {

	final Assertions assertions;

	final Map<E, N> names = new HashMap<E, N>();

	private RefGenerator refs;
	private Class<E> entityClass;

	EntityNames(Assertions assertions, RefGenerator refs, Class<E> entityClass) {

		this.assertions = assertions;
		this.refs = refs;
		this.entityClass = entityClass;
	}

	void initialise() {

		addNames();
		addAssertedLinks();
		resolveUpwardLinks();
	}

	void resolveAllLinksPostClassification(boolean classifiableType) {

		if (classifiableType) {

			clearDerivedLinks();
			resolveUpwardLinks();
			purgeSupers();
		}

		setSubs();
		resolveDownwardLinks();
	}

	abstract Collection<E> getAssertedEntities();

	abstract Collection<S> getAssertedEquivalents(E entity);

	abstract Collection<S> getAssertedSupers(E entity);

	abstract N createName(E entity, int ref);

	private void addNames() {

		for (E e : getAssertedEntities()) {

			names.put(e, createName(e, refs.getNext()));
		}
	}

	private void addAssertedLinks() {

		for (E e : getAssertedEntities()) {

			N n = names.get(e);

			addAssertedEquivalents(n, e);
			addAssertedSupers(n, e);
		}
	}

	private void addAssertedEquivalents(N n, E e) {

		for (S s : getAssertedEquivalents(e)) {

			N sn = getNameOrNull(s);

			if (sn != null) {

				n.addEquivalent(sn);
			}
		}
	}

	private void addAssertedSupers(N n, E e) {

		for (S s : getAssertedSupers(e)) {

			N sn = getNameOrNull(s);

			if (sn != null) {

				n.addSuper(sn);
			}
		}
	}

	private void resolveUpwardLinks() {

		for (Name n : names.values()) {

			n.resolveUpwardLinks();
		}
	}

	private void resolveDownwardLinks() {

		for (Name n : names.values()) {

			n.resolveDownwardLinks();
		}
	}

	private void purgeSupers() {

		for (Name n : names.values()) {

			n.purgeSupers();
		}
	}

	private void setSubs() {

		for (Name n : names.values()) {

			n.setSubs();
		}
	}

	private void clearDerivedLinks() {

		for (Name n : names.values()) {

			n.clearDerivedLinks();
		}
	}

	private N getNameOrNull(S s) {

		if (entityClass.isAssignableFrom(s.getClass())) {

			return names.get(entityClass.cast(s));
		}

		return null;
	}
}
