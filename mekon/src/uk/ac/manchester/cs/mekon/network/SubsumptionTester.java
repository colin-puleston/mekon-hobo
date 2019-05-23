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
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class SubsumptionTester {

	private LinksTester linksTester = new LinksTester();
	private NumbersTester numbersTester = new NumbersTester();

	private KSetMap<NNode, NNode> testing = new KSetMap<NNode, NNode>();
	private KSetMap<NNode, NNode> subsumptions = new KSetMap<NNode, NNode>();

	private abstract class FeaturesTester<V, F extends NFeature<V>> {

		boolean subsumptions(NNode node1, NNode node2) {

			List<F> features2 = getValueFeatures(node2);

			for (F feature1 : getValueFeatures(node1)) {

				if (!anySubsumptions(feature1, features2)) {

					return false;
				}
			}

			return true;
		}

		boolean valueSubsumptions(F feature1, F feature2) {

			List<V> values2 = feature2.getValues();

			for (V value1 : feature1.getValues()) {

				if (!anyValueSubsumptions(value1, values2)) {

					return false;
				}
			}

			return true;
		}

		boolean anyValueSubsumptions(F feature1, F feature2) {

			List<V> values2 = feature2.getValues();

			for (V value1 : feature1.getValues()) {

				if (anyValueSubsumptions(value1, values2)) {

					return true;
				}
			}

			return false;
		}

		abstract List<F> getFeatures(NNode node);

		abstract boolean valueSubsumption(V value1, V value2);

		private List<F> getValueFeatures(NNode node) {

			List<F> valueFeatures = new ArrayList<F>();

			for (F feature : getFeatures(node)) {

				if (feature.hasValues()) {

					valueFeatures.add(feature);
				}
			}

			return valueFeatures;
		}

		private boolean anySubsumptions(F feature1, List<F> features2) {

			for (F feature2 : features2) {

				if (subsumption(feature1, feature2)) {

					return true;
				}
			}

			return false;
		}

		private boolean subsumption(F feature1, F feature2) {

			return equalTypes(feature1, feature2)
					&& valueSubsumptions(feature1, feature2);
		}

		private boolean anyValueSubsumptions(V value1, List<V> values2) {

			for (V value2 : values2) {

				if (valueSubsumption(value1, value2)) {

					return true;
				}
			}

			return false;
		}

		private boolean equalTypes(F feature1, F feature2) {

			return feature1.getType().equals(feature2.getType());
		}
	}

	private class LinksTester extends FeaturesTester<NNode, NLink> {

		boolean valueSubsumptions(NLink feature1, NLink feature2) {

			if (feature1.disjunctionLink() && !feature2.disjunctionLink()) {

				return anyValueSubsumptions(feature1, feature2);
			}

			return super.valueSubsumptions(feature1, feature2);
		}

		List<NLink> getFeatures(NNode node) {

			return node.getLinks();
		}

		boolean valueSubsumption(NNode value1, NNode value2) {

			return SubsumptionTester.this.subsumption(value1, value2);
		}
	}

	private class NumbersTester extends FeaturesTester<INumber, NNumber> {

		List<NNumber> getFeatures(NNode node) {

			return node.getNumbers();
		}

		boolean valueSubsumption(INumber value1, INumber value2) {

			return value1.getType().subsumes(value2.getType());
		}
	}

	boolean subsumption(NNode node1, NNode node2) {

		if (testing.getSet(node1).contains(node2)) {

			return true;
		}

		if (subsumptions.getSet(node1).contains(node2)) {

			return true;
		}

		boolean subsumption = false;

		testing.add(node1, node2);

		if (nodeSubsumption(node1, node2)) {

			subsumption = true;
			subsumptions.add(node1, node2);
		}

		testing.remove(node1, node2);

		return subsumption;
	}

	private boolean nodeSubsumption(NNode node1, NNode node2) {

		if (node1.instanceReference()) {

			return node2.instanceReference()
					&& node1.getInstanceRef().equals(node2.getInstanceRef());
		}

		return typeSubsumption(node1, node2)
				&& linksTester.subsumptions(node1, node2)
				&& numbersTester.subsumptions(node1, node2);
	}

	private boolean typeSubsumption(NNode node1, NNode node2) {

		CFrame cFrame1 = node1.getCFrame();
		CFrame cFrame2 = node2.getCFrame();

		if (cFrame1 != null && cFrame2 != null) {

			return cFrame1.subsumes(cFrame2);
		}

		return node2.getTypeDisjuncts().containsAll(node1.getTypeDisjuncts());
	}
}
