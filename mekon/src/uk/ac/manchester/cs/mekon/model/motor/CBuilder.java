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

package uk.ac.manchester.cs.mekon.model.motor;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * Provides mechanisms for building a Frames Model (FM), as
 * represented by a {@link CModel}.
 *
 * @author Colin Puleston
 */
public interface CBuilder {

	/**
	 * Enables or disables the {@link IUpdating#autoUpdate} facility
	 * for the model. By default auto-update will be enabled.
	 *
	 * @param autoUpdate True if auto-update is to be enabled
	 */
	public void setAutoUpdate(boolean autoUpdate);

	/**
	 * Resets default enabled-status for a specific instance-update
	 * operation, as represented via the {@link IUpdating} object
	 * for the model. If not reset the default enabled-status for each
	 * operation will be true.
	 *
	 * @param op Relevant instance-update operation
	 * @param enabled Default enabled-status of operation
	 */
	public void setDefaultUpdateOp(IUpdateOp op, boolean enabled);

	/**
	 * Adds a listener for events occuring during with the build
	 * process.
	 *
	 * @param listener Listener to add
	 */
	public void addListener(CBuildListener listener);

	/**
	 * Removes a listener for events occuring during with the build
	 * process.
	 *
	 * @param listener Listener to remove
	 */
	public void removeListener(CBuildListener listener);

	/**
	 * Adds a section-builder to build a section of the model.
	 *
	 * @param sectionBuilder Section-builder to add
	 */
	public void addSectionBuilder(CSectionBuilder sectionBuilder);

	/**
	 * Removes the specified section-builder, if present.
	 *
	 * @param sectionBuilder Section-builder to add
	 */
	public void removeSectionBuilder(CSectionBuilder sectionBuilder);

	/**
	 * Removes any section-builders of the specified type that have been added.
	 *
	 * @param type Type of section-builder to remove
	 */
	public void removeSectionBuilders(Class<? extends CSectionBuilder> type);

	/**
	 * Removes all section-builders that have been added.
	 */
	public void clearSectionBuilders();

	/**
	 * Creates a new atomic-frame and adds it to the model.
	 *
	 * @param identity Identify of frame to create
	 * @param hidden True if created frame is to be a "hidden" frame
	 * @return Created frame
	 * @throws KAccessException if frame with specified identity
	 * already exists
	 */
	public CFrame addFrame(CIdentity identity, boolean hidden);

	/**
	 * Either retrieves an existing atomic-frame with the specified
	 * identity or, if no such frame currently exists, creates a new
	 * atomic-frame and adds it to the model.
	 *
	 * @param identity Identify of frame to retrieve or create
	 * @param hidden True if created frame, or existing frame, is to
	 * be a "hidden" frame (if already defined as hidden, a value of
	 * false will have no effect)
	 * @return Retrieved or created frame
	 */
	public CFrame resolveFrame(CIdentity identity, boolean hidden);

	/**
	 * Removes the specified atomic-frame from the model, if possible.
	 * Does nothing if frame is not part of the current model, or if
	 * it has a {@link CSource#internal} source. If removed then direct
	 * hierarchical links will be created from each sub-frame to each
	 * super-frame.
	 *
	 * @param identity Identify of frame to be removed
	 * @return True if frame was removed
	 */
	public boolean removeFrame(CIdentity identity);

	/**
	 * Sets the instance-reasoner object for an existing atomic-frame.
	 *
	 * @param frame Frame whose instance-reasoner is to be set
	 * @param iReasoner Instance-reasoner for frame
	 */
	public void setIReasoner(CFrame frame, IReasoner iReasoner);

	/**
	 * Provides the object for defining the required ordering, for
	 * purposes of presentaion to an end-user, of the slots associated
	 * with specific atomic-frames.
	 *
	 * @return Object for defining frame-slot orderings
	 */
	public CFrameSlotOrders getFrameSlotOrders();

	/**
	 * Retrieves all section-building mechanisms that will be used
	 * in creating the model.
	 *
	 * @return All section-building mechanisms
	 */
	public List<CSectionBuilder> getAllSectionBuilders();

	/**
	 * Retrieves a required section-building mechanism as specified
	 * via the relevant java class.
	 *
	 * @param <B> Generic version of type
	 * @param type Java class of required section-building mechanism
	 * @return Required section-building mechanism
	 */
	public <B extends CSectionBuilder>B getSectionBuilder(Class<B> type);

	/**
	 * Provides the unique root-frame for the model.
	 *
	 * @return Root-frame for model
	 */
	public CFrame getRootFrame();

	/**
	 * Retrieves all frames in the model.
	 *
	 * @return All frames in model
	 */
	public CIdentifieds<CFrame> getFrames();

	/**
	 * Provides an editor for the specified frame.
	 *
	 * @param frame Frame for which editor is required
	 * @return Editor for specified frame
	 */
	public CFrameEditor getFrameEditor(CFrame frame);

	/**
	 * Provides an editor for the specified slot.
	 *
	 * @param slot Slot for which editor is required
	 * @return Editor for specified slot
	 */
	public CSlotEditor getSlotEditor(CSlot slot);

	/**
	 * Provides an editor for annotations on the model itself.
	 *
	 * @return Editor for model annotations
	 */
	public CAnnotationsEditor getModelAnnotationsEditor();

	/**
	 * Provides an editor for annotations on the specified frame.
	 *
	 * @param frame Frame for which editor is required
	 * @return Editor for relevant frame annotations
	 */
	public CAnnotationsEditor getFrameAnnotationsEditor(CFrame frame);

	/**
	 * Provides an editor for annotations on the specified slot-set.
	 *
	 * @param slotId Identity of slots in slot-set for which editor
	 * is required
	 * @return Editor for relevant slot-set annotations
	 */
	public CAnnotationsEditor getSlotAnnotationsEditor(CIdentity slotId);

	/**
	 * Provides the editor that allows "under-the-hood" editing of
	 * model instantiations.
	 *
	 * @return Under-the-hood editor for model instantiations
	 */
	public IEditor getIEditor();

	/**
	 * Builds the model, which will incorporate all entities
	 * provided by the various section-building mechanisms.
	 * Subsequent invocations will cause any section-builders
	 * for which the {@link CSectionBuilder#supportsIncrementalBuild}
	 * flag is set to perform appropriate incremental updates.
	 *
	 * @return Built/updated model
	 */
	public CModel build();

	/**
	 * Performs subsumption-check optimisations on partially built
	 * model. Should be invoked at strategic points in the build
	 * process after which subsequent model-building will be
	 * heavily dependent on subsumption-checks within the currently
	 * built sections.
	 */
	public void optimiseSubsumptionTesting();
}
