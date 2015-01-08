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

				addTab(title, new JScrollPane(this));
			}
		}

		CategoryPanel(String title) {

			super(JTabbedPane.BOTTOM);

			GFonts.setMedium(this);
			addCategory(title, this);
		}
	}

	private class EntitiesPanel extends CategoryPanel {

		static final long serialVersionUID = -1;

		private class ShapesPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ShapesPanel() {

				super("Shapes");

				addColumns(
					"Shape",
					"Entity Category",
					"Entity Level",
					"Entity Types");

				addRow(
					mValueShape,
					"Value-entity",
					"Meta",
					"MFrame");
				addRow(
					cValueShape,
					"Value-entity",
					"Concept",
					"CFrame, CNumber");
				addRow(
					iValueShape,
					"Value-entity",
					"Instance",
					"IFrame, INumber");
				addRow(
					defaultSlotShape,
					"Slot",
					"Concept OR Instance",
					"CSlot, ISlot");
			}
		}

		private class ColoursPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ColoursPanel() {

				super("Colours");

				addColumns(
					"Colour",
					"Entity Types",
					"Entity Level",
					"Entity Source");

				addRow(
					directColour,
					"Frame, Slot",
					"Any",
					"Internal (i.e. Object Model)");
				addRow(
					indirectColour,
					"Frame, Slot",
					"Any",
					"External (e.g. Ontology)");
				addRow(
					dualColour,
					"Frame, Slot",
					"Any",
					"Dual (Internal AND External)");
				addRow(
					numberColour,
					"Number",
					"Any",
					"Any (determined by context)");
			}
		}

		private class ShapeModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ShapeModifiersPanel() {

				super("Shape Modifiers");

				addColumns(
					"Shape with Modifier",
					"Entity Types",
					"Entity State");

				addRow(
					hiddenMFrameShape,
					"MFrame",
					"Hidden");
				addRow(
					hiddenCFrameShape,
					"CFrame",
					"Hidden");
				addRow(
					inactiveSlotShape,
					"CSlot",
					"Inactive");
				addRow(
					blockedSlotShape,
					"CSlot",
					"Dependent (automatically derived values)");
				addRow(
					blockedSlotShape,
					"ISlot",
					"Non-Editable (dependent slot on assertion instance)");
			}
		}

		private class LabelModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			LabelModifiersPanel() {

				super("Label Modifiers");

				addColumns("Entity Types", "Label Modifier", "Denotes");

				addSlotCardinalityModifier(CCardinality.FREE);
				addSlotCardinalityModifier(CCardinality.UNIQUE_TYPES);
				addSlotCardinalityModifier(CCardinality.SINGLETON);
			}

			private void addSlotCardinalityModifier(CCardinality cardinality) {

				addRow(
					"CSlot, ISlot",
					SlotLabels.getCardinalityModifierHelp(cardinality),
					"Cardinality = " + cardinality);
			}
		}

		EntitiesPanel() {

			super("Entities");

			new ShapesPanel();
			new ColoursPanel();
			new ShapeModifiersPanel();
			new LabelModifiersPanel();
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

		private class DynamicLabelModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			DynamicLabelModifiersPanel() {

				super("Dynamic Label Modifiers");

				addColumns("Entity Types", "Node State", "Label Modifier", "Denotes");

				addRow(
					"ALL",
					"Collapsed",
					"\"" + ITree.UPDATED_NODE_MARKER + "\"",
					"Latest action caused update to entity or to non-visible descendant(s)");
			}
		}

		InstantiationTreesPanel() {

			super("Instantiation Trees");

			new TreeSemanticsPanel();
			new ActionsPanel();
			new DynamicLabelModifiersPanel();
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
