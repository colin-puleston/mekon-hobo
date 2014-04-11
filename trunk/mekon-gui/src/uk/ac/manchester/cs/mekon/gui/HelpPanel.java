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

package uk.ac.manchester.cs.mekon.gui;

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class HelpPanel extends JTabbedPane {

	static final long serialVersionUID = -1;

	static private final float CATEGORY_TITLE_FONT_SIZE = 16;
	static private final float SECTION_TITLE_FONT_SIZE = 14;

	static private final EntityLevel DEFAULT_LEVEL = EntityLevel.CONCEPT;
	static private final CSource DEFAULT_SOURCE = CSource.UNSPECIFIED;
	static private final String NOT_APPLICABLE_STRING = "N/A";
	static private final String LEFT_CLICK_STRING = "LEFT-CLICK";
	static private final String RIGHT_CLICK_STRING = "RIGHT-CLICK";

	static final Icon mValueShape = getValueShape(EntityLevel.META);
	static final Icon cValueShape = getValueShape(EntityLevel.CONCEPT);
	static final Icon iValueShape = getValueShape(EntityLevel.INSTANCE);
	static final Icon hiddenMFrameShape = getHiddenFrameShape(EntityLevel.META);
	static final Icon hiddenCFrameShape = getHiddenFrameShape(EntityLevel.CONCEPT);
	static final Icon defaultSlotShape = getDefaultSlotShape();
	static final Icon inactiveSlotShape = getInactiveSlotShape();
	static final Icon editBlockedSlotShape = getEditBlockedSlotShape();

	static final Icon directColour = getColour(CSource.DIRECT);
	static final Icon indirectColour = getColour(CSource.INDIRECT);
	static final Icon dualColour = getColour(CSource.DUAL);
	static final Icon numberColour = getNumberColour();

	static private Icon getValueShape(EntityLevel level) {

		return getIcons().exposedFrames.get(DEFAULT_SOURCE, level);
	}

	static private Icon getHiddenFrameShape(EntityLevel level) {

		return getIcons().hiddenFrames.get(DEFAULT_SOURCE, level);
	}

	static private Icon getDefaultSlotShape() {

		return getIcons().defaultSlots.get(DEFAULT_SOURCE);
	}

	static private Icon getInactiveSlotShape() {

		return getIcons().inactiveSlots.get(DEFAULT_SOURCE);
	}

	static private Icon getEditBlockedSlotShape() {

		return getIcons().editBlockedSlots.get(DEFAULT_SOURCE);
	}

	static private Icon getColour(CSource source) {

		return getIcons().exposedFrames.get(source, DEFAULT_LEVEL);
	}

	static private Icon getNumberColour() {

		return getIcons().numbers.get(EntityLevel.CONCEPT);
	}

	static private EntityIcons getIcons() {

		return EntityIcons.get();
	}

	private class CategoryPanel extends JTabbedPane {

		static final long serialVersionUID = -1;

		class SectionPanel extends GTable {

			static final long serialVersionUID = -1;

			SectionPanel(String title) {

				addSection(title, this);
			}
		}

		CategoryPanel(String title) {

			super(JTabbedPane.BOTTOM);

			setFont(getFont().deriveFont(SECTION_TITLE_FONT_SIZE));

			addCategory(title, this);
		}

		private void addSection(String title, JComponent section) {

			addTab(title, createSectionBox(section));
		}

		private JComponent createSectionBox(JComponent section) {

			JPanel box = new JPanel(new GridBagLayout());

			box.add(section);

			return box;
		}
	}

	private class EntitiesPanel extends CategoryPanel {

		static final long serialVersionUID = -1;

		private class ShapesPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ShapesPanel() {

				super("Shapes");

				addColumns("Shape", "Represents", "Entity Types");

				addRow(
					cValueShape,
					"Concept-level entity",
					"CFrame, CNumber, CProperty");
				addRow(
					iValueShape,
					"Instance-level entity",
					"IFrame, INumber");
				addRow(
					defaultSlotShape,
					"Slot (concept-level or instance-level)",
					"CSlot, ISlot");
			}
		}

		private class ColoursPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ColoursPanel() {

				super("Colours");

				addColumns("Colour", "Represents", "Source", "Entity Types");

				addRow(
					directColour,
					"Frame / Slot",
					"Object Model",
					"MFrame, CFrame, CSlot, IFrame, ISlot");
				addRow(
					indirectColour,
					"Frame / Slot",
					"Ontology",
					"MFrame, CFrame, CSlot, IFrame, ISlot");
				addRow(
					dualColour,
					"Frame / Slot",
					"Object Model + Ontology",
					"MFrame, CFrame, CSlot, IFrame, ISlot");
				addRow(
					numberColour,
					"Number",
					"Context Dependent",
					"CNumber, INumber");
			}
		}

		private class ModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ModifiersPanel() {

				super("Modifiers");

				addColumns("Modifier", "Represents", "Entity Types");

				addRow(
					hiddenMFrameShape,
					"Frame is hidden",
					"MFrame");
				addRow(
					hiddenCFrameShape,
					"Frame is hidden",
					"CFrame");
				addRow(
					inactiveSlotShape,
					"Slot is inactive",
					"CSlot");
				addRow(
					editBlockedSlotShape,
					"Slot-values are automatically derived",
					"CSlot");
				addRow(
					editBlockedSlotShape,
					"Slot is non-editable "
					+ "(i.e. automatically-derived-values slot on concrete instance)",
					"ISlot");
			}
		}

		EntitiesPanel() {

			super("Model Entities");

			new ShapesPanel();
			new ColoursPanel();
			new ModifiersPanel();
		}
	}

	private class ModelTreesPanel extends CategoryPanel {

		static final long serialVersionUID = -1;

		private class TreeSemanticsPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			private boolean conceptTreeOnly;

			TreeSemanticsPanel(String laterality, boolean conceptTreeOnly) {

				super("Tree Semantics: " + laterality + "-Hand Panel");

				this.conceptTreeOnly = conceptTreeOnly;

				addColumns("Parent", "Child", "Semantics", "Entity Types");

				checkAddRow(
					cValueShape,
					defaultSlotShape,
					"frame-has-slot",
					"CFrame ==> CSlot",
					false);
				checkAddRow(
					defaultSlotShape,
					mValueShape,
					"slot-has-value-type",
					"CSlot ==> MFrame",
					false);
				checkAddRow(
					defaultSlotShape,
					cValueShape,
					"slot-has-value-type",
					"CSlot ==> CFrame / CNumber",
					false);
				checkAddRow(
					mValueShape,
					mValueShape,
					"frame-has-sub-frame",
					"MFrame ==> MFrame",
					false);
				checkAddRow(
					cValueShape,
					cValueShape,
					"frame-has-sub-frame ",
					"CFrame ==> CFrame",
					true);
			}

			private void checkAddRow(
							Icon parent,
							Icon child,
							String semantics,
							String entities,
							boolean conceptTreeTree) {

				if (conceptTreeTree || !conceptTreeOnly) {

					addRow(parent, child, semantics, entities);
				}
			}
		}

		ModelTreesPanel() {

			super("Model Trees");

			new TreeSemanticsPanel("Left", true);
			new TreeSemanticsPanel("Right", false);
		}
	}

	private class InstantiationTreesPanel extends CategoryPanel {

		static final long serialVersionUID = -1;

		private class TreeSemanticsPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			TreeSemanticsPanel() {

				super("Tree Semantics");

				addColumns("Parent", "Child", "Grandchild", "Semantics", "Entity Types");

				addRow(
					iValueShape,
					defaultSlotShape,
					NOT_APPLICABLE_STRING,
					"frame-has-slot",
					"IFrame ==> ISlot");
				addRow(
					defaultSlotShape,
					mValueShape,
					NOT_APPLICABLE_STRING,
					"slot-has-value-type",
					"ISlot ==> MFrame");
				addRow(
					defaultSlotShape,
					cValueShape,
					NOT_APPLICABLE_STRING,
					"slot-has-value-type",
					"ISlot ==> CFrame");
				addRow(
					defaultSlotShape,
					mValueShape,
					cValueShape,
					"slot-with-value-type-has-value",
					"ISlot ==> MFrame ==> CFrame");
				addRow(
					defaultSlotShape,
					cValueShape,
					iValueShape,
					"slot-with-value-type-has-value",
					"ISlot ==> CFrame ==> IFrame");
			}
		}

		private class ActionsPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ActionsPanel() {

				super("Actions");

				addColumns("Target Type", "Mouse Action", "Model Action");

				addRow(
					"SLOT-VALUE-TYPE",
					LEFT_CLICK_STRING,
					"Add slot value");
				addRow(
					"SLOT-VALUE-TYPE",
					RIGHT_CLICK_STRING,
					"Clear slot value(s)");
				addRow(
					"SLOT-VALUE (CFrame only)",
					LEFT_CLICK_STRING,
					"Add disjunct to value");
				addRow(
					"SLOT-VALUE (IFrame only)",
					LEFT_CLICK_STRING,
					"Add disjunct to value-type");
				addRow(
					"SLOT-VALUE",
					RIGHT_CLICK_STRING,
					"Remove slot value");
			}
		}

		private class LabelModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			LabelModifiersPanel() {

				super("Label Modifiers");

				addColumns("Entity Type", "Node State", "Label Modifier", "Denotes");

				addRow(
					"All Types",
					"Collapsed",
					ITree.UPDATED_COLLAPSED_NODE_MARKER,
					"Latest action caused updates to descendant entities");
			}
		}

		InstantiationTreesPanel() {

			super("Instantiation Tree");

			new TreeSemanticsPanel();
			new ActionsPanel();
			new LabelModifiersPanel();
		}
	}

	HelpPanel() {

		super(JTabbedPane.TOP);

		setFont(getFont().deriveFont(CATEGORY_TITLE_FONT_SIZE));

		new EntitiesPanel();
		new ModelTreesPanel();
		new InstantiationTreesPanel();
	}

	private void addCategory(String title, CategoryPanel category) {

		addTab(title, category);
	}
}
