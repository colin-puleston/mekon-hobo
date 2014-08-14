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

package uk.ac.manchester.cs.mekon.owl.reason.frames;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents an entity in the pre-processable frames-based
 * instance representation.
 *
 * @author Colin Puleston
 */
public abstract class ORFramesEntity {

	private String identifier;
	private IRI iri;

	/**
	 * Sets the IRI to be used in generating the classifiable
	 * OWL expression.
	 *
	 * @param iri IRI to be used in generating classifiable OWL
	 * expression
	 */
	public void setIRI(IRI iri) {

		this.iri = iri;
	}

	/**
	 */
	public String toString() {

		return getClass().getSimpleName() + ":" + identifier;
	}

	/**
	 * Provides the identifier for the represented frames-based
	 * entity.
	 *
	 * @return Identifier for frames-based entity
	 */
	public String getIdentifier() {

		return identifier;
	}

	/**
	 * Provides the IRI to be used in generating the classifiable OWL
	 * expression. If the identifier for the frames-based entity
	 * represents a valid IRI which refers to an entity in the OWL model
	 * then that identifier will be used to provide the default IRI. The
	 * {@link #setIRI} method enables the pre-processer to override the
	 * default, or to provide an IRI for an entity that does not have a
	 * default.
	 *
	 * @return IRI to be used in generating classifiable OWL expression
	 * @throws KAccessException if the IRI has not been set
	 */
	public IRI getIRI() {

		if (iri == null) {

			throw new KAccessException(
						"Not a recognised OWL entity: "
						+ identifier);
		}

		return iri;
	}

	/**
	 * Specifies whether the represented frames-based entity maps to
	 * an entity in the OWL model, which will be the case if the
	 * IRI is currently set.
	 *
	 * @return True if frames-based entity maps to OWL entity
	 */
	public boolean mapsToOWLEntity() {

		return iri != null;
	}

	ORFramesEntity(IRI iri) {

		this(iri.toString(), iri);
	}

	ORFramesEntity(String identifier, IRI iri) {

		this.identifier = identifier;
		this.iri = iri;
	}

	ORFramesEntity(CIdentified identified, IRI iri) {

		this(identified.getIdentity().getIdentifier(), iri);
	}

	boolean equalIdentifiers(ORFramesEntity other) {

		return identifier.equals(other.identifier);
	}
}
