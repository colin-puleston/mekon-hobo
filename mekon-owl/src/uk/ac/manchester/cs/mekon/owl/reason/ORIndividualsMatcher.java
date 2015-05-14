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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * Extension of {@link ORMatcher} that represents the instances
 * as networks of individuals, which are added to the in-memory
 * version of the ontology, and represents the queries as
 * class-expressions.
 *
 * @author Colin Puleston
 */
public class ORIndividualsMatcher extends ORMatcher {

	private IndividualsRenderer storeRenderer;
	private IndividualsRenderer dynamicRenderer;

	/**
	 * Constructs matcher for specified model.
	 *
	 * @param model Model over which matcher is to operate
	 */
	public ORIndividualsMatcher(OModel model) {

		super(model);

		storeRenderer = createRenderer(IndividualCategory.MATCHER_NAMED);
		dynamicRenderer = createRenderer(IndividualCategory.MATCHER_ANON);
	}

	void addInstance(ORFrame instance, CIdentity identity) {

		storeRenderer.render(instance, identity.getIdentifier());
	}

	boolean removeInstance(CIdentity identity) {

		return storeRenderer.removeGroup(identity.getIdentifier());
	}

	boolean containsInstance(CIdentity identity) {

		return storeRenderer.rendered(identity.getIdentifier());
	}

	List<CIdentity> match(ConceptExpression queryExpr) {

		return purgeMatches(queryExpr.getMatchingIndividuals());
	}

	boolean matches(ConceptExpression queryExpr, ORFrame instance) {

		return createIndividualNetwork(instance).matches(queryExpr);
	}

	private IndividualsRenderer createRenderer(IndividualCategory category) {

		return new IndividualsRenderer(getModel(), category);
	}

	private List<CIdentity> purgeMatches(List<CIdentity> all) {

		List<CIdentity> purged = new ArrayList<CIdentity>();

		for (CIdentity match : all) {

			if (storeRenderer.rendered(match.getIdentifier())) {

				purged.add(match);
			}
		}

		return purged;
	}

	private IndividualNetwork createIndividualNetwork(ORFrame frame) {

		return new IndividualNetwork(getModel(), frame, dynamicRenderer);
	}
}