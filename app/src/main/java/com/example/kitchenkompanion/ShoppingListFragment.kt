package com.example.kitchenkompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class ShoppingListFragment : Fragment() {

    private lateinit var adapter: ShoppingListAdapter
    private lateinit var etNewItem: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shopping_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.shoppingListView)
        val backButton = view.findViewById<ImageView>(R.id.btnBackHome)
        val btnAddItem = view.findViewById<Button>(R.id.btnAddItem)
        val btnSuggestLow = view.findViewById<Button>(R.id.btnSuggestLow)
        val btnAddFromRecipe = view.findViewById<Button>(R.id.btnAddFromRecipe)
        etNewItem = view.findViewById(R.id.etNewItem)

        adapter = ShoppingListAdapter(requireContext(), ShoppingListData.items)
        listView.adapter = adapter

        btnAddItem.setOnClickListener {
            val itemName = etNewItem.text.toString().trim()
            if (itemName.isNotEmpty()) {
                ShoppingListData.addItems(listOf(itemName), isSuggested = false)
                sortAndNotify()
                etNewItem.text.clear()
            }
        }

        btnSuggestLow.setOnClickListener {
            val lowStockItems = listOf("Milk", "Eggs", "Bread")
            ShoppingListData.addItems(lowStockItems, isSuggested = true)
            sortAndNotify()
            Toast.makeText(context, "Added suggestions", Toast.LENGTH_SHORT).show()
        }

        btnAddFromRecipe.setOnClickListener {
            val allMissing = RecipeData.recipes.flatMap { it.missingIngredients }.distinct()
            if (allMissing.isNotEmpty()) {
                ShoppingListData.addItems(allMissing, isSuggested = true)
                sortAndNotify()
                Toast.makeText(context, "Added recipe suggestions", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun sortAndNotify() {
        // Sort to keep "My Items" at the top and "Suggested" at the bottom
        ShoppingListData.items.sortBy { it.isSuggested }
        adapter.notifyDataSetChanged()
    }
}
