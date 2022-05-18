package com.camackenzie.exvi.client.model

object ListUtils {

    inline fun <T, E> groupByToSet(list: Array<E>, crossinline supplier: (E) -> T): Map<T, Set<E>> {
        val ret = HashMap<T, MutableSet<E>>()
        for (item in list) {
            val bucketKey = supplier(item)
            if (ret[bucketKey] == null) ret[bucketKey] = HashSet(list.size / 6 + 10)
            ret[bucketKey]!!.add(item)
        }
        return ret
    }

    inline fun <T, E> groupBy(list: Array<E>, crossinline supplier: (E) -> T): Map<T, List<E>> {
        val ret = HashMap<T, ArrayList<E>>()
        for (item in list) {
            val bucketKey = supplier(item)
            if (ret[bucketKey] == null) ret[bucketKey] = ArrayList(list.size / 6 + 10)
            ret[bucketKey]!!.add(item)
        }
        for (item in ret.values) item.trimToSize()
        return ret
    }

    inline fun <T> getExerciseBy(sorted: List<T>, crossinline comparator: (T) -> Int): T? {
        val idx = sorted.binarySearch { comparator(it) }
        return if (idx < 0) null
        else sorted[idx]
    }

    inline fun <T> addToSortedArray(value: T, sorted: MutableList<T>, crossinline comparator: (T) -> Int) {
        var position = sorted.binarySearch(comparison = { comparator(it) })
        if (position < 0) position = -(position + 1)
        sorted.add(position, value)
    }

    inline fun <T> addAllToSortedArray(
        values: Array<T>,
        sorted: MutableList<T>,
        crossinline comparator: (T, T) -> Int
    ) {
        for (item in values) addToSortedArray(item, sorted) {
            comparator(item, it)
        }
    }
}