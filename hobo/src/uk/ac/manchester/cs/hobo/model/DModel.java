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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.mechanism.core.*;
import uk.ac.manchester.cs.hobo.*;
import uk.ac.manchester.cs.hobo.mechanism.*;
import uk.ac.manchester.cs.hobo.mechanism.core.*;

/**
 * Represents the HOBO direct model, which is an Object Model
 * (OM) within which domain concepts are represented directly
 * by domain-specific Java classes (OM classes). The direct
 * model will be associated with a Frames Model (FM), as
 * represented via a {@link CModel} object.
 * <p>
 * Each class in the OM will implement the {@link DObject}
 * interface and will be bound to a {@link CFrame} object in
 * the FM. The OM fields associated with the OM classes will be
 * represented via {@link DField} objects of the different types
 * and will be bound to a {@link CSlot} objects in the FM.
 *
 * @author Colin Puleston
 */
public class DModel {

	static {

		ZDModelAccessor.set(new ZDModelAccessorImpl());
	}

	private CModel cModel;
	private IEditor iEditor;
	private ZObjectModelMapper objectModelMapper;

	private DInitialiser initialiser;
	private DBindings bindings = new DBindings();

	/**
	 * Provides the associated FM.
	 *
	 * @return Associated FM
	 */
	public CModel getCModel() {

		return cModel;
	}

	/**
	 * Tests whether there is an OM class that is bound to the
	 * specified concept-level frame.
	 *
	 * @param frame Frame for which binding is required
	 * @return True if required binding exists
	 */
	public boolean hasBoundClass(CFrame frame) {

		return bindings.isBound(frame);
	}

	/**
	 * Provides the OM class to which the specified concept-level
	 * frame is bound.
	 *
	 * @param identity Identity of frame for which bound OM class is
	 * required
	 * @return OM class bound to specified frame
	 * @throws HModelException if no such binding exists
	 */
	public Class<? extends DObject> getDClass(CIdentity identity) {

		return getDClass(getFrame(identity));
	}

	/**
	 * Provides the OM class to which the specified concept-level
	 * frame is bound.
	 *
	 * @param frame Frame for which bound OM class is required
	 * @return OM class bound to specified frame
	 * @throws HModelException if no such binding exists
	 */
	public Class<? extends DObject> getDClass(CFrame frame) {

		return bindings.get(frame).getDClass();
	}

	/**
	 * Provides the concept-level frame to which the specified OM
	 * class is bound.
	 *
	 * @param dClass OM class for which bound frame is required
	 * @return Frame bound to specified OM class
	 * @throws HModelException if no such binding exists
	 */
	public CFrame getFrame(Class<? extends DObject> dClass) {

		return bindings.get(dClass).getFrame();
	}

	/**
	 * Provides a representation of the specified concept.
	 *
	 * @param <D> Generic version of dClass
	 * @param dClass OM class that defines both a general type for
	 * the represented concept, and the represented concept itself
	 * @return Representation of relevant concept
	 */
	public <D extends DObject>DConcept<D> getConcept(Class<D> dClass) {

		return getConcept(dClass, dClass);
	}

	/**
	 * Provides a representation of the specified concept.
	 *
	 * @param <D> Generic version of generalDClass
	 * @param generalDClass OM class that defines a general type for
	 * the represented concept
	 * @param specificDClass OM class that defines the specific type
	 * of the represented concept
	 * @return Representation of relevant concept
	 */
	public <D extends DObject>DConcept<D> getConcept(
											Class<D> generalDClass,
											Class<? extends D> specificDClass) {

		return new DConcept<D>(this, generalDClass, getFrame(specificDClass));
	}

	/**
	 * Provides a representation of the specified concept.
	 *
	 * @param <D> Generic version of generalDClass
	 * @param generalDClass OM class that defines the general type of
	 * the represented concept
	 * @param frame Frame representation of concept
	 * @return Representation of relevant concept
	 * @throws HModelException if specified frame is not subsumed by
	 * specified root-concept-class
	 */
	public <D extends DObject>DConcept<D> getConcept(
											Class<D> generalDClass,
											CFrame frame) {

		if (!getFrame(generalDClass).subsumes(frame)) {

			throw new HAccessException(
						"Cannot instantiate DConcept for root-concept-class: "
						+ generalDClass
						+ ", attempting to instantiate for invalid CFrame: "
						+ frame);
		}

		return new DConcept<D>(this, generalDClass, frame);
	}

	/**
	 * Provides a representation of the specified concept.
	 *
	 * @param <D> Generic version of generalDClass
	 * @param generalDClass OM class that defines the general type of
	 * the represented concept
	 * @param identity Identity of frame representation of concept
	 * @return Representation of relevant concept
	 * @throws HModelException if specified frame is not subsumed by
	 * specified root-concept-class
	 */
	public <D extends DObject>DConcept<D> getConcept(
											Class<D> generalDClass,
											CIdentity identity) {

		return getConcept(generalDClass, getFrame(identity));
	}

