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
 * Represents a set of OWL properties defined via a single
 * root-property. The set will include all descendant properties
 * of the root-property, and optionally the root-property itself.
 *
 * @author Colin Puleston
 */
public class OSPropertyGroup extends OSEntityGroup {

	private boolean mirrorAsFrames = true;

	/**
	 * Constructor.
	 *
	 * @param rootPropertyIRI IRI of root-property
	 */
	public OSPropertyGroup(IRI rootPropertyIRI) {

		super(rootPropertyIRI);
	}

	/**
	 * Sets the flag that specifies whether for every property
	 * in this group there will be created, in addition to the
	 * frames-model property, a corresponding frame with the same
	 * IRI-derived identifier.
	 *
	 * @param mirrorAsFrames True if each created frames-model
	 * property should be mirrrored by a corresponding frame
	 */
	public void setMirrorAsFrames(boolean mirrorAsFrames) {

		this.mirrorAsFrames = mirrorAsFrames;
	}

	boolean mirrorAsFrames() {

		return mirrorAsFrames;
	}
}
