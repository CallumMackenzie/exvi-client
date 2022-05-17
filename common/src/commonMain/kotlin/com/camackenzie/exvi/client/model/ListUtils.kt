package com.camackenzie.exvi.client.model

object ListUtils {
    inline fun <T> getExerciseBy(sorted: List<T>, crossinline comparator: (T) -> Int): T? {
        val idx = sorted.binarySearch { comparator(it) }
        return if (idx < 0) null
        else sorted[idx]
    }

    inline fun <T> getExercisesBy(sorted: List<T>, crossinline comparator: (T) -> Int) : List<T> {
        // Binary search to find general location of elements
        val idxInRange = sorted.binarySearch { comparator(it) }

        // Find the start of the matching elements
        // TODO: Use a binary edge detection search
        var startIndex = idxInRange
        do {
            --startIndex
        } while (startIndex >= 0 && comparator(sorted[startIndex]) == 0)

        // Find the end of the matching elements
        // TODO: Use a binary edge detection search
        var endIndex = idxInRange
        do {
            ++endIndex
        } while (endIndex < sorted.size && comparator(sorted[endIndex]) == 0)

        return sorted.subList(startIndex, endIndex)
    }

    inline fun <T> addToSortedArray(value: T, sorted: MutableList<T>, crossinline comparator: (T) -> Int) {
        var position = sorted.binarySearch(comparison = { comparator(it) })
        if (position < 0) position = -(position + 1)
        sorted.add(position, value)
    }
}