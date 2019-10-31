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

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class InstanceRefExpander {

	private NMatcher matcher;

	private ExpansionAdder expansionAdder = new ExpansionAdder();
	private InstanceRefExpansionTracker expansionTracker;

	private class ExpansionAdder extends NCrawler {

		protected void visit(NNode node) {
		}

		protected void visit(NLink link) {

			addExpansions(link);
		}

		protected void visit(NNumber number) {
		}

		protected void visit(NString string) {
		}
	}

	InstanceRefExpander(NMatcher matcher, NNode rootInstance) {

		this.matcher = matcher;

		expansionTracker = new InstanceRefExpansionTracker(rootInstance);

		expansionAdder.process(rootInstance);
	}

	private void addExpansions(NLink link) {

		for (NNode valueNode : link.getValues()) {

			if (valueNode.instanceRef()) {

				addExpansion(link, valueNode.getInstanceRef());
			}
		}
	}

	private void addExpansion(NLink link, CIdentity instanceRef) {

		NNode refedNode = matcher.getReferencedInstanceNodeOrNull(instanceRef);

		if (refedNode != null && expansionTracker.startExpansion(link, refedNode)) {

			link.addValue(refedNode);

			expansionAdder.process(refedNode);
			expansionTracker.endExpansion();
		}
	}
}
