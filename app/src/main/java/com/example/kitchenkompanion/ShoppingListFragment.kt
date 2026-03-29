package com.example.kitchenkompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import androidx.fragment.app.Fragment

class ShoppingListFragment : Fragment() {

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

        val adapter = ShoppingListAdapter(requireContext(), ShoppingListData.items)
        listView.adapter = adapter

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}