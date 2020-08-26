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

package uk.ac.manchester.cs.mekon.gui.storedoctor;

import java.io.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
public class MekonStoreDoctor {

	static final String INSTANCE_FILE_PREFIX = "INSTANCE-";
	static final String INSTANCE_FILE_SUFFIX = ".xml";

	static public void main(String[] args) {

		File configFile = lookForConfigFileArg(args);

		if (configFile != null) {

			if (checkConfigFile(configFile)) {

				createFromConfig(configFile).run();
			}
		}
	}

	static private MekonStoreDoctor createFromConfig(File configFile) {

		MekonStoreDoctor doctor = new MekonStoreDoctor();

		new ConfigFileParser(doctor, configFile);

		return doctor;
	}

	static private File lookForConfigFileArg(String[] args) {

		if (args.length == 0) {

			System.out.println("ERROR: Must provide config-file argument");

			return null;
		}

		return new File(args[0]);
	}

	static private boolean checkConfigFile(File configFile) {

		if (!configFile.exists()) {

			System.out.println("ERROR: Invalid config-file path: " + configFile);

			return false;
		}

		return true;
	}

	private File storeDir = null;
	private CModel model = null;

	private InstanceDoctor instanceDoctor = new InstanceDoctor();

	public MekonStoreDoctor(File storeDir) {

		this.storeDir = storeDir;

		run();
	}

	public void setMekonModel(File mekonConfigFile) {

		model = CManager.createBuilder(new KConfigFile(mekonConfigFile)).build();
	}

	public void addEntityDoctor(EntityDoctor entityDoctor) {

		instanceDoctor.addEntityDoctor(entityDoctor);
	}

	void setStoreDir(File storeDir) {

		this.storeDir = storeDir;
	}

	private MekonStoreDoctor() {
	}

	private void run() {

		for (File file : getAllInstanceFiles()) {

			instanceDoctor.checkDoctor(file);
		}
	}

	private boolean checkConfigFile() {

		if (!storeDir.exists()) {

			System.out.println("ERROR: Invalid store-directory path: " + storeDir);

			return false;
		}

		return true;
	}

	private File[] getAllInstanceFiles() {

		KFileStore store = new KFileStore(INSTANCE_FILE_PREFIX, INSTANCE_FILE_SUFFIX);

		store.setDirectory(storeDir);

		return store.getAllFiles();
	}
}
