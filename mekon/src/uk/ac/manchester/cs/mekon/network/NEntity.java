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

package uk.ac.manchester.cs.mekon.network;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents an entity in the network-based instance representation
 *
 * @author Colin Puleston
 */
public abstract class NEntity {

	private List<CIdentity> typeDisjuncts = new ArrayList<CIdentity>();

	/**
	 * Sets an atomic type for the entity.
	 *
	 * @param type Atomic type for entity
	 */
	public void setType(CIdentity type) {

		typeDisjuncts.clear();
		typeDisjuncts.add(type);
	}

	/**
	 */
	public String toString() {

		NEntityRenderer r = new NEntityRenderer();

		render(r);

		return r.getRendering();
	}

	/**
	 * Specifies whether the entity type is atomic, rather than
	 * a disjunction.
	 *
	 * @return True if entity type is atomic
	 */
	public boolean atomicType() {

		return typeDisjuncts.size() == 1;
	}

	/**
	 * Specifies whether the entity type is atomic and equal
	 * to the specified type.
	 *
	 * @param type Atomic type to test for
	 * @return True if entity type is as specified
	 */
	public boolean atomicType(CIdentity type) {

		return atomicType() && getType().equals(type);
	}

	/**
	 * Provides the atomic type of the entity, for relevant entities.
	 *
	 * @return Associated atomic type
	 * @throws KAccessException if entity type is disjunction
	 */
	public CIdentity getType() {

		if (atomicType()) {

			return typeDisjuncts.iterator().next();
		}

		throw new KAccessException("Does not have atomic type: " + this);
	}

	/**
	 * Provides all disjuncts of entity type. Where associated
	 * type is atomic, the returned set will consist of that single
	 * atomic type
	 *
	 * @return All disjuncts of entity type
	 */
	public List<CIdentity> getTypeDisjuncts() {

		return new ArrayList<CIdentity>(typeDisjuncts);
	}

	NEntity(CIdentity type) {

		typeDisjuncts.add(type);
	}

	NEntity(Collection<CIdentity> typeDisjuncts) {

		setTypeDisjuncts(typeDisjuncts);
	}

	void setTypeDisjuncts(Collection<CIdentity> disjuncts) {

		if (disjuncts.isEmpty()) {

			throw new KAccessException("Cannot have empty type-disjuncts set");
		}

		typeDisjuncts.clear();
		typeDisjuncts.addAll(disjuncts);
	}

	void render(NEntityRenderer renderer) {

		renderer.addLine(getClass().getSimpleName() + "(" + typeDisjuncts + ")");
		renderAttributes(renderer.nextLevel());
	}

	private String typeDisjunctsToString() {

		StringBuilder s = new StringBuilder();
		boolean first = true;

		for (CIdentity typeDisjunct : typeDisjuncts) {

			if (first) {

				first = false;
			}
			else {

				s.append(" OR ");
			}

			s.append(typeDisjunct.toString());
		}

		return s.toString();
	}

	abstract void renderAttributes(NEntityRenderer renderer);
}
