/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.collection;

import static java.lang.Math.min;
import static java.lang.String.format;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.0
 */
public class ArrayMSeq<T> extends ArraySeq<T> implements MSeq<T> {

	private static final long serialVersionUID = 1L;

	private ArrayISeq<T> _iseq = null;

	public ArrayMSeq(final Array<T> array) {
		super(array);
	}

	@Override
	public MSeq<T> copy() {
		return new ArrayMSeq<>(array.copy());
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayMIterator<>(array);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ArrayMIterator<>(array);
	}

	@Override
	public void set(final int index, final T value) {
		array.set(index, value);
	}

	@Override
	public MSeq<T> setAll(final Iterator<? extends T> it) {
		for (int i = 0; i < array.length() && it.hasNext(); ++i) {
			array.set(i, it.next());
		}
		return this;
	}

	@Override
	public MSeq<T> setAll(final Iterable<? extends T> values) {
		return setAll(values.iterator());
	}

	@Override
	public MSeq<T> setAll(final T[] values) {
		for (int i = 0, n = min(array.length(), values.length); i < n; ++i) {
			array.set(i, values[i]);
		}
		return this;
	}

	public MSeq<T> fill(final Supplier<? extends T> supplier) {
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, supplier.get());
		}
		return this;
	}

	@Override
	public MSeq<T> shuffle(final Random random) {
		for (int j = length() - 1; j > 0; --j) {
			swap(j, random.nextInt(j + 1));
		}
		return this;
	}

	@Override
	public void swap(final int i, final int j) {
		final T temp = array.get(i);
		array.set(i, array.get(j));
		array.set(j, temp);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void swap(int start, int end, MSeq<T> other, int otherStart) {
		checkIndex(start, end, otherStart, other.length());

		if (start < end) {
			for (int i = end - start; --i >= 0;) {
				final T temp = array.get(i + start);
				array.set(i + start, other.get(otherStart + i));
				other.set(otherStart + i, temp);
			}
		}
	}

	private void checkIndex(
		final int start, final int end,
		final int otherStart, final int otherLength
	) {
		array.checkIndex(start, end);
		if (otherStart < 0 || (otherStart + (end - start)) > otherLength) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Invalid index range: [%d, %d)",
				otherStart, otherStart + end - start
			));
		}
	}

	@Override
	public MSeq<T> subSeq(final int start, final int end) {
		return new ArrayMSeq<>(array.slice(start, end));
	}

	@Override
	public MSeq<T> subSeq(final int start) {
		return new ArrayMSeq<>(array.slice(start, length()));
	}

	@Override
	public <B> MSeq<B> map(final Function<? super T, ? extends B> mapper) {
		final Array<B> mapped = Array.of(ObjectStore.ofLength(length()));
		for (int i = 0; i < length(); ++i) {
			mapped.set(i, mapper.apply(array.get(i)));
		}
		return new ArrayMSeq<>(mapped);
	}

	@Override
	public ISeq<T> toISeq() {
		if (_iseq == null) {
			_iseq = new ArrayISeq<>(array.seal());
		}

		return _iseq;
	}

	@Override
	public List<T> asList() {
		return new ArrayMList<>(array);
	}

}