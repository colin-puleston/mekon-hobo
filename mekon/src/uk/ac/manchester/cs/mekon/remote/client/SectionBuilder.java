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

package uk.ac.manchester.cs.mekon.remote.client;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.util.*;

/**
 * @author Colin Puleston
 */
class SectionBuilder implements CSectionBuilder {

	private CFrameHierarchy hierarchy;
	private IReasoner reasoner;

	private class HierarchyBuilder {

		private CBuilder builder;

		HierarchyBuilder(CBuilder builder) {

			this.builder = builder;

			addSubTree(hierarchy.getRootFrameId());
		}

		private CFrame addSubTree(CIdentity subRootId) {

			CFrame subRoot = addFrame(subRootId);

			for (CIdentity subId : hierarchy.getSubFrameIds(subRootId)) {

				addSubFrame(subRoot, checkAddSubTree(subId));
			}

			return subRoot;
		}

		private CFrame addFrame(CIdentity id) {

			CFrame frame = builder.addFrame(id, false);

			builder.setIReasoner(frame, reasoner);

			return frame;
		}

		private CFrame checkAddSubTree(CIdentity subRootId) {

			CFrame subRoot = builder.getFrames().getOrNull(subRootId);

			return subRoot != null ? subRoot : addSubTree(subRootId);
		}

		private void addSubFrame(CFrame sup, CFrame sub) {

			builder.getFrameEditor(sub).addSuper(sub);
		}
	}

	public boolean supportsIncrementalBuild() {

		return false;
	}

	public void build(CBuilder builder) {

		new HierarchyBuilder(builder);
	}

	SectionBuilder(CFrameHierarchy hierarchy, IReasoner reasoner) {

		this.hierarchy = hierarchy;
		this.reasoner = reasoner;
	}
}
