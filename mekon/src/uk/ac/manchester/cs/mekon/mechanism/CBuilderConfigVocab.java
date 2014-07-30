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

package uk.ac.manchester.cs.mekon.mechanism;

/**
 * Vocabulary used in the {@link CBuilder}-definition section
 * of the MEKON configuration file.
 *
 * @author Colin Puleston
 */
public interface CBuilderConfigVocab {

	static public final String MODEL_SECTION_ID = "ModelSection";
	static public final String INSTANCE_OPTIONS_ID = "InstanceUpdateOptions";

	static public final String QUERIES_ENABLED_ATTR = "queriesEnabled";
	static public final String SECTION_BLDER_CLASS_ATTR = "builder";
	static public final String UPDATE_INFERREDS_ATTR = "inferredTypes";
	static public final String UPDATE_SUGGESTEDS_ATTR = "suggestedTypes";
	static public final String UPDATE_SLOTS_ATTR = "slots";
	static public final String UPDATE_SLOT_VALUES_ATTR = "slotValues";
}
