/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.user.util.gui.inputter;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
public class IStringInputter extends SimpleTextInputter<IString> {

	static private final long serialVersionUID = -1;

	static private final String DEFAULT_TITLE_FORMAT = "Enter Value: %s";

	static private String createDefaultTitle(CString type) {

		return String.format(DEFAULT_TITLE_FORMAT, type.describeValidityCriteria());
	}

	private CString type;

	public IStringInputter(JComponent parent, CString type, boolean canClear) {

		this(parent, createDefaultTitle(type), type, canClear);
	}

	public IStringInputter(JComponent parent, String title, CString type, boolean canClear) {

		super(parent, title, canClear);

		this.type = type;
	}

	protected IString convertInputValue(String text) {

		return type.validValueText(text) ? type.instantiate(text) : IString.EMPTY_STRING;
	}
}


