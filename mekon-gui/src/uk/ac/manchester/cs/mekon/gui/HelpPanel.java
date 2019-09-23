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
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class HelpPanel extends JTabbedPane {

	static final long serialVersionUID = -1;

	static private final EntityLevel DEFAULT_LEVEL = EntityLevel.CONCEPT;
	static private final CSource DEFAULT_SOURCE = CSource.UNSPECIFIED;
	static private final String POSITIVE_ACTION = "LEFT-CLICK";
	static private final String POSITIVE_ACTION2 = "LEFT-CLICK + ALT-KEY";
	static private final String POSITIVE_ACTION3 = "LEFT-CLICK + SHIFT-KEY";
	static private final String NEGATIVE_ACTION = "RIGHT-CLICK";
	static private final String ANY_MOUSE_ACTION = "LEFT-CLICK OR RIGHT-CLICK";
	static private final String ESCAPE_KEY = "ESCAPE-KEY";

	static final Icon mValueShape = getValueShape(EntityLevel.META);
	static final Icon cValueShape = getValueShape(EntityLevel.CONCEPT);
	static final Icon iValueShape = getValueShape(EntityLevel.INSTANCE);
	static final Icon hiddenMFrameShape = getHiddenFrameShape(EntityLevel.META);
	static final Icon hiddenCFrameShape = getHiddenFrameShape(EntityLevel.CONCEPT);
	static final Icon defaultEditAssertionsSlotShape = getDefaultSlotShape();
	static final Icon nonEditAssertionsSlotShape = getNonEditSlotShape();
	static final Icon fullEditAssertionsSlotShape = getFullEditSlotShape();
	static final Icon inactiveSlotShape = getInactiveSlotShape();

	static final Icon internalColour = getColour(CSource.INTERNAL);
	static final Icon externalColour = getColour(CSource.EXTERNAL);
	static final Icon dualColour = getColour(CSource.DUAL);
	static final Icon dataValueColor = getDataValueColour();

	static private Icon getValueShape(EntityLevel level) {

		return getIcons().exposedFrames.get(DEFAULT_SOURCE, level);
	}

	static private Icon getHiddenFrameShape(EntityLevel level) {

		return getIcons().hiddenFrames.get(DEFAULT_SOURCE, level);
	}

	static private Icon getDefaultSlotShape() {

		return getIcons().defaultEditAssertionsSlots.get(DEFAULT_SOURCE);
	}

	static private Icon getNonEditSlotShape() {

		return getIcons().nonEditAssertionsSlots.get(DEFAULT_SOURCE);
	}

	static private Icon getFullEditSlotShape() {

		return getIcons().fullEditAssertionsSlots.get(DEFAULT_SOURCE);
	}

	static private Icon getInactiveSlotShape() {

		return getIcons().inactiveSlots.get(DEFAULT_SOURCE);
	}

	static private Icon getColour(CSource source) {

		return getIcons().exposedFrames.get(source, DEFAULT_LEVEL);
	}

	static private Icon getDataValueColour() {

		return getIcons().dataValues.get(EntityLevel.CONCEPT);
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
					"CFrame, CNumber, CString");
				addRow(
					iValueShape,
					"Value-entity",
					"Instance",
					"IFrame, INumber, IString");
				addRow(
					defaultEditAssertionsSlotShape,
					"Slot",
					"Concept OR Instance (context dependent)",
					"CSlot, ISlot");
			}
		}

		private class NodeColoursPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			NodeColoursPanel() {

				super("Node Colours");

				addColumns(
					"Colour",
					"Entity Types (All Levels)",
					"Entity Source");

				addRow(
					internalColour,
					"Frame, Slot",
					"Internal (i.e. Object Model)");
				addRow(
					externalColour,
					"Frame, Slot",
					"External (e.g. Ontology)");
				addRow(
					dualColour,
					"Frame, Slot",
					"Internal AND External");
				addRow(
					dataValueColor,
					"Number, String",
					"Internal AND / OR External (context dependent)");
			}
		}

		private class LabelModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			LabelModifiersPanel() {

				super("Label Modifiers");

				addColumns("Entity Types", "Label Modifier", "Represents");

				addCardinalityRow(
					CCardinality.SINGLE_VALUE,
					false,
					"Single-valued");
				addCardinalityRow(
					CCardinality.UNIQUE_TYPES,
					false,
					"Multi-valued + Unique value-types");
				addCardinalityRow(
					CCardinality.REPEATABLE_TYPES,
					false,
					"Multi-valued + Multiple repeatable value-types");
				addCardinalityRow(
					CCardinality.REPEATABLE_TYPES,
					true,
					"Multi-valued + Single repeatable value-type");
			}

			private void addCardinalityRow(
							CCardinality cardinality,
							boolean singleType,
							String description) {

				addRow(
					"CSlot, ISlot",
					SlotLabelModifiers
						.forCardinality(
							cardinality,
							singleType),
					description);
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
					defaultEditAssertionsSlotShape,
					"CFrame has slot",
					false);
				checkAddRow(
					defaultEditAssertionsSlotShape,
					mValueShape,
					"CSlot has MFrame value-type",
					false);
				checkAddRow(
					defaultEditAssertionsSlotShape,
					cValueShape,
					"CSlot has CFrame / CNumber / CString value-type",
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
					"Shape + / - Modifier",
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
					defaultEditAssertionsSlotShape,
					"CSlot",
					"Concrete-only editable on assertions"
					+ " [query-editability not represented]");
				addRow(
					fullEditAssertionsSlotShape,
					"CSlot",
					"Fully editable on assertions"
					+ " [query-editability not represented]");
				addRow(
					nonEditAssertionsSlotShape,
					"CSlot",
					"Non editable on assertions"
					+ " [query-editability not represented]");
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

	private class TreeInstantiationsPanel extends CategoryPanel {

		static final long serialVersionUID = -1;

		private class TreeSemanticsPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			TreeSemanticsPanel() {

				super("Tree Semantics");

				addColumns("Parent", "Child", "Represents");

				addRow(
					iValueShape,
					defaultEditAssertionsSlotShape,
					"IFrame has slot");
				addRow(
					defaultEditAssertionsSlotShape,
					cValueShape,
					"ISlot has CFrame value");
				addRow(
					defaultEditAssertionsSlotShape,
					iValueShape,
					"ISlot has IFrame / INumber / IString value");
			}
		}

		private class NodeShapeModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			NodeShapeModifiersPanel() {

				super("Node-Shape Modifiers");

				addColumns(
					"Shape + / - Modifier",
					"Entity Types",
					"Represents Attribute");

				addRow(
					defaultEditAssertionsSlotShape,
					"ISlot",
					"Concrete-only editable");
				addRow(
					fullEditAssertionsSlotShape,
					"ISlot",
					"Fully editable");
				addRow(
					nonEditAssertionsSlotShape,
					"ISlot",
					"Non editable");
			}
		}

		private class ExtraLabelModifiersPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ExtraLabelModifiersPanel() {

				super("Extra Label Modifiers");

				addColumns("Entity Types", "Label Modifier", "Represents");

				addSlotValueTypeRow("CFrame", true);
				addSlotValueTypeRow("IFrame / INumber / IString", false);
			}

			private void addSlotValueTypeRow(String valueType, boolean conceptLevel) {

				addRow(
					"ISlot",
					SlotLabelModifiers.forValueType("VALUE-TYPE", conceptLevel),
					"" + valueType + "-valued with specified value-type");
			}
		}

		private class ActionsPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ActionsPanel() {

				super("Actions");

				addColumns(
					"Target",
					"Applicability",
					"Mouse-Click / Key",
					"Model Action");

				addRow(
					"SLOT",
					"Editable slots",
					POSITIVE_ACTION,
					"Add value");
				addRow(
					"SLOT",
					"Editable slots",
					NEGATIVE_ACTION,
					"Clear value(s)");
				addRow(
					"SLOT-VALUE",
					"Editable slots",
					NEGATIVE_ACTION,
					"Remove value");
				addRow(
					"SLOT-VALUE",
					"Abstract-editable CFrame-valued slots",
					POSITIVE_ACTION,
					"Add disjunct to value");
				addRow(
					"SLOT-VALUE",
					"Abstract-editable CFrame-valued slots",
					NEGATIVE_ACTION,
					"Remove disjunct(s) from value");
				addRow(
					"SLOT-VALUE",
					"Abstract-editable IFrame-valued slots",
					POSITIVE_ACTION,
					"Add disjunct to value-type of value");
				addRow(
					"SLOT-VALUE",
					"Abstract-editable IFrame-valued slots",
					NEGATIVE_ACTION,
					"Remove disjunct(s) from value-type of value");
				addRow(
					"SLOT-VALUE",
					"Abstract-editable IFrame-valued slots",
					POSITIVE_ACTION2,
					"Add disjunct to value");
			}
		}

		private class LabelColoursPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			LabelColoursPanel() {

				super("Label Colours");

				addColumns(
					"Entity Types",
					"Entity Label / Label-Section Colours",
					"Effect(s) of Latest User Action on Entity");

				addRow(
					"CFrame, INumber, IString",
					getValueLabelComponent(ITree.DIRECT_UPDATES_CLR),
					"Value added by user");
				addRow(
					"IFrame",
					getValueLabelComponent(ITree.DIRECT_UPDATES_CLR),
					"Value added, or non-visible descendant(s) updated, by user");
				addRow(
					"ISlot",
					getSlotLabelComponent(ITree.DIRECT_UPDATES_CLR, null),
					"Value(s) removed, or non-visible descendant(s) updated, by user");

				addRow(
					"CFrame, INumber, IString",
					getValueLabelComponent(ITree.INDIRECT_UPDATES_CLR),
					"Value added by model");
				addRow(
					"IFrame",
					getValueLabelComponent(ITree.INDIRECT_UPDATES_CLR),
					"Value added, or non-visible descendant(s) updated, by model");
				addRow(
					"ISlot",
					getSlotLabelComponent(ITree.INDIRECT_UPDATES_CLR, null),
					"Value(s) removed, or non-visible descendant(s) updated, by model");
				addRow(
					"ISlot",
					getSlotLabelComponent(null, ITree.INDIRECT_UPDATES_CLR),
					"Value-type updated by model");

				addRow(
					"IFrame",
					getValueLabelComponent(ITree.DIRECT_AND_INDIRECT_UPDATES_CLR),
					"Value added by user and non-visible descendant(s) initialised by model");
				addRow(
					"ISlot",
					getSlotLabelComponent(ITree.DIRECT_AND_INDIRECT_UPDATES_CLR, null),
					"Value(s) removed by user and non-visible descendant(s) updated by model");
			}
		}

		TreeInstantiationsPanel() {

			super("Instantiations: Tree");

			new TreeSemanticsPanel();
			new NodeShapeModifiersPanel();
			new ExtraLabelModifiersPanel();
			new ActionsPanel();
			new LabelColoursPanel();
		}
	}

	private class GraphLinkInstantiationsPanel extends CategoryPanel {

		static final long serialVersionUID = -1;

		private class EditModesPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			EditModesPanel() {

				super("Edit Modes");

				addColumns(
					"Edit Mode",
					"Applicable Actions",
					"Background Colour");

				addRow(
					"DEFAULT",
					"All TREE actions + CROSS-LINK actions for DEFAULT mode",
					getColouredPanel(ITree.DEFAULT_BACKGROUND_CLR));
				addRow(
					"LINK-CREATE",
					"CROSS-LINK actions for LINK-CREATE mode",
					getColouredPanel(ITree.CROSS_LINKING_BACKGROUND_CLR));
				addRow(
					"LINK-VIEW",
					"CROSS-LINK actions for LINK-VIEW mode",
					getColouredPanel(ITree.CROSS_LINKS_SHOW_BACKGROUND_CLR));
			}
		}

		private class ActionsPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			ActionsPanel() {

				super("Actions");

				addColumns(
					"Current Edit Mode",
					"Target",
					"Applicability",
					"Mouse-Click / Key",
					"Action");

				addRow(
					"DEFAULT",
					"SLOT",
					"Editable IFrame-slots for which linkable values exist",
					POSITIVE_ACTION3,
					"Select slot / Enter LINK-CREATE mode");
				addRow(
					"DEFAULT",
					"SLOT-VALUE",
					"Currently linked IFrame-values",
					POSITIVE_ACTION3,
					"Select value / Enter LINK-VIEW mode");

				addRow(
					"LINK-CREATE",
					"SLOT-VALUE",
					"Highlighted IFrame-values",
					POSITIVE_ACTION,
					"Select as slot-value / Exit LINK-CREATE mode");
				addRow(
					"LINK-CREATE",
					"SLOT / SLOT-VALUE",
					"All non-highlighted entities",
					ANY_MOUSE_ACTION,
					"Exit LINK-CREATE mode");
				addRow(
					"LINK-CREATE",
					"N/A",
					"N/A",
					ESCAPE_KEY,
					"Exit LINK-CREATE mode");

				addRow(
					"LINK-VIEW",
					"SLOT / SLOT-VALUE",
					"All non-highlighted entities",
					ANY_MOUSE_ACTION,
					"Exit LINK-VIEW mode");
				addRow(
					"LINK-VIEW",
					"N/A",
					"N/A",
					ESCAPE_KEY,
					"Exit LINK-VIEW mode");
			}
		}

		private class LabelColoursPanel extends SectionPanel {

			static final long serialVersionUID = -1;

			LabelColoursPanel() {

				super("Label Colours");

				addColumns(
					"Current Edit Mode",
					"Entity Types",
					"Entity Label",
					"Denotes");

				addRow(
					"LINK-CREATE",
					"IFrame",
					getValueLabelComponent(ITree.CROSS_LINKABLE_IFRAME_CLR),
					"Selectable value");
				addRow(
					"LINK-VIEW",
					"IFrame",
					getValueLabelComponent(ITree.CROSS_LINKED_IFRAME_CLR),
					"Linked value");
			}
		}

		GraphLinkInstantiationsPanel() {

			super("Instantiations: Cross-Links");

			new EditModesPanel();
			new ActionsPanel();
			new LabelColoursPanel();
		}
	}

	HelpPanel() {

		super(JTabbedPane.TOP);

		GFonts.setLarge(this);

		new GeneralPanel();
		new ModelPanel();
		new TreeInstantiationsPanel();
		new GraphLinkInstantiationsPanel();
	}

	private void addCategory(String title, CategoryPanel category) {

		addTab(title, category);
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
		comp.add(new JLabel("[...]"));

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

	private JPanel getColouredPanel(Color colour) {

		JPanel panel = new JPanel();

		panel.setBorder(new EtchedBorder());
		panel.setBackground(colour);

		return panel;
	}
}
