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

package uk.ac.manchester.cs.mekon.model;

import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * Represents a model-property.
 *
 * @author Colin Puleston
 */
public class CProperty implements CIdentified {

	private CModel model;
	private CIdentity identity;

	private CAnnotations annotations = new CAnnotations(this);

	private class Editor implements CPropertyEditor {

		public void resetLabel(String newLabel) {

			identity = identity.deriveIdentity(newLabel);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return FEntityDescriber.entityToString(this, this);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayLabel() {

		return identity.getLabel();
	}

	/**
	 * Provides the identity of the property.
	 *
	 * @return Identity of property
	 */
	public CIdentity getIdentity() {

		return identity;
	}

	/**
	 * Provides the model with which the property is associated.
	 *
	 * @return Model with which property is associated
	 * @throws KAccessException if this is the root-property
	 */
	public CModel getModel() {

		return model;
	}

	/**
	 * Provides any annotations on the property.
	 *
	 * @return Annotations on property
	 */
	public CAnnotations getAnnotations() {

		return annotations;
	}

	CProperty(CModel model, CIdentity identity) {

		this.model = model;
		this.identity = identity;
	}

	CPropertyEditor createEditor() {

		return new Editor();
	}
}
