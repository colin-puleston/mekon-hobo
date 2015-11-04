package uk.ac.manchester.cs.hobo.mechanism.match;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.hobo.model.*;

/**
 * @author Colin Puleston
 */
public class DMatcherCustomisers {

	private IFreeInstanceGenerator freeInstances;

	private List<DMatcherCustomiser<?, ?>> customisers
				= new ArrayList<DMatcherCustomiser<?, ?>>();

	public DMatcherCustomisers(DModel model) {

		freeInstances = new IFreeInstanceGenerator(model.getCModel());
	}

	public void addAll(List<DMatcherCustomiser<?, ?>> customisers) {

		this.customisers.addAll(customisers);
	}

	public void add(DMatcherCustomiser<?, ?> customiser) {

		customisers.add(customiser);
	}

	IFrame preProcess(IFrame instance) {

		instance = freeInstances.generateFrom(instance);

		for (DMatcherCustomiser<?, ?> customiser : filterCustomisers(instance)) {

			customiser.preProcess(instance);
		}

		return instance;
	}

	IMatches processMatches(IFrame query, IMatches matches) {

		for (DMatcherCustomiser<?, ?> customiser : filterCustomisers(query)) {

			matches = customiser.processMatches(query, matches);
		}

		return matches;
	}

	boolean passesMatchesFilter(IFrame query, IFrame instance) {

		for (DMatcherCustomiser<?, ?> customiser : filterCustomisers(query)) {

			if (!customiser.passesMatchesFilter(query, instance)) {

				return false;
			}
		}

		return true;
	}

	private List<DMatcherCustomiser<?, ?>> filterCustomisers(IFrame tester) {

		List<DMatcherCustomiser<?, ?>> filtered
			= new ArrayList<DMatcherCustomiser<?, ?>>();

		for (DMatcherCustomiser<?, ?> customiser : customisers) {

			if (customiser.handles(tester)) {

				filtered.add(customiser);
			}
		}

		return filtered;
	}
}