	/**
	 * Instantiates the specified OM class to represent an instance
	 * of the concept represented by the root concept-level frame
	 * ({@link CFrame}) for that class. The root-frame will be
	 * "instantiated" to provide the instance-level frame
	 * ({@link IFrame}) that will be associated with the created OM
	 * object.
	 *
	 * @param <D> Generic version of dClass
	 * @param dClass OM class to be instantiated
	 * @return Required OM class instantiation
	 */
	public <D extends DObject>D instantiate(Class<D> dClass) {

		return instantiate(dClass, getFrame(dClass));
	}

	/**
	 * Instantiates the specified OM class to represent an instance
	 * of the concept represented by the specified concept-level frame.
	 * The frame will be "instantiated" to provide the instance-level
	 * frame ({@link IFrame}) object that will be associated with the
	 * created OM object.
	 *
	 * @param <D> Generic version of dClass
	 * @param dClass OM class to be instantiated
	 * @param identity Identity of frame representing relevant concept
	 * @return Resulting OM object
	 */
	public <D extends DObject>D instantiate(Class<D> dClass, CIdentity identity) {

		return instantiate(dClass, getFrame(identity));
	}

	/**
	 * Instantiates the specified OM class to represent an instance
	 * of the concept represented by the specified concept-level frame.
	 * The frame will be "instantiated" to provide the instance-level
	 * frame ({@link IFrame}) object that will be associated with the
	 * created OM object.
	 *
	 * @param <D> Generic version of dClass
	 * @param dClass OM class to be instantiated
	 * @param frame Frame representing relevant concept
	 * @return Resulting OM object
	 */
	public <D extends DObject>D instantiate(Class<D> dClass, CFrame frame) {

		return getDObject(frame.instantiate(), dClass);
	}

	/**
	 * Retrieves the OM object associated with the specified instance-level
	 * frame.
	 *
	 * @param frame Frame for which OM object is required
	 * @return Required OM object
	 * @throws HAccessException if frame is not part of this model
	 */
	public DObject getDObject(IFrame frame) {

		return getDObject(frame, DObject.class);
	}

	/**
	 * Retrieves the OM object associated with the specified instance-level
	 * frame.
	 *
	 * @param <D> Generic version of dClass
	 * @param frame Frame for which OM object is required
	 * @param dClass Expected type of OM object
	 * @return Required OM object
	 * @throws HAccessException if frame is not part of this model or if OM
	 * object is not of the specified type
	 */
	public <D extends DObject>D getDObject(IFrame frame, Class<D> dClass) {

		Object mappedObj = getMappedObject(frame);

		if (mappedObj == null) {

			throw new HAccessException("Mapped-object not set for: " + frame);
		}

		if (dClass.isAssignableFrom(mappedObj.getClass())) {

			return dClass.cast(mappedObj);
		}

		throw new HAccessException(
					"Mapped-object not of expected type for: " + frame
					+ ", expected type: " + dClass
					+ " , found type: " + mappedObj.getClass());
	}

	DModel() {

		ZCModelAccessor mekonAccessor = ZCModelAccessor.get();
		ZMekonCustomiserLocal mekonCustomiser = new ZMekonCustomiserLocal(this);

		cModel = mekonAccessor.createModel(mekonCustomiser);
		iEditor = mekonAccessor.getIEditor(cModel);
		objectModelMapper = mekonAccessor.getObjectModelMapper();

		CBuilder cBuilder = mekonAccessor.createBuilder(cModel);

		initialiser = new DInitialiser(cBuilder, bindings);
	}

	void initialise() {

		initialiser.initialise(this);
		initialiser = null;
	}

	boolean initialised() {

		return initialiser == null;
	}

	DInitialiser getInitialiser() {

		if (initialiser == null) {

			throw new Error(
						"Cannot perform operation: "
						+ "Build has completed");
		}

		return initialiser;
	}

	DBindings getBindings() {

		return bindings;
	}

	IEditor getIEditor() {

		return iEditor;
	}

	void ensureMappedDObject(IFrame frame, boolean freeInstance) {

		if (getMappedObject(frame) == null) {

			instantiate(DObject.class, frame, freeInstance);
		}
	}

	ISlotValuesEditor getISlotValuesEditor(ISlot slot) {

		return getIEditor().getSlotValuesEditor(slot);
	}

	private <D extends DObject>D instantiate(
									Class<D> dClass,
									IFrame frame,
									boolean freeInstance) {

		DObjectInstantiator<D> instantiator = new DObjectInstantiator<D>(
													this,
													dClass,
													freeInstance);
		D dObject = instantiator.instantiate(frame);

		objectModelMapper.setMappedObject(frame, dObject);

		return dObject;
	}

	private Object getMappedObject(IFrame frame) {

		return objectModelMapper.getMappedObject(frame);
	}

	private CFrame getFrame(CIdentity identity) {

		return cModel.getFrames().get(identity);
	}
}
