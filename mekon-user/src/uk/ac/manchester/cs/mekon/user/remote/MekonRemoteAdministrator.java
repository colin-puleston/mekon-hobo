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

import java.net.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.remote.admin.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.client.*;
import uk.ac.manchester.cs.mekon_util.misc.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
public class MekonRemoteAdministrator extends GFrame {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Mekon Remote Administrator";

	static private final int FRAME_WIDTH = 550;
	static private final int FRAME_HEIGHT = 300;

	private RAdminClient adminClient;

	public MekonRemoteAdministrator(URL serverURL, KProxyPasswords proxyPasswords) {

		super(TITLE, FRAME_WIDTH, FRAME_HEIGHT);

		adminClient = new RAdminClient(serverURL);

		if (attemptLogin(proxyPasswords)) {

			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			display(new UsersPanel(adminClient));
		}
	}

	private boolean attemptLogin(KProxyPasswords proxyPasswords) {

		return createLoginDialog(proxyPasswords).checkLogin() != RLoginResult.LOGIN_FAILED;
	}

	private RLoginDialog createLoginDialog(KProxyPasswords proxyPasswords) {

		return new RAdministratorLoginDialog(adminClient, proxyPasswords, TITLE);
	}
}
