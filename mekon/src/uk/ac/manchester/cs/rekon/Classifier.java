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
class Classifier {

	private Assertions assertions;
	private Names names;
	private Descriptions extractor;

	private ClassDefinition rootClassDefinition = new ClassDefinition();
	private SortedSet<ClassDefinition> classDefinitions = new TreeSet<ClassDefinition>();

	private Set<Description> classDescriptions = new HashSet<Description>();

	private class ClassDefinitionsInitialiser {

		private Set<Name> referencedNames = new HashSet<Name>();

		ClassDefinitionsInitialiser() {

			extractDefinitions();
			setActiveAncestors();
		}

		private void extractDefinitions() {

			for (ClassName name : names.getAllClassNames()) {

				extractDefinitions(name);
			}
		}

		private void setActiveAncestors() {

			for (ClassDefinition d : classDefinitions) {

				d.getDefinition().setActiveAncestors(referencedNames);
			}
		}

		private void extractDefinitions(ClassName name) {

			for (Description d : extractDefinitions(name.getCls())) {

				classDefinitions.add(new ClassDefinition(name, d));
				referencedNames.addAll(d.getReferencedNames().getSet());
			}
		}

		private Set<Description> extractDefinitions(OWLClass cls) {

			Collection<OWLClassExpression> eqs = assertions.getDistictEquivalents(cls);

			return extractor.toStructuredDescriptions(eqs);
		}
	}

	private class ClassDescriptionsInitialiser {

		private Map<Name, Description> finder = new HashMap<Name, Description>();
		private Set<Description> resolvedSuccessors = new HashSet<Description>();

		ClassDescriptionsInitialiser() {

			extractDescriptions();
			resolveSuccessors();
		}

		private void extractDescriptions() {

			for (ClassName name : names.getAllClassNames()) {

				checkExtractDescription(name.getCls());
			}
		}

		private void resolveSuccessors() {

			for (Description d : classDescriptions) {

				resolveSuccessors(d);
			}
		}

		private void checkExtractDescription(OWLClass cls) {

			Description d = extractDescription(cls);

			if (d != null) {

				classDescriptions.add(d);
				finder.put(d.getName(), d);
			}
		}

		private Description extractDescription(OWLClass cls) {

			Collection<OWLClassExpression> sups = assertions.getSupers(cls);
			Set<Description> descs = extractor.toStructuredDescriptions(sups);

			return descs.isEmpty() ? null : new Description(names.get(cls), descs);
		}

		private Set<Expression> resolveSuccessors(Description desc) {

			if (resolvedSuccessors.add(desc)) {

				for (Name an : desc.getName().getAncestors()) {

					desc.addSuccessors(resolveSuccessors(finder.get(an)));
				}
			}

			return desc.getSuccessors();
		}
	}

	private class ClassificationPass {

		private Collection<ClassDefinition> classifiables;
		private NameSet updateds = new NameSet();

		ClassificationPass(Collection<ClassDefinition> classifiables) {

			this.classifiables = classifiables;
		}

		Collection<ClassDefinition> perfomPass() {

			absorbClassifiables();

			inferSubsumptionsFromClassifiables();
			inferSupersFromClassDescriptions();

			return removeReclassifiables();
		}

		private void absorbClassifiables() {

			for (ClassDefinition d : classifiables) {

				rootClassDefinition.absorb(d);
			}
		}

		private void inferSubsumptionsFromClassifiables() {

			for (ClassDefinition d : classifiables) {

				d.inferSubsumptionsFromClassDefinition(updateds);
			}
		}

		private void inferSupersFromClassDescriptions() {

			for (Description d : classDescriptions) {

				rootClassDefinition.inferSupersFromClassDescription(d, updateds);
			}
		}

		private Collection<ClassDefinition> removeReclassifiables() {

			List<ClassDefinition> reclassifiables = new ArrayList<ClassDefinition>();

			for (ClassDefinition d : classDefinitions) {

				if (d.removeReclassifiable(updateds)) {

					reclassifiables.add(d);
				}
			}

			return reclassifiables;
		}
	}

	Classifier(Assertions assertions, Names names) {

		this.assertions = assertions;
		this.names = names;

		extractor = new Descriptions(names);

		new ClassDefinitionsInitialiser();
		new ClassDescriptionsInitialiser();

		perfomClassifications();
	}

	Set<ClassName> getEquivalents(OWLClassExpression expr) {

		Description d = extractor.toStructuredDescription(expr);

		if (d != null) {

			return rootClassDefinition.getEquivalents(d);
		}

		return Collections.emptySet();
	}

	Set<ClassName> getSupers(OWLClassExpression expr, boolean directOnly) {

		Description d = extractor.toStructuredDescription(expr);

		if (d != null) {

			return rootClassDefinition.getSupers(d, directOnly);
		}

		return Collections.emptySet();
	}

	private void perfomClassifications() {

		Collection<ClassDefinition> classifiables = classDefinitions;

		while (true) {

			classifiables = new ClassificationPass(classifiables).perfomPass();

			if (classifiables.isEmpty()) {

				break;
			}

			resetClassDefinitionNameReferences();
			resetClassDescriptionNameReferences();
		}

		names.resolveAllLinksPostClassification();
		extractor.setCacheAdditionsEnabled(false);
	}

	private void resetClassDefinitionNameReferences() {

		for (ClassDefinition d : classDefinitions) {

			d.getDefinition().resetNameReferences();
		}
	}

	private void resetClassDescriptionNameReferences() {

		for (Description d : classDescriptions) {

			d.resetNameReferences();
		}
	}
}
