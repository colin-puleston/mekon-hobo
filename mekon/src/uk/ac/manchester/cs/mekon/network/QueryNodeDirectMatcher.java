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
import uk.ac.manchester.cs.mekon.store.disk.*;

import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
class QueryNodeDirectMatcher {

	private QueryCustomiser queryCustomiser;

	private LinksMatcher linksMatcher = new LinksMatcher();
	private NumbersMatcher numbersMatcher = new NumbersMatcher();
	private StringsMatcher stringsMatcher = new StringsMatcher();

	private KSetMap<NNode, NNode> testing = new KSetMap<NNode, NNode>();
	private KSetMap<NNode, NNode> matches = new KSetMap<NNode, NNode>();

	private abstract class FeaturesMatcher<V, F extends NFeature<V>> {

		boolean matches(NNode query, NNode instance) {

			List<F> iFeatures = getValueFeatures(instance);

			for (F qFeature : getValueFeatures(query)) {

				if (!anyMatches(qFeature, iFeatures)) {

					return false;
				}
			}

			return true;
		}

		boolean valueMatches(F qFeature, F iFeature) {

			for (V qValue : qFeature.getValues()) {

				if (!anyValueMatches(qValue, iFeature)) {

					return false;
				}
			}

			return true;
		}

		boolean anyValueMatches(F qFeature, F iFeature) {

			for (V qValue : qFeature.getValues()) {

				if (anyValueMatches(qValue, iFeature)) {

					return true;
				}
			}

			return false;
		}

		abstract List<F> getFeatures(NNode node);

		abstract boolean valueMatch(CIdentity featureType, V qValue, V iValue);

		private List<F> getValueFeatures(NNode node) {

			List<F> valueFeatures = new ArrayList<F>();

			for (F feature : getFeatures(node)) {

				if (feature.hasValues()) {

					valueFeatures.add(feature);
				}
			}

			return valueFeatures;
		}

		private boolean anyMatches(F qFeature, List<F> iFeatures) {

			for (F iFeature : iFeatures) {

				if (match(qFeature, iFeature)) {

					return true;
				}
			}

			return false;
		}

		private boolean match(F qFeature, F iFeature) {

			return equalTypes(qFeature, iFeature) && valueMatches(qFeature, iFeature);
		}

		private boolean anyValueMatches(V qValue, F iFeature) {

			CIdentity featureType = iFeature.getType();

			for (V iValue : iFeature.getValues()) {

				if (valueMatch(featureType, qValue, iValue)) {

					return true;
				}
			}

			return false;
		}

		private boolean equalTypes(F qFeature, F iFeature) {

			return qFeature.getType().equals(iFeature.getType());
		}
	}

	private class LinksMatcher extends FeaturesMatcher<NNode, NLink> {

		boolean valueMatches(NLink qFeature, NLink iFeature) {

			if (qFeature.disjunctionLink() && !iFeature.disjunctionLink()) {

				return anyValueMatches(qFeature, iFeature);
			}

			return super.valueMatches(qFeature, iFeature);
		}

		List<NLink> getFeatures(NNode node) {

			return node.getLinks();
		}

		boolean valueMatch(CIdentity featureType, NNode qValue, NNode iValue) {

			ICustomFrameMatcher matcher = lookForCustomMatcher(featureType);

			if (matcher != null) {

				IFrame qFrame = qValue.getIFrame();
				IFrame iFrame = iValue.getIFrame();

				if (qFrame != null && iFrame != null ) {

					return matcher.matches(qFrame, iFrame);
				}
			}

			return QueryNodeDirectMatcher.this.matches(qValue, iValue);
		}

		private ICustomFrameMatcher lookForCustomMatcher(CIdentity featureType) {

			return lookForCustomValueMatcher(featureType, ICustomFrameMatcher.class);
		}
	}

	private class NumbersMatcher extends FeaturesMatcher<INumber, NNumber> {

		List<NNumber> getFeatures(NNode node) {

			return node.getNumbers();
		}

		boolean valueMatch(CIdentity featureType, INumber qValue, INumber iValue) {

			ICustomNumberMatcher matcher = lookForCustomMatcher(featureType);

			if (matcher != null) {

				return matcher.matches(qValue, iValue);
			}

			return qValue.getType().subsumes(iValue.getType());
		}

		private ICustomNumberMatcher lookForCustomMatcher(CIdentity featureType) {

			return lookForCustomValueMatcher(featureType, ICustomNumberMatcher.class);
		}
	}

	private class StringsMatcher extends FeaturesMatcher<String, NString> {

		List<NString> getFeatures(NNode node) {

			return node.getStrings();
		}

		boolean valueMatch(CIdentity featureType, String qValue, String iValue) {

			ICustomStringMatcher matcher = lookForCustomMatcher(featureType);

			if (matcher != null) {

				return matcher.matches(qValue, iValue);
			}

			return qValue.equals(iValue);
		}

		private ICustomStringMatcher lookForCustomMatcher(CIdentity featureType) {

			return lookForCustomValueMatcher(featureType, ICustomStringMatcher.class);
		}
	}

	QueryNodeDirectMatcher(QueryCustomiser queryCustomiser) {

		this.queryCustomiser = queryCustomiser;
	}

	boolean matches(NNode query, NNode instance) {

		if (testing.getSet(query).contains(instance)) {

			return true;
		}

		if (matches.getSet(query).contains(instance)) {

			return true;
		}

		boolean subsumption = false;

		testing.add(query, instance);

		if (nodeMatch(query, instance)) {

			subsumption = true;
			matches.add(query, instance);
		}

		testing.remove(query, instance);

		return subsumption;
	}

	private boolean nodeMatch(NNode query, NNode instance) {

		if (query.instanceRef()) {

			if (!instance.instanceRef()) {

				return false;
			}

			return query.getInstanceRef().equals(instance.getInstanceRef());
		}

		return typeSubsumption(query, instance)
				&& linksMatcher.matches(query, instance)
				&& numbersMatcher.matches(query, instance)
				&& stringsMatcher.matches(query, instance);
	}

	private boolean typeSubsumption(NNode query, NNode instance) {

		CFrame qFrame = query.getCFrame();
		CFrame iFrame = instance.getCFrame();

		if (qFrame != null && iFrame != null) {

			return qFrame.subsumes(iFrame);
		}

		return instance.getTypeDisjuncts().containsAll(query.getTypeDisjuncts());
	}

	private <M extends ICustomValueMatcher>M lookForCustomValueMatcher(
													CIdentity featureType,
													Class<M> expectClass) {

		return queryCustomiser.lookForCustomValueMatcher(featureType, expectClass);
	}
}
