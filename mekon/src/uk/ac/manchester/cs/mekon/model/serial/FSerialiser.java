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
import uk.ac.manchester.cs.mekon.model.zlink.*;
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
	 * Renders an identity to an XML node.
	 *
	 * @param identity Identity to render
	 * @param node Node to render to
	 */
	static public void renderIdentity(CIdentity identity, XNode node) {

		node.setValue(IDENTIFIER_ATTR, identity.getIdentifier());
		node.setValue(LABEL_ATTR, identity.getLabel());
	}

	/**
	 * Renders an identity to an XML node.
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
	static public XDocument renderIdentities(Collection<CIdentity> identities) {

		XDocument document = new XDocument(IDENTITIES_LIST_ID);

		renderIdentities(identities, document.getRootNode(), IDENTITY_ID);

		return document;
	}

	/**
	 * Renders a list of identities to a set of specifically-created
	 * XML nodes, using standard tag for created nodes.
	 *
	 * @param identities Identities to be be rendered
	 * @param parentNode Parent of nodes to be created
	 */
	static public void renderIdentities(
							Collection<CIdentity> identities,
							XNode parentNode) {

		renderIdentities(identities, parentNode, IDENTITY_ID);
	}

	/**
	 * Renders a list of identities to a set of specifically-created
	 * XML nodes, using specified tag for created nodes.
	 *
	 * @param identities Identities to be be rendered
	 * @param parentNode Parent of nodes to be created
	 * @param tag Tag for created nodes
	 */
	static public void renderIdentities(
							Collection<CIdentity> identities,
							XNode parentNode,
							String tag) {

		for (CIdentity identity : identities) {

			renderIdentity(identity, parentNode.addChild(tag));
		}
	}

	/**
	 * Renders a meta-level frame, of either {@link
	 * IFrameCategory#ATOMIC} or {@link IFrameCategory#DISJUNCTION}
	 * category, to an XML node.
	 *
	 * @param frame Meta-level frame to render
	 * @param node Node to render to
	 */
	static public void renderMFrame(MFrame frame, XNode node) {

		renderCFrame(frame.getRootCFrame(), node.addChild(CFRAME_ID));
	}

	/**
	 * Renders a meta-level frame, of either {@link
	 * IFrameCategory#ATOMIC} or {@link IFrameCategory#DISJUNCTION}
	 * category, represented via a set of one-or-more disjunct-frame
	 * identities, to an XML node.
	 *
	 * @param disjunctIds Set of one-or-more disjunct-frame identities
	 * @param node Node to render to
	 */
	static public void renderMFrame(Collection<CIdentity> disjunctIds, XNode node) {

		renderCFrame(disjunctIds, node.addChild(CFRAME_ID));
	}

	/**
	 * Renders a concept-level frame, of either {@link
	 * IFrameCategory#ATOMIC} or {@link IFrameCategory#DISJUNCTION}
	 * category, to an XML node.
	 *
	 * @param frame Concept-level frame to render
	 * @param node Node to render to
	 */
	static public void renderCFrame(CFrame frame, XNode node) {

		if (frame.getCategory().disjunction()) {

			renderIdentities(getDisjunctIds(frame), node, CFRAME_ID);
		}
		else {

			renderIdentity(frame, node);
		}
	}

	/**
	 * Renders a concept-level frame, of either {@link
	 * IFrameCategory#ATOMIC} or {@link IFrameCategory#DISJUNCTION}
	 * category, represented via a set of one-or-more disjunct-frame
	 * identities, to an XML node.
	 *
	 * @param disjunctIds Set of one-or-more disjunct-frame identities
	 * @param node Node to render to
	 */
	static public void renderCFrame(Collection<CIdentity> disjunctIds, XNode node) {

		if (disjunctIds.size() == 1) {

			renderIdentity(disjunctIds.iterator().next(), node);
		}
		else {

			renderIdentities(disjunctIds, node, CFRAME_ID);
		}
	}

	/**
	 * Renders a concept-level number to an XML node.
	 *
	 * @param number Concept-level number to render
	 * @param node Node to render to
	 */
	static public void renderCNumber(CNumber number, XNode node) {

		renderNumberType(number, node);
		renderNumberRange(number, node);
	}

	/**
	 * Renders an instance-level number to an XML node.
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
	 * Renders a concept-level string to an XML node.
	 *
	 * @param string Concept-level string to render
	 * @param node Node to render to
	 */
	static public void renderCString(CString string, XNode node) {

		CStringFormat format = string.getFormat();

		node.setValue(STRING_FORMAT_ATTR, format);

		if (format == CStringFormat.CUSTOM) {

			node.setValue(STRING_CUSTOM_CONFIG_CLASS_ATTR, string.getClass());
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
	 * Parses an identity from a XML node.
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
	 * Parses a list of identities from a set of XML nodes
	 * with standard tag.
	 *
	 * @param parentNode Parent of relevant nodes
	 * @return Generated identities
	 */
	static public List<CIdentity> parseIdentities(XNode parentNode) {

		return parseIdentities(parentNode, IDENTITY_ID);
	}

	/**
	 * Parses a list of identities from a set of XML nodes
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
	 * Parses a meta-level frame, of either {@link
	 * IFrameCategory#ATOMIC} or {@link IFrameCategory#DISJUNCTION}
	 * category, from an XML node, providing the result as a set of
	 * one-or-more disjunct-frame identities.
	 *
	 * @param node Node to parse from
	 * @return parsed set of one-or-more disjunct-frame identities
	 */
	static public List<CIdentity> parseMFrameAsDisjunctIds(XNode node) {

		return parseCFrameAsDisjunctIds(node.getChild(CFRAME_ID));
	}

	/**
	 * Parses a concept-level frame, of either {@link
	 * IFrameCategory#ATOMIC} or {@link IFrameCategory#DISJUNCTION}
	 * category, from an XML node, providing the result as a set of
	 * one-or-more disjunct-frame identities.
	 *
	 * @param node Node to parse from
	 * @return parsed set of one-or-more disjunct-frame identities
	 */
	static public List<CIdentity> parseCFrameAsDisjunctIds(XNode node) {

		if (node.hasAttribute(IDENTIFIER_ATTR)) {

			return Collections.singletonList(parseIdentity(node));
		}

		return parseIdentities(node, CFRAME_ID);
	}

	/**
	 * Parses a concept-level number from a XML node.
	 *
	 * @param node Node to parse from
	 * @return parsed concept-level number
	 */
	static public CNumber parseCNumber(XNode node) {

		return parseCNumber(parseNumberType(node), node);
	}

	/**
	 * Parses an instance-level number from a XML node.
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
	 * Parses a concept-level string from a XML node.
	 *
	 * @param node Node to parse from
	 * @return parsed concept-level string
	 */
	static public CString parseCString(XNode node) {

		CStringFormat format = getCStringFormat(node);

		return format == CStringFormat.CUSTOM
					? parseCustomCString(node)
					: format.getStandardValueType();
	}

	/**
	 * Parses an instance-level string value from a XML node.
	 *
	 * @param node Node to parse from
	 * @return parsed string value
	 */
	static public IString parseIString(XNode node) {

		return CString.FREE.instantiate(node.getString(STRING_VALUE_ATTR));
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

	static private CString parseCustomCString(XNode node) {

		Class<? extends CStringConfig> configCls = getCustomCStringConfigClass(node);

		return ZCModelAccessor.get().resolveCustomCString(configCls);
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

	static private List<CIdentity> getDisjunctIds(CFrame disjunctionFrame) {

		return CIdentified.extractIdentities(disjunctionFrame.getSubs());
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

	static private Class<? extends CStringConfig> getCustomCStringConfigClass(XNode node) {

		return loadClass(node.getString(STRING_CUSTOM_CONFIG_CLASS_ATTR), CStringConfig.class);
	}

	static private <T>Class<? extends T> loadClass(String className, Class<T> type) {

		return new KConfigClassLoader(className).load(type);
	}
}
