/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 University of Manchester
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

package uk.ac.manchester.cs.goblin.gui;

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class ConceptTree extends GSelectorTree {

	static private final long serialVersionUID = -1;

	static Concept extractConcept(GNode selectedNode) {

		return extractConcept((ConceptTreeNode)selectedNode);
	}

	static private Concept extractConcept(ConceptTreeNode selectedNode) {

		return selectedNode != null ? selectedNode.getConceptOrNull() : null;
	}

	private Set<Concept> rootConcepts;

	private abstract class ConceptTreeNode extends GNode {

		protected boolean orderedChildren() {

			return true;
		}

		ConceptTreeNode() {

			super(ConceptTree.this);
		}

		void redisplayAllConstraints(boolean parentWasCollapsed) {

			boolean wasCollapsed = collapsed();

			for (GNode child : getChildren()) {

				((ConceptTreeNode)child).redisplayAllConstraints(wasCollapsed);
			}

			boolean hasConstraintChildren = redisplayConstraints();

			if (parentWasCollapsed || (wasCollapsed && !hasConstraintChildren)) {

				collapse();
			}
		}

		boolean redisplayConstraints() {

			return false;
		}

		void addChildrenFor(Set<Concept> concepts) {

			for (Concept concept : concepts) {

				if (requiredConcept(concept)) {

					addChildFor(concept);
				}
			}
		}

		void addChildFor(Concept concept) {

			addChild(new ConceptNode(concept));
		}

		Concept getConceptOrNull() {

			return null;
		}

		ConceptNode findDescendantNode(Concept forConcept) {

			for (GNode child : getChildren()) {

				if (child instanceof ConceptNode) {

					ConceptNode found = ((ConceptNode)child).findNode(forConcept);

					if (found != null) {

						return found;
					}
				}
			}

			return null;
		}
	}

	private class RootNode extends ConceptTreeNode {

		private Set<Concept> rootConcepts;

		protected void addInitialChildren() {

			addChildrenFor(rootConcepts);
		}

		protected GCellDisplay getDisplay() {

			return GCellDisplay.NO_DISPLAY;
		}

		RootNode(Set<Concept> rootConcepts) {

			this.rootConcepts = rootConcepts;
		}
	}

	private class ConceptNode extends ConceptTreeNode {

		private Concept concept;

		private class ModelUpdateTracker implements ConceptListener {

			public void onChildAdded(Concept child, boolean replacement) {

				ConceptTreeNode parentNode = findParentNodeFor(child);

				if (parentNode != null) {

					parentNode.addChildFor(child);
				}
			}

			public void onConstraintAdded(Constraint constraint) {

				onConstraintChange();
			}

			public void onConstraintRemoved(Constraint constraint) {

				onConstraintChange();
			}

			public void onConceptRemoved(Concept concept, boolean replacing) {

				remove();
			}

			ModelUpdateTracker() {

				concept.addListener(this);
			}
		}

		protected void addInitialChildren() {

			addChildrenFor(concept.getChildren());
			addConstraintChildren();
		}

		protected int compareChildrenPriorToLabelCompare(GNode first, GNode second) {

			boolean firstIsConcept = first instanceof ConceptNode;
			boolean secondIsConcept = second instanceof ConceptNode;

			if (firstIsConcept == secondIsConcept) {

				return 0;
			}

			return firstIsConcept ? -1 : 1;
		}

		protected GCellDisplay getDisplay() {

			return getConceptDisplay(concept);
		}

		ConceptNode(Concept concept) {

			this.concept = concept;

			new ModelUpdateTracker();
		}

		boolean redisplayConstraints() {

			return addConstraintChildren();
		}

		Concept getConceptOrNull() {

			return concept;
		}

		ConceptNode findNode(Concept forConcept) {

			return concept.equals(forConcept) ? this : findDescendantNode(forConcept);
		}

		private boolean addConstraintChildren() {

			boolean anyAdded = false;

			for (Constraint constraint : concept.getConstraints()) {

				if (showConstraint(constraint)) {

					addChild(new ConstraintNode(this, constraint));

					anyAdded = true;
				}
			}

			return anyAdded;
		}
	}

	private class ConstraintNode extends ConceptTreeNode {

		private GCellDisplay display;

		private class Deselector extends GSelectionListener<GNode> {

			private ConceptNode parentNode;

			protected void onSelected(GNode node) {

				if (node == ConstraintNode.this) {

					parentNode.select();
				}
			}

			protected void onDeselected(GNode node) {
			}

			Deselector(ConceptNode parentNode) {

				this.parentNode = parentNode;

				addNodeSelectionListener(this);
			}
		}

		protected GCellDisplay getDisplay() {

			return display;
		}

		ConstraintNode(ConceptNode parentNode, Constraint constraint) {

			display = GoblinCellDisplay.CONCEPTS_CONSTRAINT.forConstraint(constraint);

			new Deselector(parentNode);
		}

		boolean redisplayConstraints() {

			remove();

			return false;
		}
	}

	ConceptTree(boolean multiSelect) {

		super(multiSelect);

		setRootVisible(false);
		setShowsRootHandles(true);
	}

	void initialise(Concept rootConcept) {

		initialise(Collections.singleton(rootConcept));
	}

	void initialise(Set<Concept> rootConcepts) {

		initialise(new RootNode(rootConcepts));

		this.rootConcepts = rootConcepts;
	}

	void redisplayConstraints() {

		getConceptTreeRootNode().redisplayAllConstraints(false);
	}

	boolean showConstraint(Constraint constraint) {

		throw new Error("Method should never be invoked!");
	}

	Set<Concept> getRootConcepts() {

		return rootConcepts;
	}

	Concept getSelectedConcept() {

		return extractConcept(getSelectedNode());
	}

	List<Concept> getAllSelectedConcepts() {

		List<Concept> selectedConcepts = new ArrayList<Concept>();

		for (GNode node : getAllSelectedNodes()) {

			selectedConcepts.add(extractConcept(node));
		}

		return selectedConcepts;
	}

	void selectConcept(Concept concept) {

		ConceptTreeNode node = lookForNodeFor(concept);

		if (node != null) {

			node.select();
		}
	}

	void selectConcepts(Collection<Concept> concepts) {

		List<ConceptTreeNode> nodes = new ArrayList<ConceptTreeNode>();

		for (Concept concept : concepts) {

			ConceptTreeNode node = lookForNodeFor(concept);

			if (node != null) {

				nodes.add(node);
			}
		}

		selectAll(nodes);
	}

	boolean requiredConcept(Concept concept) {

		return true;
	}

	abstract GCellDisplay getConceptDisplay(Concept concept);

	abstract void onConstraintChange();

	private ConceptTreeNode lookForNodeFor(Concept concept) {

		return getConceptTreeRootNode().findDescendantNode(concept);
	}

	private ConceptTreeNode findParentNodeFor(Concept concept) {

		ConceptTreeNode root = getConceptTreeRootNode();

		return concept.isRoot() ? root : root.findDescendantNode(concept.getParent());
	}

	private ConceptTreeNode getConceptTreeRootNode() {

		return (ConceptTreeNode)getRootNode();
	}
}
