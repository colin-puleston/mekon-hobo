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

package uk.ac.manchester.cs.mekon.store.disk;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;

/**
 * @author Colin Puleston
 */
class IDiskStore implements IStore {

	private CModel model;

	private StoreSerialiser serialiser;
	private LogFile logFile;

	private List<IMatcher> matchers = new ArrayList<IMatcher>();
	private NDirectMatcher defaultMatcher = new NDirectMatcher();

	private List<CIdentity> identities = new ArrayList<CIdentity>();
	private Map<CIdentity, IRegenType> regenTypes = new HashMap<CIdentity, IRegenType>();
	private InstanceIndexes indexes = new InstanceIndexes();

	private IStoreActiveRegenReport regenReport;
	private InstanceRefIntegrityManager refIntegrityManager;

	private class Initialiser {

		Initialiser(IMatcherConfig matcherConfig) {

			initialiseMatchers(matcherConfig);
			reloadInstances();
		}

		private void initialiseMatchers(IMatcherConfig matcherConfig) {

			for (IMatcher matcher : matchers) {

				matcher.initialise(matcherConfig);
			}

			defaultMatcher.initialise(matcherConfig);
		}

		private void reloadInstances() {

			for (IInstanceProfile profile : serialiser.resolveStoredProfiles()) {

				reloadInstance(profile);
			}
		}

		private void reloadInstance(IInstanceProfile profile) {

			CIdentity identity = profile.getInstanceIdentity();
			IRegenType regenType = createRegenType(profile.getTypeIdentity());
			int index = profile.getIndex();

			indexes.assignIndex(identity, index);
			identities.add(identity);
			regenTypes.put(identity, regenType);

			refIntegrityManager.onReloadedInstance(identity, profile);

			IRegenInstance regen = load(identity, index, false);

			logRegen(identity, regen);

			if (regen.getStatus() != IRegenStatus.FULLY_INVALID) {

				addToMatcher(regen.getRootFrame(), identity);
			}
		}

		private void logRegen(CIdentity identity, IRegenInstance regen) {

			logFile.logParsedInstance(identity, regen);

			switch (regen.getStatus()) {

				case FULLY_INVALID:
					regenReport.addFullyInvalidRegenId(identity);
					return;

				case PARTIALLY_VALID:
					regenReport.addPartiallyValidRegenId(identity);
					break;
			}
		}
	}

	public synchronized IFrame add(IFrame instance, CIdentity identity) {

		IFrame previous = removePreIntegrityUpdates(identity);

		addPreIntegrityUpdates(instance, identity, indexes.assignIndex(identity));
		refIntegrityManager.onAddedInstance(instance, identity);

		return previous;
	}

	public synchronized boolean rename(CIdentity identity, CIdentity newIdentity) {

		int index = indexes.getIndex(identity);
		IFrame instance = removePreIntegrityUpdates(identity, index);

		if (instance != null) {

			indexes.reassignIndex(newIdentity, index);

			addPreIntegrityUpdates(instance, newIdentity, index);
			refIntegrityManager.onRenamedInstance(instance, identity, newIdentity);

			return true;
		}

		return false;
	}

	public synchronized boolean remove(CIdentity identity) {

		if (removePreIntegrityUpdates(identity) != null) {

			refIntegrityManager.onRemovedInstance(identity);

			return true;
		}

		return false;
	}

	public synchronized boolean clear() {

		if (identities.isEmpty()) {

			return false;
		}

		for (CIdentity identity : getAllIdentities()) {

			remove(identity);
		}

		return true;
	}

	public CModel getModel() {

		return model;
	}

	public IStoreRegenReport getRegenReport() {

		return regenReport;
	}

	public synchronized boolean contains(CIdentity identity) {

		return indexes.hasIndex(identity);
	}

	public synchronized IRegenType getType(CIdentity identity) {

		return regenTypes.get(identity);
	}

	public synchronized IRegenInstance get(CIdentity identity) {

		return indexes.hasIndex(identity) ? load(identity, false) : null;
	}

	public synchronized List<CIdentity> getAllIdentities() {

		return new ArrayList<CIdentity>(identities);
	}

	public synchronized IMatches match(IFrame query) {

		query = createFreeCopy(query);

		IMatches matches = getMatcher(query).match(query);

		indexes.ensureOriginalLabelsInMatches(matches);

		return matches;
	}

