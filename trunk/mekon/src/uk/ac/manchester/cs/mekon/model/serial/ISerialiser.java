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

package uk.ac.manchester.cs.mekon.model.serial;

/**
 * @author Colin Puleston
 */
abstract class ISerialiser extends FSerialiser {

	static final String STORE_ID = "Store";
	static final String INSTANCE_ID = "Instance";

	static final String MFRAME_ID = "MFrame";
	static final String CNUMBER_ID = "CNumber";
	static final String CSLOT_ID = "CSlot";
	static final String IFRAME_ID = "IFrame";
	static final String INUMBER_ID = "INumber";
	static final String ISLOT_ID = "ISlot";

	static final String NUMBER_TYPE_ATTR = "numberType";
	static final String NUMBER_MIN_ATTR = "min";
	static final String NUMBER_MAX_ATTR = "max";
	static final String NUMBER_VALUE_ATTR = "value";
	static final String EDITABLE_SLOT_ATTR = "editable";
}
