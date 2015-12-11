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
public class NNode extends NEntity {

	static private List<CIdentity> getTypeDisjuncts(CFrame cFrame) {

		List<CIdentity> typeDisjuncts = new ArrayList<CIdentity>();

		if (cFrame.getCategory().disjunction()) {

			for (CFrame cSub : cFrame.getSubs()) {

				typeDisjuncts.add(cSub.getIdentity());
			}
		}
		else {

			typeDisjuncts.add(cFrame.getIdentity());
		}

		return typeDisjuncts;
	}

	private References<NFeature<?>> features = new References<NFeature<?>>();

	private CFrame cFrame = null;
	private IFrame iFrame = null;

	/**
	 * Constructor for nodes with atomic type.
	 *
	 * @param type Atomic type for node
	 */
	public NNode(CIdentity type) {

		super(type);
	}

	/**
	 * Constructor for nodes with disjunction type.
	 *
	 * @param typeDisjuncts Disjuncts of type for node
	 * @throws KAccessException if specified collection is empty
	 */
	public NNode(Collection<CIdentity> typeDisjuncts) {

		super(typeDisjuncts);
	}

	/**
	 * Sets a disjunction type for the node.
	 *
	 * @param typeDisjuncts Disjuncts of type for node
	 * @throws KAccessException if specified collection is empty
	 */
	public void setDisjunctionType(Collection<CIdentity> typeDisjuncts) {

		setTypeDisjuncts(typeDisjuncts);
	}

	/**
	 * Adds disjunct to the node type.
	 *
	 * @param typeDisjunct Disjunct to add
	 */
	public void addTypeDisjunct(CIdentity typeDisjunct) {

		Collection<CIdentity> disjuncts = getTypeDisjuncts();

		disjuncts.add(typeDisjunct);
		setTypeDisjuncts(disjuncts);
	}

	/**
	 * Removes disjunct from the node type.
	 *
	 * @param typeDisjunct Disjunct to remove
	 */
	public void removeTypeDisjunct(CIdentity typeDisjunct) {

		Collection<CIdentity> disjuncts = getTypeDisjuncts();

		disjuncts.remove(typeDisjunct);
		setTypeDisjuncts(disjuncts);
	}

	/**
	 * Adds an feature to the node.
	 *
	 * @param feature Feature to add
	 */
	public void addFeature(NFeature<?> feature) {

		features.add(feature);
	}

	/**
	 * Removes an feature from the node.
	 *
	 * @param feature Feature to remove
	 */
	public void removeFeature(NFeature<?> feature) {

		features.remove(feature);
	}

	/**
	 * Removes all features from the node.
	 */
	public void clearFeatures() {

		features.clear();
	}

	/**
	 * Checks whether the node has any features.
	 *
	 * @return True if node has features
	 */
	public boolean hasFeatures() {

		return !features.isEmpty();
	}

	/**
	 * Provides all features on the node, including both links and
	 * numerics.
	 *
	 * @return All features on node
	 */
	public List<NFeature<?>> getFeatures() {

		return new ArrayList<NFeature<?>>(features);
	}

	/**
	 * Provides all links on the node.
	 *
	 * @return All links on node
	 */
	public List<NLink> getLinks() {

		return getTypeFeatures(NLink.class);
	}

	/**
	 * Provides all numerics on the node.
	 *
	 * @return All numerics on node
	 */
	public List<NNumeric> getNumerics() {

		return getTypeFeatures(NNumeric.class);
	}

	/**
	 * Provides the corresponding type-level frame, for nodes
	 * that have been directly derived from either type-level
	 * or instance-level frames.
	 *
	 * @return Corresponding type-level frame, or null if not
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

		this(getTypeDisjuncts(cFrame));

		this.cFrame = cFrame;
	}

	void setIFrame(IFrame iFrame) {

		this.iFrame = iFrame;
	}

	References<?> getLateralReferences() {

		return features;
	}

	private <F extends NFeature<?>>List<F> getTypeFeatures(Class<F> type) {

		List<F> typeFeatures = new ArrayList<F>();

		for (NFeature<?> feature : features) {

			if (feature.getClass() == type) {

				typeFeatures.add(type.cast(feature));
			}
		}

		return typeFeatures;
	}
}
