/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon.appmodeller;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.mekon.appmodeller.model.*;

/**
 * @author Colin Puleston
 */
public class MekonAppModeller extends GFrame {

	static private final long serialVersionUID = -1;

	static final String SIMPLE_TITLE = "MEKON-APP Model Editor";

	static private final String COMPOUND_TITLE_FORMAT = SIMPLE_TITLE + ": %s";

	static private final String LOAD_BUTTON_LABEL = "Load...";
	static private final String SAVE_BUTTON_LABEL = "Save";
	static private final String SAVE_AS_BUTTON_LABEL = "Save As...";
	static private final String EXIT_BUTTON_LABEL = "Exit";

	static private final int FRAME_WIDTH = 1200;
	static private final int FRAME_HEIGHT = 700;

	private ModelHandler modelHandler;

	private JTabbedPane modelEditPanel = new JTabbedPane(JTabbedPane.LEFT);

	private class LoadButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			modelHandler.loadNew();

			displayNewModel();
		}

		LoadButton() {

			super(LOAD_BUTTON_LABEL);
		}
	}

	private class SaveButton extends GButton {

		static private final long serialVersionUID = -1;

		private class Enabler implements ModelListener {

			public void onModelUpdate() {

				setEnabled(true);
			}

			Enabler() {

				modelHandler.addModelListener(this);
			}
		}

		protected void doButtonThing() {

			if (modelHandler.checkSave()) {

				setEnabled(false);
			}
		}

		SaveButton() {

			super(SAVE_BUTTON_LABEL);

			setEnabled(false);

			new Enabler();
		}
	}

	private class SaveAsButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			modelHandler.saveAs();
		}

		SaveAsButton() {

			super(SAVE_AS_BUTTON_LABEL);
		}
	}

	private class ExitButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			modelHandler.checkSaveOnExit();

			dispose();
		}

		ExitButton() {

			super(EXIT_BUTTON_LABEL);
		}
	}

	static public void main(String[] args) {

		new MekonAppModeller(getTitle(args));
	}

	static private String getTitle(String[] args) {

		return args.length == 1 ? createCompoundTitle(args[0]) : SIMPLE_TITLE;
	}

	static private String createCompoundTitle(String subTitle) {

		return String.format(COMPOUND_TITLE_FORMAT, subTitle);
	}

	public MekonAppModeller(String title) {

		super(title, FRAME_WIDTH, FRAME_HEIGHT);

		modelHandler = new ModelHandler(this);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		populateModelEditPanel();
		display(createMainPanel());
	}

	private void displayNewModel() {

		modelEditPanel.removeAll();
		populateModelEditPanel();
	}

	private JComponent createMainPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(modelEditPanel, BorderLayout.CENTER);
		panel.add(createExternalActionsPanel(), BorderLayout.NORTH);

		return panel;
	}

	private void populateModelEditPanel() {

		modelEditPanel.setFont(GFonts.toMedium(modelEditPanel.getFont()));

		for (Hierarchy hierarchy : getModel().getHierarchies()) {

			String title = hierarchy.getRoot().getConceptId().getLabel();

			modelEditPanel.addTab(title, new HierarchyPanel(hierarchy));
		}
	}

	private JComponent createExternalActionsPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBorder(LineBorder.createGrayLineBorder());
		panel.add(createExternalActionButtons(), BorderLayout.EAST);

		return panel;
	}

	private JComponent createExternalActionButtons() {

		return ControlsPanel.horizontal(
					new LoadButton(),
					new SaveButton(),
					new SaveAsButton(),
					new ExitButton());
	}

	private Model getModel() {

		return modelHandler.getModel();
	}
}
