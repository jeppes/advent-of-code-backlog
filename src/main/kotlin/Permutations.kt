fun <T> Set<T>.permutations(): List<List<T>> {
    fun permute(list: MutableList<T>, start: Int): List<List<T>> {
        if (start == list.size - 1) {
            return listOf(list.toList())
        }

        val result = mutableListOf<List<T>>()
        for (i in start until list.size) {
            list[start] = list[i].also { list[i] = list[start] }
            result += permute(list, start + 1)
            list[start] = list[i].also { list[i] = list[start] }
        }

        return result
    }

    return permute(toMutableList(), 0)
}