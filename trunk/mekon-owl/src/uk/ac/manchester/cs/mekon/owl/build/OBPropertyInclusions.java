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

package uk.ac.manchester.cs.mekon.owl.build;

import org.semanticweb.owlapi.model.*;

/**
 * Represents a set of OWL properties to be included in the model.
 *
 * @author Colin Puleston
 */
public class OBPropertyInclusions extends OBEntityGroup {

	private boolean mirrorAsFrames = false;
	private boolean abstractAssertables = false;

	/**
	 * Constructor.
	 *
	 * @param rootPropertyIRI IRI of root-property
	 */
	public OBPropertyInclusions(IRI rootPropertyIRI) {

		super(rootPropertyIRI);
	}

	/**
	 * Sets the flag that specifies whether for every property
	 * in this group there will be created, in addition to the
	 * frames-model property, a corresponding frame with the same
	 * IRI-derived identifier. By default will be set to false.
	 *
	 * @param mirrorAsFrames True if each created frames-model
	 * property should be mirrored by a corresponding frame
	 */
	public void setMirrorAsFrames(boolean mirrorAsFrames) {

		this.mirrorAsFrames = mirrorAsFrames;
	}

	/**
	 * Sets the flag that specifies whether the frames-model
	 * property that will be created for each property in this
	 * group will be {@link CProperty#abstractAssertable}. By
	 * default will be set to false.
	 *
	 * @param abstractAssertables True if each created property
	 * should be abstract-assertable
	 */
	public void setAbstractAssertables(boolean abstractAssertables) {

		this.abstractAssertables = abstractAssertables;
	}

	boolean mirrorAsFrames() {

		return mirrorAsFrames;
	}

	boolean abstractAssertables() {

		return abstractAssertables;
	}
}
