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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * Vocabulary for the standard XML serialisation of MEKON
 * frames model entities.
 *
 * @author Colin Puleston
 */
public interface FSerialiserVocab {

	static public final String IDENTITY_ID = "CIdentity";
	static public final String IDENTITIES_LIST_ID = "CIdentities";
	static public final String MFRAME_ID = "MFrame";
	static public final String CFRAME_ID = "CFrame";
	static public final String CNUMBER_ID = "CNumber";
	static public final String CSTRING_ID = "CString";
	static public final String CSLOT_ID = "CSlot";
	static public final String IFRAME_ID = "IFrame";
	static public final String IREFERENCE_ID = "IReference";
	static public final String INUMBER_ID = "INumber";
	static public final String ISTRING_ID = "IString";
	static public final String ISLOT_ID = "ISlot";
	static public final String IVALUES_ID = "IValues";
	static public final String ANNOTATION_ID = "CAnnotation";

	static public final String IDENTIFIER_ATTR = "id";
	static public final String LABEL_ATTR = "label";
	static public final String NUMBER_TYPE_ATTR = "numberType";
	static public final String NUMBER_MIN_ATTR = "min";
	static public final String NUMBER_MAX_ATTR = "max";
	static public final String NUMBER_VALUE_ATTR = "value";
	static public final String STRING_FORMAT_ATTR = "format";
	static public final String STRING_VALIDATOR_CLASS_ATTR = "validatorClass";
	static public final String STRING_VALUE_ATTR = "value";
	static public final String SOURCE_ATTR = "source";
	static public final String CARDINALITY_ATTR = "cardinality";
	static public final String ACTIVATION_ATTR = "activation";
	static public final String EDITABILITY_ATTR = "editability";
	static public final String ANNOTATION_KEY_ATTR = "key";
	static public final String ANNOTATION_VALUE_ATTR =  "value";
}
