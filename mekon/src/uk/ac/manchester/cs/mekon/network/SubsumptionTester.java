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
	private NumericsTester numericsTester = new NumericsTester();

	private KSetMap<NNode, NNode> testing = new KSetMap<NNode, NNode>();
	private KSetMap<NNode, NNode> subsumptions = new KSetMap<NNode, NNode>();

	private abstract class AttributesTester<V, A extends NAttribute<V>> {

		boolean subsumptions(NNode node1, NNode node2) {

			List<A> attrs2 = getAttributes(node2);

			for (A attr1 : getAttributes(node1)) {

				if (!anySubsumptions(attr1, attrs2)) {

					return false;
				}
			}

			return true;
		}

		abstract List<A> getAttributes(NNode node);

		abstract boolean valueSubsumption(V value1, V value2);

		private boolean anySubsumptions(A attr1, List<A> attrs2) {

			for (A attr2 : attrs2) {

				if (subsumption(attr1, attr2)) {

					return true;
				}
			}

			return false;
		}

		private boolean subsumption(A attr1, A attr2) {

			return equalProperties(attr1, attr2)
					&& valueSubsumptions(attr1, attr2);
		}

		private boolean valueSubsumptions(A attr1, A attr2) {

			List<V> values2 = attr1.getValues();

			for (V value1 : attr2.getValues()) {

				if (!anyValueSubsumptions(value1, values2)) {

					return false;
				}
			}

			return true;
		}

		private boolean anyValueSubsumptions(V value1, List<V> values2) {

			for (V value2 : values2) {

				if (valueSubsumption(value1, value2)) {

					return true;
				}
			}

			return false;
		}

		private boolean equalProperties(A attr1, A attr2) {

			return attr1.getProperty().equals(attr2.getProperty());
		}
	}

	private class LinksTester extends AttributesTester<NNode, NLink> {

		List<NLink> getAttributes(NNode node) {

			return node.getLinks();
		}

		boolean valueSubsumption(NNode value1, NNode value2) {

			return SubsumptionTester.this.subsumption(value1, value2);
		}
	}

	private class NumericsTester extends AttributesTester<INumber, NNumeric> {

		List<NNumeric> getAttributes(NNode node) {

			return node.getNumerics();
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

		return typeSubsumption(node1, node2)
				&& linksTester.subsumptions(node1, node2)
				&& numericsTester.subsumptions(node1, node2);
	}

	private boolean typeSubsumption(NNode node1, NNode node2) {

		CFrame cFrame1 = node1.getCFrame();
		CFrame cFrame2 = node2.getCFrame();

		if (cFrame1 != null && cFrame2 != null) {

			return cFrame1.subsumes(cFrame2);
		}

		return conceptsSubsumption(node1, node2);
	}

	private boolean conceptsSubsumption(NNode node1, NNode node2) {

		Set<CIdentity> disjuncts1 = node1.getConceptDisjuncts();
		Set<CIdentity> disjuncts2 = node2.getConceptDisjuncts();

		return disjuncts2.containsAll(disjuncts1);
	}
}
