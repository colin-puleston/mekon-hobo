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

package uk.ac.manchester.cs.mekon.owl.jena;

import java.util.*;

import org.apache.jena.rdf.model.*;

import uk.ac.manchester.cs.mekon.owl.triples.*;

/**
 * @author Colin Puleston
 */
class OJenaGraphRemover implements OTGraphRemover {

	private Model model;
	private Resource context;

	public void removeGraphFromStore() {

		for (Statement statement : getStatements()) {

			removeStatement(statement);
		}
	}

	OJenaGraphRemover(Model model, String contextURI) {

		this.model = model;

		context = model.createResource(contextURI);
	}

	private List<Statement> getStatements() {

		StmtIterator iterator = getStatementIterator();
		List<Statement> statements = iterator.toList();

		iterator.close();

		return statements;
	}

	private void removeStatement(Statement cxtStatement) {

		model.remove(cxtStatement);
		model.remove(getReifiedStatement(cxtStatement).getStatement());
	}

	private StmtIterator getStatementIterator() {

		return model.listStatements(context, null, (RDFNode)null);
	}

	private ReifiedStatement getReifiedStatement(Statement cxtStatement) {

		return cxtStatement.getObject().as(ReifiedStatement.class);
	}
}
