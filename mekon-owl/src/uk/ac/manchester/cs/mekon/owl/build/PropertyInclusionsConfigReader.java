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

package uk.ac.manchester.cs.mekon.owl.build;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.config.*;

/**
 * @author Colin Puleston
 */

class PropertyInclusionsConfigReader
			extends EntityGroupConfigReader<OBPropertyInclusions, OBProperties>
			implements OBSectionBuilderConfigVocab {

	PropertyInclusionsConfigReader(KConfigNode configNode) {

		super(configNode);
	}

	String getInclusionId() {

		return PROPERTY_INCLUSION_ID;
	}

	OBPropertyInclusions createGroup(KConfigNode groupNode, IRI rootIRI) {

		OBPropertyInclusions group = new OBPropertyInclusions(rootIRI);

		group.setMirrorAsFrames(getMirrorAsFrames(groupNode));
		group.setAbstractAssertables(getAbstractAssertables(groupNode));

		return group;
	}

	private boolean getMirrorAsFrames(KConfigNode groupNode) {

		return groupNode.getBoolean(MIRROR_PROPERTIES_AS_FRAMES_ATTR, false);
	}

	private boolean getAbstractAssertables(KConfigNode groupNode) {

		return groupNode.getBoolean(ABSTRACT_ASSERTABLES_PROPERTIES_ATTR, false);
	}
}
