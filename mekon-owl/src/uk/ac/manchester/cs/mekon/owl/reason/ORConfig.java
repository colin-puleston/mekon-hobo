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

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * @author Colin Puleston
 */
abstract class ORConfig implements ORConfigVocab {

	private KConfigNode configNode;

	ORConfig(KConfigNode parentConfigNode, String rootId) {

		configNode = parentConfigNode.getChild(rootId);
	}

	void configure(ORSlotSemantics slotSemantics, ORLogger logger) {

		checkSetSlotSemantics(slotSemantics);
		checkEnableLogging(logger);
	}

	KConfigNode getConfigNode() {

		return configNode;
	}

	private void checkSetSlotSemantics(ORSlotSemantics slotSemantics) {

		KConfigNode slotSemsNode = configNode.getChildOrNull(SEMANTICS_ID);

		if (slotSemsNode != null) {

			setSlotSemanticsDefault(slotSemsNode, slotSemantics);
			setSlotSemanticsOverrides(slotSemsNode, slotSemantics);
		}
	}

	private void setSlotSemanticsDefault(
					KConfigNode slotSemsNode,
					ORSlotSemantics slotSemantics) {

		slotSemantics.setDefaultSemantics(getDefaultSemantics(slotSemsNode));
	}

	private void setSlotSemanticsOverrides(
					KConfigNode node,
					ORSlotSemantics slotSemantics) {

		for (KConfigNode expPropNode : node.getChildren(EXCEPTION_PROP_ID)) {

			slotSemantics.addExceptionProperty(getExceptionPropertyURI(expPropNode));
		}
	}

	private ORSemantics getDefaultSemantics(KConfigNode semanticsNode) {

		return semanticsNode.getEnum(DEFAULT_SEMANTICS_ATTR, ORSemantics.class);
	}

	private void checkEnableLogging(ORLogger logger) {

		ORLoggingMode mode = getLoggingMode();

		if (mode != ORLoggingMode.DISABLED) {

			logger.start();

			if (mode != ORLoggingMode.TIMES_ONLY) {

				logger.setShowRequests(true);
				logger.setShowResults(true);
			}
		}
	}

	private ORLoggingMode getLoggingMode() {

		return getConfigNode().getEnum(LOGGING_MODE_ATTR, ORLoggingMode.class);
	}

	private String getExceptionPropertyURI(KConfigNode expPropNode) {

		return expPropNode.getString(EXCEPTION_PROP_URI_ATTR);
	}
}