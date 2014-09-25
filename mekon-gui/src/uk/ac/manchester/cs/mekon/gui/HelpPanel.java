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
	static final Icon blockedSlotShape = getEditBlockedSlotShape();

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

		return getIcons().blockedSlots.get(DEFAULT_SOURCE);
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

			GFonts.setMedium(this);
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

				addColumns("Shape", "Represents");

				addRow(
					mValueShape,
					"Meta-level frame");
				addRow(
					cValueShape,
					"Concept-level frame/number");
				addRow(
					iValueShape,
					"Instance-level frame/number");
				addRow(
					defaultSlotShape,
					"Slot (concept-level or instance-level)");
			}
		}

		private class ColoursPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ColoursPanel() {

				super("Colours");

				addColumns("Colour", "Represents");

				addRow(
					directColour,
					"Frame/Slot (any level) with internal source (i.e. Object Model)");
				addRow(
					indirectColour,
					"Frame/Slot (any level) with external source (e.g. Ontology)");
				addRow(
					dualColour,
					"Frame/Slot (any level) with dual sources (internal + external)");
				addRow(
					numberColour,
					"Number (concept-level or instance-level)");
			}
		}

		private class ModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ModifiersPanel() {

				super("Modifiers");

				addColumns("Modifier", "Represents");

				addRow(
					hiddenMFrameShape,
					"MFrame is hidden");
				addRow(
					hiddenCFrameShape,
					"CFrame is hidden");
				addRow(
					inactiveSlotShape,
					"CSlot is inactive");
				addRow(
					blockedSlotShape,
					"CSlot is dependent "
					+ "(i.e. values are automatically derived)");
				addRow(
					blockedSlotShape,
					"ISlot is non-editable "
					+ "(i.e. dependent slot on concrete instance)");
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

				addColumns("Parent", "Child", "Represents");

				checkAddRow(
					cValueShape,
					defaultSlotShape,
					"CFrame-has-slot",
					false);
				checkAddRow(
					defaultSlotShape,
					mValueShape,
					"CSlot-has-MFrame-value-type",
					false);
				checkAddRow(
					defaultSlotShape,
					cValueShape,
					"CSlot-has-CFrame/CNumber-value-type",
					false);
				checkAddRow(
					mValueShape,
					mValueShape,
					"MFrame-has-sub-frame",
					false);
				checkAddRow(
					cValueShape,
					cValueShape,
					"CFrame-has-sub-frame ",
					true);
			}

			private void checkAddRow(
							Icon parent,
							Icon child,
							String semantics,
							boolean conceptTree) {

				if (conceptTree || !conceptTreeOnly) {

					addRow(parent, child, semantics);
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

				addColumns("Parent", "Child", "Grandchild", "Represents");

				addRow(
					iValueShape,
					defaultSlotShape,
					NOT_APPLICABLE_STRING,
					"IFrame-has-slot");
				addRow(
					defaultSlotShape,
					mValueShape,
					NOT_APPLICABLE_STRING,
					"ISlot-has-MFrame-value-type");
				addRow(
					defaultSlotShape,
					cValueShape,
					NOT_APPLICABLE_STRING,
					"ISlot-has-CFrame-value-type");
				addRow(
					defaultSlotShape,
					mValueShape,
					cValueShape,
					"ISlot-with-MFrame-value-type-has-CFrame-value");
				addRow(
					defaultSlotShape,
					cValueShape,
					iValueShape,
					"ISlot-with-CFrame-value-type-has-IFrame-value");
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

		GFonts.setLarge(this);

		new EntitiesPanel();
		new ModelTreesPanel();
		new InstantiationTreesPanel();
	}

	private void addCategory(String title, CategoryPanel category) {

		addTab(title, category);
	}
}
