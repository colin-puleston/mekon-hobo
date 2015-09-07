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

package uk.ac.manchester.cs.mekon.owl.triples;

import uk.ac.manchester.cs.mekon.config.*;

/**
 * Represents the configuration for a {@link OTMatcher}.
 *
 * @author Colin Puleston
 */
public class OTConfig implements OTConfigVocab {

	static private final OTReasoningType DEFAULT_REASONING_TYPE = OTReasoningType.DL;

	private OTReasoningType reasoningType;

	/**
	 * Constructs configuration for matcher with default
	 * reasoning-type.
	 */
	public OTConfig() {

		this(DEFAULT_REASONING_TYPE);
	}

	/**
	 * Constructs configuration for matcher with specified
	 * reasoning-type.
	 *
	 * @param reasoningType Type of reasoning required from matcher
	 */
	public OTConfig(OTReasoningType reasoningType) {

		this.reasoningType = reasoningType;
	}

	/**
	 * Constructs configuration for matcher, with the configuration
	 * being defined via the appropriately-tagged child of the
	 * specified parent configuration-node.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 */
	public OTConfig(KConfigNode parentConfigNode) {

		reasoningType = getReasoningType(parentConfigNode);
	}

	/**
	 * Specifies the type of reasoning required from the matcher.
	 *
	 * @param reasoningType Type of reasoning required from matcher
	 */
	public OTReasoningType getReasoningType() {

		return reasoningType;
	}

	private OTReasoningType getReasoningType(KConfigNode parentConfigNode) {

		KConfigNode configNode = parentConfigNode.getChild(MATCHER_ROOT_ID);

		return configNode.getEnum(
					REASONING_TYPE_ATTR,
					OTReasoningType.class,
					DEFAULT_REASONING_TYPE);
	}
}
