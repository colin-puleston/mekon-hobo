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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Type-safe representation of a concept-level frame within the
 * Object Model (OM). Combines a reference to an OM class that
 * defines a general type for the represented concept, with a
 * concept-level frame representing the actual concept. The frame
 * must be subsumed by the frame to which the OM class is bound.
 * <p>
 * NOTE: A concept-level frame that has multiple ancestors that
 * are bound to OM classes can give rise to multiple distinct
 * {@link DConcept} objects, each pairing the frame with a
 * different OM class.
 *
 * @author Colin Puleston
 */
public class DConcept<D extends DObject> {

	private DModel model;
	private Class<D> dClass;
	private CFrame frame;

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>DConcept</code>
	 * whose associated frame object is equal to that of this one
	 */
	public boolean equals(Object other) {

		if (other == this) {

			return true;
		}

		return other instanceof DConcept && ((DConcept)other).frame.equals(frame);
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
	 * Provides the model with which the concept is associated.
	 *
	 * @return Model with which concept is associated
	 */
	public DModel getModel() {

		return model;
	}

	/**
	 * Provides the OM class that defines the general type for the
	 * represented concept.
	 *
	 * @return Relevant OM class
	 */
	public Class<D> getDClass() {

		return dClass;
	}

	/**
	 * Provides the concept-level frame representing the concept.
	 *
	 * @return Frame representing concept
	 */
	public CFrame getFrame() {

		return frame;
	}

	/**
	 * Tests whether the concept-level frame representing the concept
	 * is subsumed by the frame that is bound to the specified
	 * sub-class of the OM class that defines the general type for the
	 * represented concept.
	 *
	 * @param subConceptClass Relevant OM sub-class
	 * @return True if required subsumption holds
	 */
	public boolean subsumedBy(Class<? extends D> subConceptClass) {

		return model.getFrame(subConceptClass).subsumes(frame);
	}

	/**
	 * Instantiates this concept as an instance of the OM class that
	 * defines the general type for the concept.
	 *
	 * @return Instantiation of concept
	 */
	public D instantiate() {

		return instantiate(dClass);
	}

	/**
	 * Instantiates this concept as an instance of the specified OM
	 * class.
	 *
	 * @param <N> Generic version of dClass
	 * @param dClass OM class to be instantiated
	 * @return Instantiation of concept
	 * @throws KAccessException if required instantiation is invalid
	 */
	public <N extends DObject>N instantiate(Class<N> dClass) {

		return model.instantiate(dClass, frame);
	}

	/**
	 * Creates a new object representing the same concept but with
	 * a different OM class defining the general type for the
	 * represented concept.
	 *
	 * @param <N> Generic version of dClass
	 * @param dClass New OM class
	 * @return Derived concept
	 * @throws KAccessException if new OM class is not applicable
	 */
	public <N extends DObject>DConcept<N> asType(Class<N> dClass) {

		return new DConcept<N>(model, dClass, frame);
	}

	DConcept(DModel model, Class<D> dClass, CFrame frame) {

		this.model = model;
		this.dClass = dClass;
		this.frame = frame;
	}
}
