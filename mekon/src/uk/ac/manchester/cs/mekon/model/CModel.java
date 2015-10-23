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

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.serial.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.mechanism.core.*;

/**
 * Represents a generic MEKON Frames Model (FM), within which
 * domain concepts are represented by {@link CFrame} objects,
 * and inter-concept relationships and concept-attributes by
 * {@link CSlot} objects.
 *
 * @author Colin Puleston
 */
public class CModel implements CAnnotatable {

	static {

		ZMekonAccessor.set(new ZMekonAccessorImpl());
	}

	private ZMekonCustomiser customiser;

	private CFrame rootFrame = new CRootFrame(this);
	private CIdentifiedsLocal<CFrame> frames = new CIdentifiedsLocal<CFrame>();

	private CAnnotations annotations = new CAnnotations(this);

	private IEditor iEditor = new IEditorImpl(this);
	private IUpdating iUpdating = new IUpdating(this);
	private IStore iStore = new IStore(this);

	private List<InitialisationListener> initialisationListeners
							= new ArrayList<InitialisationListener>();

	private boolean queriesEnabled = false;
	private boolean initialised = false;

	/**
	 * Specifies whether query-instances are allowed (see {@link
	 * IFrameCategory}.
	 * <p>
	 * By default query-instances will not be allowed.
	 *
	 * @return True if query-instances are allowed
	 */
	public boolean queriesEnabled() {

		return queriesEnabled;
	}

	/**
	 * Provides configuration information concerning the nature
	 * of the updates to be performed on instance-level frames as
	 * the result of reasoning.
	 *
	 * @return Instance-level frame update configuration
	 */
	public IUpdating getIUpdating() {

		return iUpdating;
	}

	/**
	 * Provides the instance-store for the model.
	 *
	 * @return Instance-store for model
	 */
	public IStore getIStore() {

		return iStore;
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
	 * Provides any annotations on the actual model (as opposed
	 * to annotations on the individual model components).
	 *
	 * @return Annotations on model
	 */
	public CAnnotations getAnnotations() {

		return annotations;
	}

	/**
	 * Creates instantiation of the frame, setting the category
	 * of the instantiation to {@link IFrameCategory#ASSERTION}.
	 *
	 * @param identity Identity of frame to be instantiated
	 * @return Instantiation of specified frame, as
	 * assertion-instance
	 */
	public IFrame instantiate(CIdentity identity) {

		return getFrame(identity).instantiate();
	}

	/**
	 * Creates instantiation of a frame, setting the
	 * frame-category of the instantiation as specified.
	 *
	 * @param identity Identity of frame to be instantiated
	 * @param category Required frame-category
	 * @return Instantiation of specified frame, with required
	 * frame-category
	 */
	public IFrame instantiate(CIdentity identity, IFrameCategory category) {

		return getFrame(identity).instantiate(category);
	}

	/**
	 * Creates instantiation of a frame with frame-category
	 * {@link IFrameCategory#QUERY}.
	 *
	 * @param identity Identity of frame to be instantiated
	 * @return Instantiation of specified frame, as query
	 */
	public IFrame instantiateQuery(CIdentity identity) {

		return getFrame(identity).instantiateQuery();
	}

	CModel(ZMekonCustomiser customiser) {

		this.customiser = customiser;
	}

	void addInitialisationListener(InitialisationListener listener) {

		initialisationListeners.add(listener);
	}

	void removeInitialisationListener(InitialisationListener listener) {

		initialisationListeners.remove(listener);
	}

	void setQueriesEnabled(boolean enabled) {

		queriesEnabled = enabled;
	}

	CAtomicFrame addFrame(CIdentity identity, boolean hidden) {

		CAtomicFrame frame = new CAtomicFrame(this, identity, hidden);

		frames.add(frame);
		customiser.onFrameAdded(frame);

		return frame;
	}

	boolean removeFrame(CAtomicFrame frame) {

		if (frame.getSource().internal() || !frames.contains(frame)) {

			return false;
		}

		frames.remove(frame);

		removeFrameTraces(frame);
		customiser.onFrameRemoved(frame);

		return true;
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
		iStore.checkLoad();

		pollInitialisationListeners();

		initialised = true;
	}

	boolean initialised() {

		return initialised;
	}

	IEditor getIEditor() {

		return iEditor;
	}

	private void removeFrameTraces(CAtomicFrame frame) {

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

	private CFrame getFrame(CIdentity identity) {

		return getFrames().get(identity);
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
