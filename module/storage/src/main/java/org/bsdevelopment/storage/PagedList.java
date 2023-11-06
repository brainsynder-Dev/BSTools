package org.bsdevelopment.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The `PagedList` class extends `ArrayList` to provide a paginated list of items, allowing you to group items into pages
 * with a specified content limit per page.
 *
 * @param <T> The type of items stored in the paginated list.
 */
public class PagedList<T> extends ArrayList<T> {
    private final int contentLimit;

    /**
     * Creates a new `PagedList` with a specified content limit.
     *
     * @param contentLimit The number of items that can be in each page.
     */
    public PagedList(int contentLimit) {
        this(contentLimit, new ArrayList<>());
    }

    /**
     * Creates a new `PagedList` with a specified content limit and initial items.
     *
     * @param contentLimit The number of items that can be in each page.
     * @param objects      The collection of items to be added to the list.
     */
    public PagedList(int contentLimit, T... objects) {
        this(contentLimit, Arrays.asList(objects));
    }

    /**
     * Creates a new `PagedList` with a specified content limit and initial items.
     *
     * @param contentLimit The number of items that can be in each page.
     * @param objects      The collection of items to be added to the list.
     */
    public PagedList(int contentLimit, List<T> objects) {
        this.contentLimit = contentLimit;
        addAll(objects);
    }

    /**
     * Returns the content limit, which is the number of items that can be in each page.
     *
     * @return The content limit.
     */
    public int getContentLimit() {
        return contentLimit;
    }

    /**
     * Calculates and returns the total number of pages in the list.
     *
     * @return The total number of pages.
     */
    public int totalPages() {
        return (((int) Math.ceil((double) size() / contentLimit)));
    }

    /**
     * Checks if a selected page exists.
     *
     * @param page The page number to check.
     * @return true if the page exists, otherwise false.
     */
    public boolean exists(int page) {
        page = (page - 1);
        return !(page < 0) && page < totalPages();
    }

    /**
     * Fetches the items for the selected page.
     *
     * @param page The selected page.
     * @return A `List` containing the items on the specified page.
     * @throws IndexOutOfBoundsException if the page is out of bounds.
     */
    public List<T> getPage(int page) {
        page = (page - 1);
        if (page < 0 || page >= totalPages())
            throw new IndexOutOfBoundsException("Index: " + page + ", Size: " + totalPages());
        List<T> objects = new ArrayList<>();
        int min = page * contentLimit;
        int max = ((page * contentLimit) + contentLimit);
        if (max > size()) max = size();
        for (int i = min; max > i; i++) objects.add(get(i));
        return objects;
    }
}
