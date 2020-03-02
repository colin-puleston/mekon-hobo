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

import java.io.*;
import java.util.*;
import java.awt.BorderLayout;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
public class MekonApp extends GFrame {

	static private final long serialVersionUID = -1;

	static private final String EXIT_LABEL = "Exit";

	static private final int FRAME_WIDTH = 700;
	static private final int FRAME_HEIGHT = 600;

	static private final String CONFIG_FILE_NAME = "mekon-app.xml";

	static public void main(String[] args) {

		configureAndDisplay(new MekonApp(), args);
	}

	static protected void configureAndDisplay(MekonApp app, String[] args) {

		if (args.length == 0) {

			app.configureFromFile();
		}
		else {

			app.configureFromFile(getConfigFile(args[0]));
		}

		app.display();
	}

	static private KConfigFile getConfigFile(String nameOrPath) {

		File file = new File(nameOrPath);

		return file.exists() ? new KConfigFile(file) : new KConfigFile(nameOrPath);
	}

	static private IStore getIStore(CBuilder builder) {

		return IDiskStoreManager.getBuilder(builder).build();
	}

	private CModel model;
	private IStore store;

	private List<CFrame> instanceGroupRootTypes = new ArrayList<CFrame>();

	private Customiser customiser = null;

	private class ExitButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			dispose();
		}

		ExitButton() {

			super(EXIT_LABEL);

			setFont(GFonts.toLarge(getFont()));
		}
	}

	public MekonApp() {

		this(CManager.createBuilder());
	}

	public MekonApp(CBuilder builder) {

		this(builder.build(), getIStore(builder));
	}

	public MekonApp(CModel model, IStore store) {

		super(FRAME_WIDTH, FRAME_HEIGHT);

		this.model = model;
		this.store = store;

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void setCustomiser(Customiser customiser) {

		this.customiser = customiser;
	}

	public void configureFromFile() {

		new MekonAppConfig(this);
	}

	public void configureFromFile(KConfigFile configFile) {

		new MekonAppConfig(this, configFile);
	}

	public void addInstanceGroup(CFrame rootType) {

		instanceGroupRootTypes.add(rootType);
	}

	public void addInstanceGroups(List<CFrame> rootTypes) {

		this.instanceGroupRootTypes.addAll(rootTypes);
	}

	public void display() {

		if (instanceGroupRootTypes.isEmpty()) {

			displayConfigError("No instance-groups have been specified");
		}
		else {

			display(createMainPanel());
		}
	}

	public CModel getModel() {

		return model;
	}

	public IStore getStore() {

		return store;
	}

	private JPanel createMainPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createInstanceGroupsPanel(), BorderLayout.CENTER);
		panel.add(new ExitButton(), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createInstanceGroupsPanel() {

		return new InstanceGroupsPanel(createController(), instanceGroupRootTypes);
	}

	private Controller createController() {

		ensureCustomiser();

		return new Controller(new Store(store, customiser), customiser);
	}

	private void ensureCustomiser() {

		if (customiser == null) {

			customiser = new DefaultCustomiser();
		}
	}

	private void displayConfigError(String message) {

		System.err.println("MEKON-APP CONFIG ERROR: " + message);
	}
}
