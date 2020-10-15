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
class MekonAppIcons {

	static private final Color ASSERT_CLR = new Color(49,130,189);
	static private final Color QUERY_CLR = new Color(49,163,84);
	static private final Color ASSERT_SUMMARY_CLR = new Color(158,202,225);
	static private final Color QUERY_SUMMARY_CLR = new Color(161,217,155);
	static private final Color VALUE_ENTRY_CLR = Color.WHITE;

	static private final int DIMENSION = 12;

	static final TreeIcons VALUE_ICONS = new ValueIcons();
	static final TreeIcons ARRAY_ICONS = new ArrayIcons();
	static final TreeIcons REF_ICONS = new RefIcons();

	static final Icon VALUE_ENTRY = createValueEntryIcon();

	static private final Icon ASSERT_LIST_ICON = createListIcon(ASSERT_CLR);
	static private final Icon QUERY_LIST_ICON = createListIcon(QUERY_CLR);

	static abstract class TreeIcons {

		private FunctionIcons editIcons = new FunctionIcons(true);
		private FunctionIcons noEditIcons = new FunctionIcons(false);

		private class FunctionIcons {

			private Icon assertIcon;
			private Icon queryIcon;

			private Icon assertSummaryIcon;
			private Icon querySummaryIcon;

			FunctionIcons(boolean edit) {

				assertIcon = createIcon(edit, ASSERT_CLR);
				queryIcon = createIcon(edit, QUERY_CLR);

				assertSummaryIcon = createIcon(edit, ASSERT_SUMMARY_CLR);
				querySummaryIcon = createIcon(edit, QUERY_SUMMARY_CLR);
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

		abstract GIconRenderer createValueRenderer(Color clr);

		private Icon get(boolean query, boolean summary, boolean edit) {

			return (edit ? editIcons : noEditIcons).get(query, summary);
		}

		private Icon createIcon(boolean edit, Color clr) {

			return edit ? createEditIcon(clr) : createNoEditIcon(clr);
		}

		private GIcon createEditIcon(Color clr) {

			GIconRenderer valueRenderer = createValueRenderer(clr);

			valueRenderer.setXOffset(DIMENSION);

			return new GIcon(createValueEntryRenderer(), valueRenderer);
		}

		private GIcon createNoEditIcon(Color clr) {

			return new GIcon(createValueRenderer(clr));
		}
	}

	static private class ValueIcons extends TreeIcons {

		GIconRenderer createValueRenderer(Color clr) {

			return new GDiamondRenderer(clr, DIMENSION);
		}
	}

	static private class ArrayIcons extends TreeIcons {

		GIconRenderer createValueRenderer(Color clr) {

			return new GDiamondRenderer(clr, DIMENSION);
		}
	}

	static private class RefIcons extends TreeIcons {

		GIconRenderer createValueRenderer(Color clr) {

			return createRightTriangleRenderer(clr);
		}
	}

	static Icon getListIcon(boolean query) {

		return query ? QUERY_LIST_ICON : ASSERT_LIST_ICON;
	}

	static private GIcon createListIcon(Color clr) {

		return new GIcon(new GOvalRenderer(clr, DIMENSION));
	}

	static private GIcon createValueEntryIcon() {

		return new GIcon(createValueEntryRenderer());
	}

	static private GIconRenderer createValueEntryRenderer() {

		return createRightTriangleRenderer(VALUE_ENTRY_CLR);
	}

	static private GIconRenderer createRightTriangleRenderer(Color clr) {

		return new GTriangleRenderer(
						GTriangleRenderer.Type.RIGHTWARD,
						clr,
						DIMENSION,
						DIMENSION);
	}
}
