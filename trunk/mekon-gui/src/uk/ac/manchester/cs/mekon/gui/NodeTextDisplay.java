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

package uk.ac.manchester.cs.mekon.gui;

import java.awt.*;

/**
 * @author Colin Puleston
 */
enum NodeTextDisplay {

	VALUE(Color.darkGray.darker(), Font.BOLD),
	SLOT(Color.darkGray.darker(), Font.PLAIN),
	SLOT_VALUE_TYPE_MODIFIER(Color.orange.darker(), Font.PLAIN),
	SLOT_CARDINALITY_MODIFIER(Color.orange.darker().darker(), Font.PLAIN),
	SLOT_VALUES(Color.darkGray.darker(), Font.ITALIC),
	INFO(Color.darkGray.darker(), Font.ITALIC);

	private Color colour;
	private int style;

	Color getColour() {

		return colour;
	}

	int getStyle() {

		return style;
	}

	private NodeTextDisplay(Color colour, int style) {

		this.colour = colour;
		this.style = style;
	}
}
