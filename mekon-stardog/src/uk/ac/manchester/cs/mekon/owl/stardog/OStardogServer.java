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

import java.io.*;
import java.nio.file.*;

import com.complexible.common.base.*;

import com.complexible.stardog.*;
import com.complexible.stardog.api.*;
import com.complexible.stardog.api.admin.*;

import com.stardog.stark.*;

import uk.ac.manchester.cs.mekon_util.config.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class OStardogServer {

	static private final String USERNAME = "admin";
	static private final String PASSWORD = "admin";

	private Stardog server;
	private Connection connection;

	private String databaseName;

	OStardogServer(OModel model, String databaseName, boolean forceNewDB) {

		this.databaseName = databaseName;

		server = Stardog.builder().create();
		connection = startDatabase(forceNewDB);

		loadModel(model);
	}

	Connection getConnection() {

		return connection;
	}

	void stop(boolean keepDB) {

		if (server != null) {

			if (!keepDB) {

				removeDatabase();
			}

			connection.close();
			server.shutdown();

			connection = null;
			server = null;
		}
	}

	private Connection startDatabase(boolean forceNewDB) {

		ensureDatabase(forceNewDB);

		return connectToDatabase();
	}

	private void ensureDatabase(boolean forceNewDB) {

		AdminConnection admin = connectForAdmin();
		boolean exists = admin.list().contains(databaseName);

		if (exists && forceNewDB) {

			admin.drop(databaseName);

			exists = false;
		}

		if (!exists) {

			admin.newDatabase(databaseName).create();
		}

		admin.close();
	}

	private void removeDatabase() {

		AdminConnection admin = connectForAdmin();

		admin.drop(databaseName);
		admin.close();
	}

	private Connection connectToDatabase() {

		return ConnectionConfiguration
				.to(databaseName)
				.credentials(PASSWORD, PASSWORD)
				.reasoning(true)
				.connect();
	}

	private AdminConnection connectForAdmin() {

		return AdminConnectionConfiguration
				.toEmbeddedServer()
				.credentials(USERNAME, PASSWORD)
				.connect();
	}

	private void loadModel(OModel model) {

		File file = model.renderModelToTempFile();
		Path path = Paths.get(file.toURI());

		connection.begin();
		connection.add().io().file(path);
		connection.commit();

		file.delete();
	}
}
