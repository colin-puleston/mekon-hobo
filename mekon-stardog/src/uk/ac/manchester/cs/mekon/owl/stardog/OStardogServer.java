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

import com.complexible.stardog.*;
import com.complexible.stardog.api.*;
import com.complexible.stardog.api.admin.*;
import com.complexible.stardog.db.*;

import com.stardog.stark.io.*;

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

	OStardogServer(OModel model, String databaseName) {

		this.databaseName = databaseName;

		server = Stardog.builder().create();
		connection = startDatabase();

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

	private Connection startDatabase() {

		createDatabase();

		return connectToDatabase();
	}

	private void createDatabase() {

		AdminConnection admin = connectForAdmin();

		if (admin.list().contains(databaseName)) {

			admin.drop(databaseName);
		}

		admin
			.newDatabase(databaseName)
			.set(DatabaseOptions.QUERY_ALL_GRAPHS, true)
			.create();

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
				.credentials(USERNAME, PASSWORD)
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
		connection.add().io().format(RDFFormats.RDFXML).file(path);
		connection.commit();

		file.delete();
	}
}
