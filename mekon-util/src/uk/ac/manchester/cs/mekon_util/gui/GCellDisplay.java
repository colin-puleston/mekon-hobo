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

package uk.ac.manchester.cs.mekon_util.gui;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.util.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
public class GCellDisplay implements Comparable<GCellDisplay> {

	static public final GCellDisplay NO_DISPLAY = new GCellDisplay("");

	static private final int SEPARATOR_WIDTH = 5;

	private String text;
	private String filterText;
	private Color textColour = null;
	private Color backgroundColour = null;
	private int fontStyle = Font.PLAIN;
	private Icon icon = null;

	private List<GCellDisplay> modifiers = new ArrayList<GCellDisplay>();

	public GCellDisplay(GCellDisplay template) {

		text = template.text;
		filterText = template.filterText;
		textColour = template.textColour;
		backgroundColour = template.backgroundColour;
		fontStyle = template.fontStyle;
		icon = template.icon;
	}

	public GCellDisplay(String text) {

		this.text = text;

		filterText = text;
	}

	public GCellDisplay(String text, Icon icon) {

		this(text);

		this.icon = icon;
	}

	public int compareTo(GCellDisplay other) {

		int c = text.toLowerCase().compareTo(other.text.toLowerCase());

		return c != 0 ? c : text.compareTo(other.text);
	}

	public void addModifier(GCellDisplay modifier) {

		modifiers.add(modifier);
	}

	public void setText(String text) {

		this.text = text;
	}

	public void setFilterText(String filterText) {

		this.filterText = filterText;
	}

	public void setTextColour(Color textColour) {

		this.textColour = textColour;
	}

	public void setBackgroundColour(Color backgroundColour) {

		this.backgroundColour = backgroundColour;
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

	public String getFilterText() {

		return filterText;
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

	protected void onLabelAdded(JLabel label) {
	}

	JComponent createComponent(boolean selected) {

		List<Component> components = new ArrayList<Component>();

		collectComponents(components, selected);

		if (components.size() == 1) {

			return (JLabel)components.get(0);
		}

		return createCompoundComponent(components);
	}

	private JComponent createCompoundComponent(List<Component> components) {

		Box compound = Box.createHorizontalBox();

		for (Component component : components) {

			compound.add(component);
		}

		return compound;
	}

	private void collectComponents(List<Component> components, boolean selected) {

		components.add(createLabel(selected));

		for (GCellDisplay modifier : modifiers) {

			components.add(createSeparator(selected));
			modifier.collectComponents(components, selected);
		}
	}

	private JLabel createLabel(boolean selected) {

		JLabel label = new JLabel();

		label.setIcon(icon);
		label.setText(text);
		label.setFont(deriveFont(label.getFont()));
		label.setForeground(textColour);

		Color background = getLabelBackground(selected);

		if (background != null) {

			label.setOpaque(true);
			label.setBackground(background);
		}

		onLabelAdded(label);

		return label;
	}

	private Color getLabelBackground(boolean selected) {

		return selected ? getSelectionBackground() : backgroundColour;
	}

	private Component createSeparator(boolean selected) {

		Component sep = Box.createHorizontalStrut(SEPARATOR_WIDTH);

		if (selected) {

			sep.setBackground(getSelectionBackground());
		}

		return sep;
	}

	private Font deriveFont(Font font) {

		return GFonts.toLarge(font).deriveFont(fontStyle);
	}

	private Color getSelectionBackground() {

		return UIManager.getColor("Tree.selectionBackground");
	}
}
