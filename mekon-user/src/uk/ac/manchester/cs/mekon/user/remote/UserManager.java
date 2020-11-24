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

package uk.ac.manchester.cs.mekon.user.remote;

import java.util.*;

import uk.ac.manchester.cs.mekon_util.remote.admin.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.client.*;

/**
 * @author Colin Puleston
 */
abstract class UserManager {

	private RAdminClient adminClient;
	private UserUpdates userUpdates;

	private List<RUserProfile> profiles = new ArrayList<RUserProfile>();
	private ProfileComparator profileComparator = new ProfileComparator();

	private class ProfileComparator implements Comparator<RUserProfile> {

		public int compare(RUserProfile first, RUserProfile second) {

			return first.getName().compareTo(second.getName());
		}
	}

	private class UpdateProcess implements Runnable {

		public void run() {

			while (true) {

				waitABit();
				updateFromServer();
			}
		}

		UpdateProcess() {

			new Thread(this).start();
		}

		private void waitABit() {

			try {

				Thread.sleep(10000);
			}
			catch (InterruptedException e) {

				throw new RuntimeException(e);
			}
		}
	}

	UserManager(RAdminClient adminClient, UserUpdates userUpdates) {

		this.adminClient = adminClient;
		this.userUpdates = userUpdates;

		new UpdateProcess();
	}

	synchronized void updateFromServer() {

		List<RUserProfile> serverProfiles = adminClient.getUserProfiles();

		if (!profileSetsEqual(profiles, serverProfiles)) {

			profiles = serverProfiles;

			onUpdate(true);
		}
	}

	synchronized void addUser() {

		RUserProfile profile = userUpdates.checkAddUser();

		if (profile != null) {

			profiles.add(profile);

			onUpdate(true);
		}
	}

	synchronized void editUser(int index) {

		editUser(profiles.get(index));
	}

	synchronized void deleteUser(int index) {

		deleteUser(profiles.get(index));
	}

	synchronized List<RUserProfile> getProfiles() {

		return profiles;
	}

	abstract void onUpdate();

	private void editUser(RUserProfile profile) {

		String name = profile.getName();
		String role = profile.getRoleName();

		String newRole = userUpdates.checkEditRole(name, role);

		if (newRole != null) {

			profiles.remove(profile);
			profiles.add(profile.deriveProfileWithRole(newRole));

			onUpdate(true);
		}
	}

	private void deleteUser(RUserProfile profile) {

		String name = profile.getName();

		if (userUpdates.checkRemoveUser(name)) {

			profiles.remove(profile);

			onUpdate(false);
		}
	}

	private void onUpdate(boolean sort) {

		if (sort) {

			sortProfiles();
		}

		onUpdate();
	}

	private void sortProfiles() {

		TreeSet<RUserProfile> sorter = new TreeSet<RUserProfile>(profileComparator);

		sorter.addAll(profiles);

		profiles.clear();
		profiles.addAll(sorter);
	}

	private boolean profileSetsEqual(List<RUserProfile> list1, List<RUserProfile> list2) {

		return profilesAsSet(list1).equals(profilesAsSet(list2));
	}

	private Set<RUserProfile> profilesAsSet(List<RUserProfile> list) {

		return new HashSet<RUserProfile>(list);
	}
}
