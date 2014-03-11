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
 * Represents the generic MEKON frames-based model, within
 * which domain concepts are represented by {@link CFrame}
 * objects, and inter-concept relationships and
 * concept-attributes by {@link CSlot} objects.
 *
 * @author Colin Puleston
 */
public abstract class CModel implements CAnnotatable {

	private CFrame rootFrame;

	private CIdentifiedsLocal<CFrame> frames = new CIdentifiedsLocal<CFrame>();
	private CIdentifiedsLocal<CProperty> properties = new CIdentifiedsLocal<CProperty>();

	private CAnnotations annotations = new CAnnotations(this);

	private CAdjuster adjuster = new CAdjuster();
	private IEditor iEditor = new IEditorImpl(this);
	private boolean autoUpdate = true;

	private List<InitialisationListener> initialisationListeners
							= new ArrayList<InitialisationListener>();

	private boolean initialised = false;

	/**
	 * Part of the MEKON mechanism - not relevant to the client.
	 */
	public class CAdjuster {

		protected void onFrameAdded(CFrame frame) {
		}

		protected void onFrameRemoved(CFrame frame) {
		}

		protected void onSlotRemoveded(CSlot slot) {
		}

		protected boolean mappedToNonInstantiableObject(CFrame frame) {

			return false;
		}
	}

	/**
	 * Part of the MEKON mechanism - not relevant to the client.
	 */
	public class CAccessor {

		public CBuilder createBuilder(boolean completeInitialisation) {

			return new CBuilderImpl(CModel.this, completeInitialisation);
		}

		public void setAdjuster(CAdjuster adjuster) {

			CModel.this.adjuster = adjuster;
		}

		public void completeInitialisation() {

			CModel.this.completeInitialisation();
		}

		public void setIFrameMappedObject(IFrame frame, Object mappedObject) {

			frame.setMappedObject(mappedObject);
		}

		public <T>T getIFrameMappedObject(IFrame frame, Class<T> expectedType) {

			return frame.getMappedObject(expectedType);
		}

		public IEditor getIEditor() {

			return iEditor;
		}
	}

	/**
	 * Enables or disables the {@link #autoUpdate} facility.
	 *
	 * @param autoUpdate True if auto-update is to be enabled
	 */
	public void setAutoUpdate(boolean autoUpdate) {

		this.autoUpdate = autoUpdate;
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

	/**
	 * Part of the MEKON mechanism - not relevant to the client.
	 */
	protected CModel() {

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
		adjuster.onFrameAdded(frame);

		return frame;
	}

	void removeFrame(CModelFrame frame) {

		frames.remove(frame);

		removeFrameTraces(frame);
		adjuster.onFrameRemoved(frame);
	}

	CProperty addProperty(CIdentity identity) {

		CProperty property = new CProperty(this, identity);

		properties.add(property);

		return property;
	}

	void registerRemovedSlot(CSlot slot) {

		adjuster.onSlotRemoveded(slot);
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

	boolean mappedToNonInstantiableObject(CFrame frame) {

		return adjuster.mappedToNonInstantiableObject(frame);
	}

	IEditor getIEditor() {

		return iEditor;
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
