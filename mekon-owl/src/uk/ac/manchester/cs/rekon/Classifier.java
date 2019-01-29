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

	private Names names;
	private Descriptions descriptions;

	private ActiveClasses activeClasses = new ActiveClasses();

	private class ClassificationPass {

		private Collection<ActiveClass> classifiables;
		private NameSet updateds = new NameSet();

		ClassificationPass(Collection<ActiveClass> classifiables) {

			this.classifiables = classifiables;
		}

		Collection<ActiveClass> perfomPass() {

			absorbClassifiables();
			setNewNameSubsumptions();

			return removeReclassifiables();
		}

		private void absorbClassifiables() {

			for (ActiveClass c : classifiables) {

				activeClasses.absorbIntoHierarchy(c);
			}
		}

		private void setNewNameSubsumptions() {

			for (ActiveClass c : classifiables) {

				c.setNameSubsumptions(updateds);
			}
		}

		private Collection<ActiveClass> removeReclassifiables() {

			List<ActiveClass> reclassifiables = new ArrayList<ActiveClass>();

			for (ActiveClass c : activeClasses.getAll()) {

				if (c.removeReclassifiable(updateds)) {

					reclassifiables.add(c);
				}
			}

			return reclassifiables;
		}
	}

	Classifier(Assertions assertions, Names names) {

		this.names = names;

		descriptions = new Descriptions(names);

		initialiseActiveClasses(assertions);
		setActiveNames();
		perfomClassifications();
	}

	Set<ClassName> getEquivalents(OWLClassExpression expr) {

		Description d = descriptions.toStructure(expr);

		if (d != null) {

			return activeClasses.getEquivalents(d);
		}

		return Collections.emptySet();
	}

	Set<ClassName> getSupers(OWLClassExpression expr, boolean directOnly) {

		Description d = descriptions.toStructure(expr);

		if (d != null) {

			return activeClasses.getSupers(d, directOnly);
		}

		return Collections.emptySet();
	}

	private void initialiseActiveClasses(Assertions assertions) {

		for (ClassName name : names.getAllClassNames()) {

			OWLClass cls = name.getCls();
			Set<Description> defns = getDefinitions(assertions, cls);
			Set<Description> sups = getAssertedSupers(assertions, cls);

			activeClasses.addFor(name, defns, sups);
		}
	}

	private void setActiveNames() {

		Set<Name> activeNames = new HashSet<Name>();

		for (ActiveClass c : activeClasses.getAll()) {

			activeNames.addAll(c.getAllNames());
		}

		for (ActiveClass c : activeClasses.getAll()) {

			c.setActiveNames(activeNames);
		}
	}

	private void perfomClassifications() {

		Collection<ActiveClass> classifiables = activeClasses.getAll();

		while (true) {

			classifiables = new ClassificationPass(classifiables).perfomPass();

			if (classifiables.isEmpty()) {

				break;
			}

			resetNameActiveClassReferences();
		}

		names.resolveAllLinksPostClassification();
		descriptions.setCacheAdditionsEnabled(false);
	}

	private void resetNameActiveClassReferences() {

		for (ActiveClass c : activeClasses.getAll()) {

			c.resetNameReferences();
		}
	}

	private Set<Description> getDefinitions(Assertions assertions, OWLClass cls) {

		return descriptions.toStructures(assertions.getDistictEquivalents(cls));
	}

	private Set<Description> getAssertedSupers(Assertions assertions, OWLClass cls) {

		return descriptions.toSuccessors(assertions.getSupers(cls));
	}
}
