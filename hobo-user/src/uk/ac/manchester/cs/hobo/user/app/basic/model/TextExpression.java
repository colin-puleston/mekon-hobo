/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.hobo.user.app.basic.model;

import java.util.*;
import java.util.regex.*;

/**
 * @author Colin Puleston
 */
public class TextExpression {

	static public final String RESERVED_QUERY_CHARS = "&|";

	static private final String QUERY_AND_SYMBOL = "&";
	static private final String QUERY_OR_SYMBOL = "|";

	static private final String DISPLAY_AND_SYMBOL = " AND ";
	static private final String DISPLAY_OR_SYMBOL = " OR ";

	static public TextExpression fromQueryString(String queryString) {

		TextExpression expr = new TextExpression();

		for (String conjunctQueryString : splitQueryString(queryString, QUERY_AND_SYMBOL)) {

			expr.addConjunct(disjunctionFromQueryString(conjunctQueryString));
		}

		return expr;
	}

	static private TextDisjunction disjunctionFromQueryString(String queryString) {

		return new TextDisjunction(disjunctsFromQueryString(queryString));
	}

	static private List<String> disjunctsFromQueryString(String queryString) {

		return Arrays.asList(splitQueryString(queryString, QUERY_OR_SYMBOL));
	}

	static private String[] splitQueryString(String queryString, String symbol) {

		return queryString.split(Pattern.quote(symbol));
	}

	private List<TextDisjunction> conjuncts = new ArrayList<TextDisjunction>();

	private abstract class Renderer {

		String render() {

			StringJoiner r = new StringJoiner(getAndSymbol());

			for (TextDisjunction conjunct : conjuncts) {

				r.add(renderConjunct(conjunct));
			}

			return r.toString();
		}

		String renderConjunct(TextDisjunction conjunct) {

			List<String> disjuncts = conjunct.getDisjuncts();
			StringJoiner r = createDisjunctionRenderer(disjuncts);

			for (String disjunct : disjuncts) {

				r.add(renderDisjunct(disjunct));
			}

			return r.toString();
		}

		abstract String getAndSymbol();

		abstract String getOrSymbol();

		abstract boolean bracketMultiDisjuncts();

		abstract boolean quoteDisjuncts();

		private StringJoiner createDisjunctionRenderer(List<String> disjuncts) {

			if (bracketMultiDisjuncts() && disjuncts.size() > 1) {

				return new StringJoiner(getOrSymbol(), "(", ")");
			}

			return new StringJoiner(getOrSymbol());
		}

		private String renderDisjunct(String disjunct) {

			return quoteDisjuncts() ? ("\"" + disjunct + "\"") : disjunct;
		}
	}

	private class QueryRenderer extends Renderer {

		String getAndSymbol() {

			return QUERY_AND_SYMBOL;
		}

		String getOrSymbol() {

			return QUERY_OR_SYMBOL;
		}

		boolean bracketMultiDisjuncts() {

			return false;
		}

		boolean quoteDisjuncts() {

			return false;
		}
	}

	private class DisplayRenderer extends Renderer {

		String getAndSymbol() {

			return DISPLAY_AND_SYMBOL;
		}

		String getOrSymbol() {

			return DISPLAY_OR_SYMBOL;
		}

		boolean bracketMultiDisjuncts() {

			return true;
		}

		boolean quoteDisjuncts() {

			return true;
		}
	}

	public void addConjunct(TextDisjunction conjunct) {

		conjuncts.add(conjunct);
	}

	public List<TextDisjunction> getConjuncts() {

		return new ArrayList<TextDisjunction>(conjuncts);
	}

	public boolean matchingText(String text) {

		for (TextDisjunction conjunct : conjuncts) {

			if (!conjunctMatchInText(text, conjunct)) {

				return false;
			}
		}

		return true;
	}

	public String toQueryString() {

		return new QueryRenderer().render();
	}

	String toDisplayString() {

		return new DisplayRenderer().render();
	}

	private boolean conjunctMatchInText(String text, TextDisjunction conjunct) {

		return queryMatchInText(text, renderConjunctAsQuery(conjunct));
	}

	private boolean queryMatchInText(String text, String queryString) {

		return Pattern.compile(queryString).matcher(text).find();
	}

	private String renderConjunctAsQuery(TextDisjunction conjunct) {

		return new QueryRenderer().renderConjunct(conjunct);
	}
}
