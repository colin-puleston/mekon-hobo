package uk.ac.manchester.cs.hobo.mechanism.match;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * @author Colin Puleston
 */
public abstract class DMatcherCustomiser<M extends DObject, Q extends M> {

	private DModel dModel;
	private IStore iStore;

	private CFrame matchingType;

	public DMatcherCustomiser(DModel dModel) {

		this.dModel = dModel;

		iStore = IStoreManager.get(dModel.getCModel());
		matchingType = dModel.getFrame(getMatchingClass());
	}

	protected abstract boolean handles(M instance);

	protected abstract void preProcess(M instance);

	protected abstract IMatches processMatches(M query, IMatches matches);

	protected abstract boolean passesMatchesFilter(M query, M instance);

	protected abstract Class<M> getMatchingClass();

	protected abstract Class<Q> getQueryClass();

	protected M getStoredInstance(CIdentity id) {

		return getMatchingObject(iStore.get(id));
	}

	boolean handles(IFrame instance) {

		return hasMatchingType(instance) && handles(getMatchingObject(instance));
	}

	void preProcess(IFrame instance) {

		preProcess(getMatchingObject(instance));
	}

	IMatches processMatches(IFrame query, IMatches matches) {

		return processMatches(getMatchingObject(query), matches);
	}

	boolean passesMatchesFilter(IFrame query, IFrame instance) {

		return passesMatchesFilter(
					getMatchingObject(query),
					getMatchingObject(instance));
	}

	private boolean hasMatchingType(IFrame instance) {

		return matchingType.subsumes(instance.getType());
	}

	private M getMatchingObject(IFrame instance) {

		return dModel.getDObject(instance, getMatchingClass());
	}
}
