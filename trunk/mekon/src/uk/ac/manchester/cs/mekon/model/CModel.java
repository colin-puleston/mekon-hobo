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

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * Represents the generic MEKON Frames Model (FM), within
 * which domain concepts are represented by {@link CFrame}
 * objects, and inter-concept relationships and
 * concept-attributes by {@link CSlot} objects.
 *
 * @author Colin Puleston
 */
public class CModel implements CAnnotatable {

	private CAccessor accessor = new CAccessorImpl(this);
	private CCustomiser customiser;

	private CFrame rootFrame;

	private CIdentifiedsLocal<CFrame> frames = new CIdentifiedsLocal<CFrame>();
	private CIdentifiedsLocal<CProperty> properties = new CIdentifiedsLocal<CProperty>();

	private CAnnotations annotations = new CAnnotations(this);

	private IEditor iEditor = new IEditorImpl(this);

	private boolean autoUpdate = true;
	private boolean abstractInstantiations = false;

	private List<InitialisationListener> initialisationListeners
							= new ArrayList<InitialisationListener>();

	private boolean initialised = false;

	/**
	 * Enables or disables the {@link #autoUpdate} facility.
	 *
	 * @param autoUpdate True if auto-update is to be enabled
	 */
	public void setAutoUpdate(boolean autoUpdate) {

		this.autoUpdate = autoUpdate;
	}

	/**
	 * Enables or disables {@link #abstractInstantiations}.
	 *
	 * @param abstractInstantiations True if abstract model-instantiations
	 * are to be allowed
	 */
	public void setAbstractInstantiations(boolean abstractInstantiations) {

		this.abstractInstantiations = abstractInstantiations;
	}

	/**
	 * Specifies whether the sets of slots for specific
	 * instance-level frames will be dynamically updated based on
	 * the current states of the frames. By default auto-update
	 * will be enabled.
	 *
	 * @return True if auto-update is enabled
	 */
	public boolean autoUpdate() {

		return autoUpdate;
	}

	/**
	 * Specifies whether abstract model-instantiations, representing
	 * sets of possible instances, rather than specific concrete
	 * instances, are allowed.
	 * <p>
	 * Abstract instantiations differ from concrete instantiations in
	 * the following ways:
	 * <li>
	 *   <ul>Disjunction-frames (see {@link CFrame#disjunction})
	 *   can be instantiated
	 *   <ul>Disjunction-frames, or instances of disjunction-frames,
	 *   can be used as slot-values
	 *   <ul>Indefinite numeric values (see {@link INumber#indefinite})
	 *   can be used as slot-values
	 *   <ul>Derived-values slots (see {@link ISlot#derivedValues}) are
	 *   editable by the client (see {@link ISlot#editable}), which is
	 *   not the case for concrete instances.
	 * </li>
	 * By default abstract model-instantiations will not be allowed.
	 *
	 * @return True if abstract model-instantiations are allowed
	 */
	public boolean abstractInstantiations() {

		return abstractInstantiations;
	}

	/**
	 * Provides the unique root-frame for the model.
	 *
	 * @return Root-frame for model
	 */
	public CFrame getRootFrame() {

		return rootFrame;
	}

	/**
	 * Provides all frames in the model.
	 *
	 * @return All frames in model
	 */
	public CIdentifieds<CFrame> getFrames() {

		return frames;
	}

	/**
	 * Provides all properties in the model.
	 *
	 * @return All properties in model
	 */
	public CIdentifieds<CProperty> getProperties() {

		return properties;
	}

	/**
	 * Provides any annotations on the actual model (as opposed
	 * to annotations on the individual model components).
	 *
	 * @return Annotations on model
	 */
	public CAnnotations getAnnotations() {

		return annotations;
	}

	/**
	 * Instantiates a model-frame.
	 *
	 * @param identity Identity of frame to be instantiated
	 * @return Instantiation of specified frame
	 */
	public IFrame instantiate(CIdentity identity) {

		return getFrames().get(identity).instantiate();
	}

	CModel() {

		this(new CCustomiserDefault());
	}

	CModel(CCustomiser customiser) {

		this.customiser = customiser;

		rootFrame = new CRootFrame(this, DefaultIReasoner.singleton);
	}

	void addInitialisationListener(InitialisationListener listener) {

		initialisationListeners.add(listener);
	}

	void removeInitialisationListener(InitialisationListener listener) {

		initialisationListeners.remove(listener);
	}

	CModelFrame addFrame(CIdentity identity, boolean hidden, IReasoner iReasoner) {

		CModelFrame frame = new CModelFrame(this, identity, hidden, iReasoner);

		frames.add(frame);
		customiser.onFrameAdded(frame);

		return frame;
	}

	void removeFrame(CModelFrame frame) {

		frames.remove(frame);

		removeFrameTraces(frame);
		customiser.onFrameRemoved(frame);
	}

	CProperty addProperty(CIdentity identity) {

		CProperty property = new CProperty(this, identity);

		properties.add(property);

		return property;
	}

	void registerRemovedSlot(CSlot slot) {

		customiser.onSlotRemoved(slot);
	}

	void startInitialisation() {

		initialised = false;

		new CFramesInitialiser(frames).startInitialisation();
	}

	void optimiseSubsumptionTesting() {

		new CFramesInitialiser(frames).optimiseSubsumptionTesting();
	}

	void completeInitialisation() {

		new CHierarchyNormaliser(this);
		new CFramesInitialiser(frames).completeInitialisation();

		pollInitialisationListeners();

		initialised = true;
	}

	boolean initialised() {

		return initialised;
	}

	CAccessor getAccessor() {

		return accessor;
	}

	IEditor getIEditor() {

		return iEditor;
	}

	boolean mappedToNonInstantiableObject(CFrame frame) {

		return customiser.mappedToNonInstantiableObject(frame);
	}

	private void removeFrameTraces(CModelFrame frame) {

		frame.removeFromHierarchy();

		removeReferencesTo(frame);
		removeReferencesTo(frame.getType());
	}

	private void removeReferencesTo(CValue<?> value) {

		removeReferencingSlots(value);
		removeReferencingSlotValues(value);
	}

	private void removeReferencingSlots(CValue<?> value) {

		for (CSlot refSlot : value.getReferencingSlots()) {

			refSlot.remove();
		}
	}

	private void removeReferencingSlotValues(CValue<?> value) {

		for (CFrame refFrame : value.getSlotValueReferencingFrames()) {

			refFrame.getSlotValues().removeAll(value);
		}
	}

	private void pollInitialisationListeners() {

		for (InitialisationListener listener : copyInitialisationListeners()) {

			listener.onInitialised();
		}
	}

	private List<InitialisationListener> copyInitialisationListeners() {

		return new ArrayList<InitialisationListener>(initialisationListeners);
	}
}
