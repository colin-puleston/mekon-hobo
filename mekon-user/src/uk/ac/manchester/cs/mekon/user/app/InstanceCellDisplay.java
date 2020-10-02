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

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
abstract class InstanceCellDisplay {

	static private final String QUERY_ROOT_SEMANTICS_PREFIX = "FIND ALL instances of";
	static private final String OR_SEMANTICS_PREFIX = "OR";

	static private final String SINGLE_SLOT_SEMANTICS_SUFFIX = "with VALUE >>";
	static private final String MULTI_SLOT_SEMANTICS_SUFFIX = "with ALL VALUES >>";

	static private final int VALUE_FONT_STYLE = Font.BOLD;
	static private final Color SEMANTICS_TEXT_COLOR = Color.GREEN.darker().darker();

	private InstanceNode node;

	InstanceCellDisplay(InstanceNode node) {

		this.node = node;
	}

	GCellDisplay create() {

		GCellDisplay display = createDefault();

		return showQuerySemantics() ? addQuerySemantics(display) : addIcon(display);
	}

	abstract GCellDisplay createDefault();

	abstract Icon getIcon();

	abstract boolean editableSlot();

	boolean editable() {

		return editableSlot() && !viewOnly();
	}

	boolean query() {

		return getInstanceTree().getInstantiator().queryInstance();
	}

	boolean showQuerySemantics() {

		return node.showQuerySemantics();
	}

	GCellDisplay createForValue(String label) {

		GCellDisplay display = new GCellDisplay(label);

		display.setFontStyle(VALUE_FONT_STYLE);

		return display;
	}

	GCellDisplay createForValue(List<String> valueDisjunctLabels) {

		GCellDisplay display = null;

		for (String label : valueDisjunctLabels) {

			if (display == null) {

				display = createForValue(label);
			}
			else {

				display.addModifier(createSemanticsComponent(OR_SEMANTICS_PREFIX));
				display.addModifier(createForValue(label));
			}
		}

		return display;
	}

	private GCellDisplay addIcon(GCellDisplay display) {

		display.setIcon(getIcon());

		return display;
	}

	private GCellDisplay addQuerySemantics(GCellDisplay display) {

		String prefix = getQueryPrefix();
		String suffix = getQuerySuffix();

		if (prefix != null) {

			GCellDisplay prefixed = createSemanticsComponent(prefix);

			prefixed.addModifier(display);
			display = prefixed;
		}

		if (suffix != null) {

			display.addModifier(createSemanticsComponent(suffix));
		}

		return display;
	}

	private GCellDisplay createSemanticsComponent(String label) {

		GCellDisplay display = new GCellDisplay(label);

		display.setTextColour(SEMANTICS_TEXT_COLOR);

		return display;
	}

	private String getQueryPrefix() {

		return node.isRootNode() ? QUERY_ROOT_SEMANTICS_PREFIX : null;
	}

	private String getQuerySuffix() {

		int childCount = node.getChildCount();

		if (childCount == 0) {

			return null;
		}

		if (childCount == 1) {

			return SINGLE_SLOT_SEMANTICS_SUFFIX;
		}

		return MULTI_SLOT_SEMANTICS_SUFFIX;
	}

	private boolean viewOnly() {

		return getInstanceTree().viewOnly();
	}

	private InstanceTree getInstanceTree() {

		return node.getInstanceTree();
	}
}
