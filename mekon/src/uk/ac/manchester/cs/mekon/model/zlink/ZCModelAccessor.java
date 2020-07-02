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

package uk.ac.manchester.cs.mekon.model.zlink;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * THIS CLASS SHOULD NOT BE ACCESSED DIRECTLY BY EITHER THE CLIENT
 * OR THE PLUGIN CODE.
 * <p>
 * Provides the MEKON mechanisms, and the mechanisms of any
 * extensions of the MEKON framework, with privileged access to
 * the MEKON model.
 *
 * @author Colin Puleston
 */
public abstract class ZCModelAccessor {

	static private KSingleton<ZCModelAccessor> singleton
							= new KSingleton<ZCModelAccessor>();

	/**
	 * Sets the singleton accessor object.
	 *
	 * @param accessor Accessor to set as singleton
	 */
	static public synchronized void set(ZCModelAccessor accessor) {

		singleton.set(accessor);
	}

	/**
	 * Retrieves the singleton accessor object. Ensures that the
	 * {@link CModel} class is initialised, since it is the static
	 * initialisation method on that class that sets the singleton
	 * accessor, via the {@link #set} method.
	 *
	 * @return Singleton accessor object
	 */
	static public ZCModelAccessor get() {

		return singleton.get(CModel.class);
	}

	/**
	 * Creates an empty model.
	 *
	 * @return Created empty model
	 */
	public abstract CModel createModel();

	/**
	 * Creates a builder for the specified model.
	 *
	 * @param model Relevant model
	 * @return Created builder
	 */
	public abstract CBuilder createBuilder(CModel model);

	/**
	 * Retrieves the model being built by the specified builder.
	 *
	 * @param builder Relevant builder
	 * @return Model being built
	 */
	public abstract CModel getModel(CBuilder builder);

	/**
	 * Provides an instantiation editor for the specified model.
	 *
	 * @param model Relevant model
	 * @return Instantiation editor for model
	 */
	public abstract IEditor getIEditor(CModel model);

	/**
	 * Provides a free-copier object applicable to any model.
	 *
	 * @return Free-copier object for any model
	 */
	public abstract IFreeCopier getFreeCopier();

	/**
	 * Provides a relaxed-instantiator object applicable to any model.
	 *
	 * @return Relaxed-instantiator object for any model
	 */
	public abstract IRelaxedInstantiator getRelaxedInstantiator();

	/**
	 * Checks whether frame is a "free-instance" (see {@link IFreeCopier})
	 *
	 * @param frame Frame to check
	 * @return True if frame is free-instance
	 */
	public abstract boolean freeInstance(IFrame frame);

	/**
	 * Maps an object from an external domain-specific Object Model
	 * (OM) to an instance-level frame in a MEKON frames-model
	 * instantiation.
	 *
	 * @param frame Frame to which object is to be mapped
	 * @param mappedObject Object to be mapped
	 */
	public abstract void setMappedObject(IFrame frame, Object mappedObject);

	/**
	 * Retrieves an object from an external domain-specific Object
	 * Model (OM) that is mapped to an instance-level in a MEKON
	 * frames-model instantiation.
	 *
	 * @param frame Frame from which mapped object is to be retrieved
	 * @return Mapped object, or null if frame does not have a mapped
	 * object
	 */
	public abstract Object getMappedObject(IFrame frame);

	/**
	 * Adds a slot with an activation status of {@link
	 * CActivation#INACTIVE} to a frame of category {@link
	 * IFrameCategory#REFERENCE}. Enables the addition of slots that
	 * are required for mappings to fields on objects from an external
	 * domain-specific Object Model (OM). The added slot will be
	 * permenantly deactivated, since in general slots are not relevant
	 * to the representation of reference-frames.
	 *
	 * @param frame Reference-frame to which slot is to be added
	 * @param slotType Type for slot to be added
	 * @return Added slot
	 */
	public abstract ISlot addReferenceFrameMappingSlot(IFrame frame, CSlot slotType);
}
