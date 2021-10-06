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
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;

import org.basex.core.*;
import org.basex.core.cmd.*;
import org.basex.query.*;
import org.basex.query.iter.*;
import org.basex.query.value.item.*;
import org.basex.api.dom.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.config.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
class Database {

	static private final String TEMP_DB_NAME = "TEMP-DB";

	static private final String STORE_FILE_PREFIX = "INSTANCE-";
	static private final String STORE_FILE_SUFFIX = ".xml";

	static Database createTempDB(BaseXConfig config) {

		return new Database(TEMP_DB_NAME, getTempDBStoreDir(config), false);
	}

	static private File getTempDBStoreDir(BaseXConfig config) {

		return new File(config.getStoreDirectory(), TEMP_DB_NAME);
	}

	private String databaseName;
	private boolean persist;

	private KFileStore fileStore = new KFileStore(STORE_FILE_PREFIX, STORE_FILE_SUFFIX);
	private Context context = new Context();

	Database(BaseXConfig config) {

		this(
			config.getDatabaseName(),
			config.getStoreDirectory(),
			config.persistStore());
	}

	void add(XDocument instance, int index) {

		File file = fileStore.getFile(index);

		instance.writeToFile(file);

		execute(new Add(getDatabasePath(file), file.getPath()));
	}

	void remove(int index) {

		File file = fileStore.getFile(index);

		execute(new Delete(getDatabasePath(file)));

		fileStore.removeFile(index);
	}

	List<Integer> executeQuery(String query) {

		QueryProcessor proc = new QueryProcessor(query, context);

		try {

			return extractInstanceIndexes(proc.iter());
		}
		catch (QueryException e) {

			throw new KSystemConfigException(e);
		}
		finally {

			proc.close();
		}
    }

	void stop() {

		if (context != null) {

			try {

				execute(getStopCommand());
			}
			finally {

				context.close();
				context = null;
			}
		}

		if (!persist) {

			fileStore.clear();
		}
	}

	private Database(String databaseName, File storeDir, boolean persist) {

		this.databaseName = databaseName;
		this.persist = persist;

		fileStore.setDirectory(storeDir);
		fileStore.clear();

		execute(new CreateDB(databaseName));
	}

	private Command getStopCommand() {

		return persist ? new Close() : new DropDB(databaseName);
	}

	private String getDatabasePath(File file) {

		String name = file.getName();

		return name.substring(0, name.indexOf('.'));
	}

	private String execute(Command command) {

		try {

			return command.execute(context);
		}
		catch (BaseXException e) {

			throw new KSystemConfigException(e);
		}
	}

	private List<Integer> extractInstanceIndexes(Iter queryResults) throws QueryException {

		List<Integer> indexes = new ArrayList<Integer>();
		Set<Integer> indexSet = new HashSet<Integer>();

		for (Item item ; (item = queryResults.next()) != null ; ) {

			int index = extractInstanceIndex(item);

			if (indexSet.add(index)) {

				indexes.add(index);
			}
		}

		return indexes;
    }

	private Integer extractInstanceIndex(Item queryResult) throws QueryException {

		BXAttr attribute = (BXAttr)queryResult.toJava();

		return Integer.parseInt(attribute.getValue());
	}
}
