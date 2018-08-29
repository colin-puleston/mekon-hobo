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
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;

/**
 * @author Colin Puleston
 */
class CBuilderImpl implements CBuilder {

	private CModel model;
	private List<CSectionBuilder> sectionBuilders = new ArrayList<CSectionBuilder>();

	public void setQueriesEnabled(boolean enabled) {

		model.setQueriesEnabled(enabled);
	}

	public void setAutoUpdate(boolean autoUpdate) {

		model.getIUpdating().setAutoUpdate(autoUpdate);
	}

	public void setDefaultUpdateOp(IUpdateOp op, boolean enabled) {

		model.getIUpdating().setDefaultOp(op, enabled);
	}

	public void addListener(CBuildListener listener) {

		model.getBuildListeners().add(listener);
	}

	public void removeListener(CBuildListener listener) {

		model.getBuildListeners().remove(listener);
	}

	public void addSectionBuilder(CSectionBuilder sectionBuilder) {

		sectionBuilders.add(sectionBuilder);
	}

	public void removeSectionBuilder(CSectionBuilder sectionBuilder) {

		sectionBuilders.remove(sectionBuilder);
	}

	public void removeSectionBuilders(Class<? extends CSectionBuilder> type) {

		for (CSectionBuilder bldr : new ArrayList<CSectionBuilder>(sectionBuilders)) {

			if (type.isAssignableFrom(bldr.getClass())) {

				sectionBuilders.remove(bldr);
			}
		}
	}

	public void clearSectionBuilders() {

		sectionBuilders.clear();
	}

	public CFrame addFrame(CIdentity identity, boolean hidden) {

		if (getFrames().containsValueFor(identity)) {

			throw new KModelException("Frame already defined: " + identity);
		}

		return model.addFrame(identity, hidden);
	}

	public CFrame resolveFrame(CIdentity identity, boolean hidden) {

		CFrame frame = getFrames().getOrNull(identity);

		if (frame != null) {

			frame.asAtomicFrame().absorbHidden(hidden);

			return frame;
		}

		return model.addFrame(identity, hidden);
	}

	public boolean removeFrame(CIdentity identity) {

		CFrame frame = getFrames().getOrNull(identity);

		return frame != null && model.removeFrame(frame.asAtomicFrame());
	}

	public void setIReasoner(CFrame frame, IReasoner iReasoner) {

		frame.asAtomicFrame().setIReasoner(iReasoner);
	}

	public List<CSectionBuilder> getAllSectionBuilders() {

		return sectionBuilders;
	}

	public <B extends CSectionBuilder>B getSectionBuilder(Class<B> type) {

		for (CSectionBuilder sectionBuilder : sectionBuilders) {

			if (type.isAssignableFrom(sectionBuilder.getClass())) {

				return type.cast(sectionBuilder);
			}
		}

		throw new KAccessException(
					"Cannot find section-builder of type: "
					+ type);
	}

	public CFrame getRootFrame() {

		return model.getRootFrame();
	}

	public CIdentifieds<CFrame> getFrames() {

		return model.getFrames();
	}

	public CFrameEditor getFrameEditor(CFrame frame) {

		return frame.asAtomicFrame().createEditor();
	}

	public CSlotEditor getSlotEditor(CSlot slot) {

		return slot.createEditor();
	}

	public CAnnotationsEditor getAnnotationsEditor(CAnnotations annotations) {

		return annotations.createEditor();
	}

	public CAnnotationsEditor getSlotAnnotationsEditor(CIdentity slotId) {

		return model.getSlotAnnotations(slotId).createEditor();
	}

	public IEditor getIEditor() {

		return model.getIEditor();
	}

	public CModel build() {

		model.startInitialisation();
		buildSections();
		model.completeInitialisation();

		return model;
	}

	public void optimiseSubsumptionTesting() {

		model.optimiseSubsumptionTesting();
	}

	CBuilderImpl(CModel model) {

		this.model = model;
	}

	CModel getModel() {

		return model;
	}

	private void buildSections() {

		boolean initialBuild = initialBuild();

		for (CSectionBuilder sectionBuilder : sectionBuilders) {

			if (initialBuild || sectionBuilder.supportsIncrementalBuild()) {

				sectionBuilder.build(this);
			}
		}
	}

	private boolean initialBuild() {

		return model.getRootFrame().getSubs().isEmpty();
	}
}
