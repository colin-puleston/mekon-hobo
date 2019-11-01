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
import uk.ac.manchester.cs.mekon.model.regen.*;
import uk.ac.manchester.cs.mekon.model.regen.zlink.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class IDiskStore implements IStore {

	static private final ZIRegenAccessor regenAccessor = ZIRegenAccessor.get();

	private CModel model;

	private FileStore fileStore;
	private List<IMatcher> matchers;
	private IDirectMatcher defaultMatcher = new IDirectMatcher();

	private List<CIdentity> identities = new ArrayList<CIdentity>();
	private Map<CIdentity, IRegenType> types = new HashMap<CIdentity, IRegenType>();
	private InstanceIndexes indexes = new InstanceIndexes();

	private KSetMap<CIdentity, CIdentity> referencingIds = new KSetMap<CIdentity, CIdentity>();

	public CModel getModel() {

		return model;
	}

	public synchronized IFrame add(IFrame instance, CIdentity identity) {

		IFrame previous = checkRemove(identity);
		int index = indexes.assignIndex(identity);

		identities.add(identity);
		types.put(identity, createRegenType(instance));
		addInstanceRefs(instance, identity);

		fileStore.write(instance, identity, index);

		addToMatcher(instance, identity);

		return previous;
	}

	public synchronized boolean remove(CIdentity identity) {

		return checkRemove(identity) != null;
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

	public synchronized boolean contains(CIdentity identity) {

		return indexes.hasIndex(identity);
	}

	public synchronized IRegenType getType(CIdentity identity) {

		return types.get(identity);
	}

	public synchronized IRegenInstance get(CIdentity identity) {

		if (indexes.hasIndex(identity)) {

			return fileStore.read(identity, indexes.getIndex(identity), false);
		}

		return null;
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
	}

	void addMatcher(IMatcher matcher) {

		matchers.add(matcher);
	}

	void initialisePostRegistration() {

		initialiseMatchers();

		reloadStore();
		reloadMatchers();

		indexes.reinitialiseFreeIndexes();
	}

	void stop() {

		for (IMatcher matcher : matchers) {

			matcher.stop();
		}

		matchers.clear();
	}

	private void initialiseMatchers() {

		defaultMatcher.initialise(this, indexes);

		for (IMatcher matcher : matchers) {

			matcher.initialise(this, indexes);
		}
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
		addInstanceRefs(identity, profile.getReferenceIds());
	}

	private void reloadMatchers() {

		List<IMatcher> reloadableMatchers = getReloadableMatchers();

		for (CIdentity identity : getAllIdentities()) {

			checkReloadToMatcher(reloadableMatchers, identity);
		}
	}

	private void checkReloadToMatcher(List<IMatcher> reloadables, CIdentity identity) {

		IFrame instance = getOrNull(identity, true);

		if (instance != null) {

			IMatcher matcher = lookForMatcher(reloadables, instance.getType());

			if (matcher != null) {

				matcher.add(instance, identity);
			}
		}
	}

	private IFrame checkRemove(CIdentity identity) {

		IFrame removed = null;

		if (indexes.hasIndex(identity)) {

			identities.remove(identity);
			types.remove(identity);

			int index = indexes.getIndex(identity);

			removed = getOrNull(identity, index, false);

			CFrame type = removed != null ? removed.getType() : getType(index);

			removeFromMatcher(type, identity);
			removeInstanceRefs(identity);

			fileStore.remove(index);
			indexes.freeIndex(identity);
		}

		return removed;
	}

	private void addInstanceRefs(IFrame instance, CIdentity identity) {

		addInstanceRefs(identity, instance.getAllReferenceIds());
	}

	private void addInstanceRefs(CIdentity identity, List<CIdentity> referenceIds) {

		for (CIdentity refedId : referenceIds) {

			referencingIds.add(refedId, identity);
		}
	}

	private void removeInstanceRefs(CIdentity identity) {

		for (CIdentity refingId : referencingIds.getSet(identity)) {

			removeReferenceId(refingId, identity);
		}

		referencingIds.removeAll(identity);
	}

	private void removeReferenceId(CIdentity refingId, CIdentity refedId) {

		int refingIndex = indexes.getIndex(refingId);
		IFrame refingInstance = getOrNull(refingId, refingIndex, true);

		if (refingInstance != null) {

			refingInstance.removeReferenceId(refedId);
			removeFromMatcher(refingInstance.getType(), refingId);

			fileStore.remove(refingIndex);
			fileStore.write(refingInstance, refingId, refingIndex);

			addToMatcher(refingInstance, refingId);
		}
	}

	private CFrame getType(int index) {

		return model.getFrames().get(fileStore.readTypeId(index));
	}

	private IFrame getOrNull(CIdentity identity, boolean freeInstance) {

		return getOrNull(identity, indexes.getIndex(identity), freeInstance);
	}

	private IFrame getOrNull(CIdentity identity, int index, boolean freeInstance) {

		IRegenInstance regen = fileStore.read(identity, index, freeInstance);

		if (regen.getStatus() == IRegenStatus.FULLY_INVALID) {

			return null;
		}

		return regen.getRootFrame();
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

	private List<IMatcher> getReloadableMatchers() {

		List<IMatcher> reloadables = new ArrayList<IMatcher>();

		for (IMatcher matcher : matchers) {

			if (matcher.rebuildOnStartup()) {

				reloadables.add(matcher);
			}
		}

		return reloadables;
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

		CFrame type = instance.getType();

		return regenAccessor.createRegenType(type.getIdentity(), type);
	}

	private IRegenType createRegenType(CIdentity typeId) {

		CFrame type = model.getFrames().getOrNull(typeId);

		return regenAccessor.createRegenType(typeId, type);
	}

	private IFrame createFreeCopy(IFrame instance) {

		return IFreeCopier.get().createFreeCopy(instance);
	}
}
