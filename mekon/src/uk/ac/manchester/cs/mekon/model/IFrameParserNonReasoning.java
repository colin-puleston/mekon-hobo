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

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.model.serial.*;

/**
 * @author Colin Puleston
 */
class IFrameParserNonReasoning extends IFrameParserLocal {

	static private Map<Class<? extends Number>, CNumberDef> defaultNumberDefs
							= new HashMap<Class<? extends Number>, CNumberDef>();

	static {

		defaultNumberDefs.put(Integer.class, CIntegerDef.UNCONSTRAINED);
		defaultNumberDefs.put(Long.class, CLongDef.UNCONSTRAINED);
		defaultNumberDefs.put(Float.class, CFloatDef.UNCONSTRAINED);
		defaultNumberDefs.put(Double.class, CDoubleDef.UNCONSTRAINED);
	}

	private CFrame rootFrame;

	protected IFrame instantiateFrame(CFrame type, IFrameCategory category) {

		return new IFrame(type, category);
	}

	protected ISlot checkResolveIFrameSlot(IFrame frame, CIdentity slotId) {

		return addSlot(frame, slotId, rootFrame);
	}

	protected ISlot checkResolveCFrameSlot(IFrame frame, CIdentity slotId) {

		return addSlot(frame, slotId, rootFrame.getType());
	}

	protected ISlot checkResolveINumberSlot(
						IFrame frame,
						CIdentity slotId,
						Class<? extends Number> numberType) {

		return addSlot(frame, slotId, getDefaultNumber(numberType));
	}

	protected void checkUpdateFrameSlotSets(List<IFrame> frames) {
	}

	protected void checkUpdateFramesOnParseCompletion(List<IFrame> frames) {
	}

	IFrameParserNonReasoning(CModel model, IFrameCategory frameCategory) {

		super(model, frameCategory);

		rootFrame = model.getRootFrame();
	}

	private ISlot addSlot(IFrame frame, CIdentity id, CValue<?> valueType) {

		return frame.addSlot(createSlotType(frame.getType(), id, valueType));
	}

	private CSlot createSlotType(CFrame frameType, CIdentity id, CValue<?> valueType) {

		return new CSlot(frameType, id, CCardinality.REPEATABLE_TYPES, valueType);
	}

	private CNumber getDefaultNumber(Class<? extends Number> numberType) {

		if (numberType == null) {

			throw new KSystemConfigException("Number type not specified");
		}

		CNumberDef numberDef = defaultNumberDefs.get(numberType);

		if (numberDef == null) {

			throw new Error("Unrecognised number type: " + numberType);
		}

		return numberDef.createNumber();
	}
}