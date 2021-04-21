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

package uk.ac.manchester.cs.mekon.user.storedoctor;

import java.io.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
public class MekonStoreDoctor {

	static public void main(String[] args) {

		File configFile = lookForConfigFileArg(args);

		if (configFile != null) {

			if (checkConfigFile(configFile)) {

				createFromConfig(configFile).run();
			}
		}
	}

	static public MekonStoreDoctor createFromConfig(File configFile) {

		MekonStoreDoctor doctor = new MekonStoreDoctor();

		doctor.readConfigFile(configFile);

		return doctor;
	}

	static private File lookForConfigFileArg(String[] args) {

		if (args.length == 0) {

			showError("Must provide config-file argument");

			return null;
		}

		return new File(args[0]);
	}

	static private boolean checkConfigFile(File configFile) {

		if (!configFile.exists()) {

			showError("Invalid config-file path: " + configFile);

			return false;
		}

		return true;
	}

	static private void showError(String message) {

		System.out.println("STORE-DOCTOR ERROR: " + message);
	}

	private File storeDir = null;
	private boolean includeSubDirs = true;

	private CModel model = null;

	private InstanceDoctor instanceDoctor = new InstanceDoctor();

	public MekonStoreDoctor(File storeDir) {

		this.storeDir = storeDir;
	}

	public void readConfigFile(File configFile) {

		new ConfigFileParser(this, configFile);
	}

	public void setIncludeSubDirs(boolean include) {

		includeSubDirs = include;
	}

	public void setModel(CModel model) {

		this.model = model;
	}

	public void setModel(File mekonConfigFile) {

		setModel(CManager.createBuilder(new KConfigFile(mekonConfigFile)).build());
	}

	public void addEntityDoctor(EntityDoctor entityDoctor) {

		instanceDoctor.addEntityDoctor(entityDoctor);
	}

	public void run() {

		if (model != null) {

			instanceDoctor.setModel(model);
		}

		instanceDoctor.run(storeDir);

		if (includeSubDirs) {

			for (File subStoreDir : storeDir.listFiles()) {

				if (subStoreDir.isDirectory()) {

					instanceDoctor.run(subStoreDir);
				}
			}
		}
	}

	void setStoreDir(File storeDir) {

		this.storeDir = storeDir;
	}

	private MekonStoreDoctor() {
	}

	private boolean checkConfigFile() {

		if (!storeDir.exists()) {

			showError("Invalid store-directory path: " + storeDir);

			return false;
		}

		return true;
	}
}
