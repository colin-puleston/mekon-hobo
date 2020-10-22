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

package uk.ac.manchester.cs.mekon.model.zlink;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * THIS CLASS SHOULD NOT BE ACCESSED DIRECTLY BY EITHER THE CLIENT
 * OR THE PLUGIN CODE.
 * <p>
 * Provides the MEKON mechanisms, and the mechanisms of any
 * extensions of the MEKON framework, with privileged access to
 * the MEKON model.
 *
 * @author Colin Puleston
 */
public abstract class ZCModelAccessor {

	static private KSingleton<ZCModelAccessor> singleton
							= new KSingleton<ZCModelAccessor>();

	static public synchronized void set(ZCModelAccessor accessor) {

		singleton.set(accessor);
	}

	static public ZCModelAccessor get() {

		return singleton.get(CModel.class);
	}

	public abstract CBuilder createBuilder();

	public abstract CModel getModel(CBuilder builder);

	public abstract CBuilder getBuilder(CModel model);

	public abstract CNumber createCNumber(
								Class<? extends Number> numberType,
								INumber min,
								INumber max);

	public abstract CString resolveCustomCString(Class<? extends CStringConfig> configClass);

	public abstract Class<? extends CStringConfig> getCustomCStringConfigClass(CString string);

	public abstract IEditor getIEditor(CModel model);

	public abstract IFreeCopier getFreeCopier();

	public abstract IRelaxedInstantiator getRelaxedInstantiator();

	public abstract boolean freeInstance(IFrame frame);

	public abstract void setMappedObject(IFrame frame, Object mappedObject);

	public abstract Object getMappedObject(IFrame frame);

	public abstract ISlot addReferenceFrameMappingSlot(IFrame frame, CSlot slotType);
}
