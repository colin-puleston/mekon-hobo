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
import uk.ac.manchester.cs.hobo.*;
import uk.ac.manchester.cs.hobo.modeller.*;
import uk.ac.manchester.cs.hobo.mechanism.*;

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
public abstract class DModel {

	private CModelLocal cModel = new CModelLocal();
	private CBuilder cBuilder = cModel.accessor.createBuilder(true);

	private DBindings bindings = new DBindings();
	private IFrameMapper iFrameMapper = new IFrameMapper();
	private boolean labelsFromDirectFields = true;

	/**
	 * Part of the HOBO mechanism - not relevant to the client.
	 */
	public class DAccessor {

		public DBuilder createBuilder() {

			return new DBuilderImpl(cModel, getCBuilder(), DModel.this);
		}
	}

	private class CModelLocal extends CModel {

		final CAccessor accessor = new CAccessor();

		private class LocalAdjuster extends CAdjuster {

			protected void onFrameAdded(CFrame frame) {

				frame.addListener(iFrameMapper);
			}

			protected void onFrameRemoved(CFrame frame) {

				checkRemovableFrame(frame);
			}

			protected void onSlotRemoveded(CSlot slot) {

				checkRemovableSlot(slot);
			}

			protected boolean mappedToNonInstantiableObject(CFrame frame) {

				return !instantiableDClassFor(frame);
			}
		}

		CModelLocal() {

			accessor.setAdjuster(new LocalAdjuster());
		}
	}

	private class IFrameMapper implements CFrameListener {

		public void onExtended(CFrame extension) {

			extension.addListener(this);
		}

