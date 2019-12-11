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

/**
 * @author Colin Puleston
 */
abstract class ActiveTableCell {

	static final Color IDENTITY_COLOUR = Color.GRAY.darker();
	static final Color VALUE_COLOUR = Color.BLUE.darker();
	static final Color NO_VALUE_COLOUR = Color.GRAY;

	static final int DEFAULT_FONT_STYLE = Font.PLAIN;
	static final int IDENTITY_FONT_STYLE = Font.BOLD;
	static final int VALUE_FONT_STYLE = Font.PLAIN;
	static final int NO_VALUE_FONT_STYLE = Font.ITALIC;

	static final Color DEFAULT_BACKGROUND_COLOUR = Color.WHITE;
	static final Color AUTO_EDIT_BACKGROUND_COLOUR = mixAutoEditBackground();

	static private Color mixAutoEditBackground() {

		return mixColours(
				Color.GREEN.darker(),
				Color.LIGHT_GRAY,
				Color.LIGHT_GRAY,
				Color.LIGHT_GRAY,
				Color.LIGHT_GRAY,
				Color.LIGHT_GRAY);
	}

	static private Color mixColours(Color... colours) {

		int r = 0;
		int g = 0;
		int b = 0;

		for (Color c : colours) {

			r += c.getRed() / colours.length;
			g += c.getGreen() / colours.length;
			b += c.getBlue() / colours.length;
		}

		return new Color(r, g, b);
	}

	public String toString() {

		return getLabel();
	}

	abstract String getLabel();

	abstract Color getForeground();

	Color getBackground() {

		return DEFAULT_BACKGROUND_COLOUR;
	}

	int getFontStyle() {

		return DEFAULT_FONT_STYLE;
	}

	boolean userActionable() {

		return false;
	}

	void performCellAction() {
	}
}
