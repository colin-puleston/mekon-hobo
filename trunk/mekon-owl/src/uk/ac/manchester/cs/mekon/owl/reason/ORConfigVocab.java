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

/**
 * Vocabulary used in the {@link ORClassifier} and
 * {@link ORMatcher}-definition sections of the MEKON
 * configuration file.
 *
 * @author Colin Puleston
 */
public interface ORConfigVocab {

	static public final String CLASSIFIER_ROOT_ID = "OWLClassifier";
	static public final String MATCHER_ROOT_ID = "OWLMatcher";
	static public final String SEMANTICS_ID = "SlotSemantics";
	static public final String EXCEPTION_PROP_ID = "ExceptionProperty";

	static public final String DEFAULT_SEMANTICS_ATTR = "default";
	static public final String EXCEPTION_PROP_URI_ATTR = "uri";
	static public final String LOGGING_MODE_ATTR = "loggingMode";
}