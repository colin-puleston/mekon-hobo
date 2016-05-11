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

import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
class IFreeInstantiatorImpl extends IFreeInstantiator {

	private FreeValueTypes freeValueTypes = new FreeValueTypes();

	private class FreeValueTypes extends CValueVisitor {

		private CValue<?> freeValueType;

		protected void visit(CFrame value) {

			freeValueType = value.getModel().getRootFrame();
		}

		protected void visit(CNumber value) {

			freeValueType = value.getUnconstrained();
		}

		protected void visit(CString value) {

			freeValueType = value;
		}

		protected void visit(MFrame value) {

			freeValueType = value.getRootCFrame().getModel().getRootFrame().getType();
		}

		CValue<?> get(CValue<?> templateValueType) {

			visit(templateValueType);

			return freeValueType;
		}
	}

	private class OneTimeInstantiator extends IFrameCopierAbstract {

		ISlot addSlot(IFrame container, CSlot slotType) {

			return addFreeSlot(container, slotType.getIdentity(), slotType.getValueType());
		}

		boolean freeInstance() {

			return true;
		}
	}

	public IFrame startInstantiation(CFrame type, IFrameFunction function) {

		return new IFrame(type, function);
	}

	public ISlot addSlot(
					IFrame container,
					CIdentity slotTypeId,
					CValue<?> templateValueType) {

		return addFreeSlot(container, slotTypeId, templateValueType);
	}

	public void completeInstantiation(IFrame frame) {

		frame.completeInstantiation(true);
	}

	public IFrame createFreeCopy(IFrame sourceInstance) {

		return new OneTimeInstantiator().copy(sourceInstance);
	}

	private ISlot addFreeSlot(IFrame container, CIdentity id, CValue<?> templateValueType) {

		return container.addFreeSlot(createFreeSlotType(container, id, templateValueType));
	}

	private CSlot createFreeSlotType(
						IFrame container,
						CIdentity id,
						CValue<?> templateValueType) {

		CFrame contType = container.getType();
		CValue<?> valueType = freeValueTypes.get(templateValueType);

		CSlot slotType = new CSlot(contType, id, valueType, CCardinality.REPEATABLE_TYPES);

		slotType.setEditability(CEditability.FULL);

		return slotType;
	}
}
