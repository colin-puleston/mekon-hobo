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

package uk.ac.manchester.cs.mekon.app;

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceIdsList extends GList<CIdentity> {

	static private final long serialVersionUID = -1;

	static private final String TYPE_ENHANCED_LABEL_FORMAT = "[%s] %s";

	private InstanceGroup instanceGroup;
	private boolean queryInstances;

	InstanceIdsList(InstanceGroup instanceGroup, boolean queryInstances) {

		super(false, true);

		this.instanceGroup = instanceGroup;
		this.queryInstances = queryInstances;
	}

	void update(Collection<CIdentity> ids) {

		clearList();
		addIds(ids);
	}

	void addIds(Collection<CIdentity> ids) {

		for (CIdentity id : ids) {

			addId(id);
		}
	}

	void addId(CIdentity id) {

		addEntity(id, getCellDisplay(id));
	}

	void checkAddId(CIdentity id) {

		if (!containsEntity(id)) {

			addId(id);
		}
	}

	void replaceId(CIdentity id, CIdentity newId) {

		removeEntity(id);
		addId(newId);
	}

	GCellDisplay getCellDisplay(CIdentity id) {

		if (instanceGroup.hasSubTypes()) {

			return createTypeEnhancedCellDisplay(id);
		}

		return new GCellDisplay(id.getLabel(), getIcon());
	}

	private GCellDisplay createTypeEnhancedCellDisplay(CIdentity id) {

		GCellDisplay display = new GCellDisplay(createTypeEnhancedLabel(id), getIcon());

		display.setFilterText(id.getLabel());

		return display;
	}

	private String createTypeEnhancedLabel(CIdentity id) {

		return String.format(TYPE_ENHANCED_LABEL_FORMAT, getTypeLabel(id), id.getLabel());
	}

	private String getTypeLabel(CIdentity id) {

		return instanceGroup.getInstanceType(id).getIdentity().getLabel();
	}

	private Icon getIcon() {

		return queryInstances ? MekonAppIcons.QUERY_REF : MekonAppIcons.ASSERTION_REF;
	}
}