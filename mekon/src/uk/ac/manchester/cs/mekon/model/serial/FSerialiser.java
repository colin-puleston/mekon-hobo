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

package uk.ac.manchester.cs.mekon.model.serial;

import java.lang.reflect.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * Provides access to the vocabulary for the standard XML
 * serialisation of MEKON frames-model entities, via the
 * implementation of the {@link FSerialiserVocab} interface,
 * whilst providing a set of utility methods for performing
 * various basic serialisation operations.
 *
 * @author Colin Puleston
 */
public class FSerialiser implements FSerialiserVocab {

	static private Set<Class<? extends Number>> numberTypes
						= new HashSet<Class<? extends Number>>();

	static {

		numberTypes.add(Integer.class);
		numberTypes.add(Long.class);
		numberTypes.add(Float.class);
		numberTypes.add(Double.class);
	}

	/**
	 * Renders a single identity to produce an XML document.
	 *
	 * @param identity Identity to render
	 * @return Rendered document
	 */
	static public XDocument renderIdentity(CIdentity identity) {

		XDocument document = new XDocument(IDENTITY_ID);

		renderIdentity(identity, document.getRootNode().addChild(IDENTITY_ID));

		return document;
	}

	/**
	 * Renders an identity to a configuration file node.
	 *
	 * @param identity Identity to render
	 * @param node Node to render to
	 */
	static public void renderIdentity(CIdentity identity, XNode node) {

		node.setValue(IDENTIFIER_ATTR, identity.getIdentifier());
		node.setValue(LABEL_ATTR, identity.getLabel());
	}

	/**
	 * Renders an identity to a configuration file node.
	 *
	 * @param identified Identified object whose identity is to be
	 * rendered
	 * @param node Node to render to
	 */
	static public void renderIdentity(CIdentified identified, XNode node) {

		renderIdentity(identified.getIdentity(), node);
	}

	/**
	 * Renders a list of identities to produce an XML document.
	 *
	 * @param identities Identities to render
	 * @return Rendered document
	 */
	static public XDocument renderIdentities(List<CIdentity> identities) {

		XDocument document = new XDocument(IDENTITIES_LIST_ID);

		renderIdentities(identities, document.getRootNode(), IDENTITY_ID);

		return document;
	}

	/**
	 * Renders a list of identities to a set of specifically-created
	 * configuration file nodes, using standard tag for created nodes.
	 *
	 * @param identities Identities to be be rendered
	 * @param parentNode Parent of nodes to be created
	 */
	static public void renderIdentities(List<CIdentity> identities, XNode parentNode) {

		renderIdentities(identities, parentNode, IDENTITY_ID);
	}

	/**
	 * Renders a list of identities to a set of specifically-created
	 * configuration file nodes, using specified tag for created nodes.
	 *
	 * @param identities Identities to be be rendered
	 * @param parentNode Parent of nodes to be created
	 * @param tag Tag for created nodes
	 */
	static public void renderIdentities(List<CIdentity> identities, XNode parentNode, String tag) {

		for (CIdentity identity : identities) {

			renderIdentity(identity, parentNode.addChild(tag));
		}
	}

	/**
	 * Renders a concept-level number to a configuration file node.
	 *
	 * @param number Concept-level number to render
	 * @param node Node to render to
	 */
	static public void renderCNumber(CNumber number, XNode node) {

		renderNumberType(number, node);
		renderNumberRange(number, node);
	}

	/**
	 * Renders an instance-level number to a configuration file node.
	 *
	 * @param number Instance-level number to render
	 * @param node Node to render to
	 */
	static public void renderINumber(INumber number, XNode node) {

		if (number.indefinite()) {

			renderNumberRange(number.getType(), node);
		}
		else {

			node.setValue(NUMBER_VALUE_ATTR, number.asTypeNumber());
		}
	}

	/**
	 * Renders a concept-level string to a configuration file node.
	 *
	 * @param string Concept-level string to render
	 * @param node Node to render to
	 */
	static public void renderCString(CString string, XNode node) {

		CStringFormat format = string.getFormat();

		node.setValue(STRING_FORMAT_ATTR, format);

		if (format == CStringFormat.CUSTOM) {

			node.setValue(
				STRING_VALIDATOR_CLASS_ATTR,
				CStringFactory.getCustomValidatorClass(string));
		}
	}

	/**
	 * Renders an instance-level string value to a configuration file
	 * node.
	 *
	 * @param number Instance-level string value to render
	 * @param node Node to render to
	 */
	static public void renderIString(IString number, XNode node) {

		node.setValue(STRING_VALUE_ATTR, number.get());
	}

	/**
	 * Parses a single identity from the specified XML document.
	 *
	 * @param document Document for parsing
	 * @return Generated identity
	 */
	static public CIdentity parseIdentity(XDocument document) {

		return parseIdentity(document.getRootNode().getChild(IDENTITY_ID));
	}

	/**
	 * Parses an identity from a configuration file node.
	 *
	 * @param node Node to parse from
	 * @return parsed identity
	 */
	static public CIdentity parseIdentity(XNode node) {

		String id = node.getString(IDENTIFIER_ATTR);
		String label = node.getString(LABEL_ATTR, null);

		return label != null ? new CIdentity(id, label) : new CIdentity(id);
	}

