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
import java.util.List;
import java.util.ArrayList;

import org.basex.core.*;
import org.basex.core.cmd.*;
import org.basex.query.*;
import org.basex.query.iter.*;
import org.basex.query.value.item.*;
import org.basex.api.dom.*;

import uk.ac.manchester.cs.mekon.config.*;

/**
 * @author Colin Puleston
 */
class Database {

	private Context context = new Context();
	private String databaseName;

	Database(String databaseName, boolean rebuild) {

		this.databaseName = databaseName;

		execute(getStartCommand(rebuild));
	}

	void add(File file) {

		execute(new Add("", file.getPath()));
	}

	void remove(File file) {

		execute(new Delete(file.getPath()));
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

	void stop(boolean persist) {

		if (context != null) {

			try {

				execute(getStopCommand(persist));
			}
			finally {

				context.close();
				context = null;
			}
		}
	}

	private Command getStartCommand(boolean rebuild) {

		return rebuild
				? new CreateDB(databaseName)
				: new Check(databaseName);
	}

	private Command getStopCommand(boolean persist) {

		return persist ? new Close() : new DropDB(databaseName);
	}

	private String execute(Command command) {

		try {

			return command.execute(context);
		}
		catch (BaseXException e) {

			throw new KSystemConfigException(e);
		}
	}

	private List<Integer> extractInstanceIndexes(
							Iter queryResults)
							throws QueryException {

		List<Integer> indexes = new ArrayList<Integer>();

		for (Item item ; (item = queryResults.next()) != null ; ) {

			indexes.add(extractInstanceIndex(item));
		}

		return indexes;
    }

	private Integer extractInstanceIndex(
						Item queryResult)
						throws QueryException {

		BXAttr attribute = (BXAttr)queryResult.toJava();

		return Integer.parseInt(attribute.getValue());
	}
}
