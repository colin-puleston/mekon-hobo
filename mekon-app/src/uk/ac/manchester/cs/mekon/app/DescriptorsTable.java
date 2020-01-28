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

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class DescriptorsTable extends ActiveTable {

	static private final long serialVersionUID = -1;

	static private final String[] TITLES = new String[]{"Attribute", "Value"};

	static final Color IDENTITY_COLOUR = Color.GRAY.darker();
	static final Color VALUE_COLOUR = Color.BLUE.darker();
	static final Color NO_VALUE_COLOUR = Color.GRAY;

	static final int DEFAULT_FONT_STYLE = Font.PLAIN;
	static final int IDENTITY_FONT_STYLE = Font.BOLD;
	static final int VALUE_FONT_STYLE = Font.PLAIN;
	static final int NO_VALUE_FONT_STYLE = Font.ITALIC;

	static final Color DEFAULT_BACKGROUND_COLOUR = Color.WHITE;
	static final Color AUTO_EDIT_BACKGROUND_COLOUR = mixAutoEditBackground();

	static private DisplayColours colours = new DisplayColours();
	static private DisplayFontStyles fontStyles = new DisplayFontStyles();

	static private abstract class DisplayOptions<O> {

		O getOption(Descriptor descriptor) {

			return descriptor.anyEffectiveValues()
					? getValuesOption()
					: getNoValuesOption();
		}

		abstract O getValuesOption();

		abstract O getNoValuesOption();
	}

	static private class DisplayColours extends DisplayOptions<Color> {

		Color getValuesOption() {

			return VALUE_COLOUR;
		}

		Color getNoValuesOption() {

			return NO_VALUE_COLOUR;
		}
	}

	static private class DisplayFontStyles extends DisplayOptions<Integer> {

		Integer getValuesOption() {

			return VALUE_FONT_STYLE;
		}

		Integer getNoValuesOption() {

			return NO_VALUE_FONT_STYLE;
		}
	}

	static private Color mixAutoEditBackground() {

		return ColourMixer.mix(Color.LIGHT_GRAY, Color.WHITE, Color.WHITE);
	}

	private AspectWindow aspectWindow;
	private DescriptorsList list;

	private class IdentityCell extends ActiveTableCell {

		private Descriptor descriptor;
		private DescriptorDisplay display;

		IdentityCell(Descriptor descriptor, DescriptorDisplay display) {

			this.descriptor = descriptor;
			this.display = display;
		}

		String getLabel() {

			return display.getIdentityLabel();
		}

		Color getForeground() {

			return IDENTITY_COLOUR;
		}

		Color getBackground() {

			return DEFAULT_BACKGROUND_COLOUR;
		}

		int getFontStyle() {

			return IDENTITY_FONT_STYLE;
		}
	}

	private class ValueCell extends ActiveTableCell {

		private Descriptor descriptor;
		private DescriptorDisplay display;

		ValueCell(Descriptor descriptor, DescriptorDisplay display) {

			this.descriptor = descriptor;
			this.display = display;
		}

		String getLabel() {

			return display.getValueLabel();
		}

		Color getForeground() {

			return colours.getOption(descriptor);
		}

		Color getBackground() {

			return descriptor.anyUserEditability()
					? DEFAULT_BACKGROUND_COLOUR
					: AUTO_EDIT_BACKGROUND_COLOUR;
		}

		int getFontStyle() {

			return fontStyles.getOption(descriptor);
		}

		boolean userActionable() {

			return display.active();
		}

		void performCellAction() {

			aspectWindow.dispose();
			display.performAction();
			aspectWindow.displayCopy();
		}
	}

	private class DescriptorCellDisplay extends DescriptorDisplay {

		DescriptorCellDisplay(Descriptor descriptor) {

			super(aspectWindow, descriptor);
		}

		void onAspectActionPerformed(IFrame aspect, DescriptorsList descriptors) {

			createEditManager(aspect, descriptors).invokeEdit();
		}

		private AspectEditManager createEditManager(IFrame aspect, DescriptorsList descriptors) {

			return new AspectEditManager(aspectWindow, getSlot(), aspect, descriptors);
		}
	}

	DescriptorsTable(AspectWindow aspectWindow, DescriptorsList list) {

		super(TITLES);

		this.aspectWindow = aspectWindow;
		this.list = list;

		addRows();
		setPreferredScrollableViewportSize(getPreferredSize());
	}

	private void addRows() {

		for (Descriptor descriptor : list.getList()) {

			addRow(toRow(descriptor));
		}
	}

	private Object[] toRow(Descriptor descriptor) {

		DescriptorDisplay display = new DescriptorCellDisplay(descriptor);

		return new ActiveTableCell[] {
					new IdentityCell(descriptor, display),
					new ValueCell(descriptor, display)};
	}
}
