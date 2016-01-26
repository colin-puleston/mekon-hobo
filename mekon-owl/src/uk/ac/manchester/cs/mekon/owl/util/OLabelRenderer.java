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

package uk.ac.manchester.cs.mekon.owl.util;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.vocab.*;
import org.semanticweb.owlapi.util.*;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.*;

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * Renderer for OWL objects that uses the standard OWL-API short-form
 * renderer with the standard RDFS label attribute.
 *
 * @author Colin Puleston
 */
public class OLabelRenderer {

	static private IRI LABEL_ANNOTATION_IRI = OWLRDFVocabulary.RDFS_LABEL.getIRI();

	private OWLObjectRenderer renderer = new ManchesterOWLSyntaxOWLObjectRendererImpl();
	private boolean allowCarriageReturns = true;

	/**
	 * Constructor.
	 *
	 * @param model Model containing objects to be rendered
	 */
	public OLabelRenderer(OModel model) {

		renderer.setShortFormProvider(getShortFormProvider(model));
	}

	/**
	 * Sets the attribute that determines whether or not the renderings
	 * can contain carriage-returns (which by default they can).
	 *
	 * @param allowCarriageReturns True if carriage-returns are to be allowed
	 */
	public void setAllowCarriageReturns(boolean allowCarriageReturns) {

		this.allowCarriageReturns = allowCarriageReturns;
	}

	/**
	 * Creates rendering for specified object.
	 *
	 * @param object Object to be rendered
	 * @return appropriate rendering
	 */
	public String render(OWLObject object) {

		String rendering = renderer.render(object);

		return allowCarriageReturns
				? rendering
				: reduceRenderingToSingleLine(rendering);
	}

	/**
	 * Creates rendering for each element in set of specified object.
	 *
	 * @param objects Objects to be rendered
	 * @return Resulting set of renderings
	 */
	public Set<String> renderAll(Set<? extends OWLObject> objects) {

		return renderAll(objects, new HashSet<String>());
	}

	/**
	 * Creates rendering for each element in list of specified object.
	 *
	 * @param objects Objects to be rendered
	 * @return Resulting list of renderings
	 */
	public List<String> renderAll(List<? extends OWLObject> objects) {

		return renderAll(objects, new ArrayList<String>());
	}

	private <CO extends Collection<? extends OWLObject>,
			CS extends Collection<String>>
			CS renderAll(CO objects, CS renderings) {

		for (OWLObject object : objects) {

			renderings.add(render(object));
		}

		return renderings;
	}

	private String reduceRenderingToSingleLine(String rendering) {

		return removeExcessSpaces(rendering.replaceAll("\n", " "));
	}

	private String removeExcessSpaces(String s) {

		String last = null;

		do {

			last = s;
			s = s.replaceAll("  ", " ");
		}
		while(!s.equals(last));

		return s;
	}

	private ShortFormProvider getShortFormProvider(OModel model) {

		List<OWLAnnotationProperty> labels = getLabelAnnoProperties(model);
		OWLOntologySetProvider onts = getOntologiesProvider(model);
		Map<OWLAnnotationProperty, List<String>> langMap = Collections.emptyMap();

		return new AnnotationValueShortFormProvider(labels, langMap, onts);
	}

	private List<OWLAnnotationProperty> getLabelAnnoProperties(OModel model) {

		return Collections.singletonList(getLabelAnnoProperty(model));
	}

	private OWLAnnotationProperty getLabelAnnoProperty(OModel model) {

		return model.getDataFactory().getOWLAnnotationProperty(LABEL_ANNOTATION_IRI);
	}

	private OWLOntologySetProvider getOntologiesProvider(OModel model) {

		return new OWLOntologyImportsClosureSetProvider(
						model.getManager(),
						model.getModelOntology());
	}
}
