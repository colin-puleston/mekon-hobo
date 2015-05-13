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

	static private final int SEPARATOR_WIDTH = 5;

	private String text;
	private Color textColour = null;
	private int fontStyle = Font.PLAIN;
	private Icon icon = null;

	private GCellDisplay modifier = null;

	public GCellDisplay(GCellDisplay template) {

		this(template.text);

		textColour = template.textColour;
		fontStyle = template.fontStyle;
		icon = template.icon;
	}

	public GCellDisplay(String text) {

		this.text = text;
	}

	public int compareTo(GCellDisplay other) {

		return text.toLowerCase().compareTo(other.text.toLowerCase());
	}

	public void setModifier(GCellDisplay modifier) {

		this.modifier = modifier;
	}

	public void setText(String text) {

		this.text = text;
	}

	public void setTextColour(Color textColour) {

		this.textColour = textColour;
	}

	public void setFontStyle(int fontStyle) {

		this.fontStyle = fontStyle;
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

	public int getFontStyle() {

		return fontStyle;
	}

	public Icon getIcon() {

		return icon;
	}

	JComponent createComponent(boolean selected) {

		return modifier == null
				? createLabel(selected)
				: createCompoundComponent(selected);
	}

	private JComponent createCompoundComponent(boolean selected) {

		Box comp = Box.createHorizontalBox();

		comp.add(createLabel(selected));
		modifier.addModifierLabels(comp, selected);

		return comp;
	}

	private JLabel createLabel(boolean selected) {

		JLabel label = new JLabel();

		label.setText(text);
		label.setFont(deriveFont(label.getFont()));
		label.setIcon(icon);
		label.setForeground(textColour);

		if (checkSetBackground(label, selected)) {

			label.setOpaque(true);
		}

		return label;
	}

	private void addModifierLabels(Box comp, boolean selected) {

		comp.add(createSeparator(selected));
		comp.add(createLabel(selected));

		if (modifier != null) {

			modifier.addModifierLabels(comp, selected);
		}
	}

	private Component createSeparator(boolean selected) {

		Component sep = Box.createHorizontalStrut(SEPARATOR_WIDTH);

		checkSetBackground(sep, selected);

		return sep;
	}

	private boolean checkSetBackground(Component comp, boolean selected) {

		if (selected) {

			comp.setBackground(getSelectionBackground());

			return true;
		}

		return false;
	}

	private Font deriveFont(Font font) {

		return GFonts.toLarge(font).deriveFont(fontStyle);
	}

	private Color getSelectionBackground() {

		return UIManager.getColor("Tree.selectionBackground");
	}
}
