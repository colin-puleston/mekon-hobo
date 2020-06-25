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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;

/**
 * @author Colin Puleston
 */
class IDiskStore implements IStore {

	private CModel model;

	private FileStore fileStore;
	private List<IMatcher> matchers;
	private IDirectMatcher defaultMatcher = new IDirectMatcher();

	private List<CIdentity> identities = new ArrayList<CIdentity>();
	private Map<CIdentity, IRegenType> types = new HashMap<CIdentity, IRegenType>();
	private InstanceIndexes indexes = new InstanceIndexes();

	private IStoreActiveRegenReport regenReport;
	private InstanceRefIntegrityManager refIntegrityManager;

	private boolean loaded = false;

	private class Reloader {

		private List<IMatcher> reloadableMatchers = initialiseMatchers();

		Reloader() {

			reloadStore();
			processInstances();

			indexes.reinitialiseFreeIndexes();
		}

		private void reloadStore() {

			for (StoredProfile storedProfile : fileStore.getStoredProfiles()) {

				reloadToStore(storedProfile.getProfile(), storedProfile.getIndex());
			}
		}

		private void reloadToStore(InstanceProfile profile, int index) {

			CIdentity identity = profile.getInstanceId();
			IRegenType type = createRegenType(profile.getTypeId());

			identities.add(identity);
			types.put(identity, type);
			indexes.assignIndex(identity, index);

			refIntegrityManager.onReloadedInstance(identity, profile);
		}

		private void processInstances() {

			for (CIdentity identity : getAllIdentities()) {

				IFrame instance = checkRegen(identity);

				if (instance != null) {

					checkAddToMatcher(instance, identity);
				}
			}
		}

		private IFrame checkRegen(CIdentity identity) {

			IRegenInstance regen = regen(identity, true);

			switch (regen.getStatus()) {

				case FULLY_INVALID:
					regenReport.addFullyInvalidRegenId(identity);
					return null;

				case PARTIALLY_VALID:
					regenReport.addPartiallyValidRegenId(identity);
					break;
			}

			return regen.getRootFrame();
		}

		private void checkAddToMatcher(IFrame instance, CIdentity identity) {

			IMatcher matcher = lookForMatcher(reloadableMatchers, instance.getType());

			if (matcher != null) {

				matcher.add(instance, identity);
			}
		}
	}

	public synchronized IFrame add(IFrame instance, CIdentity identity) {

		IFrame previous = checkRemove(identity);
		int index = indexes.assignIndex(identity);

		identities.add(identity);
		types.put(identity, createRegenType(instance));

		fileStore.write(instance, identity, index);

		refIntegrityManager.onAddedInstance(instance, identity);
		addToMatcher(instance, identity);

		return previous;
	}

	public synchronized boolean remove(CIdentity identity) {

		if (checkRemove(identity) != null) {

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

		return types.get(identity);
	}

	public synchronized IRegenInstance get(CIdentity identity) {

		return indexes.hasIndex(identity) ? regen(identity, false) : null;
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

		this(model, new ArrayList<IMatcher>(), null);
	}

	IDiskStore(CModel model, List<IMatcher> matchers, File directory) {

		this.model = model;
		this.matchers = matchers;

		fileStore = new FileStore(model, directory);
		regenReport = new IStoreActiveRegenReport(fileStore.getLogFile().getFile());
		refIntegrityManager = new InstanceRefIntegrityManager(this);
	}

	void addMatcher(IMatcher matcher) {

		matchers.add(matcher);
	}

	void initialisePostRegistration() {

		new Reloader();

		loaded = true;
	}

	void stop() {

		for (IMatcher matcher : matchers) {

			matcher.stop();
		}

		matchers.clear();
	}

	void update(IFrame instance, CIdentity identity) {

		int index = indexes.getIndex(identity);

		removeFromMatcher(instance.getType(), identity);

		fileStore.remove(index);
		fileStore.write(instance, identity, index);

		addToMatcher(instance, identity);
	}

	IFrame regenOrNull(CIdentity identity, boolean freeInstance) {

		return regenOrNull(identity, indexes.getIndex(identity), freeInstance);
	}

	private List<IMatcher> initialiseMatchers() {

		List<IMatcher> reloadables = new ArrayList<IMatcher>();

		defaultMatcher.initialise(this, indexes);

		for (IMatcher matcher : matchers) {

			matcher.initialise(this, indexes);

			if (matcher.rebuildOnStartup()) {

				reloadables.add(matcher);
			}
		}

		return reloadables;
	}

	private IFrame checkRemove(CIdentity identity) {

		IFrame removed = null;

		if (indexes.hasIndex(identity)) {

			identities.remove(identity);
			types.remove(identity);

			int index = indexes.getIndex(identity);

			removed = regenOrNull(identity, index, false);

			CFrame type = removed != null ? removed.getType() : getType(index);

			removeFromMatcher(type, identity);

			fileStore.remove(index);
			indexes.freeIndex(identity);
		}

		return removed;
	}

	private CFrame getType(int index) {

		return model.getFrames().get(fileStore.readTypeId(index));
	}

	private IFrame regenOrNull(CIdentity identity, int index, boolean freeInstance) {

		IRegenInstance regen = regen(identity, index, freeInstance);

		if (regen.getStatus() == IRegenStatus.FULLY_INVALID) {

			return null;
		}

		return regen.getRootFrame();
	}

	private IRegenInstance regen(CIdentity identity, boolean freeInstance) {

		return regen(identity, indexes.getIndex(identity), freeInstance);
	}

	private IRegenInstance regen(CIdentity identity, int index, boolean freeInstance) {

		return fileStore.read(identity, index, freeInstance, !loaded);
	}

	private void addToMatcher(IFrame instance, CIdentity identity) {

		getMatcher(instance).add(createFreeCopy(instance), identity);
	}

	private void removeFromMatcher(CFrame type, CIdentity identity) {

		getMatcher(type).remove(identity);
	}

	private IMatcher getMatcher(IFrame frame) {

		return getMatcher(frame.getType());
	}

	private IMatcher getMatcher(CFrame frameType) {

		IMatcher matcher = lookForMatcher(matchers, frameType);

		return matcher != null ? matcher : defaultMatcher;
	}

	private IMatcher lookForMatcher(List<IMatcher> candidates, CFrame frameType) {

		for (IMatcher candidate : candidates) {

			if (candidate.handlesType(frameType)) {

				return candidate;
			}
		}

		return null;
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
}
