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

package uk.ac.manchester.cs.mekon.user.app;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
class MekonAppConfig {

	static private final String CONFIG_FILE_NAME = "mekon-app.xml";

	static private final String INSTANCE_GROUP_ID = "InstanceGroup";

	static private final String TITLE_ATTR = "title";
	static private final String INSTANCE_GROUP_ROOT_ATTR = "rootType";
	static private final String INSTANCE_GROUP_EDIT_ATTR = "editable";

	private MekonApp app;
	private KConfigNode rootNode;

	MekonAppConfig(MekonApp app) {

		this(app, new KConfigFile(CONFIG_FILE_NAME));
	}

	MekonAppConfig(MekonApp app, KConfigFile configFile) {

		this.app = app;

		rootNode = configFile.getRootNode();

		checkReadTitle();
		readInstanceGroups();
	}

	private void checkReadTitle() {

		String title = rootNode.getString(TITLE_ATTR, null);

		if (title != null) {

			app.setTitle(title);
		}
	}

	private void readInstanceGroups() {

		for (KConfigNode typeNode : rootNode.getChildren(INSTANCE_GROUP_ID)) {

			app.addInstanceGroup(
				getInstanceGroup(typeNode),
				editableInstanceGroup(typeNode));
		}
	}

	private CFrame getInstanceGroup(KConfigNode node) {

		return app.getModel().getFrames().get(getInstanceGroupRootId(node));
	}

	private CIdentity getInstanceGroupRootId(KConfigNode node) {

		return new CIdentity(node.getString(INSTANCE_GROUP_ROOT_ATTR));
	}

	private boolean editableInstanceGroup(KConfigNode node) {

		return node.getBoolean(INSTANCE_GROUP_EDIT_ATTR, false);
	}
}