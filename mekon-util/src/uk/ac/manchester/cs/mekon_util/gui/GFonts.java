/**
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
package uk.ac.manchester.cs.mekon_util.gui;

import java.awt.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
public class GFonts {

	static private final float SMALL_SIZE = 12;
	static private final float MEDIUM_SIZE = 14;
	static private final float LARGE_SIZE = 16;

	static public void setSmall(JComponent component) {

		set(component, SMALL_SIZE);
	}

	static public void setMedium(JComponent component) {

		set(component, MEDIUM_SIZE);
	}

	static public void setLarge(JComponent component) {

		set(component, LARGE_SIZE);
	}

	static public Font toSmall(Font font) {

		return font.deriveFont(SMALL_SIZE);
	}

	static public Font toMedium(Font font) {

		return font.deriveFont(MEDIUM_SIZE);
	}

	static public Font toLarge(Font font) {

		return font.deriveFont(LARGE_SIZE);
	}

	static private void set(JComponent component, float size) {

		component.setFont(component.getFont().deriveFont(size));
	}
}
