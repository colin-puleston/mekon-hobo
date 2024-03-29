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

package uk.ac.manchester.cs.hobo.manage;

/**
 * Vocabulary used in the direct-model definition section
 * of the HOBO/MEKON configuration file.
 *
 * @author Colin Puleston
 */
public interface DConfigVocab {

	static public final String DIRECT_MODEL_ID = "DirectModel";
	static public final String SECTIONS_ID = "Sections";
	static public final String SECTION_ID = "Section";
	static public final String MAPPINGS_ID = "Mappings";
	static public final String CLASS_MAP_ID = "ClassMap";
	static public final String FIELD_MAP_ID = "FieldMap";
	static public final String CLASS_MAPPER_ID = "ClassMapper";
	static public final String CLASS_MAPPER_PACKAGE_ID = "MappingPackage";
	static public final String CLASS_MAPPER_PACKAGE_GROUP_ID = "MappingPackageGroup";
	static public final String CLASS_MAPPER_COMPOUND_FIELD_IDS_ID = "CompoundFieldIds";

	static public final String DIRECT_CLASS_LABELS_ATTR = "labelsFromDirectClasses";
	static public final String DIRECT_FIELD_LABELS_ATTR = "labelsFromDirectFields";
	static public final String TOP_LEVEL_PACKAGE_ATTR = "topLevelPackage";
	static public final String CLASS_MAP_CLASS_ATTR = "javaClass";
	static public final String CLASS_MAP_EXTERNAL_ID_ATTR = "externalId";
	static public final String FIELD_MAP_FIELD_NAME_ATTR = "fieldName";
	static public final String FIELD_MAP_EXTERNAL_ID_ATTR = "externalId";
	static public final String CLASS_MAPPER_CLASS_ATTR = "mapperClass";
	static public final String CLASS_MAPPER_PACKAGE_ATTR = "package";
	static public final String CLASS_MAPPER_BASE_PACKAGE_ATTR = "topLevelPackage";
}