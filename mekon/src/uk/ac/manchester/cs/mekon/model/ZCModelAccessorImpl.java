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
import uk.ac.manchester.cs.mekon.model.zlink.*;

/**
 * @author Colin Puleston
 */
class ZCModelAccessorImpl extends ZCModelAccessor {

	private IFreeCopier freeCopier = new IFreeCopierImpl();
	private IRelaxedInstantiator relaxedInstantiator = new IRelaxedInstantiatorImpl();

	public CBuilder createBuilder() {

		return new CModel().getBuilder();
	}

	public CModel getModel(CBuilder builder) {

		return ((CBuilderImpl)builder).getModel();
	}

	public CBuilder getBuilder(CModel model) {

		return model.getBuilder();
	}

	public CNumber createCNumber(
						Class<? extends Number> numberType,
						INumber min,
						INumber max) {

		return new CNumber(numberType, min, max);
	}

	public CString resolveCustomCString(Class<? extends CStringConfig> configClass) {

		return CustomCStrings.resolve(configClass);
	}

	public Class<? extends CStringConfig> getCustomCStringConfigClass(CString string) {

		return CustomCStrings.getConfigClass(string);
	}

	public IEditor getIEditor(CModel model) {

		return model.getIEditor();
	}

	public IFreeCopier getFreeCopier() {

		return freeCopier;
	}

	public IRelaxedInstantiator getRelaxedInstantiator() {

		return relaxedInstantiator;
	}

	public boolean freeInstance(IFrame frame) {

		return frame.freeInstance();
	}

	public void setMappedObject(IFrame frame, Object mappedObject) {

		frame.setMappedObject(mappedObject);
	}

	public Object getMappedObject(IFrame frame) {

		return frame.getMappedObject();
	}

	public ISlot addReferenceFrameMappingSlot(IFrame frame, CSlot slotType) {

		return ((IReference)frame).addSlotInternal(slotType);
	}
}
