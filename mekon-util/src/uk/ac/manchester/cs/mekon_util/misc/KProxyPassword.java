/**
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
package uk.ac.manchester.cs.mekon_util.misc;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class KProxyPassword {

	static private final int PROXY_LENGTH = 10;
	static private final int PROXY_DIGITS_LENGTH = PROXY_LENGTH * 2;

	static private List<Character> PROXY_CHARS = new ArrayList<Character>();

	static private class AProxyPassword extends KProxyPassword {

		AProxyPassword(String password) {

			super(password);
		}
	}

	static private class BProxyPassword extends KProxyPassword {

		BProxyPassword(String password) {

			super(password);
		}
	}

	static public void main(String[] args) {

		AProxyPassword aCreator = new AProxyPassword(args[0]);
		BProxyPassword bCreator = new BProxyPassword(args[0]);

		System.out.println("\nINPUT: " + args[0]);
		System.out.println("PROXY-A: " + String.valueOf(aCreator.getProxy()));
		System.out.println("PROXY-B: " + String.valueOf(bCreator.getProxy()));
	}

	static {

		addProxyChars('0','9');
		addProxyChars('a','z');
		addProxyChars('A','Z');
	}

	static void addProxyChars(int start, int end) {

		for (int i = start ; i <= end ; i++) {

			PROXY_CHARS.add((char)i);
		}
	}

	private char[] password;

	private List<Integer> customiserDigits;

	KProxyPassword(String password) {

		this(password.toCharArray());
	}

	KProxyPassword(char[] password) {

		this.password = password;

		customiserDigits = toDigits(getCustomiserChars());

		jiggleDigits(customiserDigits);
	}

	public char[] getProxy() {

		List<Integer> digits = toDigits(password);

		customiseProxyDigits(digits);
		jiggleDigits(digits);

		digits = normaliseProxyDigits(digits);

		jiggleDigits(digits);

		return proxyDigitsToProxy(digits);
	}

	private void customiseProxyDigits(List<Integer> digits) {

		for (int i = 0 ; i < digits.size() ; i++) {

			int ci = i % customiserDigits.size();

			digits.set(i, (digits.get(i) + customiserDigits.get(ci)) % 10);
		}
	}

	private List<Integer> normaliseProxyDigits(List<Integer> digits) {

		if (digits.size() < PROXY_DIGITS_LENGTH) {

			digits.addAll(customiserDigits);
			jiggleDigits(digits);
		}

		return digits.subList(0, PROXY_DIGITS_LENGTH);
	}

	private char[] proxyDigitsToProxy(List<Integer> digits) {

		char[] proxy = new char[PROXY_LENGTH];

		for (int i = 0 ; i < digits.size() ; i += 2) {

			int tens = digits.get(i) * 10;
			int units = digits.get(i + 1);

			proxy[i/2] = PROXY_CHARS.get((tens + units) % PROXY_CHARS.size());
		}

		return proxy;
	}

	private void jiggleDigits(List<Integer> digits) {

		List<Integer> startDigits = new ArrayList<Integer>(digits);

		for (int i = 0 ; i < startDigits.size() ; i++) {

			jiggleDigits(digits, startDigits.get(i) % digits.size());
		}
	}

	private void jiggleDigits(List<Integer> digits, int jigglerIdx) {

		for (int i = 0 ; i < digits.size() ; i++) {

			int current = digits.get(i);
			int jiggler = digits.get(jigglerIdx);

			digits.set(i, (current + jiggler) % 10);

			jigglerIdx = (jigglerIdx + jiggler) % digits.size();
		}
	}

	private List<Integer> toDigits(char[] chars) {

		List<Integer> digits = new ArrayList<Integer>();

		for (char c : chars) {

			digits.addAll(toDigits(c));
		}

		return digits;
	}

	private List<Integer> toDigits(char c) {

		int i = PROXY_CHARS.indexOf(c);

		return toDigits(i == -1 ? (int)c : i);
	}

	private List<Integer> toDigits(int i) {

		LinkedList<Integer> digits = new LinkedList<Integer>();

		while (i > 0) {

			digits.addFirst(i % 10);

			i = i / 10;
		}

		while (digits.size() < 2) {

			digits.addFirst(0);
		}

		return digits;
	}

	private char[] getCustomiserChars() {

		return getClass().getSimpleName().toCharArray();
	}
}