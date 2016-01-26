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
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
abstract class ORConfig implements ORConfigVocab {

	private ReasoningModel reasoningModel;
	private KConfigNode configNode;

	ORConfig(
		ReasoningModel reasoningModel,
		KConfigNode parentConfigNode,
		String rootId) {

		this.reasoningModel = reasoningModel;

		configNode = parentConfigNode.getChild(rootId);

		checkSetReasoningType();
		checkSetSemantics();
		checkEnableLogging();
	}

	abstract ORLogger getLogger();

	private void checkSetReasoningType() {

		OReasoningType reasoningType = getReasoningTypeOrNull();

		if (reasoningType != null) {

			reasoningModel.setReasoningType(reasoningType);
		}
	}

	private void checkSetSemantics() {

		ORSemantics semantics = new ORSemantics();
		KConfigNode semanticNode = configNode.getChildOrNull(SEMANTICS_ID);

		if (semanticNode != null) {

			setDefaultSemantics(semanticNode, semantics);
			setSemanticsOverrides(semanticNode, semantics);

			reasoningModel.setSemantics(semantics);
		}
	}

	private void setDefaultSemantics(
					KConfigNode semanticNode,
					ORSemantics semantics) {

		semantics.setDefaultWorld(getDefaultSemantics(semanticNode));
	}

	private void setSemanticsOverrides(
					KConfigNode semanticNode,
					ORSemantics semantics) {

		for (KConfigNode exNode : semanticNode.getChildren(EXCEPTION_PROP_ID)) {

			semantics.addExceptionProperty(getSemanticsExceptionPropURI(exNode));
		}
	}

	private void checkEnableLogging() {

		ORLoggingMode mode = getLoggingMode();

		if (mode != ORLoggingMode.DISABLED) {

			ORLogger logger = getLogger();

			logger.checkStart();

			if (mode != ORLoggingMode.TIMES_ONLY) {

				logger.setShowRequests(true);
				logger.setShowResults(true);
			}
		}
	}

	private OReasoningType getReasoningTypeOrNull() {

		 return configNode.getEnum(
			 		REASONING_TYPE_ATTR,
			 		OReasoningType.class,
			 		null);
	}

	private ORSemanticWorld getDefaultSemantics(KConfigNode semanticsNode) {

		return semanticsNode.getEnum(DEFAULT_SEMANTICS_ATTR, ORSemanticWorld.class);
	}

	private String getSemanticsExceptionPropURI(KConfigNode expPropNode) {

		return expPropNode.getString(SEMANICS_EXCEPTION_PROP_URI_ATTR);
	}

	private ORLoggingMode getLoggingMode() {

		return configNode.getEnum(LOGGING_MODE_ATTR, ORLoggingMode.class);
	}
}
