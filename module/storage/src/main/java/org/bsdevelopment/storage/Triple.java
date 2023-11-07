package org.bsdevelopment.storage;

/**
 * The {@code Triple} class is a generic class for storing three values of potentially different types.
 * @param <L> The type of the left value.
 * @param <M> The type of the middle value.
 * @param <R> The type of the right value.
 */
public final class Triple<L, M, R> {
    // The left, middle, and right values.
    public L left;
    public M middle;
    public R right;

    /**
     * Creates a new Triple with the specified left, middle, and right values.
     *
     * @param left   The left value.
     * @param middle The middle value.
     * @param right  The right value.
     */
    public Triple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    /**
     * Creates and returns a new Triple with the specified left, middle, and right values.
     *
     * @param left   The left value.
     * @param middle The middle value.
     * @param right  The right value.
     * @param <L>    The type of the left value.
     * @param <M>    The type of the middle value.
     * @param <R>    The type of the right value.
     * @return A new Triple with the specified values.
     */
    public static <L, M, R> Triple<L, M, R> of(L left, M middle, R right) {
        return new Triple(left, middle, right);
    }

    /**
     * Sets the left value of the Triple.
     *
     * @param left The left value.
     * @return This Triple instance for method chaining.
     */
    public Triple setLeft(L left) {
        this.left = left;
        return this;
    }

    /**
     * Sets the middle value of the Triple.
     *
     * @param middle The middle value.
     * @return This Triple instance for method chaining.
     */
    public Triple setMiddle(M middle) {
        this.middle = middle;
        return this;
    }

    /**
     * Sets the right value of the Triple.
     *
     * @param right The right value.
     * @return This Triple instance for method chaining.
     */
    public Triple setRight(R right) {
        this.right = right;
        return this;
    }

    /**
     * Gets the left value of the Triple.
     *
     * @return The left value.
     */
    public L getLeft() {
        return this.left;
    }

    /**
     * Gets the middle value of the Triple.
     *
     * @return The middle value.
     */
    public M getMiddle() {
        return this.middle;
    }

    /**
     * Gets the right value of the Triple.
     *
     * @return The right value.
     */
    public R getRight() {
        return this.right;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "left=" + left +
                ", middle=" + middle +
                ", right=" + right +
                '}';
    }
}
