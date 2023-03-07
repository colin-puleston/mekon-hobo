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