	/**
	 * Parses a list of identities from the specified XML document.
	 *
	 * @param document Document for parsing
	 * @return Generated identities
	 */
	static public List<CIdentity> parseIdentities(XDocument document) {

		return parseIdentities(document.getRootNode(), IDENTITY_ID);
	}

	/**
	 * Parses a list of identities from a set of configuration file nodes
	 * with standard tag.
	 *
	 * @param parentNode Parent of relevant nodes
	 * @return Generated identities
	 */
	static public List<CIdentity> parseIdentities(XNode parentNode) {

		return parseIdentities(parentNode, IDENTITY_ID);
	}

	/**
	 * Parses a list of identities from a set of configuration file nodes
	 * with specified tag.
	 *
	 * @param parentNode Parent of relevant nodes
	 * @param tag Tag of relevant nodes
	 * @return Generated identities
	 */
	static public List<CIdentity> parseIdentities(XNode parentNode, String tag) {

		List<CIdentity> identities = new ArrayList<CIdentity>();

		for (XNode idNode : parentNode.getChildren(tag)) {

			identities.add(parseIdentity(idNode));
		}

		return identities;
	}

	/**
	 * Parses a concept-level number from a configuration file node.
	 *
	 * @param node Node to parse from
	 * @return parsed concept-level number
	 */
	static public CNumber parseCNumber(XNode node) {

		return parseCNumber(parseNumberType(node), node);
	}

	/**
	 * Parses an instance-level number from a configuration file node.
	 *
	 * @param type Type of number to be parsed
	 * @param node Node to parse from
	 * @return parsed instance-level number
	 */
	static public INumber parseINumber(CNumber type, XNode node) {

		Class<? extends Number> numberType = type.getNumberType();

		return node.hasAttribute(NUMBER_VALUE_ATTR)
				? parseDefiniteINumber(numberType, node, NUMBER_VALUE_ATTR)
				: parseCNumber(numberType, node).asINumber();
	}

	/**
	 * Parses a concept-level string from a configuration file node.
	 *
	 * @param node Node to parse from
	 * @return parsed concept-level string
	 */
	static public CString parseCString(XNode node) {

		CStringFormat format = getCStringFormat(node);

		if (format == CStringFormat.CUSTOM) {

			return CStringFactory.custom(getCStringValidatorClass(node));
		}

		return CStringFactory.standard(format);
	}

	/**
	 * Parses an instance-level string value from a configuration file node.
	 *
	 * @param node Node to parse from
	 * @return parsed string value
	 */
	static public IString parseIString(XNode node) {

		return CStringFactory.FREE.instantiate(node.getString(STRING_VALUE_ATTR));
	}

	static private void renderNumberType(CNumber number, XNode node) {

		renderClassId(number.getNumberType(), node, NUMBER_TYPE_ATTR);
	}

	static private void renderNumberRange(CNumber number, XNode node) {

		if (number.hasMin()) {

			node.setValue(NUMBER_MIN_ATTR, number.getMin().asTypeNumber());
		}

		if (number.hasMax()) {

			node.setValue(NUMBER_MAX_ATTR, number.getMax().asTypeNumber());
		}
	}

	static private void renderClassId(Class<?> leafClass, XNode node, String attr) {

		node.setValue(attr, getPublicClassId(leafClass));
	}

	static private CNumber parseCNumber(Class<? extends Number> numberType, XNode node) {

		INumber min = INumber.MINUS_INFINITY;
		INumber max = INumber.PLUS_INFINITY;

		if (node.hasAttribute(NUMBER_MIN_ATTR)) {

			min = parseDefiniteINumber(numberType, node, NUMBER_MIN_ATTR);
		}

		if (node.hasAttribute(NUMBER_MAX_ATTR)) {

			max = parseDefiniteINumber(numberType, node, NUMBER_MAX_ATTR);
		}

		return CNumberFactory.range(numberType, min, max);
	}

	static private Class<? extends Number> parseNumberType(XNode node) {

		return getNumberType(node.getString(NUMBER_TYPE_ATTR));
	}

	static private INumber parseDefiniteINumber(
								Class<? extends Number> numberType,
								XNode node,
								String attrName) {

		return new INumber(numberType, node.getString(attrName));
	}

	static private String getPublicClassId(Class<?> leafClass) {

		if (Modifier.isPublic(leafClass.getModifiers())) {

			return leafClass.getSimpleName();
		}

		return getPublicClassId(leafClass.getSuperclass());
	}

	static private Class<? extends Number> getNumberType(String className) {

		for (Class<? extends Number> numberType : numberTypes) {

			if (numberType.getSimpleName().equals(className)) {

				return numberType;
			}
		}

		throw new XDocumentException("Unrecognised number class: " + className);
	}

	static private CStringFormat getCStringFormat(XNode node) {

		return node.getEnum(STRING_FORMAT_ATTR, CStringFormat.class, CStringFormat.FREE);
	}

	static private Class<? extends CStringValidator> getCStringValidatorClass(XNode node) {

		return loadClass(node.getString(STRING_VALIDATOR_CLASS_ATTR), CStringValidator.class);
	}

	static private <T>Class<? extends T> loadClass(String className, Class<T> type) {

		return new KConfigClassLoader(className).load(type);
	}
}
