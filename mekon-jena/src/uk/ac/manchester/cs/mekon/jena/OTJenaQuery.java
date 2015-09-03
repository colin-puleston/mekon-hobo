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

package uk.ac.manchester.cs.mekon.jena;

import java.util.*;

import org.apache.jena.rdf.model.*;
import org.apache.jena.query.*;

import uk.ac.manchester.cs.mekon.owl.reason.triples.*;

/**
 * @author Colin Puleston
 */
class OTJenaQuery implements OTQuery {

	private Model model;
	private OTJenaQueryConstants constants;

	public OTQueryConstants getConstants() {

		return constants;
	}

	public boolean executeAsk(String query) {

		return createExecution(query).execAsk();
	}

	public List<List<OTValue>> executeSelect(String query) {

		List<List<OTValue>> bindingSets = new ArrayList<List<OTValue>>();
		ResultSet resultSet = createExecution(query).execSelect();

		while (resultSet.hasNext()) {

			bindingSets.add(getBindingSet(resultSet.next()));
		}

		return bindingSets;
	}

	OTJenaQuery(Model model) {

		this.model = model;

		constants = new OTJenaQueryConstants(model);
	}

	private QueryExecution createExecution(String query) {

		return createExecution(QueryFactory.create(query));
	}

	private QueryExecution createExecution(Query query) {

		return QueryExecutionFactory.create(query, model, constants.getMap());
	}

	private List<OTValue> getBindingSet(QuerySolution solution) {

		List<OTValue> bindingSet = new ArrayList<OTValue>();
		Iterator<String> vars = solution.varNames();

		while (vars.hasNext()) {

			RDFNode binding = solution.get(vars.next());

			bindingSet.add(new OTJenaValue(binding));
		}

		return bindingSet;
	}
}
