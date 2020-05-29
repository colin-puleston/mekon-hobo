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
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;

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

		ZCModelAccessor.set(new ZCModelAccessorImpl());
	}

	private CFrame rootFrame = new CRootFrame(this);
	private CIdentifiedsLocal<CFrame> frames = new CIdentifiedsLocal<CFrame>();
	private Map<String, CIdentity> slotIdentitiesByIdentifiers = new HashMap<String, CIdentity>();

	private CAnnotations annotations = new CAnnotations(this);
	private CSlotAnnotations slotAnnotations = new CSlotAnnotations();

	private IEditor iEditor = new IEditorImpl(this);
	private IUpdating iUpdating = new IUpdating(this);

	private boolean queriesEnabled = false;
	private boolean initialised = false;

	private CBuildListeners buildListeners = new CBuildListeners();

	/**
	 * Specifies whether query-instances are allowed (see {@link
	 * IFrameFunction}.
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
	 * Provides the unique root-frame for the model.
	 *
	 * @return Root-frame for model
	 */
	public CFrame getRootFrame() {

		return rootFrame;
	}

	/**
	 * Provides all frames in the model, excluding the root-frame.
	 *
	 * @return All frames in model, excluding root
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
	 * Provides any annotations on the slot-set that consists of all
	 * slots in the model with the specified identity.
	 *
	 * @param slotId Identity of slots in slot-set
	 * @return Annotations on relevant slot-set
	 */
	public CAnnotations getSlotAnnotations(CIdentity slotId) {

		return slotAnnotations.get(slotId);
	}

	CModel() {
	}

	void setQueriesEnabled(boolean enabled) {

		queriesEnabled = enabled;
	}

	CAtomicFrame addFrame(CIdentity identity, boolean hidden) {

		CAtomicFrame frame = new CAtomicFrame(this, identity, hidden);

		frames.add(frame);
		buildListeners.onFrameAdded(frame);

		return frame;
	}

	boolean removeFrame(CAtomicFrame frame) {

		if (frame.getSource().internal() || !frames.remove(frame)) {

			return false;
		}

		removeFrameTraces(frame);
		buildListeners.onFrameRemoved(frame);

		return true;
	}

	CIdentity resolveSlotIdentity(CIdentity id) {

		String identifier = id.getIdentifier();
		CIdentity resolvedId = slotIdentitiesByIdentifiers.get(identifier);

		if (resolvedId == null) {

			resolvedId = id.copy();
			slotIdentitiesByIdentifiers.put(identifier, resolvedId);
		}

		return resolvedId;
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

		initialised = true;

		buildListeners.onBuildComplete();
	}

	boolean initialised() {

		return initialised;
	}

	IEditor getIEditor() {

		return iEditor;
	}

	CBuildListeners getBuildListeners() {

		return buildListeners;
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
}
