package com.example.kitchenkompanion

object ShoppingListData {
    val items = mutableListOf<ShoppingItem>()

    fun addItems(newItems: List<String>, isSuggested: Boolean = false) {
        for (name in newItems) {
            val existing = items.find { it.name.equals(name, ignoreCase = true) }
            if (existing == null) {
                items.add(ShoppingItem(name, 1, false, isSuggested))
            } else {
                existing.quantity += 1
                if (isSuggested) existing.isSuggested = true
            }
        }
    }
}

data class ShoppingItem(
    val name: String,
    var quantity: Int = 1,
    var checked: Boolean = false,
    var isSuggested: Boolean = false
)