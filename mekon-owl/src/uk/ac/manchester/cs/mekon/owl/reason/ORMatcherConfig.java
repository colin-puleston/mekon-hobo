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

import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
class ORMatcherConfig extends ORConfig {

	static boolean configNodeExists(KConfigNode parentConfigNode) {

		return parentConfigNode.getChildOrNull(MATCHER_ROOT_ID) != null;
	}

	ORMatcherConfig(OModel model, KConfigNode parentConfigNode) {

		super(model, parentConfigNode);
	}

	void checkConfigPersistentInstances(ORMatcher matcher) {

		String file = getInstancePersistenceFileOrNull();

		if (file != null) {

			matcher.setPersistentInstances(file);
		}
	}

	String getRootId() {

		return MATCHER_ROOT_ID;
	}

	ORLogger getLogger() {

		return ORMatcherLogger.get();
	}

	private String getInstancePersistenceFileOrNull() {

		KConfigNode node = getInstancePersistenceNodeOrNull();

		return node != null ? node.getString(INSTANCES_FILE_NAME_ATTR) : null;
	}

	private KConfigNode getInstancePersistenceNodeOrNull() {

		return getConfigNode().getChildOrNull(INSTANCE_PERSISTENCE_ID);
	}
}
