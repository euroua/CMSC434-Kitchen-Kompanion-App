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
                // If it was manual and now suggested, or vice versa, we keep the original flag or update? 
                // Let's keep the suggested flag if it's ever suggested.
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