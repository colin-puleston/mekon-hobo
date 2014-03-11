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

/**
 * @author Colin Puleston
 */
class DynamicSlotValueTypeFrames {

	static private final String VALUE_TYPE_FRAME_NAME_EXTN_FORMAT = "-SUB-%d";

	private CModel model;

	private int valueTypeFrameCount = 0;
	private Map<CModelFrame, ValueTypeSet> valueTypeSetsBySupers
							= new HashMap<CModelFrame, ValueTypeSet>();

	private class ValueTypeSet {

		private CModelFrame sup;
		private Map<List<CModelFrame>, CModelFrame> valueTypesBySubs
						= new HashMap<List<CModelFrame>, CModelFrame>();

		ValueTypeSet(CModelFrame sup) {

			this.sup = sup;

			valueTypeSetsBySupers.put(sup, this);
		}

		CModelFrame getForSubs(List<CModelFrame> subs) {

			CModelFrame valueTypeFrame = valueTypesBySubs.get(subs);

			return valueTypeFrame != null ? valueTypeFrame : ensureForSubs(subs);
		}

		private synchronized CModelFrame ensureForSubs(List<CModelFrame> subs) {

			CModelFrame valueTypeFrame = valueTypesBySubs.get(subs);

			if (valueTypeFrame == null) {

				valueTypeFrame = createForSubs(subs);
				valueTypesBySubs.put(subs, valueTypeFrame);
			}

			return valueTypeFrame;
		}

		private CModelFrame createForSubs(List<CModelFrame> subs) {

			CModelFrame valueTypeFrame = create();

			valueTypeFrame.addSuper(sup);

			for (CModelFrame sub : subs) {

				sub.addSuper(valueTypeFrame);
			}

			return valueTypeFrame;
		}

		private CModelFrame create() {

			return model.addFrame(getNewIdentity(), true, DefaultIReasoner.singleton);
		}

		private CIdentity getNewIdentity() {

			CIdentity id = null;

			do {

				id = createNextIdentity();
			}
			while (model.getFrames().containsValueFor(id));

			return id;
		}

		private CIdentity createNextIdentity() {

			CIdentity supId = sup.getIdentity();
			String ext = getNextIdentityExtension();

			return new CIdentity(supId.getIdentifier() + ext, supId.getLabel() + ext);
		}

		private String getNextIdentityExtension() {

			return String.format(VALUE_TYPE_FRAME_NAME_EXTN_FORMAT, ++valueTypeFrameCount);
		}
	}

	DynamicSlotValueTypeFrames(CModel model) {

		this.model = model;
	}

	synchronized CFrame get(CFrame sup, List<CFrame> subs) {

		return get(sup.asModelFrame(), asModelFrames(subs));
	}

	private CModelFrame get(CModelFrame sup, List<CModelFrame> subs) {

		ValueTypeSet set = valueTypeSetsBySupers.get(sup);

		if (set == null) {

			set = ensureValueTypeSet(sup);
		}

		return set.getForSubs(subs);
	}

	private synchronized ValueTypeSet ensureValueTypeSet(CModelFrame sup) {

		ValueTypeSet set = valueTypeSetsBySupers.get(sup);

		return set != null ? set : new ValueTypeSet(sup);
	}

	private List<CModelFrame> asModelFrames(List<CFrame> frames) {

		List<CModelFrame> modelFrames = new ArrayList<CModelFrame>();

		for (CFrame frame : frames) {

			modelFrames.add(frame.asModelFrame());
		}

		return modelFrames;
	}
}
