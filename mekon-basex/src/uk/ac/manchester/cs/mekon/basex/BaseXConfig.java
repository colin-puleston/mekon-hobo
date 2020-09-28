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

package uk.ac.manchester.cs.mekon.basex;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * Represents the configuration for a {@link BaseXMatcher}.
 * See individual "set" methods for default values.
 *
 * @author Colin Puleston
 */
public class BaseXConfig implements BaseXConfigVocab {

	static private final String DEFAULT_STORE_DIR_NAME = "basex-store";

	static private File getDefaultStoreDir(String parentDir) {

		return new File(parentDir, DEFAULT_STORE_DIR_NAME);
	}

	private String databaseName = "MEKON";
	private File storeDirectory = getDefaultStoreDir(".");
	private boolean rebuildStore = true;
	private boolean persistStore = false;

	private class ConfigNodeBasedInitialiser {

		private KConfigNode configNode;

		ConfigNodeBasedInitialiser(KConfigNode parentConfigNode) {

			configNode = parentConfigNode.getChild(MATCHER_ROOT_ID);

			databaseName = getDatabaseName();
			storeDirectory = getStoreDirectory();
			rebuildStore = getRebuildStore();
			persistStore = getPersistStore();
		}

		private File getStoreDirectory() {

			return configNode.getResource(
						STORE_DIRECTORY_ATTR,
						KConfigResourceFinder.DIRS,
						getDefaultStoreDirInConfigDir());
		}

		private String getDatabaseName() {

			return configNode.getString(DATABASE_NAME_ATTR, databaseName);
		}

		private boolean getRebuildStore() {

			return configNode.getBoolean(REBUILD_STORE_ATTR, rebuildStore);
		}

		private boolean getPersistStore() {

			return configNode.getBoolean(PERSIST_STORE_ATTR, persistStore);
		}

		private File getDefaultStoreDirInConfigDir() {

			return getDefaultStoreDir(getConfigStorePath());
		}

		private String getConfigStorePath() {

			return configNode.getConfigFile().getFile().getParent();
		}
	}

	/**
	 * Constructor.
	 */
	public BaseXConfig() {
	}

	/**
	 * Sets the name of the database. Defaults to "MEKON".
	 *
	 * @param databaseName Name of database
	 */
	public void setDatabaseName(String databaseName) {

		this.databaseName = databaseName;
	}

	/**
	 * Sets the directory for the file-store. Defaults to a
	 * a directory named "basex-store" under the current directory.
	 *
	 * @param storeDirectory Directory for file-store
	 */
	public void setStoreDirectory(File storeDirectory) {

		this.storeDirectory = storeDirectory;
	}

	/**
	 * Sets whether the store should be completely rebuilt
	 * from the main MEKON instance store on start-up. Defaults to
	 * true.
	 *
	 * @param rebuildStore True if store should be rebuilt
	 */
	public void setRebuildStore(boolean rebuildStore) {

		this.rebuildStore = rebuildStore;
	}

	/**
	 * Sets whether the store should persist after the matcher
	 * is destroyed. Defaults to false.
	 *
	 * @param persistStore True if store should persist
	 */
	public void setPersistStore(boolean persistStore) {

		this.persistStore = persistStore;
	}

	BaseXConfig(KConfigNode parentConfigNode) {

		new ConfigNodeBasedInitialiser(parentConfigNode);
	}

	String getDatabaseName() {

		return databaseName;
	}

	File getStoreDirectory() {

		return storeDirectory;
	}

	boolean rebuildStore() {

		return rebuildStore;
	}

	boolean persistStore() {

		return persistStore;
	}
}
