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
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
class QueryCustomiser {

	private Map<CIdentity, IValueMatchCustomiser> valueMatchCustomisers
							= new HashMap<CIdentity, IValueMatchCustomiser>();

	private abstract class QueryProcessor  {

		boolean processFromNode(NNode node) {

			boolean anyCustomValues = false;

			for (NFeature<?> feature : node.getFeatures()) {

				if (hasCustomValue(feature)) {

					anyCustomValues |= true;

					onCustomValueFound(node, feature);
				}

				anyCustomValues |= processFromFeature(node, feature);
			}

			return anyCustomValues;
		}

		boolean processFromFeature(NNode node, NFeature<?> feature) {

			return feature instanceof NLink && processFromLink((NLink)feature);
		}

		boolean processFromLink(NLink link) {

			boolean anyCustomValues = false;

			for (NNode value : link.getValues()) {

				anyCustomValues |= processFromNode(value);
			}

			return anyCustomValues;
		}

		void onCustomValueFound(NNode node, NFeature<?> feature) {
		}

		private boolean hasCustomValue(NFeature<?> feature) {

			return feature.hasValues() && customMatchFeature(feature);
		}

		private boolean customMatchFeature(NFeature<?> feature) {

			return valueMatchCustomisers.keySet().contains(feature.getType());
		}
	}

	private class CustomValueRemover extends QueryProcessor {

		private NNode modifiedQuery;

		CustomValueRemover(NNode query) {

			modifiedQuery = query.copy();
		}

		NNode checkModify() {

			return processFromNode(modifiedQuery) ? modifiedQuery : null;
		}

		void onCustomValueFound(NNode node, NFeature<?> feature) {

			node.removeFeature(feature);
		}
	}

	private class CustomValueRetainer {

		private NNode modifiedQuery;
		private Set<NFeature<?>> requiredFeatures = new HashSet<NFeature<?>>();

		private class Pass extends QueryProcessor {

			Pass() {

				processFromNode(modifiedQuery);
			}
		}

		private class Pass1 extends Pass {

			boolean processFromLink(NLink link) {

				if (super.processFromLink(link)) {

					requiredFeatures.add(link);

					return true;
				}

				return false;
			}

			void onCustomValueFound(NNode node, NFeature<?> feature) {

				requiredFeatures.add(feature);
			}
		}

		private class Pass2 extends Pass {

			private boolean allRequired = false;

			boolean processFromFeature(NNode node, NFeature<?> feature) {

				if (allRequired) {

					requiredFeatures.add(feature);
				}

				return super.processFromFeature(node, feature);
			}

			boolean processFromLink(NLink link) {

				if (!allRequired && allRequiredFrom(link)) {

					allRequired = true;
					super.processFromLink(link);
					allRequired = false;

					return true;
				}

				return super.processFromLink(link);
			}

			private boolean allRequiredFrom(NLink link) {

				return repeatableTypesLink(link) && requiredFeatures.contains(link);
			}

			private boolean repeatableTypesLink(NLink link) {

				return link.getCSlot().getCardinality().repeatableTypes();
			}
		}

		private class Pass3 extends Pass {

			boolean processFromFeature(NNode node, NFeature<?> feature) {

				if (requiredFeatures.contains(feature)) {

					return super.processFromFeature(node, feature);
				}

				node.removeFeature(feature);

				return true;
			}
		}

		CustomValueRetainer(NNode query) {

			modifiedQuery = query.copy();
		}

		NNode modify() {

			new Pass1();
			new Pass2();
			new Pass3();

			return modifiedQuery;
		}
	}

	QueryCustomiser(List<IValueMatchCustomiser> valueMatchCustomisers) {

		for (IValueMatchCustomiser customiser : valueMatchCustomisers) {

			addValueMatchCustomiser(customiser);
		}
	}

	void addValueMatchCustomiser(IValueMatchCustomiser customiser) {

		for (CIdentity slotId : customiser.getSlotIds()) {

			valueMatchCustomisers.put(slotId, customiser);
		}
	}

	CustomisedQuery checkCustomise(NNode query) {

		NNode coreQuery = new CustomValueRemover(query).checkModify();

		if (coreQuery != null) {

			NNode customQuery = new CustomValueRetainer(query).modify();

			return new CustomisedQuery(coreQuery, customQuery);
		}

		return null;
	}

	<M extends ICustomValueMatcher>M lookForCustomValueMatcher(
											CIdentity slotId,
											Class<M> expectClass) {

		IValueMatchCustomiser customiser = valueMatchCustomisers.get(slotId);

		if (customiser == null) {

			return null;
		}

		return castCustomValueMatcher(customiser.getMatcher(), slotId, expectClass);
	}

	private <M extends ICustomValueMatcher>M castCustomValueMatcher(
												ICustomValueMatcher matcher,
												CIdentity slotId,
												Class<M> expectClass) {

		Class<?> foundClass = matcher.getClass();

		if (expectClass.isAssignableFrom(foundClass)) {

			return expectClass.cast(matcher);
		}

		throw new KSystemConfigException(
					"Invalid matcher specified for slot-type: " + slotId
					+ " (Expected type: " + expectClass
					+ ", found type: " + foundClass + ")");
	}
}
