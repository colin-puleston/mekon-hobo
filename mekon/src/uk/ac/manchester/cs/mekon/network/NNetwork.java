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
import uk.ac.manchester.cs.mekon.model.util.*;

/**
 * Provides an intermediate network-based representation, which provides
 * a more malleable alternative to the standard MEKON frames representation,
 * for use by specific implementations of {@link IReasoner} and {@link
 * IMatcher}. The node/link  networks are generated from the corresponding
 * frame/slot networks.
 *
 * @author Colin Puleston
 */
public class NNetwork {

	private NNode rootNode;

	private class Creator extends IFrameConverter<NNode, NFeature<?>> {

		private abstract class NFeatureCreator
									<V, F extends NFeature<V>, IV>
									extends TypeISlotConverter<IV> {

			protected void convert(NNode node, ISlot slot, List<IV> iValues) {

				addFeatureSet(node, slot, iValues);
			}

			protected void convert(NNode node, CIdentity slotId, List<IV> iValues) {

				addFeature(node, slotId, null, iValues);
			}

			void addFeatureSet(NNode node, ISlot slot, List<IV> iValues) {

				addFeature(node, slot, iValues);
			}

			F addFeature(NNode node, ISlot slot, List<IV> iValues) {

				return addFeature(node, slot.getType().getIdentity(), slot, iValues);
			}

			abstract V getValue(IV iValue);

			abstract F createFeature(CIdentity slotId, ISlot slot);

			abstract void addFeature(NNode node, F feature);

			private F addFeature(NNode node, CIdentity id, ISlot slot, List<IV> iValues) {

				F feature = createFeature(id, slot);

				for (IV iValue : iValues) {

					feature.addValue(getValue(iValue));
				}

				addFeature(node, feature);

				return feature;
			}
		}

		private abstract class NLinkCreator<IV>
									extends
										NFeatureCreator<NNode, NLink, IV> {

			NLink createFeature(CIdentity id, ISlot slot) {

				return new NLink(id, slot);
			}

			void addFeature(NNode node, NLink feature) {

				node.addFeature(feature);
			}
		}

		private class IFrameSlotsNLinkCreator extends NLinkCreator<IFrame> {

			void addFeatureSet(NNode node, ISlot slot, List<IFrame> iValues) {

				List<IFrame> iConjunctionValues = new ArrayList<IFrame>();

				for (IFrame iValue : iValues) {

					if (iValue.getCategory().disjunction()) {

						checkRenderDisjunction(node, slot, iValue);
					}
					else {

						iConjunctionValues.add(iValue);
					}
				}

				addFeature(node, slot, iConjunctionValues);
			}

			NNode getValue(IFrame iValue) {

				return getFrameConversion(iValue);
			}

			private void checkRenderDisjunction(NNode node, ISlot slot, IFrame iValue) {

				List<IFrame> disjuncts = iValue.asDisjuncts();

				if (!disjuncts.isEmpty()) {

					addFeature(node, slot, disjuncts).setDisjunctionLink(true);
				}
			}
		}

		private class CFrameSlotsNLinkCreator extends NLinkCreator<CFrame> {

			NNode getValue(CFrame iValue) {

				NNode value = new NNode(iValue);

				configureFrameConversionType(value, iValue);

				return value;
			}
		}

		private class NNumberCreator
							extends
								NFeatureCreator<INumber, NNumber, INumber> {

			INumber getValue(INumber iValue) {

				return iValue;
			}

			NNumber createFeature(CIdentity id, ISlot slot) {

				return new NNumber(id, slot);
			}

			void addFeature(NNode node, NNumber feature) {

				node.addFeature(feature);
			}
		}

		protected TypeISlotConverter<IFrame> createIFrameSlotConverter() {

			return new IFrameSlotsNLinkCreator();
		}

		protected TypeISlotConverter<CFrame> createCFrameSlotConverter() {

			return new CFrameSlotsNLinkCreator();
		}

		protected TypeISlotConverter<INumber> createINumberSlotConverter() {

			return new NNumberCreator();
		}

		protected TypeISlotConverter<IString> createIStringSlotConverter() {

			return createTypeISlotNonConverter(IString.class);
		}

		protected NNode createUnconfiguredFrameConversion(IFrame frame) {

			return new NNode(frame);
		}
	}

	/**
	 * Constructor that creates a network corresponding to the specified
	 * frame/slot network.
	 *
	 * @param rootFrame Root-frame in the frame/slot network
	 */
	public NNetwork(IFrame rootFrame) {

		rootNode = new Creator().convert(rootFrame);
	}

	/**
	 * Provides the root-node of the node/link network.
	 *
	 * @return Root-node of network
	 */
	public NNode getRootNode() {

		return rootNode;
	}
}
