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

package uk.ac.manchester.cs.mekon.gui.util;

import java.awt.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
public class GCellDisplay implements Comparable<GCellDisplay> {

	static public final GCellDisplay NO_DISPLAY = new GCellDisplay("");

	private String text;
	private Color textColour;
	private int fontStyleId = Font.PLAIN;
	private Icon icon = null;

	public GCellDisplay(GCellDisplay template) {

		this(template.text);

		textColour = template.textColour;
		fontStyleId = template.fontStyleId;
		icon = template.icon;
	}

	public GCellDisplay(String text) {

		this.text = text;
	}

	public int compareTo(GCellDisplay other) {

		return text.toLowerCase().compareTo(other.text.toLowerCase());
	}

	public void setText(String text) {

		this.text = text;
	}

	public void setTextColour(Color textColour) {

		this.textColour = textColour;
	}

	public void setFontStyleId(int fontStyleId) {

		this.fontStyleId = fontStyleId;
	}

	public void setIcon(Icon icon) {

		this.icon = icon;
	}

	public String getText() {

		return text;
	}

	public Color getTextColour() {

		return textColour;
	}

	public int getFontStyleId() {

		return fontStyleId;
	}

	public Icon getIcon() {

		return icon;
	}

	void configureLabel(JLabel label) {

		label.setText(text);
		label.setFont(configureFont(label.getFont()));
		label.setIcon(icon);
		label.setForeground(textColour);
	}

	private Font configureFont(Font font) {

		return GFonts.toLarge(font).deriveFont(fontStyleId);
	}
}
