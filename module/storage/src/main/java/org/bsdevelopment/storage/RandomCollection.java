package org.bsdevelopment.storage;

import java.text.DecimalFormat;
import java.util.*;

public class RandomCollection<E> {
    private final NavigableMap<Double, Element<E>> map;
    private final Random random;
    private double total;

    /**
     * The function "randomize" takes a collection of elements and returns a randomized version of the collection.
     *
     * @param list A collection of elements that you want to randomize.
     * @return The method is returning an Element<T> object.
     */
    public static <T> Element<T> randomize(Collection<T> list) {
        return randomize(list, 50);
    }

    /**
     * The function randomize takes a collection of elements and a percentage, and returns a randomly selected element from
     * the collection based on the given percentage.
     *
     * @param list The "list" parameter is a collection of elements of type T.
     * @param percent The "percent" parameter represents the probability of selecting each element from the collection. It
     * is a value between 0 and 1, where 0 means no element will be selected and 1 means all elements will be selected.
     * @return The method is returning an Element<T> object.
     */
    public static <T> Element<T> randomize(Collection<T> list, double percent) {
        RandomCollection<T> collection = new RandomCollection<>();
        list.forEach(e -> collection.add(percent, e));
        return collection.next();
    }

    /**
     * The function returns a RandomCollection object created from a given collection, with a default weight of 50 for each
     * element.
     *
     * @param list The "list" parameter is a Collection of elements of type E. It represents the input collection from
     * which we want to create a RandomCollection.
     * @return The method is returning a RandomCollection object.
     */
    public static <E> RandomCollection<E> fromCollection(Collection<E> list) {
        return fromCollection(list, 50);
    }

    /**
     * The function creates a new RandomCollection object from a given collection, where each element has a specified
     * probability of being selected.
     *
     * @param list The "list" parameter is a collection of elements of type E. It can be any type of collection, such as a
     * List, Set, or Queue.
     * @param percent The "percent" parameter represents the probability of an element being selected from the collection.
     * It is a value between 0 and 1, where 0 means no elements will be selected and 1 means all elements will be selected.
     * @return The method is returning a RandomCollection<E> object.
     */
    public static <E> RandomCollection<E> fromCollection(Collection<E> list, double percent) {
        RandomCollection<E> collection = new RandomCollection<>();
        list.forEach(e -> collection.add(percent, e));
        return collection;
    }

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random var1) {
        this.map = new TreeMap<>();
        this.total = 0.0D;
        this.random = var1;
    }

    /**
     * The add method adds a value to a data structure at a specific index.
     *
     * @param value The parameter "value" is of type E, which means it can be any type of object.
     */
    public void add(E value) {
        add(50, value);
    }

    /**
     * The function adds a value to a map with a specified percentage as the key.
     *
     * @param percent The percent parameter represents the percentage value that will be associated with the value
     * parameter.
     * @param value The "value" parameter is the value that you want to add to the map. It can be of any type, as it is a
     * generic parameter.
     */
    public void add(double percent, E value) {
        if (percent > 0.0D) {
            this.total += percent;
            this.map.put(this.total, new Element<>(value, percent));
        }
    }

    /**
     * The function returns a collection of values from a map.
     *
     * @return The method is returning a collection of values from the map.
     */
    public Collection<Element<E>> values() {
        return map.values();
    }

    /**
     * The function returns a random element from a map based on a weighted probability.
     *
     * @return The method is returning an element of type `Element<E>`.
     */
    public Element<E> next() {
        double var1 = this.random.nextDouble() * this.total;
        return this.map.ceilingEntry(var1).getValue();
    }

    /**
     * The function checks if a map is empty and returns a boolean value.
     *
     * @return The method is returning a boolean value.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * The getSize() function returns the size of a map.
     *
     * @return The method is returning the size of the map.
     */
    public int getSize() {
        return map.size();
    }

    /**
     * The function `nextRemove()` returns and removes a random element from a map based on a weighted probability.
     *
     * @return The method is returning an object of type `Element<E>`.
     */
    public Element<E> nextRemove() {
        if (map.isEmpty()) return null;
        double var1 = this.random.nextDouble() * this.total;
        Element<E> value = this.map.ceilingEntry(var1).getValue();

        remove(value);

        return value;
    }

    /**
     * The `removeAll` function removes all elements with a specific value from a map and updates the total accordingly.
     *
     * @param value The parameter "value" is of type E, which means it can be any type of object. It represents the value
     * that we want to remove from the map.
     */
    public void removeAll (E value) {
        if (value == null) return;
        if (map.isEmpty()) return;

        List<Double> keys = new ArrayList<>();
        for (Map.Entry<Double, Element<E>> entry : map.entrySet()) {
            if (entry.getValue().value.equals(value)) keys.add(entry.getKey());
        }

        keys.forEach(map::remove);

        recalculate();
    }

    public void remove (Element<E> element) {
        if (element == null) return;
        remove(element.percent, element.value);
    }

    public void remove (double percent, E value) {
        if (value == null) return;
        if (map.isEmpty()) return;

        List<Double> keys = new ArrayList<>();
        for (Map.Entry<Double, Element<E>> entry : map.entrySet()) {
            if ((entry.getValue().percent == percent) && (entry.getValue().value.equals(value))) keys.add(entry.getKey());
        }

        keys.forEach(map::remove);

        recalculate();
    }

    public void clear () {
        map.clear();

        recalculate();
    }

    /**
     * The function recalculates the total and updates the map with the new total and sorted elements.
     */
    private void recalculate () {
        NavigableMap<Double, Element<E>> navigableMap = new TreeMap<>();
        double tempTotal = 0.0D;

        for (Map.Entry<Double, Element<E>> entry : map.entrySet()) {
            tempTotal += entry.getValue().percent;
            navigableMap.put(tempTotal, new Element<>(entry.getValue().value, entry.getValue().percent));
        }
        map.clear();

        this.total = tempTotal;
        map.putAll(navigableMap);
    }

    public static final class Element<Obj> {
        private final Obj value;
        private final double percent;

        private Element(Obj value, double percent) {
            this.value = value;
            this.percent = percent;
        }

        /**
         * The function returns the value of the percent variable.
         *
         * @return The method is returning a double value, which is the value of the variable "percent".
         */
        public double getPercent() {
            return percent;
        }

        /**
         * The getValue() function returns the value of an object.
         *
         * @return The method is returning an object of type "Obj".
         */
        public Obj getValue() {
            return value;
        }

        /**
         * The function calculates the probable chance by dividing the given percentage by 85 and then multiplying it by
         * 100, with the result being trimmed to 3 decimal places.
         *
         * @return The method is returning a double value, which is the probable chance calculated based on the given
         * formula.
         */
        public double getProbableChance() {
            return trim(3, ((percent / 85) * 100));
        }

        @Override
        public String toString() {
            return "Element{value=" + value + ", percent=" + percent + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Element<?> element = (Element<?>) o;
            return Double.compare(element.percent, percent) == 0 && Objects.equals(value, element.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, percent);
        }

        private double trim(int degree, double value) {
            DecimalFormat var5 = new DecimalFormat("#.#" + "#".repeat(Math.max(0, degree - 1)));
            return Double.parseDouble(var5.format(value));
        }
    }
}