package uk.ac.manchester.cs.hobo.user.app.basic.matcher;

import uk.ac.manchester.cs.mekon.store.disk.*;

import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
public class TextBlockMatcher implements ICustomStringMatcher {

	public boolean matches(String queryValue, String instanceValue) {

		return TextExpression.fromQueryString(queryValue).matchingText(instanceValue);
	}
}
