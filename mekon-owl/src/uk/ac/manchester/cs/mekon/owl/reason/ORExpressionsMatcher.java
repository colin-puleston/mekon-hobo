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

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * Extension of {@link ORMatcher} that represents the instances
 * as a set of anonymous class-expressions, and the queries also
 * as anonymous class-expressions.
 *
 * @author Colin Puleston
 */
public class ORExpressionsMatcher extends OROntologyBasedMatcher {

	private Map<OWLClass, InstanceGroup> instanceGroups
					= new HashMap<OWLClass, InstanceGroup>();

	private class InstanceGroup {

		private OWLClass frameConcept;
		private Map<IRI, ConceptExpression> instanceExprs
						= new HashMap<IRI, ConceptExpression>();

		InstanceGroup(OWLClass frameConcept) {

			this.frameConcept = frameConcept;
		}

		void add(ORFrame instance, IRI iri) {

			instanceExprs.put(iri, createConceptExpression(instance));
		}

		boolean checkRemove(IRI iri) {

			return instanceExprs.remove(iri) != null;
		}

		void collectMatches(ConceptExpression queryExpr, List<IRI> matches) {

			if (isSubsumption(queryExpr.getFrameConcept(), frameConcept)) {

				for (IRI iri : instanceExprs.keySet()) {

					if (queryExpr.subsumes(instanceExprs.get(iri))) {

						matches.add(iri);
					}
				}
			}
		}
	}

	/**
	 * Constructs matcher for specified model with the default
	 * reasoning-type, which is {@link ORReasoningType#DL}.
	 *
	 * @param model Model over which matcher is to operate
	 */
	public ORExpressionsMatcher(OModel model) {

		super(model);
	}

	/**
	 * Constructs matcher for specified model and reasoning-type.
	 *
	 * @param model Model over which matcher is to operate
	 * @param reasoningType Required reasoning-type for matching
	 */
	public ORExpressionsMatcher(OModel model, ORReasoningType reasoningType) {

		super(model, reasoningType);
	}

	/**
	 * Constructs matcher, with the configuration for both the
	 * matcher itself, and the model over which it is to operate,
	 * defined via the appropriately-tagged child of the specified
	 * parent configuration-node.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	public ORExpressionsMatcher(KConfigNode parentConfigNode) {

		super(parentConfigNode);
	}

	/**
	 * Constructs matcher for specified model, with the configuration
	 * defined via the appropriately-tagged child of the specified parent
	 * configuration-node.
	 *
	 * @param model Model over which matcher is to operate
	 * @param parentConfigNode Parent configuration-node
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	public ORExpressionsMatcher(OModel model, KConfigNode parentConfigNode) {

		super(model, parentConfigNode);
	}

	/**
	 */
	protected void add(ORFrame instance, IRI iri) {

		OWLClass frameConcept = getConcept(instance);
		InstanceGroup group = instanceGroups.get(frameConcept);

		if (group == null) {

			group = new InstanceGroup(frameConcept);
			instanceGroups.put(frameConcept, group);
		}

		group.add(instance, iri);
	}

	/**
	 */
	protected void remove(IRI iri) {

		for (InstanceGroup group : instanceGroups.values()) {

			if (group.checkRemove(iri)) {

				break;
			}
		}
	}

	boolean matcherModifiesOntology() {

		return false;
	}

	List<IRI> match(ConceptExpression queryExpr) {

		List<IRI> matches = new ArrayList<IRI>();

		for (InstanceGroup group : instanceGroups.values()) {

			group.collectMatches(queryExpr, matches);
		}

		return matches;
	}

	boolean matches(ConceptExpression queryExpr, ORFrame instance) {

		return queryExpr.subsumes(createConceptExpression(instance));
	}

	private ConceptExpression createConceptExpression(ORFrame frame) {

		return new ConceptExpression(getMatcherModel(), frame);
	}

	private OWLClass getConcept(ORFrame frame) {

		return getMatcherModel().getConcepts().get(frame.getIRI());
	}

	private boolean isSubsumption(OWLClass subsumer, OWLClass subsumed) {

		return getMatcherModel().isSubsumption(subsumer, subsumed);
	}
}