	public synchronized boolean matches(IFrame query, IFrame instance) {

		query = createFreeCopy(query);
		instance = createFreeCopy(instance);

		IMatcher matcher = getMatcher(query);

		if (matcher != getMatcher(instance)) {

			return false;
		}

		return matcher.matches(query, instance);
	}

	IDiskStore(CModel model) {

		this(model, new StoreStructureBuilder().build(model));
	}

	IDiskStore(CModel model, StoreStructure structure) {

		this.model = model;

		serialiser = new StoreSerialiser(model, structure);
		logFile = new LogFile(structure.getMainDirectory());
		regenReport = new IStoreActiveRegenReport(logFile.getFile());
		refIntegrityManager = new InstanceRefIntegrityManager(this);
	}

	void addMatchers(Collection<IMatcher> matchers) {

		this.matchers.addAll(matchers);
	}

	void addMatcher(IMatcher matcher) {

		matchers.add(matcher);
	}

	void initialisePostRegistration() {

		initialisePostRegistration(Collections.emptyList());
	}

	void initialisePostRegistration(List<IValueMatchCustomiser> valueMatchCustomisers) {

		new Initialiser(new IMatcherConfig(this, indexes, valueMatchCustomisers));
	}

	void stop() {

		for (IMatcher matcher : matchers) {

			matcher.stop();
		}

		matchers.clear();
	}

	void update(IFrame instance, CIdentity identity) {

		int index = indexes.getIndex(identity);

		removeFromMatcher(instance, identity);

		serialiser.remove(index);
		serialiser.write(instance, identity, index);

		addToMatcher(instance, identity);
	}

	IFrame regenOrNull(CIdentity identity, boolean freeInstance) {

		Integer index = indexes.getIndexOrNull(identity);

		return index != null ? regenOrNull(identity, index, freeInstance) : null;
	}

	private void addPreIntegrityUpdates(IFrame instance, CIdentity identity, int index) {

		identities.add(identity);
		addRegenType(instance, identity);
		serialiser.write(instance, identity, index);

		addToMatcher(instance, identity);

		refIntegrityManager.onAddedInstance(instance, identity);
	}

	private IFrame removePreIntegrityUpdates(CIdentity identity) {

		if (indexes.hasIndex(identity)) {

			return removePreIntegrityUpdates(identity, indexes.freeIndex(identity));
		}

		return null;
	}

	private IFrame removePreIntegrityUpdates(CIdentity identity, int index) {

		IFrame instance = regenOrNull(identity, index, false);

		identities.remove(identity);
		regenTypes.remove(identity);
		serialiser.remove(index);

		if (instance != null) {

			removeFromMatcher(instance, identity);
		}

		return instance;
	}

	private IFrame regenOrNull(CIdentity identity, int index, boolean freeInstance) {

		IRegenInstance regen = load(identity, index, freeInstance);

		if (regen.getStatus() == IRegenStatus.FULLY_INVALID) {

			return null;
		}

		return regen.getRootFrame();
	}

	private IRegenInstance load(CIdentity identity, boolean freeInstance) {

		return load(identity, indexes.getIndex(identity), freeInstance);
	}

	private IRegenInstance load(CIdentity identity, int index, boolean freeInstance) {

		return serialiser.read(identity, index, freeInstance);
	}

	private void addToMatcher(IFrame instance, CIdentity identity) {

		getMatcher(instance).add(createFreeCopy(instance), identity);
	}

	private void removeFromMatcher(IFrame instance, CIdentity identity) {

		getMatcher(instance).remove(identity);
	}

	private IMatcher getMatcher(IFrame frame) {

		return getMatcher(frame.getType());
	}

	private IMatcher getMatcher(CFrame type) {

		for (IMatcher matcher : matchers) {

			if (matcher.handlesType(type)) {

				return matcher;
			}
		}

		return defaultMatcher;
	}

	private IRegenType addRegenType(IFrame instance, CIdentity identity) {

		return regenTypes.put(identity, createRegenType(instance));
	}

	private IRegenType createRegenType(IFrame instance) {

		return new IRegenValidType(instance.getType());
	}

	private IRegenType createRegenType(CIdentity typeId) {

		CFrame type = model.getFrames().getOrNull(typeId);

		return type != null ? new IRegenValidType(type) : new IRegenInvalidType(typeId);
	}

	private IFrame createFreeCopy(IFrame instance) {

		return IFreeCopier.get().createFreeCopy(instance);
	}

	private CFrame getTypeOrNull(int index) {

		return model.getFrames().getOrNull(serialiser.readTypeId(index));
	}
}
