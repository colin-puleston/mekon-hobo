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

package uk.ac.manchester.cs.mekon_util.gui;

import java.awt.event.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
abstract class GTextEntryListener extends KeyAdapter {

	public void keyTyped(KeyEvent event) {

		if (!keyInputEnabled() || !acceptKey(event)) {

			event.consume();
		}
	}

	public void keyReleased(KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.VK_ENTER) {

			onTextEntered();
		}
		else {

			if (!event.isActionKey()) {

				onCharEntered(event.getKeyChar());
			}
		}
	}

	GTextEntryListener(JTextField field) {

		field.addKeyListener(this);
	}

	abstract boolean keyInputEnabled();

	abstract boolean acceptChar(char testChar);

	abstract void onCharEntered(char enteredChar);

	abstract void onTextEntered();

	private boolean acceptKey(KeyEvent event) {

		return event.isActionKey() || acceptChar(event.getKeyChar());
	}
}
