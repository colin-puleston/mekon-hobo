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

	private Window rootWindow;
	private Instantiator instantiator;

	private DescriptorsList list;

	private class AspectDescriptorEditor extends DescriptorEditor {

		private Descriptor descriptor;

		AspectDescriptorEditor(Descriptor descriptor) {

			super(rootWindow, instantiator, descriptor);

			this.descriptor = descriptor;
		}

		void performEditAction() {

			IFrame aspect = getAspectValue();
			DescriptorsList aspectDescriptors = null;

			if (aspect != null) {

				aspectDescriptors = createAspectDescriptors(aspect);
			}

			if (aspect == null || aspectDescriptors.isEmpty()) {

				super.performEditAction();

				aspect = getAspectValue();

				if (aspect == null) {

					return;
				}

				aspectDescriptors = createAspectDescriptors(aspect);
			}

			if (!aspectDescriptors.isEmpty()) {

				createEditManager(aspect, aspectDescriptors).invokeEdit();
			}
		}

		private IFrame getAspectValue() {

			return (IFrame)descriptor.getValue();
		}

		private DescriptorsList createAspectDescriptors(IFrame aspect) {

			return new DescriptorsList(instantiator, aspect, false);
		}

		private AspectEditManager createEditManager(
									IFrame aspect,
									DescriptorsList aspectDescriptors) {

			ISlot slot = descriptor.getSlot();

			return new AspectEditManager(aspectWindow, slot, aspect, aspectDescriptors);
		}
	}

	private class IdentityCell extends ActiveTableCell {

		private Descriptor descriptor;

		IdentityCell(Descriptor descriptor) {

			this.descriptor = descriptor;
		}

		String getLabel() {

			return descriptor.getIdentityLabel();
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

		ValueCell(Descriptor descriptor) {

			this.descriptor = descriptor;
		}

		String getLabel() {

			return descriptor.getValueLabel();
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

			return descriptor.active();
		}

		void performCellAction() {

			aspectWindow.dispose();
			createEditor().performEditAction();
			aspectWindow.displayCopy();
		}

		private DescriptorEditor createEditor() {

			return descriptor.aspectType()
					? new AspectDescriptorEditor(descriptor)
					: new DescriptorEditor(rootWindow, instantiator, descriptor);
		}
	}

	DescriptorsTable(AspectWindow aspectWindow, DescriptorsList list) {

		super(TITLES);

		this.aspectWindow = aspectWindow;
		this.list = list;

		rootWindow = aspectWindow.getRootWindow();
		instantiator = aspectWindow.getInstantiator();

		addRows();
		setPreferredScrollableViewportSize(getPreferredSize());
	}

	DescriptorsTable(AspectWindow aspectWindow, Instantiator instantiator, IFrame aspect) {

		this(aspectWindow, new DescriptorsList(instantiator, aspect, false));
	}

	private void addRows() {

		for (Descriptor descriptor : list.getList()) {

			addRow(toRow(descriptor));
		}
	}

	private Object[] toRow(Descriptor descriptor) {

		return new ActiveTableCell[]{
					new IdentityCell(descriptor),
					new ValueCell(descriptor)};
	}
}
