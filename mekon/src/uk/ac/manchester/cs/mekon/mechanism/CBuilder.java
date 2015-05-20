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

package uk.ac.manchester.cs.mekon.mechanism;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Provides mechanisms for building a Frames Model (FM), as
 * represented by a {@link CModel}.
 *
 * @author Colin Puleston
 */
public interface CBuilder {

	/**
	 * Sets the {@link CModel#queriesEnabled} flag.
	 *
	 * @param enabled True if query-instances are to be allowed
	 */
	public void setQueriesEnabled(boolean enabled);

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
	 * Sets the directory for {@link IStore} serialisation. Defaults
	 * to the current directory.
	 *
	 * @param storeDirectory Required serialisation directory
	 */
	public void setIStoreDirectory(File storeDirectory);

	/**
	 * Adds a pre-processor for instances that are to be added to the
	 * {@link IStore}.
	 *
	 * @param preProcessor Pre-processor for instances to be stored
	 */
	public void addIStoreInstancePreProcessor(IFrameProcessor preProcessor);

	/**
	 * Adds a pre-processor for queries that are to be executed over
	 * the {@link IStore}.
	 *
	 * @param preProcessor Pre-processor for queries to be executed
	 */
	public void addIStoreQueryPreProcessor(IFrameProcessor preProcessor);

	/**
	 * Adds an instance-matcher object for the model.
	 *
	 * @param iMatcher Instance-matcher for model
	 */
	public void addIMatcher(IMatcher iMatcher);

	/**
	 * Adds a builder for a section of the model.
	 *
	 * @param sectionBuilder Builder to build section of model
	 */
	public void addSectionBuilder(CSectionBuilder sectionBuilder);

	/**
	 * Creates a new atomic-frame and adds it to the model.
	 *
	 * @param identity Identify of frame to create
	 * @param hidden True if created frame is to be a "hidden" frame
	 * @return Created frame
	 * @throws new KAccessException if frame with specified identity
	 * already exists
	 */
	public CFrame addFrame(CIdentity identity, boolean hidden);

	/**
	 * Either retrieves an existing atomic-frame with the specified
	 * identity or, if no such frame currently exists, creates a new
	 * atomic-frame and adds it to the model.
	 *
	 * @param identity Identify of frame to retrieve or create
	 * @param hidden True if created frame is to be a "hidden" frame
	 * (will have no effect if frame already exists)
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
	 * Provides an editor for the specified annotations.
	 *
	 * @param annotations Annotations for which editor is required
	 * @return Editor for specified annotations
	 */
	public CAnnotationsEditor getAnnotationsEditor(CAnnotations annotations);

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
