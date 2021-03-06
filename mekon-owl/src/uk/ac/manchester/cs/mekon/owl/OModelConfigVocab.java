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

package uk.ac.manchester.cs.mekon.owl;

/**
 * Vocabulary used in the {@link OModel}-definition section
 * of the MEKON configuration file.
 *
 * @author Colin Puleston
 */
public interface OModelConfigVocab {

	static public final String ROOT_ID = "OWLModel";

	static public final String SOURCE_FILE_ATTR = "owlFile";
	static public final String REASONER_FACTORY_CLASS_ATTR = "reasonerFactory";
	static public final String REASONING_TYPE_ATTR = "reasoningType";
	static public final String INDIRECT_NUMERIC_PROPERTY_URI_ATTR = "indirectNumericProperty";
	static public final String INSTANCE_ONTOLOGY_URI_ATTR = "instanceOntology";
}