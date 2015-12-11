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

package uk.ac.manchester.cs.mekon.basex;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;

/**
 * @author Colin Puleston
 */
class QueryRenderer extends Renderer {

	static private final String DOCS_VARIABLE = "$d";
	static private final String DOCS_SET = "collection()";
	static private final String ROOT_PATH = DOCS_VARIABLE + "/Instance";
	static private final String INSTANCE_INDEX_PATH = ROOT_PATH + "/@" + INDEX_ATTR;

	static private final String FOR_STATEMENT_FORMAT = "for %s in %s";
	static private final String WHERE_START_FORMAT = "where %s";
	static private final String WHERE_CONTINUE_FORMAT = "and %s";
	static private final String RETURN_STATEMENT = "return " + INSTANCE_INDEX_PATH;

	static private final String VARIABLE_FORMAT = "$%s%d";
	static private final String PATH_ENTITY_FORMAT = "%s/%s";
	static private final String TYPE_FORMAT = "%s/Type/@id=\"%s\"";

	static private final String OR = " or ";
	static private final String OPEN_BRACKET = "(";
	static private final String CLOSE_BRACKET = ")";

	static private final String NUMBER_VALUE_FORMAT = "%s/@value%s%s";
	static private final String EQUAL_OPERATOR = "=";
	static private final String MIN_OPERATOR = ">=";
	static private final String MAX_OPERATOR = "<=";

	private class OneTimeRenderer {

		private StringBuilder query = new StringBuilder();
		private StringBuilder whereStatement = new StringBuilder();

		private Map<String, Integer> variableCountsByTag = new HashMap<String, Integer>();

		private abstract class FeatureStatementsAdder<V> {

			private String parentPath;

			FeatureStatementsAdder(String parentPath) {

				this.parentPath = parentPath;
			}

			void addForAll(List<? extends NFeature<V>> features) {

				for (NFeature<V> feature : features) {

					if (feature.hasValues()) {

						checkValid(feature);
						addFor(feature);
					}
				}
			}

			abstract String getEntityId();

			void checkValid(NFeature<V> feature) {
			}

			abstract void addValue(String path, V value);

			private void addFor(NFeature<V> feature) {

				String path = addDeclaration();

				addTypesStatement(path, feature);

				for (V value : feature.getValues()) {

					addValue(path, value);
				}
			}

			private String addDeclaration() {

				 return addDeclarationStatement(parentPath, getEntityId());
			}
		}

		private class LinkStatementsAdder
						extends
							FeatureStatementsAdder<NNode> {

			LinkStatementsAdder(String parentPath) {

				super(parentPath);
			}

			String getEntityId() {

				return LINK_ID;
			}

			void checkValid(NLink feature) {

				checkConjunctionLink(feature);
			}

			void addValue(String path, NNode value) {

				addNodeStatements(path, value);
			}
		}

		private class NumericStatementsAdder
						extends
							FeatureStatementsAdder<INumber> {

			NumericStatementsAdder(String parentPath) {

				super(parentPath);
			}

			String getEntityId() {

				return NUMERIC_ID;
			}

			void addValue(String path, INumber value) {

				addNumberValueComponents(path, value);
			}
		}

		OneTimeRenderer(NNode rootNode) {

			addForStatement(DOCS_VARIABLE, DOCS_SET);
			addNodeStatements(ROOT_PATH, rootNode);
			addStatement(whereStatement);
			addStatement(RETURN_STATEMENT);
		}

		String getRendering() {

			return query.toString();
		}

		private void addNodeStatements(String parentPath, NNode node) {

			String path = addDeclarationStatement(parentPath, NODE_ID);

			addTypesStatement(path, node);

			new LinkStatementsAdder(path).addForAll(node.getLinks());
			new NumericStatementsAdder(path).addForAll(node.getNumerics());
		}

		private String addDeclarationStatement(String parentPath, String tag) {

			String path = createNextPathVariable(tag);

			addForStatement(path, renderPathEntity(parentPath, tag));

			return path;
		}

		private void addTypesStatement(String path, NEntity entity) {

			addWhereComponent(renderTypes(path, entity.getTypeDisjuncts()));
		}

		private void addNumberValueComponents(String path, INumber value) {

			if (value.definite()) {

				addNumberValueComponent(path, EQUAL_OPERATOR, value);
			}
			else {

				addNumberValueComponents(path, value.getType());
			}
		}

		private void addNumberValueComponents(String path, CNumber type) {

			if (type.hasMin()) {

				addNumberValueComponent(path, MIN_OPERATOR, type.getMin());
			}

			if (type.hasMax()) {

				addNumberValueComponent(path, MAX_OPERATOR, type.getMax());
			}
		}

		private void addNumberValueComponent(
							String path,
							String operator,
							INumber value) {

			addWhereComponent(renderNumberValue(path, operator, value));
		}

		private void addForStatement(String variable, String set) {

			addStatement(renderForStatement(variable, set));
		}

		private void addStatement(CharSequence statement) {

			query.append(statement);
			query.append('\n');
		}

		private void addWhereComponent(String condition) {

			boolean first = whereStatement.length() == 0;

			if (!first) {

				whereStatement.append('\n');
			}

			whereStatement.append(renderWhereComponent(condition, first));
		}

		private String createNextPathVariable(String tag) {

			Integer count = variableCountsByTag.get(tag);

			int varNo = (count == null) ? 0 : (count + 1);
			String var = renderPathVariable(tag, varNo);

			variableCountsByTag.put(tag, varNo);

			return var;
		}
	}

	String render(NNode rootNode) {

		checkNonCyclic(rootNode);

		return new OneTimeRenderer(rootNode).getRendering();
	}

	private String renderForStatement(String variable, String set) {

		return String.format(FOR_STATEMENT_FORMAT, variable, set);
	}

	private String renderWhereComponent(String condition, boolean first) {

		String format = first ? WHERE_START_FORMAT : WHERE_CONTINUE_FORMAT;

		return String.format(format, condition);
	}

	private String renderPathVariable(String tag, int index) {

		return String.format(VARIABLE_FORMAT, tag.substring(0, 2), index);
	}

	private String renderPathEntity(String parentPath, String tag) {

		return String.format(PATH_ENTITY_FORMAT, parentPath, tag);
	}

	private String renderTypes(String path, List<CIdentity> typeIds) {

		return typeIds.size() == 1
				? renderType(path, typeIds.get(0))
				: renderMultiTypes(path, typeIds);
	}

	private String renderMultiTypes(String path, List<CIdentity> typeIds) {

		StringBuilder types = new StringBuilder();
		boolean first = true;

		types.append(OPEN_BRACKET);

		for (CIdentity typeId : typeIds) {

			if (first) {

				first = false;
			}
			else {

				types.append(OR);
			}

			types.append(renderType(path, typeId));
		}

		types.append(CLOSE_BRACKET);

		return types.toString();
	}

	private String renderType(String path, CIdentity typeId) {

		return String.format(TYPE_FORMAT, path, typeId.getIdentifier());
	}

	private String renderNumberValue(
						String path,
						String operator,
						INumber value) {

		String valStr = value.asTypeNumber().toString();

		return String.format(NUMBER_VALUE_FORMAT, path, operator, valStr);
	}
}
