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

import java.awt.Color;
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
	static private final String LEFT_CLICK_STRING = "LEFT-CLICK";
	static private final String RIGHT_CLICK_STRING = "RIGHT-CLICK";

	static final Icon mValueShape = getValueShape(EntityLevel.META);
	static final Icon cValueShape = getValueShape(EntityLevel.CONCEPT);
	static final Icon iValueShape = getValueShape(EntityLevel.INSTANCE);
	static final Icon hiddenMFrameShape = getHiddenFrameShape(EntityLevel.META);
	static final Icon hiddenCFrameShape = getHiddenFrameShape(EntityLevel.CONCEPT);
	static final Icon defaultSlotShape = getDefaultSlotShape();
	static final Icon nonEditSlotShape = getNonEditSlotShape();
	static final Icon queryOnlyEditSlotShape = getQueryOnlyEditSlotShape();
	static final Icon fullEditSlotShape = getFullEditSlotShape();
	static final Icon inactiveSlotShape = getInactiveSlotShape();

	static final Icon internalColour = getColour(CSource.INTERNAL);
	static final Icon externalColour = getColour(CSource.EXTERNAL);
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

	static private Icon getNonEditSlotShape() {

		return getIcons().nonEditSlots.get(DEFAULT_SOURCE);
	}

	static private Icon getQueryOnlyEditSlotShape() {

		return getIcons().queryOnlyEditSlots.get(DEFAULT_SOURCE);
	}

	static private Icon getFullEditSlotShape() {

		return getIcons().fullEditSlots.get(DEFAULT_SOURCE);
	}

	static private Icon getInactiveSlotShape() {

		return getIcons().inactiveSlots.get(DEFAULT_SOURCE);
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

	private class GeneralPanel extends CategoryPanel {

		static final long serialVersionUID = -1;

		private class NodeShapesPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			NodeShapesPanel() {

				super("Node Shapes");

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

		private class NodeColoursPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			NodeColoursPanel() {

				super("Node Colours");

				addColumns(
					"Colour",
					"Entity Types",
					"Entity Level",
					"Entity Source");

				addRow(
					internalColour,
					"Frame, Slot",
					"Any",
					"Internal (i.e. Object Model)");
				addRow(
					externalColour,
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

		private class LabelModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			LabelModifiersPanel() {

				super("Label Modifiers");

				addColumns("Entity Types", "Label Modifier", "Represents");

				addCardinalityModifier(CCardinality.SINGLE_VALUE);
				addCardinalityModifier(CCardinality.UNIQUE_TYPES);
				addRepeatableModifier("Single value-type", true);
				addRepeatableModifier("Multiple value-types", false);
			}

			private void addRepeatableModifier(
							String valueTypesDesc,
							boolean singleType) {

				String desc = "";

				desc += "(" + describe(CCardinality.REPEATABLE_TYPES) + ")";
				desc += " AND ";
				desc += "(" + valueTypesDesc + ")";

				addCardinalityModifier(CCardinality.REPEATABLE_TYPES, singleType, desc);
			}

			private void addCardinalityModifier(CCardinality cardinality) {

				addCardinalityModifier(cardinality, false, describe(cardinality));
			}

			private void addCardinalityModifier(
							CCardinality cardinality,
							boolean singleType,
							String description) {

				String modifier = getModifier(cardinality, singleType);

				addRow("CSlot, ISlot", modifier, description);
			}

			private String getModifier(CCardinality cardinality, boolean singleType) {

				return SlotLabelModifiers.forCardinality(cardinality, singleType);
			}

			private String describe(CCardinality cardinality) {

				return "" + cardinality + " cardinality";
			}
		}

		GeneralPanel() {

			super("General");

			new NodeShapesPanel();
			new NodeColoursPanel();
			new LabelModifiersPanel();
		}
	}

	private class ModelPanel extends CategoryPanel {

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
					"CFrame has slot",
					false);
				checkAddRow(
					defaultSlotShape,
					mValueShape,
					"CSlot has MFrame-value-type",
					false);
				checkAddRow(
					defaultSlotShape,
					cValueShape,
					"CSlot has CFrame/CNumber-value-type",
					false);
				checkAddRow(
					mValueShape,
					mValueShape,
					"MFrame has sub-frame",
					false);
				checkAddRow(
					cValueShape,
					cValueShape,
					"CFrame has sub-frame ",
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

		private class NodeShapeModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			NodeShapeModifiersPanel() {

				super("Node-Shape Modifiers");

				addColumns(
					"Shape + or - Modifier",
					"Entity Types",
					"Represents Attribute");

				addRow(
					mValueShape,
					"MFrame",
					"Exposed");
				addRow(
					hiddenMFrameShape,
					"MFrame",
					"Hidden");
				addRow(
					cValueShape,
					"CFrame",
					"Exposed");
				addRow(
					hiddenCFrameShape,
					"CFrame",
					"Hidden");
				addRow(
					defaultSlotShape,
					"CSlot",
					"Default editable "
					+ "(concrete-only on assertions / fully editable on queries)");
				addRow(
					fullEditSlotShape,
					"CSlot",
					"Fully editable "
					+ "(fully editable on assertions and queries)");
				addRow(
					queryOnlyEditSlotShape,
					"CSlot",
					"Query-only editable "
					+ "(non-editable on assertions / fully editable on queries)");
				addRow(
					nonEditSlotShape,
					"CSlot",
					"Non-editable (non-editable on assertions or queries)");
				addRow(
					inactiveSlotShape,
					"CSlot",
					"Inactive");
			}
		}

		ModelPanel() {

			super("Model");

			new TreeSemanticsPanel("Left", true);
			new TreeSemanticsPanel("Right", false);
			new NodeShapeModifiersPanel();
		}
	}

	private class InstantiationsPanel extends CategoryPanel {

		static final long serialVersionUID = -1;

		private class TreeSemanticsPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			TreeSemanticsPanel() {

				super("Tree Semantics");

				addColumns("Parent", "Child", "Represents");

				addRow(
					iValueShape,
					defaultSlotShape,
					"IFrame has slot");
				addRow(
					defaultSlotShape,
					cValueShape,
					"ISlot has CFrame-value");
				addRow(
					defaultSlotShape,
					iValueShape,
					"ISlot has IFrame/INumber-value");
			}
		}

		private class NodeShapeModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			NodeShapeModifiersPanel() {

				super("Node-Shape Modifiers");

				addColumns(
					"Shape + or - Modifier",
					"Entity Types",
					"Represents Attribute");

				addRow(
					defaultSlotShape,
					"ISlot",
					"Concrete-only editable");
				addRow(
					fullEditSlotShape,
					"ISlot",
					"Fully-editable");
				addRow(
					nonEditSlotShape,
					"ISlot",
					"Non-editable");
			}
		}

		private class ExtraLabelModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ExtraLabelModifiersPanel() {

				super("Extra Label Modifiers");

				addColumns("Entity Types", "Label Modifier", "Represents");

				addSlotValueTypeModifier("concept", true);
				addSlotValueTypeModifier("instance-or-number", false);
			}

			private void addSlotValueTypeModifier(String valueType, boolean conceptLevel) {

				addRow(
					"ISlot",
					SlotLabelModifiers.forValueType("VALUE-TYPE", conceptLevel),
					"Slot is " + valueType + "-valued with specified value-type");
			}
		}

		private class LabelColoursPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			LabelColoursPanel() {

				super("Label Colours");

				addColumns(
					"Entity Types",
					"Entity Label/Label-Section Colours",
					"Represnts Update(s) Resulting rom Latest User Action");

				addRow(
					"IFrame, CFrame, INumber",
					getValueLabelComponent(ITreeUpdateMarker.DIRECT_UPDATES_CLR),
					"Value added by user");
				addRow(
					"ISlot",
					getSlotLabelComponent(ITreeUpdateMarker.DIRECT_UPDATES_CLR, null),
					"Value(s) removed by user");

				addRow(
					"CFrame, INumber",
					getValueLabelComponent(ITreeUpdateMarker.INDIRECT_UPDATES_CLR),
					"Value added by model");
				addRow(
					"IFrame",
					getValueLabelComponent(ITreeUpdateMarker.INDIRECT_UPDATES_CLR),
					"Value added, or non-visible descendant(s) updated, by model");
				addRow(
					"ISlot",
					getSlotLabelComponent(ITreeUpdateMarker.INDIRECT_UPDATES_CLR, null),
					"Value(s) removed, or non-visible descendant(s) updated, by model");
				addRow(
					"ISlot",
					getSlotLabelComponent(null, ITreeUpdateMarker.INDIRECT_UPDATES_CLR),
					"Value-type updated by model");
			}

			private JComponent getValueLabelComponent(Color colour) {

				Box comp = Box.createHorizontalBox();

				comp.add(getLabel("VALUE-LABEL", colour));

				return comp;
			}

			private JComponent getSlotLabelComponent(
									Color nameColour,
									Color valueTypeColour) {

				Box comp = Box.createHorizontalBox();

				comp.add(getLabel("SLOT-NAME", nameColour));
				comp.add(Box.createHorizontalStrut(10));
				comp.add(getLabel("VALUE-TYPE", valueTypeColour));
				comp.add(Box.createHorizontalStrut(10));
				comp.add(new JLabel("CARDINALITY"));

				return comp;
			}

			private JLabel getLabel(String text, Color colour) {

				JLabel label = new JLabel(text);

				if (colour != null) {

					label.setOpaque(true);
					label.setBackground(colour);
				}

				return label;
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
				addRow(
					"SLOT-VALUE (CFrame only)",
					RIGHT_CLICK_STRING,
					"Remove disjunct from value (alternative action, where applicable)");
				addRow(
					"SLOT-VALUE (IFrame only)",
					RIGHT_CLICK_STRING,
					"Remove disjunct from value-type (alternative action, where applicable)");
			}
		}

		InstantiationsPanel() {

			super("Instantiations");

			new TreeSemanticsPanel();
			new NodeShapeModifiersPanel();
			new ExtraLabelModifiersPanel();
			new LabelColoursPanel();
			new ActionsPanel();
		}
	}

	HelpPanel() {

		super(JTabbedPane.TOP);

		GFonts.setLarge(this);

		new GeneralPanel();
		new ModelPanel();
		new InstantiationsPanel();
	}

	private void addCategory(String title, CategoryPanel category) {

		addTab(title, category);
	}
}
