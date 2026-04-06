package com.example.kitchenkompanion

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AllItemsPage : Fragment() {

    companion object {
        private const val ARG_TYPE = "type"
        const val TYPE_INGREDIENTS = "All Ingredients"
        const val TYPE_RECIPES = "All Recipes"

        fun newInstance(type: String): AllItemsPage {
            val fragment = AllItemsPage()
            val args = Bundle()
            args.putString(ARG_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = arguments?.getString(ARG_TYPE) ?: ""
        
        view.findViewById<TextView>(R.id.tvTitle).text = type

        val rv = view.findViewById<RecyclerView>(R.id.rvAllItems)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAdd)

        if (type == TYPE_INGREDIENTS) {
            setupIngredientList(rv)
            fab.visibility = View.VISIBLE
            fab.setOnClickListener {
                showAddIngredientDialog()
            }
        } else {
            rv.layoutManager = GridLayoutManager(context, 2)
            rv.adapter = RecipeAdapter(FavoritesPage.getAllRecipes())
            fab.visibility = View.GONE
        }
    }

    private fun setupIngredientList(rv: RecyclerView) {
        val rawIngredients = FavoritesPage.getAllIngredients()
        val itemsWithHeaders = mutableListOf<Any>()
        
        val grouped = rawIngredients.groupBy { it.category }
        grouped.forEach { (category, list) ->
            itemsWithHeaders.add(category)
            itemsWithHeaders.addAll(list)
        }

        val layoutManager = GridLayoutManager(context, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (itemsWithHeaders[position] is String) 3 else 1
            }
        }
        rv.layoutManager = layoutManager
        rv.adapter = IngredientAdapter(itemsWithHeaders)
    }

    private fun showAddIngredientDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_ingredient, null)
        val etName = dialogView.findViewById<EditText>(R.id.etIngredientName)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val tvQuantity = dialogView.findViewById<TextView>(R.id.tvDialogQuantity)
        val btnPlus = dialogView.findViewById<Button>(R.id.btnDialogPlus)
        val btnMinus = dialogView.findViewById<Button>(R.id.btnDialogMinus)

        var quantity = 1
        tvQuantity.text = quantity.toString()

        btnPlus.setOnClickListener {
            quantity++
            tvQuantity.text = quantity.toString()
        }

        btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQuantity.text = quantity.toString()
            }
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle("Add New Ingredient")
            .setView(dialogView)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val btnAdd = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val btnCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            
            btnAdd.textSize = 20f
            btnCancel.textSize = 20f
            
            btnAdd.setOnClickListener {
                val name = etName.text.toString()
                val category = spinnerCategory.selectedItem.toString()
                
                if (name.isNotBlank()) {
                    FavoritesPage.addIngredient(name, category, quantity)
                    val rv = view?.findViewById<RecyclerView>(R.id.rvAllItems)
                    if (rv != null) setupIngredientList(rv)
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        dialog.show()
    }

    private fun navigateToRecipeDetail(recipe: FavoritesPage.Companion.Recipe) {
        val fragment = RecipeDetailFragment()
        val args = Bundle()
        args.putString("name", recipe.name)
        args.putString("time", recipe.time)
        args.putString("difficulty", recipe.difficulty)
        args.putInt("imageRes", recipe.imageRes)
        args.putStringArrayList("missingIngredients", recipe.missingIngredients)
        fragment.arguments = args
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    inner class RecipeAdapter(private val items: List<FavoritesPage.Companion.Recipe>) :
        RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val name: TextView = v.findViewById(R.id.tvRecipeName)
            val time: TextView = v.findViewById(R.id.tvRecipeTime)
            val difficulty: TextView = v.findViewById(R.id.tvRecipeDifficulty)
            val heart: ImageView = v.findViewById(R.id.ivHeart)
            val image: ImageView = v.findViewById(R.id.ivRecipeImage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.name.text = item.name
            holder.time.text = item.time
            holder.difficulty.text = item.difficulty
            holder.image.setImageResource(item.imageRes)
            
            if (item.isFavorited) {
                holder.heart.setImageResource(android.R.drawable.btn_star_big_on)
                holder.heart.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.yellow_star))
            } else {
                holder.heart.setImageResource(android.R.drawable.btn_star_big_off)
                holder.heart.clearColorFilter()
            }

            holder.itemView.setOnClickListener {
                navigateToRecipeDetail(item)
            }

            holder.heart.setOnClickListener {
                FavoritesPage.toggleFavorite(item.name)
                notifyItemChanged(position)
            }
        }

        override fun getItemCount() = items.size
    }

    inner class IngredientAdapter(private val items: List<Any>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val TYPE_HEADER = 0
        private val TYPE_ITEM = 1

        override fun getItemViewType(position: Int): Int {
            return if (items[position] is String) TYPE_HEADER else TYPE_ITEM
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == TYPE_HEADER) {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_category_header, parent, false)
                HeaderViewHolder(v)
            } else {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
                ItemViewHolder(v)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is HeaderViewHolder) {
                holder.title.text = items[position] as String
            } else if (holder is ItemViewHolder) {
                val item = items[position] as FavoritesPage.Companion.Ingredient
                holder.name.text = item.name
                if (item.imageRes == FavoritesPage.NO_IMAGE) {
                    holder.image.visibility = View.GONE
                } else {
                    holder.image.visibility = View.VISIBLE
                    holder.image.setImageResource(item.imageRes)
                }
                
                holder.itemView.setOnClickListener {
                    val fragment = IngredientDetailFragment.newInstance(item.name, item.category, item.imageRes)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        override fun getItemCount() = items.size

        inner class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val title: TextView = v.findViewById(R.id.tvHeaderTitle)
        }

        inner class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val name: TextView = v.findViewById(R.id.tvIngredientName)
            val image: ImageView = v.findViewById(R.id.ivIngredientImage)
        }
    }
}
