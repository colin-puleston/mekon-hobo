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

package uk.ac.manchester.cs.mekon.store.disk;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class ReverseInstanceRefsManager {

	private IDiskStore store;

	private List<IReverseRefType> reverseRefTypes = new ArrayList<IReverseRefType>();

	private abstract class RefTypesProcessor {

		void processAll(CFrame testReferType) {

			for (IReverseRefType refType : reverseRefTypes) {

				if (getProcessReferType(refType).subsumes(testReferType)) {

					updateForMatchingRefType(refType);
				}
			}
		}

		abstract void updateForMatchingRefType(IReverseRefType refType);

		abstract CFrame getProcessReferType(IReverseRefType refType);

		private boolean hasTargetValueType(IFrame assertion, CFrame type) {

			return type.subsumes(assertion.getType());
		}
	}

	private abstract class ForwardReferProcessor extends RefTypesProcessor {

		final IFrame testForwardRefer;
		final CIdentity testForwardReferId;

		ForwardReferProcessor(IFrame testForwardRefer, CIdentity testForwardReferId) {

			this.testForwardRefer = testForwardRefer;
			this.testForwardReferId = testForwardReferId;

			processAll(testForwardRefer.getType());
		}

		void updateForMatchingRefType(IReverseRefType refType) {

			for (IFrame ref : refType.getAllForwardRefValues(testForwardRefer)) {

				checkUpdateInStore(refType, ref.getReferenceId());
			}
		}

		CFrame getProcessReferType(IReverseRefType refType) {

			return refType.getForwardReferType();
		}

		abstract boolean checkUpdateReverseRef(IReverseRefType refType, IFrame reverseRefer);

		private void checkUpdateInStore(IReverseRefType refType, CIdentity reverseReferId) {

			IFrame reverseRefer = store.regenOrNull(reverseReferId, false);

			if (reverseRefer != null && checkUpdateReverseRef(refType, reverseRefer)) {

				store.update(reverseRefer, reverseReferId);
			}
		}
	}

	private class ForwardReferAddProcessor extends ForwardReferProcessor {

		ForwardReferAddProcessor(IFrame testForwardRefer, CIdentity testForwardReferId) {

			super(testForwardRefer, testForwardReferId);
		}

		boolean checkUpdateReverseRef(IReverseRefType refType, IFrame reverseRefer) {

			return refType.checkAddReverseRef(testForwardRefer, reverseRefer, testForwardReferId);
		}
	}

	private class ForwardReferRemoveProcessor extends ForwardReferProcessor {

		ForwardReferRemoveProcessor(IFrame testForwardRefer, CIdentity testForwardReferId) {

			super(testForwardRefer, testForwardReferId);
		}

		boolean checkUpdateReverseRef(IReverseRefType refType, IFrame reverseRefer) {

			return refType.checkRemoveReverseRef(testForwardRefer, reverseRefer, testForwardReferId);
		}
	}

	private class ReverseReferReplaceProcessor extends RefTypesProcessor {

		private IFrame testOldReverseRefer;
		private IFrame testNewReverseRefer;

		ReverseReferReplaceProcessor(IFrame testOldReverseRefer, IFrame testNewReverseRefer) {

			this.testOldReverseRefer = testOldReverseRefer;
			this.testNewReverseRefer = testNewReverseRefer;

			processAll(testOldReverseRefer.getType());
		}

		void updateForMatchingRefType(IReverseRefType refType) {

			refType.alignReverseRefs(testOldReverseRefer, testNewReverseRefer);
		}

		CFrame getProcessReferType(IReverseRefType refType) {

			return refType.getReverseReferType();
		}
	}

	ReverseInstanceRefsManager(IDiskStore store) {

		this.store = store;
	}

	void addReverseRefTypes(Collection<IReverseRefType> refTypes) {

		reverseRefTypes.addAll(refTypes);
	}

	void onAddedInstance(IFrame newInstance, CIdentity identity) {

		if (assertion(newInstance)) {

			new ForwardReferAddProcessor(newInstance, identity);
		}
	}

	void onRemovedInstance(IFrame oldInstance, CIdentity identity) {

		if (assertion(oldInstance)) {

			new ForwardReferRemoveProcessor(oldInstance, identity);
		}
	}

	void onReplacedInstance(IFrame oldInstance, IFrame newInstance, CIdentity identity) {

		onRemovedInstance(oldInstance, identity);
		onAddedInstance(newInstance, identity);

		if (assertion(oldInstance) && assertion(newInstance)) {

			new ReverseReferReplaceProcessor(oldInstance, newInstance);
		}
	}

	private boolean assertion(IFrame instance) {

		return instance.getFunction().assertion();
	}
}
