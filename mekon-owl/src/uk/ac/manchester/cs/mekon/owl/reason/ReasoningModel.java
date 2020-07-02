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

package uk.ac.manchester.cs.mekon.owl.reason;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon_util.config.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class ReasoningModel {

	private OModel model;
	private OModel sourceModel;

	private ORSemantics semantics = new ORSemantics();
	private OntologyEntityResolver ontologyEntities;

	ReasoningModel(OModel model) {

		setModel(model);

		sourceModel = model;
	}

	void setModel(OModel model) {

		this.model = model;

		ontologyEntities = new OntologyEntityResolver(model);
	}

	void setSemantics(ORSemantics semantics) {

		this.semantics = semantics;
	}

	void ensureLocalModel() {

		if (model == sourceModel) {

			setModel(copyModel());
		}
	}

	OModel getModel() {

		return model;
	}

	ORSemantics getSemantics() {

		return semantics;
	}

	boolean canResolveOntologyEntities(CFrame rootType) {

		return ontologyEntities.canResolve(rootType);
	}

	void resolveOntologyEntities(NNode rootNode) {

		ontologyEntities.resolve(rootNode);
	}

	private OModel copyModel() {

		return new OModelCopier(model).create(true);
	}
}
