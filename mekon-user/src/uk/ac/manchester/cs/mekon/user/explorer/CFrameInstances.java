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

package uk.ac.manchester.cs.mekon.user.explorer;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
class CFrameInstances {

	private List<CIdentity> allForRootFrame;

	private KSetMap<CFrame, CIdentity> directForAllFrames = new KSetMap<CFrame, CIdentity>();
	private Map<CIdentity, CFrame> directFramesFor = new HashMap<CIdentity, CFrame>();

	private class Initialiser {

		private CFrame rootFrame;
		private InstanceStoreActions storeActions;

		private KSetMap<CFrame, CIdentity> allForAllFrames = new KSetMap<CFrame, CIdentity>();

		Initialiser(CFrame rootFrame, InstanceStoreActions storeActions) {

			this.rootFrame = rootFrame;
			this.storeActions = storeActions;

			allForRootFrame = queryForAllInstances();

			findAllFrameInstances();
			findAllDirectFrameInstances(rootFrame);
		}

		private void findAllFrameInstances() {

			for (CIdentity instance : allForRootFrame) {

				allForAllFrames.add(storeActions.getInstanceType(instance), instance);
			}
		}

		private Set<CIdentity> findAllDirectFrameInstances(CFrame frame) {

			Set<CIdentity> allForRootFrame = allForAllFrames.getSet(frame);

			if (!directForAllFrames.containsKey(frame)) {

				Set<CIdentity> directInstances = new HashSet<CIdentity>(allForRootFrame);

				for (CFrame sub : frame.getSubs(CVisibility.EXPOSED)) {

					directInstances.removeAll(findAllDirectFrameInstances(sub));
				}

				addDirectFrameInstances(frame, directInstances);
			}

			return allForRootFrame;
		}

		private void addDirectFrameInstances(CFrame frame, Set<CIdentity> instances) {

			directForAllFrames.addAll(frame, instances);

			for (CIdentity instance : instances) {

				directFramesFor.put(instance, frame);
			}
		}

		private List<CIdentity> queryForAllInstances() {

			IFrame query = rootFrame.instantiateQuery();

			return storeActions.executeQuery(query, false).getAllMatches();
		}
	}

	CFrameInstances(CFrame rootFrame, InstanceStoreActions storeActions) {

		new Initialiser(rootFrame, storeActions);
	}

	boolean any() {

		return !allForRootFrame.isEmpty();
	}

	List<CIdentity> getAllForRootFrame() {

		return allForRootFrame;
	}

	Set<CIdentity> getDirectFor(CFrame frame) {

		return directForAllFrames.getSet(frame);
	}

	boolean anyDirectFor(CFrame frame) {

		return directForAllFrames.containsKey(frame);
	}

	CFrame getDirectFrameFor(CIdentity instance) {

		return directFramesFor.get(instance);
	}
}
