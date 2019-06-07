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
class Descriptions {

	private Names names;

	private ClassDescriptions classDescriptions = new ClassDescriptions();
	private ObjectSuccessors objectSuccessors = new ObjectSuccessors();
	private DataSuccessors dataSuccessors = new DataSuccessors();
	private Disjunctions disjunctions = new Disjunctions();

	private DataTypes dataTypes = new DataTypes();

	private boolean cacheAdditionsEnabled = true;

	private abstract class TypeExpressions<S extends OWLObject, E extends Expression> {

		private Map<S, E> cache = new HashMap<S, E>();

		E get(S source) {

			E expr = cache.get(source);

			if (expr == null) {

				expr = create(source);

				if (cacheAdditionsEnabled) {

					cache.put(source, expr);
				}
			}

			return expr;
		}

		abstract E create(S source);
	}

	private class ClassDescriptions extends TypeExpressions<OWLClassExpression, Description> {

		private boolean structuredOnly = false;

		Description getStructured(OWLClassExpression source) {

			Description d = get(source, true);

			return d != null && d.structured() ? d : null;
		}

		Description getAny(OWLClassExpression source) {

			return get(source, false);
		}

		Description create(OWLClassExpression source) {

			if (source instanceof OWLClass) {

				return new Description(names.get((OWLClass)source));
			}

			if (source instanceof OWLObjectIntersectionOf) {

				return create((OWLObjectIntersectionOf)source);
			}

			return createAnonymous(source);
		}

		private Description get(OWLClassExpression source, boolean structuredOnly) {

			this.structuredOnly = structuredOnly;

			return get(source);
		}

		private Description create(OWLObjectIntersectionOf source) {

			Set<OWLClassExpression> ops = source.getOperands();
			Set<Name> ns = extractClassNames(ops);

			if (structuredOnly && ns.size() == 1 && ops.isEmpty()) {

				return null;
			}

			return new Description(ns, toSuccessors(ops));
		}

		private Description createAnonymous(OWLClassExpression source) {

			if (source instanceof OWLRestriction) {

				Expression s = toSuccessor((OWLRestriction)source);

				if (s != null) {

					return new Description(ClassName.THING, s);
				}
			}

			return null;
		}
	}

	private class ObjectSuccessors
					extends
						TypeExpressions<OWLQuantifiedObjectRestriction, Description> {

		private boolean complement = false;

		Description get(OWLQuantifiedObjectRestriction source, boolean complement) {

			this.complement = complement;

			return get(source);
		}

		Description create(OWLQuantifiedObjectRestriction source) {

			OWLObjectPropertyExpression expr = source.getProperty();

			if (expr instanceof OWLObjectProperty) {

				Expression s = createSuccessor(source);

				if (s != null) {

					return new Description(names.get((OWLObjectProperty)expr), s);
				}
			}

			return null;
		}

		private Expression createSuccessor(OWLQuantifiedObjectRestriction source) {

			OWLClassExpression filler = source.getFiller();

			if (complement) {

				return filler.isOWLThing() ? Nothing.SINGLETON : null;
			}

			if (source instanceof OWLObjectAllValuesFrom) {

				return filler.isOWLNothing() ? Nothing.SINGLETON : null;
			}

			if (filler instanceof OWLObjectUnionOf) {

				return disjunctions.get((OWLObjectUnionOf)filler);
			}

			return classDescriptions.getAny(filler);
		}
	}

	private class DataSuccessors extends TypeExpressions<OWLDataRestriction, Description> {

		Description create(OWLDataRestriction source) {

			OWLDataPropertyExpression expr = source.getProperty();

			if (expr instanceof OWLDataProperty) {

				Expression v = toDataValue(source);

				if (v != null) {

					return new Description(names.get((OWLDataProperty)expr), v);
				}
			}

			return null;
		}
	}

	private class Disjunctions extends TypeExpressions<OWLObjectUnionOf, Disjunction> {

