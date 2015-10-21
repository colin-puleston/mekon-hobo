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

package uk.ac.manchester.cs.mekon.serial;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * Parser for the standard XML serialisation of {@link IFrame}
 * objects that produces a "free" instantiation of the frame
 * (see {@link IFreeInstanceGenerator}).
 *
 * @author Colin Puleston
 */
public class IFrameFreeParser extends IFrameParserAbstract {

	private IFreeInstantiator freeInstantiator;

	/**
	 * Constructor
	 *
	 * @param model Relevant model
	 * @param frameCategory Category of frames to be parsed
	 */
	public IFrameFreeParser(CModel model, IFrameCategory frameCategory) {

		super(model, frameCategory);

		freeInstantiator = getAccessor().getFreeInstantiator();
	}

	IFrame instantiateFrame(CFrame type, IFrameCategory category) {

		return freeInstantiator.startInstantiation(type, category);
	}

	ISlot checkResolveIFrameSlot(IFrame frame, CIdentity slotId) {

		return freeInstantiator.addIFrameSlot(frame, slotId);
	}

	ISlot checkResolveCFrameSlot(IFrame frame, CIdentity slotId) {

		return freeInstantiator.addCFrameSlot(frame, slotId);
	}

	ISlot checkResolveINumberSlot(
				IFrame frame,
				CIdentity slotId,
				Class<? extends Number> numberType) {

		return freeInstantiator.addINumberSlot(frame, slotId, numberType);
	}

	void checkUpdateFrameSlotSets(List<IFrame> frames) {
	}

	void onParseCompletion(IFrame rootFrame, List<IFrame> frames) {

		freeInstantiator.completeInstantiation(rootFrame);
	}
}