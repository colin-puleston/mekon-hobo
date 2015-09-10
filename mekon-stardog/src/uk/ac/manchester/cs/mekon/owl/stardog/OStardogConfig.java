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
import uk.ac.manchester.cs.mekon.owl.triples.*;

/**
 * Represents the configuration for a {@link OStardogMatcher}.
 *
 * @author Colin Puleston
 */
public class OStardogConfig extends OTConfig implements OStardogConfigVocab {

	/**
	 * Default configuration.
	 */
	static public final OStardogConfig DEFAULT = new OStardogConfig();

	static private final String DEFAULT_DB_NAME = "MEKON";

	private String databaseName;

	/**
	 * Constructs configuration for matcher with the specified
	 * <i>Stardog</i> database and default reasoning-type.
	 *
	 * @param databaseName Name of database to create
	 */
	public OStardogConfig(String databaseName) {

		this.databaseName = databaseName;
	}

	/**
	 * Constructs configuration for matcher with the specified
	 * <i>Stardog</i> database and specified reasoning-type.
	 *
	 * @param databaseName Name of database to create
	 * @param reasoningType Type of reasoning required from matcher
	 */
	public OStardogConfig(String databaseName, OTReasoningType reasoningType) {

		super(reasoningType);

		this.databaseName = databaseName;
	}

	OStardogConfig(KConfigNode parentConfigNode) {

		super(parentConfigNode);

		databaseName = getDatabaseName(parentConfigNode);
	}

	String getDatabaseName() {

		return databaseName;
	}

	private OStardogConfig() {

		this(DEFAULT_DB_NAME);
	}

	private String getDatabaseName(KConfigNode parentConfigNode) {

		KConfigNode configNode = parentConfigNode.getChild(MATCHER_ROOT_ID);

		return configNode.getString(DATABASE_NAME_ATTR, DEFAULT_DB_NAME);
	}
}
