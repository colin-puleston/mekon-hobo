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
 * Vocabulary for the standard XML serialisation of MEKON
 * instances. Extends {@link FSerialiserVocab} in order to
 * incorporate vocabulary for the standard XML serialisation
 * of frames model entities.
 *
 * @author Colin Puleston
 */
public interface ISerialiserVocab extends FSerialiserVocab {

	static public final String ITREE_ID = "ITree";
	static public final String IGRAPH_ID = "IGraph";

	static public final String IVALUES_UPDATE_ID = "IValuesUpdate";

	static public final String INSTANCE_FUNCTION_ATTR = "function";
	static public final String IFRAME_XDOC_ID_ATTR = "xid";
	static public final String IFRAME_XDOC_ID_REF_ATTR = "xidRef";
	static public final String FIXED_VALUE_STATUS_ATTR = "fixedValue";

	static public final String ADDED_VALUE_INDEX_ATTR = "addedValueIndex";
}
