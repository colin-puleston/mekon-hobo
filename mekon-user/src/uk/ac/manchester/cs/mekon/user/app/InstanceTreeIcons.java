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

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.icon.*;

/**
 * @author Colin Puleston
 */
class InstanceTreeIcons {

	static private final int VALUE_DIMENSION = 12;
	static private final int VALUE_ENTRY_DIMENSION = 12;
	static private final int ARRAY_REORDER_DIMENSION = 12;

	static final IconSet VALUES = new ValueIcons();
	static final IconSet REFS = new RefIcons();

	static final Icon VALUE_ENTRY = createValueEntryIcon();
	static final Icon REORDERABLE_ARRAY = createReorderableArrayIcon();

	static abstract class IconSet {

		private FunctionIcons editIcons = new FunctionIcons(true);
		private FunctionIcons noEditIcons = new FunctionIcons(false);

		private class FunctionIcons {

			private Icon assertIcon;
			private Icon queryIcon;

			private Icon assertSummaryIcon;
			private Icon querySummaryIcon;

			FunctionIcons(boolean edit) {

				assertIcon = createIcon(edit, ValueColours.ASSERT);
				queryIcon = createIcon(edit, ValueColours.QUERY);

				assertSummaryIcon = createIcon(edit, ValueColours.ASSERT_SUMMARY);
				querySummaryIcon = createIcon(edit, ValueColours.QUERY_SUMMARY);
			}

			Icon get(boolean query, boolean summary) {

				if (summary) {

					return query ? querySummaryIcon : assertSummaryIcon;
				}

				return query ? queryIcon : assertIcon;
			}

			Icon get(InstanceNode node) {

				if (node.summaryInstance()) {

					return node.queryInstance() ? querySummaryIcon : assertSummaryIcon;
				}

				return node.queryInstance() ? queryIcon : assertIcon;
			}
		}

		Icon forTree(InstanceNode node, boolean edit) {

			return get(node.queryInstance(), node.summaryInstance(), edit);
		}

		Icon forSelector(boolean query) {

			return get(query, false, false);
		}

		Icon get(boolean query, boolean summary, boolean edit) {

			return (edit ? editIcons : noEditIcons).get(query, summary);
		}

		abstract GIconRenderer createValueRenderer(Color clr);

		private Icon createIcon(boolean edit, Color clr) {

			return edit ? createEditIcon(clr) : createNoEditIcon(clr);
		}

		private GIcon createEditIcon(Color clr) {

			GIconRenderer valueRenderer = createValueRenderer(clr);

			valueRenderer.setXOffset(VALUE_DIMENSION);

			return new GIcon(createValueEntryRenderer(), valueRenderer);
		}

		private GIcon createNoEditIcon(Color clr) {

			return new GIcon(createValueRenderer(clr));
		}
	}

	static private class ValueIcons extends IconSet {

		GIconRenderer createValueRenderer(Color clr) {

			return createDirectValueRenderer(clr);
		}
	}

	static private class RefIcons extends IconSet {

		GIconRenderer createValueRenderer(Color clr) {

			return createRightTriangleRenderer(clr, VALUE_DIMENSION);
		}
	}

	static private GIcon createValueEntryIcon() {

		return new GIcon(createValueEntryRenderer());
	}

	static private GIcon createReorderableArrayIcon() {

		GIconRenderer valueRenderer = createReorderableArrayValueRenderer();
		GIconRenderer reorderRenderer = createArrayReorderRenderer();

		reorderRenderer.setXOffset(VALUE_DIMENSION);

		return new GIcon(valueRenderer, reorderRenderer);
	}

	static private GIconRenderer createValueEntryRenderer() {

		return createRightTriangleRenderer(ValueColours.VALUE_ENTRY, VALUE_ENTRY_DIMENSION);
	}

	static private GIconRenderer createReorderableArrayValueRenderer() {

		return createDirectValueRenderer(ValueColours.ASSERT);
	}

	static private GIconRenderer createArrayReorderRenderer() {

		return new GDiamondRenderer(ValueColours.ARRAY_REORDER, ARRAY_REORDER_DIMENSION);
	}

	static private GIconRenderer createDirectValueRenderer(Color clr) {

		return new GDiamondRenderer(clr, VALUE_DIMENSION);
	}

	static private GIconRenderer createRightTriangleRenderer(Color clr, int dim) {

		return new GTriangleRenderer(GTriangleRenderer.Type.RIGHTWARD, clr, dim, dim);
	}
}
