/**
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
package uk.ac.manchester.cs.mekon.gui;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class InferredTypesList extends GList<CFrame> {

	static private final long serialVersionUID = -1;

	private class Updater implements IFrameListener {

		public void onUpdatedInferredTypes(CIdentifieds<CFrame> inferredTypes) {

			update(inferredTypes);
		}
	}

	InferredTypesList(IFrame frame) {

		frame.addListener(new Updater());
	}

	private void update(CIdentifieds<CFrame> inferredTypes) {

		clearList();

		for (CFrame inferredType : inferredTypes.asList()) {

			add(inferredType);
		}
	}

	private void add(CFrame inferredType) {

		addEntity(inferredType, getTypeDisplay(inferredType));
	}

	private GCellDisplay getTypeDisplay(CFrame inferredType) {

		return EntityDisplays.get().get(inferredType, false);
	}
}
