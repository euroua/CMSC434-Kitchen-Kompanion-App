package com.example.kitchenkompanion

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView

class ShoppingListAdapter(context: android.content.Context, private val items: MutableList<ShoppingItem>) :
    ArrayAdapter<ShoppingItem>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_shopping, parent, false)

        val item = items[position]

        val checkBox = view.findViewById<CheckBox>(R.id.checkItem)
        val nameText = view.findViewById<TextView>(R.id.tvItemName)
        val quantityText = view.findViewById<TextView>(R.id.tvQuantity)
        val suggestedHeader = view.findViewById<TextView>(R.id.tvSuggestedHeader)
        val btnPlus = view.findViewById<Button>(R.id.btnPlus)
        val btnMinus = view.findViewById<Button>(R.id.btnMinus)

        // Set the +/- for the buttons
        btnPlus.text = "+"
        btnMinus.text = "-"

        nameText.text = item.name
        quantityText.text = "Quantity: ${item.quantity}"
        checkBox.isChecked = item.checked

        // Apply strike-through based on initial checked state
        applyStrikeThrough(nameText, item.checked)

        if (item.isSuggested && (position == 0 || !items[position - 1].isSuggested)) {
            suggestedHeader.visibility = View.VISIBLE
            suggestedHeader.text = "Suggested for you"
        } else if (!item.isSuggested && (position == 0 || items[position - 1].isSuggested)) {
            suggestedHeader.visibility = View.VISIBLE
            suggestedHeader.text = "My Items"
        } else {
            suggestedHeader.visibility = View.GONE
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.checked = isChecked
            applyStrikeThrough(nameText, isChecked)
        }

        btnPlus.setOnClickListener {
            item.quantity++
            notifyDataSetChanged()
        }

        btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
            } else {
                items.removeAt(position)
            }
            notifyDataSetChanged()
        }

        return view
    }

    private fun applyStrikeThrough(textView: TextView, isChecked: Boolean) {
        if (isChecked) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}
