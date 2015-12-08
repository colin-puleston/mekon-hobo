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
 * Represents a node in the network-based instance representation.
 *
 * @author Colin Puleston
 */
public class NNode {

	private List<CIdentity> conceptDisjuncts = new ArrayList<CIdentity>();
	private List<NAttribute<?>> attributes = new ArrayList<NAttribute<?>>();

	private CFrame cFrame = null;
	private IFrame iFrame = null;

	private int hashCode = 0;

	/**
	 * Constructor for nodes whose associated concept is atomic.
	 *
	 * @param concept Associated atomic concept
	 */
	public NNode(CIdentity concept) {

		conceptDisjuncts.add(concept);
	}

	/**
	 * Constructor for nodes whose associated concept is a disjunction.
	 *
	 * @param conceptDisjuncts Disjuncts of associated concept
	 */
	public NNode(Collection<CIdentity> conceptDisjuncts) {

		this.conceptDisjuncts.addAll(conceptDisjuncts);
	}

	/**
	 * Sets the associated concept to be an atomic concept.
	 *
	 * @param concept Associated atomic concept
	 */
	public void setAtomicConcept(CIdentity concept) {

		conceptDisjuncts.clear();
		conceptDisjuncts.add(concept);
		hashCode = 0;
	}

	/**
	 * Adds disjunct for the associated concept.
	 *
	 * @param conceptDisjunct Disjunct to add
	 */
	public void addConceptDisjunct(CIdentity conceptDisjunct) {

		conceptDisjuncts.add(conceptDisjunct);
		hashCode = 0;
	}

	/**
	 * Removes disjunct for the associated concept.
	 *
	 * @param conceptDisjunct Disjunct to remove
	 */
	public void removeConceptDisjunct(CIdentity conceptDisjunct) {

		conceptDisjuncts.remove(conceptDisjunct);
		hashCode = 0;
	}

	/**
	 * Clears associated concept, whether it is atomic or a disjunction.
	 */
	public void clearConcept() {

		conceptDisjuncts.clear();
		hashCode = 0;
	}

	/**
	 * Adds an attribute to the node.
	 *
	 * @param attribute Attribute to add
	 */
	public void addAttribute(NAttribute<?> attribute) {

		attributes.add(attribute);
		hashCode = 0;
	}

	/**
	 * Removes an attribute from the node.
	 *
	 * @param attribute Attribute to remove
	 */
	public void removeAttribute(NAttribute<?> attribute) {

		attributes.remove(attribute);
		hashCode = 0;
	}

	/**
	 * Removes all attributes from the node.
	 */
	public void clearAttributes() {

		attributes.clear();
		hashCode = 0;
	}

	/**
	 */
	public String toString() {

		return "NNode:" + conceptDisjuncts;
	}

	/**
	 * Tests if the other specified object is another <code>NNode</code>
	 * with the same associated concept as this one (whether atomic or
	 * disjunction), and with identical attributes, having identical
	 * values.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if objects are equal
	 */
	public boolean equals(Object other) {

		if (other instanceof NNode) {

			return equalsNode((NNode)other);
		}

		return false;
	}

	/**
	 * Provides hash-code for this object.
	 *
	 * @return Relelevant hash-code
	 */
	public int hashCode() {

		if (hashCode == 0) {

			hashCode = calcHashCode();
		}

		return hashCode;
	}

	/**
	 * Specifies whether the associated concept is atomic, rather than
	 * a disjunction.
	 *
	 * @return True associated concept is atomic
	 */
	public boolean atomicConcept() {

		return conceptDisjuncts.size() == 1;
	}

	/**
	 * Specifies whether the associated concept is atomic and equal
	 * to the specified concept.
	 *
	 * @param concept Atomic concept to test for
	 * @return True if associated concept is as specified
	 */
	public boolean hasAtomicConcept(CIdentity concept) {

		return atomicConcept() && getAtomicConcept().equals(concept);
	}

	/**
	 * Provides the atomic concept associated with the node, for
	 * relevant nodes.
	 *
	 * @return Associated atomic concept
	 * @throws KAccessException if associated concept is disjunction
	 */
	public CIdentity getAtomicConcept() {

		if (atomicConcept()) {

			return conceptDisjuncts.iterator().next();
		}

		throw new KAccessException("Does not have atomic concept: " + this);
	}

	/**
	 * Provides all disjuncts of associated concept. Where associated
	 * concept is atomic, the returned set will consist of that single
	 * atomic concept
	 *
	 * @return All disjuncts of associated concept
	 */
	public List<CIdentity> getConceptDisjuncts() {

		return new ArrayList<CIdentity>(conceptDisjuncts);
	}

	/**
	 * Checks whether the node has any attributes.
	 *
	 * @return True if node has attributes
	 */
	public boolean hasAttributes() {

		return !attributes.isEmpty();
	}

	/**
	 * Provides all attributes on the node, including both links and
	 * numerics.
	 *
	 * @return All attributes on node
	 */
	public List<NAttribute<?>> getAttributes() {

		return new ArrayList<NAttribute<?>>(attributes);
	}

	/**
	 * Provides all links on the node.
	 *
	 * @return All links on node
	 */
	public List<NLink> getLinks() {

		return getTypeAttributes(NLink.class);
	}

	/**
	 * Provides all numerics on the node.
	 *
	 * @return All numerics on node
	 */
	public List<NNumeric> getNumerics() {

		return getTypeAttributes(NNumeric.class);
	}

	/**
	 * Provides the corresponding concept-level frame, for nodes
	 * that have been directly derived from either concept-level
	 * or instance-level frames.
	 *
	 * @return Corresponding concept-level frame, or null if not
	 * applicable
	 */
	public CFrame getCFrame() {

		return cFrame;
	}

	/**
	 * Provides the corresponding instance-level frame, for nodes
	 * that have been directly derived from such frames.
	 *
	 * @return Corresponding instance-level frame, or null if not
	 * applicable
	 */
	public IFrame getIFrame() {

		return iFrame;
	}

	/**
	 * Tests whether the node/link network emanating from this node
	 * contains any cycles.
	 *
	 * @return True if cycles detected
	 */
	public boolean leadsToCycle() {

		return new CycleTester(this).leadsToCycle();
	}

	NNode(CFrame cFrame) {

		this.cFrame = cFrame;

		if (cFrame.getCategory().disjunction()) {

			addConceptDisjuncts(cFrame.getSubs());
		}
		else {

			addConceptDisjunct(cFrame);
		}
	}

	void setIFrame(IFrame iFrame) {

		this.iFrame = iFrame;
	}

	private void addConceptDisjuncts(List<CFrame> cFrames) {

		for (CFrame cFrame : cFrames) {

			addConceptDisjunct(cFrame);
		}
	}

	private void addConceptDisjunct(CFrame cFrame) {

		conceptDisjuncts.add(cFrame.getIdentity());
	}

	private boolean equalsNode(NNode other) {

		return conceptDisjuncts.equals(other.conceptDisjuncts)
					&& attributes.equals(other.attributes);
	}

	private int calcHashCode() {

		return conceptDisjuncts.hashCode() + attributes.hashCode();
	}

	private <A extends NAttribute<?>>List<A> getTypeAttributes(Class<A> type) {

		List<A> typeAttrs = new ArrayList<A>();

		for (NAttribute<?> attribute : attributes) {

			if (attribute.getClass() == type) {

				typeAttrs.add(type.cast(attribute));
			}
		}

		return typeAttrs;
	}
}
