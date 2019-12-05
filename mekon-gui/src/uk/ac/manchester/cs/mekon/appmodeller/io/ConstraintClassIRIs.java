package uk.ac.manchester.cs.mekon.appmodeller.io;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.appmodeller.model.*;

/**
 * @author Colin Puleston
 */
class ConstraintClassIRIs {

	static private final String NAME_FORMAT = "%s-Constraint-%d--%s-to-%s";

	private String contentNamespace;

	private Map<String, Integer> indexesByFocusName = new HashMap<String, Integer>();

	ConstraintClassIRIs(String contentNamespace) {

		this.contentNamespace = contentNamespace;
	}

	IRI generate(Constraint constraint) {

		return IRI.create(contentNamespace + '#' + generateName(constraint));
	}

	private String generateName(Constraint constraint) {

		ConstraintType type = constraint.getType();

		String focus = constraint.getFocusConceptId().getLabel();
		String source = getConceptLabel(constraint.getSourceValue());
		String target = getConceptLabel(type.getTargetLink().getValue());

		int index = nextIndex(focus);

		return String.format(NAME_FORMAT, focus, index, source, target);
	}

	private int nextIndex(String focusName) {

		Integer index = indexesByFocusName.get(focusName);

		if (index == null) {

			index = 0;
		}

		indexesByFocusName.put(focusName, ++index);

		return index;
	}

	private String getConceptLabel(Concept concept) {

		return concept.getConceptId().getLabel();
	}
}
