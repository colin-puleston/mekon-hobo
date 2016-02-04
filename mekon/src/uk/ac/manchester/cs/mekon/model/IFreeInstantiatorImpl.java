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

import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
class IFreeInstantiatorImpl implements IFreeInstantiator {

	static private Map<Class<? extends Number>, CNumber> numberTypes
						= new HashMap<Class<? extends Number>, CNumber>();

	static {

		numberTypes.put(Integer.class, CNumber.INTEGER);
		numberTypes.put(Long.class, CNumber.LONG);
		numberTypes.put(Float.class, CNumber.FLOAT);
		numberTypes.put(Double.class, CNumber.DOUBLE);
	}

	private CFrame rootFrame;

	private class SlotAdder extends CValueVisitor {

		private IFrame container;
		private CIdentity slotTypeId;
		private CValue<?> valueType;

		private ISlot slot = null;

		protected void visit(CFrame value) {

			slot = addIFrameSlot(container, slotTypeId);
		}

		protected void visit(CNumber value) {

			slot = addINumberSlot(container, slotTypeId, value.getNumberType());
		}

		protected void visit(MFrame value) {

			slot = addCFrameSlot(container, slotTypeId);
		}

		SlotAdder(IFrame container, CSlot slotType) {

			this.container = container;

			slotTypeId = slotType.getIdentity();
			valueType = slotType.getValueType();
		}

		ISlot add() {

			visit(valueType);

			return slot;
		}
	}

	private class Deriver extends IFrameCopierAbstract {

		ISlot addSlot(IFrame container, CSlot slotType) {

			return new SlotAdder(container, slotType).add();
		}

		boolean freeInstance() {

			return true;
		}
	}

	public IFrame startInstantiation(CFrame type, IFrameFunction function) {

		return new IFrame(type, function);
	}

	public ISlot addIFrameSlot(IFrame container, CIdentity slotTypeId) {

		return addSlot(container, slotTypeId, rootFrame);
	}

	public ISlot addCFrameSlot(IFrame container, CIdentity slotTypeId) {

		return addSlot(container, slotTypeId, rootFrame.getType());
	}

	public ISlot addINumberSlot(
					IFrame container,
					CIdentity slotTypeId,
					Class<? extends Number> numberType) {

		return addSlot(container, slotTypeId, numberTypes.get(numberType));
	}

	public void completeInstantiation(IFrame frame) {

		frame.completeInstantiation(true);
	}

	public IFrame createFreeCopy(IFrame sourceInstance) {

		return new Deriver().copy(sourceInstance);
	}

	IFreeInstantiatorImpl(CModel model) {

		rootFrame = model.getRootFrame();
	}

	private ISlot addSlot(
					IFrame container,
					CIdentity slotTypeId,
					CValue<?> valueType) {

		CFrame contType = container.getType();
		CSlot slotType = createSlotType(contType, slotTypeId, valueType);

		slotType.setEditability(CEditability.FULL);

		return container.addSlotInternal(slotType, true);
	}

	private CSlot createSlotType(
					CFrame containerType,
					CIdentity slotTypeId,
					CValue<?> valueType) {

		return new CSlot(
					containerType,
					slotTypeId,
					CCardinality.REPEATABLE_TYPES,
					valueType);
	}
}