		Disjunction create(OWLObjectUnionOf source) {

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

	Descriptions(Names names) {

		this.names = names;
	}

	void setCacheAdditionsEnabled(boolean value) {

		cacheAdditionsEnabled = value;

		dataTypes.setCacheAdditionsEnabled(value);
	}

	Set<Description> toStructures(Collection<OWLClassExpression> sources) {

		Set<Description> descs = new HashSet<Description>();

		for (OWLClassExpression s : sources) {

			Description d = toStructure(s);

			if (d != null) {

				descs.add(d);
			}
		}

		return descs;
	}

	Description toStructure(OWLClassExpression source) {

		return classDescriptions.getStructured(source);
	}

	Set<Description> toSuccessors(Collection<OWLClassExpression> sources) {

		Set<Description> succs = new HashSet<Description>();

		for (OWLClassExpression source : sources) {

			Description s = toSuccessor(source);

			if (s != null) {

				succs.add(s);
			}
		}

		return succs;
	}

	Description toSuccessor(OWLClassExpression source) {

		return toSuccessor(source, false);
	}

	private Description toSuccessor(OWLClassExpression source, boolean complement) {

		if (source instanceof OWLObjectComplementOf) {

			return toSuccessor((OWLObjectComplementOf)source, complement);
		}

		if (source instanceof OWLCardinalityRestriction) {

			return toSuccessor((OWLCardinalityRestriction)source, complement);
		}

		if (source instanceof OWLQuantifiedObjectRestriction) {

			return toObjectSuccessor(source, complement);
		}

		if (source instanceof OWLDataSomeValuesFrom || source instanceof OWLDataHasValue) {

			return toDataSuccessor(source);
		}

		return null;
	}

	private Description toSuccessor(OWLObjectComplementOf source, boolean wasComplement) {

		return toSuccessor(source.getOperand(), !wasComplement);
	}

	private Description toSuccessor(OWLCardinalityRestriction<?> source, boolean complement) {

		if (source.getCardinality() != 0) {

			if (source instanceof OWLObjectExactCardinality
				|| source instanceof OWLObjectMinCardinality) {

				return toObjectSuccessor(source, complement);
			}

			if (!complement) {

				if (source instanceof OWLDataExactCardinality
					|| source instanceof OWLDataMinCardinality) {

					return toDataSuccessor(source);
				}
			}
		}

		return null;
	}

	private Description toObjectSuccessor(OWLClassExpression source, boolean complement) {

		return objectSuccessors.get((OWLQuantifiedObjectRestriction)source, complement);
	}

	private Description toDataSuccessor(OWLClassExpression source) {

		return dataSuccessors.get((OWLDataRestriction)source);
	}

	private Set<Name> extractClassNames(Set<OWLClassExpression> ops) {

		Set<Name> ns = new HashSet<Name>();

		for (OWLClassExpression op : new HashSet<OWLClassExpression>(ops)) {

			if (op instanceof OWLClass) {

				ns.add(names.get((OWLClass)op));
				ops.remove(op);
			}
		}

		if (ns.isEmpty()) {

			ns.add(ClassName.THING);
		}

		return ns;
	}

	private Expression toDataValue(OWLDataRestriction source) {

		if (source instanceof OWLQuantifiedDataRestriction) {

			return toDataType((OWLQuantifiedDataRestriction)source);
		}

		if (source instanceof OWLDataHasValue) {

			return toDataValue((OWLDataHasValue)source);
		}

		throw new Error("Unexpected OWLDataRestriction type");
	}

	private Expression toDataType(OWLQuantifiedDataRestriction source) {

		return dataTypes.getFor(source.getFiller());
	}

	private Expression toDataValue(OWLDataHasValue source) {

		OWLLiteral v = source.getFiller();

		if (v.isBoolean()) {

			return BooleanValue.valueFor(v.parseBoolean());
		}

		if (v.isInteger()) {

			return new IntegerRange(v.parseInteger());
		}

		if (v.isFloat()) {

			return new FloatRange(v.parseFloat());
		}

		if (v.isDouble()) {

			return new DoubleRange(v.parseDouble());
		}

		return null;
	}
}