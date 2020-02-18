/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.app;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
public class DefaultCustomiser implements Customiser {

	private IStore store;

	public DefaultCustomiser(IStore store) {

		this.store = store;
	}

	public String getDisplayLabel(CValue<?> valueType) {

		return valueType.getDisplayLabel();
	}

	public String getDisplayLabel(IValue value) {

		if (value instanceof IFrame) {

			return getFrameDisplayLabel((IFrame)value);
		}

		return value.getDisplayLabel();
	}

	public String getFrameDisplayLabel(IFrame frame) {

		IFrameCategory category = frame.getCategory();

		if (category.reference()) {

			return getReferenceFrameDisplayLabel(frame);
		}

		if (category.disjunction()) {

			return getDisjunctionFrameDisplayLabel(frame);
		}

		return getAtomicFrameDisplayLabel(frame);
	}

	public String getAtomicFrameDisplayLabel(IFrame frame) {

		return frame.getDisplayLabel();
	}

	public String getReferenceFrameDisplayLabel(IFrame reference) {

		return getAtomicFrameDisplayLabel(loadFromStore(reference.getReferenceId()));
	}

	public String getDisjunctionFrameDisplayLabel(IFrame disjunction) {

		StringBuilder label = new StringBuilder();

		for (IFrame disjunct : disjunction.asDisjuncts()) {

			if (label.length() != 0) {

				label.append(" OR ");
			}

			label.append(getFrameDisplayLabel(disjunct));
		}

		return label.toString();
	}

	public boolean hiddenSlot(ISlot slot) {

		return false;
	}

	public IFrame onNewInstance(IFrame instance, CIdentity storeId) {

		return instance;
	}

	public IFrame onRenamingInstance(
					IFrame instance,
					CIdentity storeId,
					CIdentity newStoreId) {

		return instance;
	}

	private IFrame loadFromStore(CIdentity frameId) {

		return store.get(frameId).getRootFrame();
	}
}
