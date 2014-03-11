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

package uk.ac.manchester.cs.mekon.owl.sanctions;

import org.semanticweb.owlapi.model.*;

/**
 * Represents a set of OWL entities defined via a single root-entity.
 * The set will include all descendant entities of the root-entity,
 * and optionally the root-entity itself.
 *
 * @author Colin Puleston
 */
public class OSEntityGroup {

	private IRI rootEntityIRI;
	private boolean includesRoot = true;

	/**
	 * Constructor.
	 *
	 * @param rootEntityIRI IRI of root-entity
	 */
	public OSEntityGroup(IRI rootEntityIRI) {

		this.rootEntityIRI = rootEntityIRI;
	}

	/**
	 * Sets the root-entity inclusion flag.
	 *
	 * @param includesRoot True if group includes root-entity
	 */
	public void setIncludesRoot(boolean includesRoot) {

		this.includesRoot = includesRoot;
	}

	IRI getRootEntityIRI() {

		return rootEntityIRI;
	}

	boolean includesRoot() {

		return includesRoot;
	}
}
