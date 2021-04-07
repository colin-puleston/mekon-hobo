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

	static private final String SEMANTICS_START_PREFIX = "FIND ALL INSTANCES OF";
	static private final String SEMANTICS_AND_START_PREFIX = "WITH";
	static private final String SEMANTICS_AND_CONTINUE_PREFIX = " AND";
	static private final String SEMANTICS_OR_PREFIX = "OR";
	static private final String SEMANTICS_MODIFIERS_INTRO_SUFFIX = ">>";

	static private final int VALUE_FONT_STYLE = Font.BOLD;
	static private final Color SEMANTICS_TEXT_COLOR = Color.GREEN.darker().darker();

	private InstanceNode node;

	InstanceCellDisplay(InstanceNode node) {

		this.node = node;
	}

	GCellDisplay create() {

		GCellDisplay display = createDefault();

		return node.showQuerySemantics() ? addQuerySemantics(display) : addIcon(display);
	}

	abstract GCellDisplay createDefault();

	abstract Icon getIcon();

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

				display.addModifier(createSemanticsComponent(SEMANTICS_OR_PREFIX));
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

		String prefix = getQuerySemanticsLinePrefixOrNull();
		String suffix = getQuerySemanticsLineSuffixOrNull();

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

	private String getQuerySemanticsLinePrefixOrNull() {

		if (node.isRootNode()) {

			if (instanceSubSection()) {

				return null;
			}

			return SEMANTICS_START_PREFIX;
		}

		if (node.indexInSiblings() == 0) {

			return SEMANTICS_AND_START_PREFIX;
		}

		return SEMANTICS_AND_CONTINUE_PREFIX;
	}

	private String getQuerySemanticsLineSuffixOrNull() {

		return node.getChildCount() != 0 ? SEMANTICS_MODIFIERS_INTRO_SUFFIX : null;
	}

	private boolean instanceSubSection() {

		return node.getInstanceTree().instanceSubSection();
	}
}
