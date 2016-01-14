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

package uk.ac.manchester.cs.mekon.owl.stardog;

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.reason.*;

/**
 * Represents the configuration for a {@link OStardogMatcher}.
 * See individual "set" methods for default values.
 *
 * @author Colin Puleston
 */
public class OStardogConfig implements OStardogConfigVocab {

	private String databaseName = "MEKON";
	private ORReasoningType reasoningType = ORReasoningType.DL;
	private boolean rebuildStore = true;
	private boolean persistStore = false;

	private class ConfigNodeBasedInitialiser {

		private KConfigNode configNode;

		ConfigNodeBasedInitialiser(KConfigNode parentConfigNode) {

			configNode = parentConfigNode.getChild(MATCHER_ROOT_ID);

			databaseName = getDatabaseName();
			rebuildStore = getRebuildStore();
			persistStore = getPersistStore();
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
	}

	/**
	 * Constructor.
	 */
	public OStardogConfig() {
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
	 * Sets the type of reasoning required from the matcher.
	 * Defaults to {@link ORReasoningType#DL}.
	 *
	 * @param reasoningType Required reasoning type
	 */
	public void setReasoningType(ORReasoningType reasoningType) {

		this.reasoningType = reasoningType;
	}

	/**
	 * Sets whether the BaseX store should be completely rebuilt
	 * from the main MEKON instance store on start-up. Defaults to
	 * true.
	 *
	 * @param rebuildStore True if BaseX store should be rebuilt
	 */
	public void setRebuildStore(boolean rebuildStore) {

		this.rebuildStore = rebuildStore;
	}

	/**
	 * Sets whether the BaseX store should persist after the matcher
	 * is destroyed. Defaults to false.
	 *
	 * @param persistStore True if BaseX store should persist
	 */
	public void setPersistStore(boolean persistStore) {

		this.persistStore = persistStore;
	}

	OStardogConfig(KConfigNode parentConfigNode) {

		new ConfigNodeBasedInitialiser(parentConfigNode);
	}

	String getDatabaseName() {

		return databaseName;
	}

	ORReasoningType getReasoningType() {

		return reasoningType;
	}

	boolean rebuildStore() {

		return rebuildStore;
	}

	boolean persistStore() {

		return persistStore;
	}
}
