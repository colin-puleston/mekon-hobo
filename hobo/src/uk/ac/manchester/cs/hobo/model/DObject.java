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

package uk.ac.manchester.cs.hobo.model;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.hobo.*;

/**
 * Interface implemented by all classes in the Object Model (OM).
 * Each such class will be bound to a {@link CFrame} object,
 * representing a concept-level frame in the Frames Model (FM),
 * and individual instantiations of the class will be bound to
 * {@link IFrame} objects, representing specific "instantiations"
 * of the concept-level frame.
 * <p>
 * All fields on implementing classes that are part of the OM
 * will be represented by {@link DField} objects of various kinds.
 * At the class-level such fields are bound to specific
 * {@link CSlot} objects, whilst at the object-level they will be
 * bound to corresponding {@link ISlot} objects.
 *
 * @author Colin Puleston
 */
public interface DObject {

	/**
	 * Copies this object by creating a deep copy of the associated
	 * frame and all recursively referenced frames, and using the
	 * copy to instantiate an OM of the specified type, which must
	 * represent a valid type for the frame.
	 *
	 * @param dClass Required type of OM object to be created
	 * @return Created OM object
	 * @throws HAccessException if specified type is not valid
	 */
	public <D extends DObject> D copy(Class<D> dClass);

	/**
	 * Provides the model with which the object is associated.
	 *
	 * @return Model with which object is associated
	 */
	public DModel getModel();

	/**
	 * Provides the concept of which the object represents an
	 * instance.
	 *
	 * @return Concept of which object represents instance
	 */
	public DConcept<DObject> getConcept();

	/**
	 * Provides the concept of the specified type, of which the object
	 * represents an instance.
	 *
	 * @param <D> Generic version of dClass
	 * @param dClass OM class that object implements
	 * @return Concept of which object represents instance
	 * @throws HAccessException if object does not implement provided
	 * OM class
	 */
	public <D extends DObject>DConcept<D> getConcept(Class<D> dClass);

	/**
	 * Provides the instance-level frame to which the object is bound.
	 *
	 * @return Frame to which object is bound
	 */
	public IFrame getFrame();
}