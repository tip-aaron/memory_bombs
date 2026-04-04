package flip_n_match.lib;

import java.util.ArrayList;
import java.util.List;

public class MergeSorter {

    /**
     * Sorts a list of Comparable objects using the Merge Sort algorithm.
     */
    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        if (list.size() < 2) {
            return;
        }

        int mid = list.size() / 2;

        List<T> left = new ArrayList<>(list.subList(0, mid));
        List<T> right = new ArrayList<>(list.subList(mid, list.size()));

        sort(left);
        sort(right);

        merge(list, left, right);
    }

    private static <T extends Comparable<? super T>> void merge(List<T> list, List<T> left, List<T> right) {
        int leftIndex = 0;
        int rightIndex = 0;
        int listIndex = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            if (left.get(leftIndex).compareTo(right.get(rightIndex)) <= 0) {
                list.set(listIndex++, left.get(leftIndex++));
            } else {
                list.set(listIndex++, right.get(rightIndex++));
            }
        }

        while (leftIndex < left.size()) {
            list.set(listIndex++, left.get(leftIndex++));
        }

        while (rightIndex < right.size()) {
            list.set(listIndex++, right.get(rightIndex++));
        }
    }
}