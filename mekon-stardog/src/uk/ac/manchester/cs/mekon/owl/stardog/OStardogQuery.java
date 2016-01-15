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

import java.util.*;

import org.openrdf.model.*;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.QueryEvaluationException;

import com.complexible.stardog.api.*;

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.triples.*;

/**
 * @author Colin Puleston
 */
class OStardogQuery implements OTQuery {

	private Connection connection;

	private abstract class Executor<R> {

		R execute(String query, OTQueryConstants constants) {

			Query<R> queryExec = create(query);

			addConstants(queryExec, constants);

			return queryExec.execute();
		}

		abstract Query<R> create(String query);

		private void addConstants(Query<?> query, OTQueryConstants constants) {

			for (OTValue constant : constants.getConstants()) {

				String varName = constants.getVariableName(constant);

				query.parameter(varName, ValueConverter.convert(constant));
			}
		}
	}

	private class AskExecutor extends Executor<Boolean> {

		Query<Boolean> create(String query) {

			return connection.ask(query);
		}
	}

	private class UpdateExecutor extends Executor<Boolean> {

		Query<Boolean> create(String query) {

			return connection.update(query);
		}
	}

	private class SelectExecutor extends Executor<TupleQueryResult> {

		Query<TupleQueryResult> create(String query) {

			return connection.select(query);
		}
	}

	public boolean namedGraphs() {

		return true;
	}

	public boolean executeAsk(String query, OTQueryConstants constants) {

		return new AskExecutor().execute(query, constants);
	}

	public void executeUpdate(String query, OTQueryConstants constants) {

		new UpdateExecutor().execute(query, constants);
	}

	public List<OT_URI> executeSelect(String query, OTQueryConstants constants) {

		List<OT_URI> bindings = new ArrayList<OT_URI>();
		TupleQueryResult result = new SelectExecutor().execute(query, constants);

		while (result.hasNext()) {

			bindings.add(getSingleBoundURI(result.next()));
		}

		result.close();

		return bindings;
	}

	OStardogQuery(Connection connection) {

		this.connection = connection;
	}

	private OT_URI getSingleBoundURI(BindingSet bindings) {

		IRI boundURI = (IRI)bindings.iterator().next().getValue();

		return new OT_URI(boundURI.toString());
	}
}
