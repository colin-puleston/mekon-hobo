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

	private List<RefTypeProcessor> refTypeProcessors = new ArrayList<RefTypeProcessor>();

	private class RefTypeProcessor {

		private IReverseRefType refType;

		private ReverseRefsAdder reverseRefsAdder = new ReverseRefsAdder();
		private ReverseRefsRemover reverseRefsRemover = new ReverseRefsRemover();

		private abstract class ReverseRefsUpdater {

			void checkUpdateInStore(IFrame forwardRefer) {

				CIdentity reverseReferId = getReverseReferIdOrNull(forwardRefer);

				if (reverseReferId != null) {

					checkUpdateInStore(forwardRefer, reverseReferId);
				}
			}

			abstract boolean checkUpdateReverseRef(IFrame forwardRefer, IFrame reverseRefer);

			private void checkUpdateInStore(IFrame forwardRefer, CIdentity reverseReferId) {

				IFrame reverseRefer = store.regenOrNull(reverseReferId, false);

				if (reverseRefer != null) {

					if (checkUpdateReverseRef(forwardRefer, reverseRefer)) {

						store.update(reverseRefer, reverseReferId);
					}
				}
			}
		}

		private class ReverseRefsAdder extends ReverseRefsUpdater {

			boolean checkUpdateReverseRef(IFrame forwardRefer, IFrame reverseRefer) {

				return refType.checkAddReverseRef(forwardRefer, reverseRefer);
			}
		}

		private class ReverseRefsRemover extends ReverseRefsUpdater {

			boolean checkUpdateReverseRef(IFrame forwardRefer, IFrame reverseRefer) {

				return refType.checkRemoveReverseRef(forwardRefer, reverseRefer);
			}
		}

		RefTypeProcessor(IReverseRefType refType) {

			this.refType = refType;

			refTypeProcessors.add(this);
		}

		boolean checkHandleUpdate(IFrame oldAssertion, IFrame newAssertion) {

			if (checkHandleForwardReferUpdate(oldAssertion, newAssertion)) {

				return true;
			}

			if (oldAssertion != null && newAssertion != null) {

				if (checkHandleReverseReferReplace(oldAssertion, newAssertion)) {

					return true;
				}
			}

			return false;
		}

		private boolean checkHandleForwardReferUpdate(IFrame oldAssertion, IFrame newAssertion) {

			boolean removed = checkHandleForwardReferUpdate(oldAssertion, reverseRefsRemover);
			boolean added = checkHandleForwardReferUpdate(newAssertion, reverseRefsAdder);

			return removed || added;
		}

		private boolean checkHandleForwardReferUpdate(
							IFrame assertion,
							ReverseRefsUpdater updater) {

			if (existsWithForwardReferType(assertion)) {

				updater.checkUpdateInStore(assertion);

				return true;
			}

			return false;
		}

		private boolean checkHandleReverseReferReplace(IFrame oldAssertion, IFrame newAssertion) {

			if (hasReverseReferType(oldAssertion)) {

				refType.alignReverseRefs(oldAssertion, newAssertion);

				return true;
			}

			return false;
		}

		private CIdentity getReverseReferIdOrNull(IFrame forwardRefer) {

			IFrame ref = refType.lookForForwardRefValue(forwardRefer);

			return ref != null ? ref.getReferenceId() : null;
		}

		private boolean existsWithForwardReferType(IFrame assertion) {

			return assertion != null && hasType(assertion, refType.getForwardReferType());
		}

		private boolean hasReverseReferType(IFrame assertion) {

			return hasType(assertion, refType.getReverseReferType());
		}

		private boolean hasType(IFrame assertion, CFrame type) {

			return type.subsumes(assertion.getType());
		}
	}

	ReverseInstanceRefsManager(IDiskStore store) {

		this.store = store;
	}

	void addRefTypes(Collection<IReverseRefType> refTypes) {

		for (IReverseRefType refType : refTypes) {

			new RefTypeProcessor(refType);
		}
	}

	void onNewInstance(IFrame newInstance) {

		IFrame newAssertion = getAssertionOrNull(newInstance);

		if (newAssertion != null) {

			checkHandleUpdate(null, newAssertion);
		}
	}

	void onReplacedInstance(IFrame oldInstance, IFrame newInstance) {

		IFrame oldAssertion = getAssertionOrNull(oldInstance);
		IFrame newAssertion = getAssertionOrNull(newInstance);

		if (oldAssertion != null && newAssertion != null) {

			checkHandleUpdate(oldAssertion, newAssertion);
		}
	}

	void onRemovedInstance(IFrame oldInstance) {

		IFrame oldAssertion = getAssertionOrNull(oldInstance);

		if (oldAssertion != null) {

			checkHandleUpdate(oldAssertion, null);
		}
	}

	private void checkHandleUpdate(IFrame oldAssertion, IFrame newAssertion) {

		for (RefTypeProcessor processor : refTypeProcessors) {

			if (processor.checkHandleUpdate(oldAssertion, newAssertion)) {

				break;
			}
		}
	}

	private IFrame getAssertionOrNull(IFrame instance) {

		if (instance != null && instance.getFunction().assertion()) {

			return instance;
		}

		return null;
	}
}
