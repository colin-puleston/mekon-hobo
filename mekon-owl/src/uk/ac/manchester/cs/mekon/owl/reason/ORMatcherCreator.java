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

import uk.ac.manchester.cs.mekon_util.config.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class ORMatcherCreator implements ORConfigVocab {

	private KConfigNode parentConfigNode;

	ORMatcherCreator(KConfigNode parentConfigNode) {

		this.parentConfigNode = parentConfigNode;
	}

	ORMatcher create(OModel model) {

		return getConstructor().construct(model, parentConfigNode);
	}

	private KConfigObjectConstructor<ORMatcher> getConstructor() {

		return new KConfigObjectConstructor<ORMatcher>(getMatcherClass());
	}

	private Class<? extends ORMatcher> getMatcherClass() {

		return getConfigNode().getClass(MATCHER_CLASS_ATTR, ORMatcher.class);
	}

	private KConfigNode getConfigNode() {

		return parentConfigNode.getChild(MATCHER_ROOT_ID);
	}
}
