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

import uk.ac.manchester.cs.mekon.*;

/**
 * @author Colin Puleston
 */
abstract class CExpression extends CFrame {

	static private final String ID_FORMAT = "@%s(%s)";

	private String label;

	public CIdentity getIdentity() {

		return new CIdentity(getIdentifier(), getDisplayLabel());
	}

	public String getDisplayLabel() {

		return label != null ? label : getDefaultDisplayLabel();
	}

	public CSource getSource() {

		return CSource.INDIRECT;
	}

	public boolean hidden() {

		return false;
	}

	public CSlots getSlots() {

		return CSlots.INERT_INSTANCE;
	}

	CExpression(String label) {

		this.label = label;
	}

	CExtension createExtension(String label, CSlotValues slotValues) {

		throw new KAccessException(
					"Cannot create extension-frame "
					+ "for expression-frame: "
					+ this);
	}

	CModelFrame asModelFrame() {

		throw new KAccessException(
					"Required a model-frame: "
					+ "Found expression-frame: "
					+ this);
	}

	abstract String getExpressionTypeName();

	abstract String getExpressionDescriptionForId();

	abstract String getDefaultDisplayLabel();

	private String getIdentifier() {

		String typeName = getExpressionTypeName().toUpperCase();
		String description = getExpressionDescriptionForId();

		return String.format(ID_FORMAT, typeName, description);
	}
}
