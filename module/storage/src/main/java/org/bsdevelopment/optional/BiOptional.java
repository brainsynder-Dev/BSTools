package org.bsdevelopment.optional;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The `BiOptional` class provides a container that can hold two optional values of different types,
 * along with a set of operations to work with these values.
 *
 * @param <T> The type of the left value.
 * @param <U> The type of the right value.
 */
public class BiOptional<T, U> {

    @Nullable
    private final T left;
    @Nullable
    private final U right;

    /**
     * Constructs a `BiOptional` with specified left and right values.
     *
     * @param left  The left value.
     * @param right The right value.
     */
    public BiOptional(@Nullable T left, @Nullable U right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Creates a `BiOptional` with only the left value.
     *
     * @param left The left value.
     * @param <T>  The type of the left value.
     * @return A `BiOptional` with the left value and an absent right value.
     */
    public static <T, U> BiOptional<T, U> of(T left) {
        Optional<T> leftOption = Optional.empty();
        if (left != null) leftOption = Optional.of(left);
        return from(leftOption, Optional.empty());
    }

    /**
     * Creates a `BiOptional` with both left and right values.
     *
     * @param left  The left value.
     * @param right The right value.
     * @param <T>   The type of the left value.
     * @param <U>   The type of the right value.
     * @return A `BiOptional` containing both left and right values.
     */
    public static <T, U> BiOptional<T, U> of(T left, U right) {
        Optional<T> leftOption = Optional.empty();
        Optional<U> rightOption = Optional.empty();
        if (left != null) leftOption = Optional.of(left);
        if (right != null) rightOption = Optional.of(right);
        return from(leftOption, rightOption);
    }

    /**
     * Creates an empty `BiOptional` with absent left and right values.
     *
     * @return An empty `BiOptional`.
     */
    public static <T, U> BiOptional<T, U> empty() {
        return from(Optional.empty(), Optional.empty());
    }

    /**
     * Creates a `BiOptional` from `Optional` instances of left and right values.
     *
     * @param left  An `Optional` for the left value.
     * @param right An `Optional` for the right value.
     * @return A `BiOptional` with the values from the `Optional` instances or absent if not present.
     */
    public static <T, U> BiOptional<T, U> from(Optional<T> left, Optional<U> right) {
        return new BiOptional<>(left.orElse(null), right.orElse(null));
    }

    /**
     * Retrieves an `Optional` containing the left value.
     *
     * @return An `Optional` with the left value, which can be empty if the left value is absent.
     */
    public Optional<T> left() {
        return Optional.ofNullable(left);
    }

    /**
     * Retrieves an `Optional` containing the right value.
     *
     * @return An `Optional` with the right value, which can be empty if the right value is absent.
     */
    public Optional<U> right() {
        return Optional.ofNullable(right);
    }

    /**
     * Checks if the left value is present.
     *
     * @return `true` if the left value is present, `false` otherwise.
     */
    public boolean isLeftPresent() {
        return left != null;
    }

    /**
     * Checks if the right value is present.
     *
     * @return `true` if the right value is present, `false` otherwise.
     */
    public boolean isRightPresent() {
        return right != null;
    }

    /**
     * Checks if only the left value is present (right value is absent).
     *
     * @return `true` if only the left value is present, `false` otherwise.
     */
    public boolean isLeftOnlyPresent() {
        return isLeftPresent() && !isRightPresent();
    }

    /**
     * Checks if only the right value is present (left value is absent).
     *
     * @return `true` if only the right value is present, `false` otherwise.
     */
    public boolean isRightOnlyPresent() {
        return !isLeftPresent() && isRightPresent();
    }

    /**
     * Checks if both left and right values are present.
     *
     * @return `true` if both left and right values are present, `false` otherwise.
     */
    public boolean areBothPresent() {
        return isLeftPresent() && isRightPresent();
    }

    /**
     * Checks if both left and right values are absent.
     *
     * @return `true` if both left and right values are absent, `false` otherwise.
     */
    public boolean areNonePresent() {
        return !isLeftPresent() && !isRightPresent();
    }

    /**
     * Performs an action if only the left value is present.
     *
     * @param ifLeftOnlyPresent The action to perform if only the left value is present.
     * @return This `BiOptional` for method chaining.
     */
    public BiOptional<T, U> ifLeftOnlyPresent(Consumer<? super T> ifLeftOnlyPresent) {
        if (isLeftOnlyPresent()) ifLeftOnlyPresent.accept(left);
        return this;
    }

    /**
     * Performs an action if only the right value is present.
     *
     * @param ifRightOnlyPresent The action to perform if only the right value is present.
     * @return This `BiOptional` for method chaining.
     */
    public BiOptional<T, U> ifRightOnlyPresent(Consumer<? super U> ifRightOnlyPresent) {
        if (isRightOnlyPresent()) ifRightOnlyPresent.accept(right);
        return this;
    }

    /**
     * Performs an action if both left and right values are present.
     *
     * @param ifBothPresent The action to perform if both left and right values are present.
     * @return This `BiOptional` for method chaining.
     */
    public BiOptional<T, U> ifBothPresent(BiConsumer<? super T, ? super U> ifBothPresent) {
        if (areBothPresent()) ifBothPresent.accept(left, right);
        return this;
    }

    /**
     * Performs an action if both left and right values are absent.
     *
     * @param ifNonePresent The action to perform if both left and right values are absent.
     * @return This `BiOptional` for method chaining.
     */
    public BiOptional<T, U> ifNonePresent(Runnable ifNonePresent) {
        if (areNonePresent()) ifNonePresent.run();
        return this;
    }

    /**
     * Throws a custom exception if both left and right values are absent.
     *
     * @param throwableProvider A supplier that provides the exception to be thrown if both values are absent.
     * @param <X>               The type of exception to be thrown.
     * @throws X The exception provided by the `throwableProvider`.
     */
    public <X extends Throwable> void ifNonePresentThrow(Supplier<? extends X> throwableProvider) throws X {
        if (areNonePresent()) throw throwableProvider.get();
    }

    /**
     * Returns a `BiOptionalMapper` for mapping operations on this `BiOptional`.
     *
     * @param <R> The type of the result of mapping.
     * @return A `BiOptionalMapper` for mapping operations on this `BiOptional`.
     */
    public <R> BiOptionalMapper<T, U, R> mapper() {
        return new BiOptionalMapper<>(this);
    }
}

