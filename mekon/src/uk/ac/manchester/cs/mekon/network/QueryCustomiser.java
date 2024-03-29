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

	private CustomValueRetainer customValueRetainer = new CustomValueRetainer();

	private abstract class QueryModifier  {

		private boolean anyPrunings = false;

		NNode modify(NNode query) {

			NNode copy = query.copy();

			prune(copy);

			return copy;
		}

		boolean anyPrunings() {

			return anyPrunings;
		}

		abstract void prune(NNode node);

		void pruneLinkValues(NNode node) {

			for (NLink link : node.getLinks()) {

				for (NNode value : link.getValues()) {

					prune(value);
				}
			}
		}

		void pruneFeatures(NNode node) {

			for (NFeature<?> feature : node.getFeatures()) {

				if (prunableFeature(feature)) {

					node.removeFeature(feature);

					anyPrunings |= true;
				}
			}
		}

		abstract boolean prunableFeature(NFeature<?> feature);
	}

	private class CustomValueRemover extends QueryModifier {

		void prune(NNode node) {

			pruneFeatures(node);
			pruneLinkValues(node);
		}

		boolean prunableFeature(NFeature<?> feature) {

			return customMatchFeature(feature);
		}
	}

	private class CustomValueRetainer extends QueryModifier {

		void prune(NNode node) {

			pruneLinkValues(node);
			pruneFeatures(node);
		}

		boolean prunableFeature(NFeature<?> feature) {

			return !populatedLink(feature) && !customMatchFeature(feature);
		}

		private boolean populatedLink(NFeature<?> feature) {

			return feature instanceof NLink && ((NLink)feature).hasValues();
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

		CustomValueRemover remover = new CustomValueRemover();
		NNode coreQuery = remover.modify(query);

		if (remover.anyPrunings()) {

			return new CustomisedQuery(coreQuery, customValueRetainer.modify(query));
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

	private boolean customMatchFeature(NFeature<?> feature) {

		return valueMatchCustomisers.keySet().contains(feature.getType());
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
