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
public class KProxyPasswords {

	static private final int PROXY_LENGTH = 10;
	static private final int NUMBERS_LENGTH = PROXY_LENGTH * 2;

	static private List<Character> PROXY_CHARS = new ArrayList<Character>();

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

	private List<Integer> customiserNumbers;

	public KProxyPasswords(int customiser) {

		this(Integer.toString(customiser));
	}

	public KProxyPasswords(String customiser) {

		customiserNumbers = toNumbers(customiser.toCharArray());
	}

	public String toProxy(String password) {

		return toProxy(password.toCharArray());
	}

	public String toProxy(char[] password) {

		return String.valueOf(toProxyChars(password));
	}

	public char[] toProxyChars(char[] password) {

		List<Integer> numbers = toNumbers(password);

		numbers = combineNumbers(numbers, customiserNumbers);
		numbers = extendProxyNumbers(numbers);
		numbers = contractProxyNumbers(numbers);

		jiggleNumbers(numbers);

		return proxyNumbersToProxy(numbers);
	}

	private List<Integer> extendProxyNumbers(List<Integer> numbers) {

		if (numbers.size() < NUMBERS_LENGTH) {

			List<Integer> extras = new ArrayList<Integer>(numbers);

			while (numbers.size() < NUMBERS_LENGTH) {

				jiggleNumbers(extras);
				numbers.addAll(extras);
			}
		}

		return numbers;
	}

	private List<Integer> contractProxyNumbers(List<Integer> numbers) {

		return numbers.size() > NUMBERS_LENGTH
				? numbers.subList(0, NUMBERS_LENGTH)
				: numbers;
	}

	private char[] proxyNumbersToProxy(List<Integer> numbers) {

		char[] proxy = new char[PROXY_LENGTH];

		for (int i = 0 ; i < PROXY_LENGTH ; i++) {

			proxy[i] = PROXY_CHARS.get(numbers.get(i));
		}

		return proxy;
	}

	private List<Integer> toNumbers(char[] chars) {

		List<Integer> numbers = new ArrayList<Integer>();

		for (char c : chars) {

			numbers.add(toNumber(c));
		}

		return numbers;
	}

	private Integer toNumber(char c) {

		int number = PROXY_CHARS.indexOf(c);

		if (number == -1) {

			number = (int)c;

			while (number > PROXY_CHARS.size()) {

				number /= 2;
			}
		}

		return number;
	}

	private void jiggleNumbers(List<Integer> numbers) {

		List<Integer> startNumbers = new ArrayList<Integer>(numbers);

		for (int i = 0 ; i < startNumbers.size() ; i++) {

			jiggleNumbers(numbers, startNumbers.get(i) % numbers.size());
		}
	}

	private void jiggleNumbers(List<Integer> numbers, int jigglerIdx) {

		for (int i = 0 ; i < numbers.size() ; i++) {

			int current = numbers.get(i);
			int jiggler = numbers.get(jigglerIdx);

			numbers.set(i, combineNumbers(current, jiggler));

			jigglerIdx = (jigglerIdx + jiggler) % numbers.size();
		}
	}

	private List<Integer> combineNumbers(List<Integer> ns1, List<Integer> ns2) {

		List<Integer> combined = new ArrayList<Integer>();
		int combinedSize = ns1.size() > ns2.size() ? ns1.size() : ns2.size();

		for (int i = 0 ; i < combinedSize ; i++) {

			int n1 = ns1.get(i % ns1.size());
			int n2 = ns2.get(i % ns2.size());

			combined.add(combineNumbers(n1, n2));
		}

		return combined;
	}

	private int combineNumbers(int n1, int n2) {

		return (n1 + n2) % PROXY_CHARS.size();
	}
}