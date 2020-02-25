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

package uk.ac.manchester.cs.mekon.app;

import java.awt.Dimension;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
abstract class EntitySelector<E> extends Selector<E> {

	static private final long serialVersionUID = -1;

	static private final String SINGLE_SELECT_TITLE = "Select option";
	static private final String MULTI_SELECT_TITLE = SINGLE_SELECT_TITLE + "(s)";

	static private final Dimension WINDOW_SIZE = new Dimension(500, 500);

	static private String getTitle(boolean multiSelect) {

		return multiSelect ? MULTI_SELECT_TITLE : SINGLE_SELECT_TITLE;
	}

	EntitySelector(JComponent parent, boolean multiSelect, boolean clearRequired) {

		super(parent, getTitle(multiSelect), multiSelect, clearRequired);
	}

	Dimension getWindowSize() {

		return WINDOW_SIZE;
	}

	abstract JComponent createOptionsComponent();

	abstract void onSelectedOption(E selected);
}
