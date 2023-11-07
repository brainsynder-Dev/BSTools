package org.bsdevelopment.optional;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The `BiOptionalMapper` class is used to perform mapping operations on a `BiOptional`, producing a result.
 * It allows mapping functions to be applied based on the presence or absence of left and right values.
 *
 * @param <T> The type of the left value.
 * @param <U> The type of the right value.
 * @param <R> The type of the result produced by mapping operations.
 */
public class BiOptionalMapper<T, U, R> {

    private final BiOptional<T, U> biOptional;
    private R result = null;

    /**
     * Constructs a `BiOptionalMapper` with the provided `BiOptional` to perform mapping operations on.
     *
     * @param biOptional The `BiOptional` to be used for mapping operations.
     */
    BiOptionalMapper(BiOptional<T, U> biOptional) {
        this.biOptional = biOptional;
    }

    /**
     * Maps a result using a function if only the left value is present.
     *
     * @param leftMapper The mapping function to apply to the left value.
     * @return This `BiOptionalMapper` for method chaining.
     */
    public BiOptionalMapper<T, U, R> onLeftOnlyPresent(Function<? super T, ? extends R> leftMapper) {
        if (biOptional.isLeftOnlyPresent()) setResult(leftMapper.apply(biOptional.left().get()));
        return this;
    }

    /**
     * Maps a result using a function if only the right value is present.
     *
     * @param rightMapper The mapping function to apply to the right value.
     * @return This `BiOptionalMapper` for method chaining.
     */
    public BiOptionalMapper<T, U, R> onRightOnlyPresent(Function<? super U, ? extends R> rightMapper) {
        if (biOptional.isRightOnlyPresent()) setResult(rightMapper.apply(biOptional.right().get()));
        return this;
    }

    /**
     * Maps a result using a function if both left and right values are present.
     *
     * @param bothMapper The mapping function to apply to both left and right values.
     * @return This `BiOptionalMapper` for method chaining.
     */
    public BiOptionalMapper<T, U, R> onBothPresent(BiFunction<? super T, ? super U, ? extends R> bothMapper) {
        if (biOptional.areBothPresent()) setResult(bothMapper.apply(biOptional.left().get(), biOptional.right().get()));
        return this;
    }

    /**
     * Maps a result using a supplier if both left and right values are absent.
     *
     * @param supplier The supplier to provide a result when both values are absent.
     * @return This `BiOptionalMapper` for method chaining.
     */
    public BiOptionalMapper<T, U, R> onNonePresent(Supplier<? extends R> supplier) {
        if (biOptional.areNonePresent()) setResult(supplier.get());
        return this;
    }

    /**
     * Maps a result to a provided value if both left and right values are absent.
     *
     * @param other The value to use as the result when both values are absent.
     * @return This `BiOptionalMapper` for method chaining.
     */
    public BiOptionalMapper<T, U, R> onNonePresent(R other) {
        if (biOptional.areNonePresent()) setResult(other);
        return this;
    }

    /**
     * Throws a custom exception if both left and right values are absent.
     *
     * @param throwableProvider A supplier that provides the exception to be thrown.
     * @param <X>               The type of exception to be thrown.
     * @return This `BiOptionalMapper` for method chaining.
     * @throws X The exception provided by the `throwableProvider`.
     */
    public <X extends Throwable> BiOptionalMapper<T, U, R> onNonePresentThrow(Supplier<? extends X> throwableProvider) throws X {
        biOptional.ifNonePresentThrow(throwableProvider);
        return this;
    }

    /**
     * Retrieves the result of mapping operations. Throws an exception if the result is absent.
     *
     * @return The result of mapping operations.
     * @throws IllegalStateException if the result is absent.
     */
    public R result() {
        if (result == null) throw new IllegalStateException("Result absent");
        return result;
    }

    /**
     * Retrieves the result of mapping operations as an `Optional`. The `Optional` may be empty if the result is absent.
     *
     * @return An `Optional` containing the result or empty if the result is absent.
     */
    public Optional<R> optionalResult() {
        return Optional.ofNullable(result);
    }

    private void setResult(R result) {
        if (result == null) throw new IllegalArgumentException("Null obtained from a mapper");
        if (this.result != null) throw new IllegalStateException("Result already present: " + this.result);
        this.result = result;
    }
}
