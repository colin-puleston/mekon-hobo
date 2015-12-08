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

import uk.ac.manchester.cs.mekon.config.*;

/**
 * Represents the configuration for a {@link BaseXMatcher}.
 *
 * @author Colin Puleston
 */
public class BaseXConfig implements BaseXConfigVocab {

	static private final String DEFAULT_DB_NAME = "MEKON";
	static private final String DEFAULT_STORE_DIR_NAME = "basex-store";

	private String databaseName;
	private File storeDirectory;

	/**
	 * Constructs configuration for matcher with the specified
	 * <i>Stardog</i> database and default reasoning-type. XXX
	 *
	 * @param databaseName Name of database to create
	 */
	public BaseXConfig(File storeDirectory) {

		this(DEFAULT_DB_NAME, storeDirectory);
	}

	/**
	 * Constructs configuration for matcher with the specified
	 * <i>Stardog</i> database and specified reasoning-type.
	 *
	 * @param databaseName Name of database to create
	 * @param storeDirectory Type of reasoning required from matcher
	 */
	public BaseXConfig(String databaseName, File storeDirectory) {

		this.databaseName = databaseName;
		this.storeDirectory = storeDirectory;
	}

	BaseXConfig(KConfigNode parentConfigNode) {

		KConfigNode configNode = parentConfigNode.getChild(MATCHER_ROOT_ID);

		databaseName = getDatabaseName(configNode);
		storeDirectory = getStoreDirectory(configNode);
	}

	String getDatabaseName() {

		return databaseName;
	}

	File getStoreDirectory() {

		return storeDirectory;
	}

	private File getStoreDirectory(KConfigNode configNode) {

		return configNode.getResource(
					STORE_DIRECTORY_ATTR,
					KConfigResourceFinder.DIRS,
					getDefaultStoreDirectory(configNode));
	}

	private String getDatabaseName(KConfigNode configNode) {

		return configNode.getString(DATABASE_NAME_ATTR, DEFAULT_DB_NAME);
	}

	private File getDefaultStoreDirectory(KConfigNode configNode) {

		return new File(getConfigStorePath(configNode), DEFAULT_STORE_DIR_NAME);
	}

	private String getConfigStorePath(KConfigNode configNode) {

		return configNode.getConfigFile().getFile().getParent();
	}
}
