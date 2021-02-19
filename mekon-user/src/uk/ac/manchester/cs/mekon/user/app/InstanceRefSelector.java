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

package uk.ac.manchester.cs.mekon.user.app;

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceRefSelector extends AtomicEntitySelector<IFrame> {

	static private final long serialVersionUID = -1;

	static private final String CREATE_LABEL_FORMAT = "Create %s...";
	static private final String DESCRIBE_LABEL_FORMAT = "Describe %s...";

	private Instantiator instantiator;
	private CFrame type;

	private boolean alternativeEditSelected = false;

	private class AlternativeEditButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			alternativeEditSelected = true;

			dispose();
		}

		AlternativeEditButton(boolean abstractEdit) {

			super(getAltEditLabel(abstractEdit));

			setBackground(getAltEditColour());
		}
	}

	InstanceRefSelector(
		JComponent parent,
		Instantiator instantiator,
		CFrame type,
		boolean canClear,
		boolean abstractEdit) {

		super(parent, getTypeName(type), canClear);

		this.instantiator = instantiator;
		this.type = type;

		addExtraControlButton(new AlternativeEditButton(abstractEdit));
	}

	boolean alternativeEditSelected() {

		return alternativeEditSelected;
	}

	JComponent createOptionsComponent() {

		return createOptions().createOptionsComponent();
	}

	private InstanceRefSelectionOptions createOptions() {

		return new InstanceRefSelectionOptions(this, instantiator, type);
	}

	private String getAltEditLabel(boolean abstractEdit) {

		String format = getAltEditLabelFormat(abstractEdit);

		return String.format(format, getTypeName(type));
	}

	private String getAltEditLabelFormat(boolean abstractEdit) {

		return abstractEdit ? DESCRIBE_LABEL_FORMAT : CREATE_LABEL_FORMAT;
	}

	private Color getAltEditColour() {

		if (instantiator.assertionInstance()) {

			return MekonAppIcons.ASSERT_CLR;
		}

		return summaryType() ? MekonAppIcons.QUERY_SUMMARY_CLR : MekonAppIcons.QUERY_CLR;
	}

	private boolean summaryType() {

		return getSummariser().summaryType(type);
	}

	private InstanceSummariser getSummariser() {

		return instantiator.getCustomiser().getInstanceSummariser();
	}
}
