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

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * @author Colin Puleston
 */
class IndividualIRIGenerator {

	static final String DEFAULT_ROOT_NAME = "TEMP";

	static private final String DEFAULT_NAMESPACE = "http://mekon.owl";
	static private final String NAME_REF_SECTION_PREFIX = "-REF-";

	private String namespace = DEFAULT_NAMESPACE;
	private String rootName = DEFAULT_ROOT_NAME;

	private ORFrame rootFrame = null;
	private int refCount = 0;

	void setNamespace(String namespace) {

		this.namespace = namespace;
	}

	void start(ORFrame rootFrame, String rootName) {

		this.rootName = rootName;

		start(rootFrame);
	}

	void start(ORFrame rootFrame) {

		this.rootFrame = rootFrame;

		refCount = 0;
	}

	IRI generateFor(ORFrame frame) {

		if (rootFrame == null) {

			throw new Error("Root-frame has not been set!");
		}

		return generate(frame == rootFrame ? "" : getNextNameRefSection());
	}

	String getRootName() {

		return rootName;
	}

	private IRI generate(String nameRefSection) {

		return IRI.create(namespace + '#' + rootName + nameRefSection);
	}

	private String getNextNameRefSection() {

		return NAME_REF_SECTION_PREFIX + refCount++;
	}
}