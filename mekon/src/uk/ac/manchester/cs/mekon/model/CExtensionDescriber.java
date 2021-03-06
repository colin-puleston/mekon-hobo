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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class CExtensionDescriber {

	private CExtension extension;

	private abstract class LayoutDescriber {

		private ComponentDescriber componentDescriber;
		private StringBuilder builder = new StringBuilder();

		LayoutDescriber(ComponentDescriber componentDescriber) {

			this.componentDescriber = componentDescriber;
		}

		String describe() {

			build(extension);

			return builder.toString();
		}

		void build(CExtension extension) {

			addForLayout(describeHead(extension));
			buildForLayout(extension.getSlotValues());
		}

		void build(CSlotValues slotValues) {

			for (CIdentity slotId : slotValues.getSlotIdentities()) {

				buildForLayout(slotValues, slotId);
			}
		}

		void build(CSlotValues slotValues, CIdentity slotId) {

			addForLayout(describeSlot(slotId));
			buildForLayout(slotValues.getValues(slotId));
		}

		void build(List<CValue<?>> values) {

			boolean first = true;

			for (CValue<?> value : values) {

				buildForLayout(value, first);

				first = false;
			}
		}

		void build(CValue<?> value) {

			if (value instanceof CExtension) {

				buildForLayout((CExtension)value);
			}
			else {

				addForLayout(describeTail(value));
			}
		}

		abstract void buildForLayout(CExtension extension);

		abstract void buildForLayout(CSlotValues slotValues);

		abstract void buildForLayout(CSlotValues slotValues, CIdentity slotId);

		abstract void buildForLayout(List<CValue<?>> values);

		abstract void buildForLayout(CValue<?> value, boolean first);

		abstract void addForLayout(String text);

		void add(String text) {

			builder.append(text);
		}

		private String describeHead(CExtension extension) {

			return componentDescriber.describeHead(extension);
		}

		private String describeTail(CValue<?> value) {

			return componentDescriber.describeTail(value);
		}

		private String describeSlot(CIdentity slotId) {

			return componentDescriber.describeSlot(slotId);
		}
	}

	private class SingleLineDescriber extends LayoutDescriber {

		static private final String AND = " + ";
		static private final String VALUES = " = ";
		static private final String SECTION_START = "(";
		static private final String SECTION_END = ")";

		SingleLineDescriber(ComponentDescriber componentDescriber) {

			super(componentDescriber);
		}

		void buildForLayout(CExtension extension) {

			add(SECTION_START);
			build(extension);
			add(SECTION_END);
		}

		void buildForLayout(CSlotValues slotValues) {

			build(slotValues);
		}

		void buildForLayout(CSlotValues slotValues, CIdentity slotId) {

			add(AND);
			add(SECTION_START);
			build(slotValues, slotId);
			add(SECTION_END);
		}

		void buildForLayout(List<CValue<?>> values) {

			add(VALUES);
			build(values);
		}

		void buildForLayout(CValue<?> value, boolean first) {

			if (!first) {

				add(AND);
			}

			build(value);
		}

		void addForLayout(String text) {

			add(text);
		}
	}

	private class MultiLineDescriber extends LayoutDescriber {

		static private final String TAB = "  ";

		private int currentLevel = 0;

		MultiLineDescriber(ComponentDescriber componentDescriber) {

			super(componentDescriber);
		}

		void buildForLayout(CExtension extension) {

			build(extension);
		}

		void buildForLayout(CSlotValues slotValues) {

			switchLevel(1);
			build(slotValues);
			switchLevel(-1);
		}

		void buildForLayout(CSlotValues slotValues, CIdentity slotId) {

			build(slotValues, slotId);
		}

		void buildForLayout(List<CValue<?>> values) {

			switchLevel(1);
			build(values);
			switchLevel(-1);
		}

		void buildForLayout(CValue<?> value, boolean first) {

			build(value);
		}

		void addForLayout(String text) {

			add("\n");
			addTabs();
			add(text);
		}

		private void switchLevel(int increment) {

			currentLevel += increment;
		}

		private void addTabs() {

			for (int i = 0 ; i < currentLevel ; i++) {

				add(TAB);
			}
		}
	}

	private abstract class ComponentDescriber {

		abstract String describeHead(CExtension extension);

		abstract String describeTail(CValue<?> value);

		abstract String describeSlot(CIdentity slotId);
	}

	private class IdDescriber extends ComponentDescriber {

		String describeHead(CExtension extension) {

			CIdentity id = getExtendedIdentity(extension);

			return FEntityDescriber.entityToString(extension, id);
		}

		String describeTail(CValue<?> value) {

			return value.toString();
		}

		String describeSlot(CIdentity slotId) {

			return slotId.toString();
		}
	}

	private class LabelDescriber extends ComponentDescriber {

		String describeHead(CExtension extension) {

			return getExtendedIdentity(extension).getLabel();
		}

		String describeTail(CValue<?> value) {

			return value.getDisplayLabel();
		}

		String describeSlot(CIdentity slotId) {

			return slotId.getLabel();
		}
	}

	CExtensionDescriber(CExtension extension) {

		this.extension = extension;
	}

	String describeForId() {

		return new SingleLineDescriber(new IdDescriber()).describe();
	}

	String describeForLabel() {

		return new SingleLineDescriber(new LabelDescriber()).describe();
	}

	String describeForToString() {

		return new MultiLineDescriber(new IdDescriber()).describe();
	}

	private CIdentity getExtendedIdentity(CExtension extension) {

		return extension.getAtomicFrame().getIdentity();
	}
}
