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

package uk.ac.manchester.cs.mekon.remote.server;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.remote.util.*;
import uk.ac.manchester.cs.mekon_util.remote.server.*;

/**
 * Wrapper round {@link IInstanceParser} used by the server-side
 * of the MEKON remote access mechanisms.
 *
 * @author Colin Puleston
 */
public class RServerInstanceParser extends RInstanceParser {

	/**
	 * Constructor
	 *
	 * @param model Relevant model
	 */
	public RServerInstanceParser(CModel model) {

		super(model);
	}

	/**
	 * Creates exception of type {@link RServerException}, to be
	 * thrown when parsing problem is detected on server-side.
	 *
	 * @param message Error message
	 * @return Resulting exception
	 */
	protected RServerException createException(String message) {

		return new RServerException(message);
	}
}
