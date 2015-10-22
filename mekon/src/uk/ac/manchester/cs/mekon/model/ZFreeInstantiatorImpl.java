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

import uk.ac.manchester.cs.mekon.mechanism.core.*;

/**
 * @author Colin Puleston
 */
class ZFreeInstantiatorImpl implements ZFreeInstantiator {

	static private Map<Class<? extends Number>, CNumberDef> numberDefs
						= new HashMap<Class<? extends Number>, CNumberDef>();

	static {

		numberDefs.put(Integer.class, CIntegerDef.UNCONSTRAINED);
		numberDefs.put(Long.class, CLongDef.UNCONSTRAINED);
		numberDefs.put(Float.class, CFloatDef.UNCONSTRAINED);
		numberDefs.put(Double.class, CDoubleDef.UNCONSTRAINED);
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

	public IFrame startInstantiation(CFrame type, IFrameCategory category) {

		return new IFrame(type, category);
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

		return addSlot(container, slotTypeId, getNumberValueType(numberType));
	}

	public void completeInstantiation(IFrame frame) {

		frame.completeInstantiation(true);
	}

	public IFrame deriveInstantiation(IFrame sourceInstance) {

		return new Deriver().copy(sourceInstance);
	}

	ZFreeInstantiatorImpl(CModel model) {

		rootFrame = model.getRootFrame();
	}

	private ISlot addSlot(IFrame container, CIdentity id, CValue<?> valueType) {

		return container.addSlot(createSlotType(container.getType(), id, valueType));
	}

	private CSlot createSlotType(CFrame containerType, CIdentity id, CValue<?> valueType) {

		return new CSlot(containerType, id, CCardinality.REPEATABLE_TYPES, valueType);
	}

	private CNumber getNumberValueType(Class<? extends Number> numberType) {

		return numberDefs.get(numberType).createNumber();
	}
}
