package uk.ac.manchester.cs.hobo.mechanism.match;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

import uk.ac.manchester.cs.hobo.*;
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.mechanism.match.*;

/**
 * @author Colin Puleston
 */
public abstract class DMatchSectionInverter
							<M extends DObject,
							Q extends M,
							I extends DObject>
							extends DMatcherCustomiser<M, Q> {

	private DCustomMatcher matcher;

	private class Filter extends DMatchFilter<M> {

		private I queryInversionSection;

		protected boolean pass(M instance) {

			DCell<I> inversionCell = getInversionSectionCell(instance);

			if (inversionCell.isSet()) {

				return inversionMatch(inversionCell.get());
			}

			return true;
		}

		Filter(I queryInversionSection) {

			super(DMatchSectionInverter.this);

			this.queryInversionSection = queryInversionSection;
		}

		private boolean inversionMatch(I inversionSection) {

			return matches(inversionSection, queryInversionSection);
		}
	}

	public DMatchSectionInverter(DModel dModel, DCustomMatcher matcher) {

		super(dModel);

		this.matcher = matcher;
	}

	protected boolean handles(M instance) {

		return getInversionSectionCell(instance).isSet();
	}

	protected void preProcess(M instance) {

		getInversionSectionCell(instance).clear();
	}

	protected IMatches processMatches(M query, IMatches matches) {

		DCell<I> inversionCell = getInversionSectionCell(query);

		if (inversionCell.isSet()) {

			return new Filter(inversionCell.get()).filter(matches);
		}

		return matches;
	}

	protected boolean passesMatchesFilter(M query, M instance) {

		throw new HAccessException("Method should never be invoked!");
	}

	protected abstract DCell<I> getInversionSectionCell(M instance);

	private boolean matches(DObject query, DObject instance) {

		return matcher.matches(query.getFrame(), instance.getFrame());
	}
}