package com.example.kitchenkompanion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TodoFragment : Fragment(R.layout.fragment_todo) {

    private val items = mutableListOf<TodoItem>()
    private lateinit var adapter: TodoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val input = view.findViewById<EditText>(R.id.todoInput)
        val addBtn = view.findViewById<Button>(R.id.addButton)
        val clearBtn = view.findViewById<Button>(R.id.clearButton)
        val recycler = view.findViewById<RecyclerView>(R.id.todoRecycler)

        // fill some shopping list items
        if (items.isEmpty()) {
            items.add(TodoItem("Milk"))
            items.add(TodoItem("Bread"))
            items.add(TodoItem("Eggs"))
            items.add(TodoItem("Cheese"))
        }

        adapter = TodoAdapter(items) { pos: Int ->
            if (pos != RecyclerView.NO_POSITION) {
                items.removeAt(pos)
                adapter.notifyItemRemoved(pos)
                Toast.makeText(requireContext(), "Removed from list", Toast.LENGTH_SHORT).show()
            }
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        addBtn.setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Add an item...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            items.add(0, TodoItem(text))
            adapter.notifyItemInserted(0)
            recycler.scrollToPosition(0)
            input.text.clear()
        }

        clearBtn.setOnClickListener {
            if (items.isNotEmpty()) {
                val size = items.size
                items.clear()
                adapter.notifyItemRangeRemoved(0, size)
                Toast.makeText(requireContext(), "List cleared", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