		public void onInstantiated(IFrame instance) {

			ensureMappedDObject(instance);
		}
	}

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
	 * Tests whether the specified concept-level frame, or any of
	 * it's ancestors, are mapped to the specified OM class.
	 *
	 * @param dClass OM class for which mapping is required
	 * @param frame Frame from which to search
	 * @return True if required mapping exists
	 */
	public boolean hasSubsumingDType(
						Class<? extends DObject> dClass,
						CFrame frame) {

		return new SubsumedDClassFinder(this, dClass).isSubsumed(frame);
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
	 * @param conceptClass OM class that defines both the general type of
	 * the represented concept, and the represented concept itself
	 * @return Representation of relevant concept
	 */
	public <D extends DObject>DConcept<D> getConcept(Class<D> conceptClass) {

		return getConcept(conceptClass, conceptClass);
	}

	/**
	 * Provides a representation of the specified concept.
	 *
	 * @param rootConceptClass OM class that defines the general type of
	 * the represented concept
	 * @param conceptClass OM class that defines the represented concept
	 * @return Representation of relevant concept
	 */
	public <D extends DObject>DConcept<D> getConcept(
											Class<D> rootConceptClass,
											Class<? extends D> conceptClass) {

		return new DConcept<D>(this, rootConceptClass, getFrame(conceptClass));
	}

	/**
	 * Provides a representation of the specified concept.
	 *
	 * @param rootConceptClass OM class that defines the general type of
	 * the represented concept
	 * @param frame Frame representation of concept
	 * @return Representation of relevant concept
	 * @throws HModelException if specified frame is not subsumed by
	 * specified root-concept-class
	 */
	public <D extends DObject>DConcept<D> getConcept(
											Class<D> rootConceptClass,
											CFrame frame) {

		if (!getFrame(rootConceptClass).subsumes(frame)) {

			throw new HAccessException(
						"Cannot instantiate DConcept for root-concept-class: "
						+ rootConceptClass
						+ ", attempting to instantiate for invalid CFrame: "
						+ frame);
		}

		return new DConcept<D>(this, rootConceptClass, frame);
	}

	/**
	 * Provides a representation of the specified concept.
	 *
	 * @param rootConceptClass OM class that defines the general type of
	 * the represented concept
	 * @param identity Identity of frame representation of concept
	 * @return Representation of relevant concept
	 * @throws HModelException if specified frame is not subsumed by
	 * specified root-concept-class
	 */
	public <D extends DObject>DConcept<D> getConcept(
											Class<D> rootConceptClass,
											CIdentity identity) {

		return getConcept(rootConceptClass, getFrame(identity));
	}

	/**
	 * Instantiates the specified OM class to represent an instance
	 * of the concept represented by the root concept-level frame
	 * ({@link CFrame}) for that class. The root-frame will be
	 * "instantiated" to provide the instance-level frame
	 * ({@link IFrame}) that will be associated with the created OM
	 * object.
	 *
	 * @param dClass OM class to be instantiated
	 * @return Required OM class instantiation
	 * @throws HModelException if specified OM class is not instantiable
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
	 * @param dClass OM class to be instantiated
	 * @param identity Identity of frame representing relevant concept
	 * @return Resulting OM object
	 * @throws HModelException if specified OM class or frame is not
	 * instantiable
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
	 * @param dClass OM class to be instantiated
	 * @param frame Frame representing relevant concept
	 * @return Resulting OM object
	 * @throws HModelException if specified OM class or frame is not
	 * instantiable
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
	 * @param frame Frame for which OM object is required
	 * @param dClass Expected type of OM object
	 * @return Required OM object
	 * @throws HAccessException if frame is not part of this model or if OM
	 * object is not of the specified type
	 */
	public <D extends DObject>D getDObject(IFrame frame, Class<D> dClass) {

		D dObject = cModel.accessor.getIFrameMappedObject(frame, dClass);

		if (dObject == null) {

			throw new HAccessException("Mapped-object not set for: " + frame);
		}

		return dObject;
	}

	/**
	 * Part of the HOBO mechanism - not relevant to the client.
	 */
	protected DModel() {
	}

	void initialise(boolean labelsFromDirectFields) {

		this.labelsFromDirectFields = labelsFromDirectFields;

		bindings.initialise(this);
		cModel.accessor.completeModelInitialisation();

		cBuilder = null;
	}

	boolean initialised() {

		return cBuilder == null;
	}

	DBinding addDClass(Class<? extends DObject> dClass, CFrame frame) {

		return bindings.add(dClass, frame);
	}

	DBindings getBindings() {

		return bindings;
	}

	CBuilder getCBuilder() {

		if (cBuilder == null) {

			throw new Error("CBuilder no longer available");
		}

		return cBuilder;
	}

	IEditor getIEditor() {

		return cModel.accessor.getIEditor();
	}

	ISlotValuesEditor getISlotValuesEditor(ISlot slot) {

		return cModel.accessor.getIEditor().getSlotValuesEditor(slot);
	}

	boolean labelsFromDirectFields() {

		return labelsFromDirectFields;
	}

	private boolean instantiableDClassFor(CFrame frame) {

		if (frame.getSource().direct()) {

			return new InstantiableDClassFinder(this).anyFor(frame);
		}

		return true;
	}

	private <D extends DObject>D instantiate(Class<D> dClass, IFrame frame) {

		D dObject = new DObjectInstantiator<D>(this, dClass).instantiate(frame);

		cModel.accessor.setIFrameMappedObject(frame, dObject);

		return dObject;
	}

	private void ensureMappedDObject(IFrame frame) {

		if (cModel.accessor.getIFrameMappedObject(frame, DObject.class) == null) {

			instantiate(DObject.class, frame);
		}
	}

	private void checkRemovableFrame(CFrame frame) {

		if (frame.getSource().direct()) {

			throw new HModelException("Cannot remove frame with direct source: " + frame);
		}
	}

	private void checkRemovableSlot(CSlot slot) {

		if (slot.getSource().direct()) {

			throw new HModelException(
						"Cannot remove slot with direct source: "
						+ slot
						+ ", frame: "
						+ slot.getContainer());
		}
	}

	private CFrame getFrame(CIdentity identity) {

		return cModel.getFrames().get(identity);
	}
}
