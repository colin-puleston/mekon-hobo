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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.*;

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

	private List<NFeature<?>> features = new ArrayList<NFeature<?>>();

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
	 * Provides a copy of the network emanating from the node.
	 *
	 * @return Copy of network emanating from node
	 */
	public NNode copy() {

		NNode copy = new NNode(getTypeDisjuncts());

		copy.cFrame = cFrame;
		copy.iFrame = iFrame;

		for (NFeature<?> feature : features) {

			copy.features.add(feature.copy());
		}

		return copy;
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

		Collection<CIdentity> typeDisjuncts = getTypeDisjuncts();

		if (!typeDisjuncts.contains(typeDisjunct)) {

			typeDisjuncts.add(typeDisjunct);
			setTypeDisjuncts(typeDisjuncts);
		}
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
	 * Uses the specified type-level frame to reset the type-disjuncts
	 * for the node, and also to reset the nodes corresponding
	 * type-level frame accordingly.
	 *
	 * @param cFrame Type-level frame to use for update
	 */
	public void reset(CFrame cFrame) {

		this.cFrame = cFrame;

		setTypeDisjuncts(getTypeDisjuncts(cFrame));
	}

	/**
	 * Tests whether the node represents a reference to a specific
	 * existing instance, which will be the case if and only of it
	 * has been directly derived from an instance-level frame of
	 * {@link IFrameCategory#REFERENCE} category.
	 *
	 * @return True if node represents reference to specific existing
	 * instance
	 */
	public boolean instanceRef() {

		return iFrame != null && iFrame.getCategory().reference();
	}

	/**
	 * Provides the identity of the referenced instance for nodes
	 * that represent references to specific existing instances.
	 *
	 * @return Identity of referenced instance, or null if not
	 * applicable
	 */
	public CIdentity getInstanceRef() {

		return instanceRef() ? iFrame.getReferenceId() : null;
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
	 * Provides all features on the node, including both links
	 * and data-valued features.
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
	 * Provides all number-valued features on the node.
	 *
	 * @return All number-valued features on node
	 */
	public List<NNumber> getNumbers() {

		return getTypeFeatures(NNumber.class);
	}

	/**
	 * Provides all string-valued features on the node.
	 *
	 * @return All string-valued features on node
	 */
	public List<NString> getStrings() {

		return getTypeFeatures(NString.class);
	}

	/**
	 * Provides the corresponding type-level frame for nodes
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
	 * Provides the corresponding instance-level frame for nodes
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

	/**
	 * Tests whether the type and the current feature-values of this
	 * node subsume those of another node. For link features,
	 * value-subsumption testing involves a recursive invocation of
	 * the same node-subsumption testing operation. For number-valued
	 * features, value-subsumption is determinied via invocation of
	 * the {@link CNumber#subsumes} method, and for string-valued
	 * features subsumption means string-equality.
	 *
	 * @param other Node to test for structure-subsumption by this
	 * one
	 * @return true if this nodes structure subsumes that of other
	 * node
	 */
	public boolean subsumesStructure(NNode other) {

		return new StructureSubsumptionTester().subsumption(this, other);
	}

	NNode(CFrame cFrame) {

		this(getTypeDisjuncts(cFrame));

		this.cFrame = cFrame;
	}

	NNode(IFrame iFrame) {

		this(iFrame.getType());

		this.iFrame = iFrame;
	}

	void renderAttributes(NEntityRenderer renderer) {

		if (instanceRef()) {

			renderer.addLine("INSTANCE:" + getInstanceRef());
		}
		else {

			for (NFeature<?> feature : features) {

				if (feature.hasValues()) {

					feature.render(renderer);
				}
			}
		}
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
