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

package uk.ac.manchester.cs.hobo.model;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Represents the identity for a frames-based entity derived
 * from a particular class or field in the Object Model (OM).
 *
 * @author Colin Puleston
 */
public class DIdentity extends CIdentity {

	static String createLabel(Class<?> dClass) {

		return KLabel.create(dClass);
	}

	static String createLabel(String fieldName) {

		return KLabel.create(fieldName);
	}

	/**
	 * Constructs identity for the specified OM class, with the
	 * fully-qualified class-name as the identifier and the label
	 * being heuristically-generated from the simple class-name
	 * (via the {@link KLabel}-mechanism).
	 *
	 * @param dClass OM class for which identity is required
	 */
	public DIdentity(Class<?> dClass) {

		super(dClass.getName(), createLabel(dClass));
	}

	/**
	 * Constructs identity for the specified OM field, with the
	 * field-name itself as the identifier and the label being
	 * heuristically-generated from the field-name (via the
	 * {@link KLabel}-mechanism).
	 *
	 * @param fieldName OM field for which identity is required
	 */
	public DIdentity(String fieldName) {

		super(fieldName, createLabel(fieldName));
	}
}
