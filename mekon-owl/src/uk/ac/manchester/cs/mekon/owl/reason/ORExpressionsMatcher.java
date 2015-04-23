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

package uk.ac.manchester.cs.mekon.owl.reason;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * Extension of {@link ORMatcher} that represents the instances
 * as a set of class-expressions, and the queries also as
 * class-expressions.
 *
 * @author Colin Puleston
 */
public class ORExpressionsMatcher extends ORMatcher {

	private Map<CFrame, InstanceGroup> instanceGroups
					= new HashMap<CFrame, InstanceGroup>();

	private class InstanceGroup {

		private CFrame frameType;
		private Map<CIdentity, ConceptExpression> instanceExprs
						= new HashMap<CIdentity, ConceptExpression>();

		InstanceGroup(CFrame frameType) {

			this.frameType = frameType;
		}

		void add(ORFrame instance, CIdentity identity) {

			instanceExprs.put(identity, createConceptExpression(instance));
		}

		boolean checkRemove(CIdentity identity) {

			return instanceExprs.remove(identity) != null;
		}

		boolean contains(CIdentity identity) {

			return instanceExprs.containsKey(identity);
		}

		void collectMatches(ConceptExpression queryExpr, List<CIdentity> matches) {

			CFrame queryFrameType = queryExpr.getFrame().getCFrame();

			if (queryFrameType.subsumes(frameType)) {

				for (CIdentity id : instanceExprs.keySet()) {

					if (queryExpr.subsumes(instanceExprs.get(id))) {

						matches.add(id);
					}
				}
			}
		}
	}

	/**
	 * Constructs matcher for specified model.
	 *
	 * @param model Model over which matcher is to operate
	 */
	public ORExpressionsMatcher(OModel model) {

		super(model);
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized boolean remove(CIdentity identity) {

		for (InstanceGroup group : instanceGroups.values()) {

			if (group.checkRemove(identity)) {

				return true;
			}
		}

		return false;
	}

	void add(ORFrame instance, CIdentity identity) {

		CFrame frameType = instance.getCFrame();
		InstanceGroup group = instanceGroups.get(frameType);

		if (group == null) {

			group = new InstanceGroup(frameType);
			instanceGroups.put(frameType, group);
		}

		group.add(instance, identity);
	}

	boolean contains(CIdentity identity) {

		for (InstanceGroup group : instanceGroups.values()) {

			if (group.contains(identity)) {

				return true;
			}
		}

		return false;
	}

	List<CIdentity> match(ConceptExpression queryExpr) {

		List<CIdentity> matches = new ArrayList<CIdentity>();

		for (InstanceGroup group : instanceGroups.values()) {

			group.collectMatches(queryExpr, matches);
		}

		return matches;
	}

	boolean matches(ConceptExpression queryExpr, ORFrame instance) {

		return queryExpr.subsumes(createConceptExpression(instance));
	}

	private ConceptExpression createConceptExpression(ORFrame frame) {

		return new ConceptExpression(getModel(), frame);
	}
}
