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

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceRefSelector extends AtomicEntitySelector<IFrame> {

	static private final long serialVersionUID = -1;

	static private final String CREATE_LABEL_FORMAT = "Create %s";
	static private final String DESCRIBE_LABEL_FORMAT = "Describe %s";

	private InstanceRefSelectionOptions selectionOptions;

	private boolean alternativeEditSelected = false;

	private class AlternativeEditSelectButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			alternativeEditSelected = true;

			dispose();
		}

		AlternativeEditSelectButton(CFrame type, boolean abstractEdit) {

			super(getAlternativeEditSelectLabel(type, abstractEdit));
		}
	}

	InstanceRefSelector(
		JComponent parent,
		Instantiator instantiator,
		CFrame type,
		boolean canClear,
		boolean abstractEdit) {

		super(parent, getTypeName(type), canClear);

		selectionOptions = new InstanceRefSelectionOptions(this, instantiator, type);

		addExtraControlButton(new AlternativeEditSelectButton(type, abstractEdit));
	}

	boolean alternativeEditSelected() {

		return alternativeEditSelected;
	}

	JComponent createOptionsComponent() {

		return selectionOptions.createOptionsComponent();
	}

	private String getAlternativeEditSelectLabel(CFrame type, boolean abstractEdit) {

		String format = getAlternativeEditSelectLabelFormat(abstractEdit);

		return String.format(format, getTypeName(type));
	}

	private String getAlternativeEditSelectLabelFormat(boolean abstractEdit) {

		return abstractEdit ? DESCRIBE_LABEL_FORMAT : CREATE_LABEL_FORMAT;
	}
}
