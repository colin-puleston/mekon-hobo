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

package uk.ac.manchester.cs.rekon;

import java.util.*;

import org.semanticweb.owlapi.model.*;

/**
 * @author Colin Puleston
 */
class DescriptionExtractor {

	private Names names;

	private ClassDescriptions classDescriptions = new ClassDescriptions();
	private ObjectSuccessors objectSuccessors = new ObjectSuccessors();
	private DataSuccessors dataSuccessors = new DataSuccessors();
	private Disjunctions disjunctions = new Disjunctions();

	private NumberRangeExtractor numberRanges = new NumberRangeExtractor();

	private boolean cacheAdditionsEnabled = true;

	private abstract class TypeExpressions<S extends OWLObject, E extends Expression> {

		private Map<S, E> cache = new HashMap<S, E>();

		E get(S source) {

			E expr = cache.get(source);

			if (expr == null) {

				expr = extract(source);

				if (cacheAdditionsEnabled) {

					cache.put(source, expr);
				}
			}

			return expr;
		}

		abstract E extract(S source);
	}

	private class ClassDescriptions extends TypeExpressions<OWLClassExpression, Description> {

		private boolean structuredOnly = false;

		Description getStructured(OWLClassExpression source) {

			if (source instanceof OWLObjectIntersectionOf) {

				Description d = get(source, true);

				if (d != null && d.structured()) {

					return d;
				}
			}

			return null;
		}

		Description getAny(OWLClassExpression source) {

			return get(source, false);
		}

		Description extract(OWLClassExpression source) {

			if (source instanceof OWLClass) {

				return new Description(names.get((OWLClass)source));
			}

			if (source instanceof OWLObjectIntersectionOf) {

				return extract((OWLObjectIntersectionOf)source);
			}

			return null;
		}

		private Description get(OWLClassExpression source, boolean structuredOnly) {

			this.structuredOnly = structuredOnly;

			return get(source);
		}

		private Description extract(OWLObjectIntersectionOf source) {

			Set<OWLClassExpression> ops = source.getOperands();
			OWLClass c = removeSingleNamedClass(ops);

			if (c == null || (structuredOnly && ops.isEmpty())) {

				return null;
			}

			Set<Expression> s = extractSuccessors(ops);

			return s != null ? new Description(names.get(c), s) : null;
		}

		private Set<Expression> extractSuccessors(Set<OWLClassExpression> sources) {

			Set<Expression> succs = new HashSet<Expression>();

			for (OWLClassExpression source : sources) {

				if (source instanceof OWLRestriction) {

					Expression s = extractSuccessor((OWLRestriction)source);

					if (s == null) {

						return null;
					}

					succs.add(s);
				}
				else {

					return null;
				}
			}

			return succs;
		}

		private Expression extractSuccessor(OWLRestriction source) {

			if (source instanceof OWLCardinalityRestriction) {

				return extractSuccessor((OWLCardinalityRestriction)source);
			}

			if (source instanceof OWLObjectSomeValuesFrom) {

				return extractObjectSuccessor(source);
			}

			if (source instanceof OWLDataSomeValuesFrom) {

				return extractDataSuccessor(source);
			}

			return null;
		}

		private Expression extractSuccessor(OWLCardinalityRestriction<?> source) {

			if (source.getCardinality() != 0) {

				if (source instanceof OWLObjectExactCardinality
					|| source instanceof OWLObjectMinCardinality) {

					return extractObjectSuccessor(source);
				}

				if (source instanceof OWLDataExactCardinality
					|| source instanceof OWLDataMinCardinality) {

					return extractDataSuccessor(source);
				}
			}

			return null;
		}

		private Description extractObjectSuccessor(OWLRestriction source) {

			return objectSuccessors.get((OWLQuantifiedObjectRestriction)source);
		}

		private Description extractDataSuccessor(OWLRestriction source) {

			return dataSuccessors.get((OWLQuantifiedDataRestriction)source);
		}

		private OWLClass removeSingleNamedClass(Set<OWLClassExpression> ops) {

			OWLClass named = null;

			for (OWLClassExpression op : ops) {

				if (op instanceof OWLClass) {

					if (named != null) {

						return null;
					}

					named = (OWLClass)op;
					ops.remove(op);
				}
			}

			return named;
		}
	}

	private class ObjectSuccessors
					extends
						TypeExpressions<OWLQuantifiedObjectRestriction, Description> {

		Description extract(OWLQuantifiedObjectRestriction source) {

			OWLObjectPropertyExpression expr = source.getProperty();

			if (expr instanceof OWLObjectProperty) {

				NameExpression s = extractNameExpression(source.getFiller());

				if (s != null) {

					return new Description(names.get((OWLObjectProperty)expr), s);
				}
			}

			return null;
		}

		private NameExpression extractNameExpression(OWLClassExpression source) {

			if (source instanceof OWLObjectUnionOf) {

				return disjunctions.get((OWLObjectUnionOf)source);
			}

			return classDescriptions.getAny(source);
		}
	}

	private class DataSuccessors
					extends
						TypeExpressions<OWLQuantifiedDataRestriction, Description> {

		Description extract(OWLQuantifiedDataRestriction source) {

			OWLDataPropertyExpression expr = source.getProperty();

			if (expr instanceof OWLDataProperty) {

				NumberRange s = numberRanges.extract(source.getFiller());

				if (s != null) {

					return new Description(names.get((OWLDataProperty)expr), s);
				}
			}

			return null;
		}
	}

	private class Disjunctions extends TypeExpressions<OWLObjectUnionOf, Disjunction> {

		Disjunction extract(OWLObjectUnionOf source) {

			Set<Description> disjuncts = new HashSet<Description>();

			for (OWLClassExpression op : source.getOperands()) {

				Description d = classDescriptions.getAny(op);

				if (d == null) {

					return null;
				}

				disjuncts.add(d);
			}

			return new Disjunction(disjuncts);
		}
	}

	DescriptionExtractor(Names names) {

		this.names = names;
	}

	void setCacheAdditionsEnabled(boolean value) {

		cacheAdditionsEnabled = value;

		numberRanges.setCacheAdditionsEnabled(value);
	}

	Set<Description> extractStructuredDescriptions(Collection<OWLClassExpression> sources) {

		Set<Description> descs = new HashSet<Description>();

		for (OWLClassExpression e : sources) {

			Description d = extractStructuredDescription(e);

			if (d != null) {

				descs.add(d);
			}
		}

		return descs;
	}

	Description extractStructuredDescription(OWLClassExpression source) {

		return classDescriptions.getStructured(source);
	}
}
