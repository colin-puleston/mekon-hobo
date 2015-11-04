package uk.ac.manchester.cs.hobo.mechanism.match;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * @author Colin Puleston
 */
public class DCustomMatcher implements IMatcher {

	private IMatcher coreMatcher;
	private Operations operations = new IndirectOperations();

	private abstract class Operations {

		abstract void initialise(DMatcherCustomisers customisers);

		abstract void add(IFrame instance, CIdentity identity);

		abstract void remove(CIdentity identity);

		abstract IMatches match(IFrame query);

		abstract boolean matches(IFrame query, IFrame instance);
	}

	private class IndirectOperations extends Operations {

		private Map<CIdentity, IFrame> instances = new HashMap<CIdentity, IFrame>();

		void initialise(DMatcherCustomisers customisers) {

			DirectOperations directOps = new DirectOperations(customisers);

			for (CIdentity id : instances.keySet()) {

				directOps.add(instances.get(id), id);
			}

			operations = directOps;
		}

		void add(IFrame instance, CIdentity identity) {

			instances.put(identity, instance);
		}

		void remove(CIdentity identity) {

			throw createNotInitialisedException();
		}

		IMatches match(IFrame query) {

			throw createNotInitialisedException();
		}

		boolean matches(IFrame query, IFrame instance) {

			throw createNotInitialisedException();
		}

		private KAccessException createNotInitialisedException() {

			return new KAccessException("DCustomMatcher has not been initialised");
		}
	}

	private class DirectOperations extends Operations {

		private DMatcherCustomisers customisers;

		DirectOperations(DMatcherCustomisers customisers) {

			this.customisers = customisers;
		}

		void initialise(DMatcherCustomisers customisers) {

			throw new KAccessException("DCustomMatcher has already been initialised");
		}

		void add(IFrame instance, CIdentity identity) {

			coreMatcher.add(preProcess(instance), identity);
		}

		void remove(CIdentity identity) {

			coreMatcher.remove(identity);
		}

		IMatches match(IFrame query) {

			return processMatches(query, coreMatcher.match(preProcess(query)));
		}

		boolean matches(IFrame query, IFrame instance) {

			if (coreMatcher.matches(preProcess(query), preProcess(instance))) {

				return passesMatchesFilter(query, instance);
			}

			return false;
		}

		private IFrame preProcess(IFrame instance) {

			return customisers.preProcess(instance);
		}

		private IMatches processMatches(IFrame query, IMatches matches) {

			return customisers.processMatches(query, matches);
		}

		private boolean passesMatchesFilter(IFrame query, IFrame instance) {

			return customisers.passesMatchesFilter(query, instance);
		}
	}

	public DCustomMatcher(IMatcher coreMatcher) {

		this.coreMatcher = coreMatcher;
	}

	public void initialise(DMatcherCustomisers customisers) {

		operations.initialise(customisers);
	}

	public boolean handlesType(CFrame type) {

		return coreMatcher.handlesType(type);
	}

	public void add(IFrame instance, CIdentity identity) {

		operations.add(instance, identity);
	}

	public void remove(CIdentity identity) {

		operations.remove(identity);
	}

	public IMatches match(IFrame query) {

		return operations.match(query);
	}

	public boolean matches(IFrame query, IFrame instance) {

		return operations.matches(query, instance);
	}
}
