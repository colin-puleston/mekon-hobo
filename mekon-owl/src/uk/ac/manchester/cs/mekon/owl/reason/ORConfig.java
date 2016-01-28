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

import java.net.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
abstract class ORConfig implements ORConfigVocab {

	private ReasoningModel reasoningModel;
	private KConfigNode configNode;

	ORConfig(ReasoningModel reasoningModel, KConfigNode parentConfigNode) {

		this.reasoningModel = reasoningModel;

		configNode = parentConfigNode.getChild(getRootId());

		checkUpdateReasoning();
		checkSetSemantics();
		checkEnableLogging();
	}

	abstract String getRootId();

	abstract ORLogger getLogger();

	private void checkUpdateReasoning() {

		OModel model = reasoningModel.getModel();
		OModelCopier copier = new OModelCopier(model);

		boolean update = false;

		update |= checkSetReasoner(model, copier);
		update |= checkSetReasoningType(model, copier);
		update |= checkSetInstanceOntologyIRI(model, copier);

		if (update) {

			reasoningModel.setModel(copier.create(true));
		}
	}

	private boolean checkSetReasoner(OModel model, OModelCopier copier) {

		Class<? extends OWLReasonerFactory> type = getReasonerClassOrNull();

		if (type != null && type != model.getReasonerFactory().getClass()) {

			copier.setReasoner(type);

			return true;
		}

		return false;
	}

	private boolean checkSetReasoningType(OModel model, OModelCopier copier) {

		OReasoningType type = getReasoningTypeOrNull();

		if (type != null && type != model.getReasoningType()) {

			copier.setReasoningType(type);

			return true;
		}

		return false;
	}

	private boolean checkSetInstanceOntologyIRI(OModel model, OModelCopier copier) {

		IRI iri = getInstanceOntologyIRIOrNull();

		if (iri != null && iri != getInstanceOntologyIRI(model)) {

			copier.setInstanceOntologyIRI(iri);

			return true;
		}

		return false;
	}

	private void checkSetSemantics() {

		KConfigNode node = configNode.getChildOrNull(SEMANTICS_ID);

		if (node != null) {

			setSemantics(node);
		}
	}

	private void setSemantics(KConfigNode node) {

		ORSemantics s = reasoningModel.getSemantics();

		s.setDefaultWorld(getDefaultSemantics(node));

		for (KConfigNode exNode : node.getChildren(EXCEPTION_PROP_ID)) {

			s.addExceptionProperty(getSemanticsExceptionPropURI(exNode));
		}
	}

	private void checkEnableLogging() {

		ORLoggingMode mode = getLoggingMode();

		if (mode != ORLoggingMode.DISABLED) {

			ORLogger logger = getLogger();

			logger.checkStart();

			if (mode != ORLoggingMode.TIMES_ONLY) {

				logger.setShowRequests(true);
				logger.setShowResults(true);
			}
		}
	}

	private Class<? extends OWLReasonerFactory> getReasonerClassOrNull() {

		return configNode.getClass(
				REASONER_FACTORY_CLASS_ATTR,
				OWLReasonerFactory.class,
				null);
	}

	private OReasoningType getReasoningTypeOrNull() {

		 return configNode.getEnum(
					REASONING_TYPE_ATTR,
					OReasoningType.class,
					null);
	}

	private IRI getInstanceOntologyIRIOrNull() {

		return getIRIOrNull(INSTANCE_ONTOLOGY_URI_ATTR);
	}

	private ORSemanticWorld getDefaultSemantics(KConfigNode node) {

		return node.getEnum(DEFAULT_SEMANTICS_ATTR, ORSemanticWorld.class);
	}

	private String getSemanticsExceptionPropURI(KConfigNode node) {

		return node.getString(SEMANICS_EXCEPTION_PROP_URI_ATTR);
	}

	private ORLoggingMode getLoggingMode() {

		return configNode.getEnum(LOGGING_MODE_ATTR, ORLoggingMode.class);
	}

	private IRI getIRIOrNull(String uriAttr) {

		URI uri = configNode.getURI(uriAttr, null);

		return uri != null ? IRI.create(uri) : null;
	}

	private IRI getInstanceOntologyIRI(OModel model) {

		return model.getInstanceOntology().getOntologyID().getOntologyIRI();
	}
}
