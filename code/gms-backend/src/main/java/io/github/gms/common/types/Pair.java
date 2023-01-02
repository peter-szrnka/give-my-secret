package io.github.gms.common.types;

/**
 * @author Peter Szrnka
 * 
 * Based on Dick Wall's implementation:
 * http://www.developer.com/java/data/article.php/10932_3813031_1/Java-Needs-to-Get-a-Pair-and-a-Triple.htm
 * 
 * Simplified version
 *
 * @param <A> Type of the first object
 * @param <B> Type of the second object
 * @since 1.0
 */
public class Pair<A, B> {

	public static <A, B> Pair<A, B> of(final A first, final B second) {
		return new Pair<>(first, second);
	}

	public final A first;
	public final B second;

	protected Pair(final A first, final B second) {
		this.first = first;
		this.second = second;
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return String.format("Pair[%s,%s]", first, second);
	}
}
