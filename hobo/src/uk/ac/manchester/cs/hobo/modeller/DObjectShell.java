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

package uk.ac.manchester.cs.hobo.modeller;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * Abstract implementation of {@link DObject} whose extensions
 * will be the classes in the Object Model (OM).
 * <p>
 * Each extension should provide a public constructor that takes
 * a single parameter of type {@link DObjectBuilder}. This parameter
 * should be passed on to the constructor for this class, and will
 * generally also be used by the constructor of the derived class
 * to perform the following:
 * <ul>
 *   <li>Creating any associated fields and field-viewers
 *   <li>Registering a {@link DObjectInitialiser} object to
 *   perform any required post-construction field-initialisations
 *   - setting of initial values or addition of listeners (the fields
 *   will not be fully configured until after the object has been
 *   constructed, so any attempted initialisations within the
 *   constructor will result in exceptions being thrown)
 *   <li>Obtaining the {@link DEditor} object, which provides
 *   the OM classes with access to mechanisms for performing internal
 *   model-instantiation updates that cannot be performed by the client
 *   code.
 * </ul>
 *
 * @author Colin Puleston
 */
public abstract class DObjectShell implements DObject {

	private DModel model;
	private IFrame frame;

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>DObject</code>
	 * whose associated frame object is equal to that of this one
	 * @see #getFrame
	 */
	public boolean equals(Object other) {

		return other instanceof DObject && equalsDObject((DObject)other);
	}

	/**
	 * Provides hash-code based on associated frame object.
	 *
	 * @return hash-code for this object
	 * @see #getFrame
	 */
	public int hashCode() {

		return frame.hashCode();
	}

	/**
	 * Provides a description of the entity, intended to provide
	 * information to the software developer, and not suitable for
	 * displaying to an end-user.
	 *
	 * @return Development-level description of entity
	 */
	public String toString() {

		return getClass().getSimpleName() + "(" + frame + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	public DModel getModel() {

		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	public DConcept<DObject> getConcept() {

		return getConcept(DObject.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public <D extends DObject>DConcept<D> getConcept(Class<D> dClass) {

		return model.getConcept(dClass, frame.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	public IFrame getFrame() {

		return frame;
	}

	/**
	 * Constructor.
	 *
	 * @param builder Builder for this object
	 */
	protected DObjectShell(DObjectBuilder builder) {

		model = builder.getModel();
		frame = builder.getFrame();
	}

	private boolean equalsDObject(DObject other) {

		return other.getFrame().equals(frame);
	}
}
