package com.example.kitchenkompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val rv = view.findViewById<RecyclerView>(R.id.rvAllItems)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAdd)

        if (type == TYPE_INGREDIENTS) {
            rv.layoutManager = GridLayoutManager(context, 3)
            rv.adapter = IngredientAdapter(FavoritesPage.getAllIngredients())
            fab.visibility = View.VISIBLE
            fab.setOnClickListener {
                Toast.makeText(context, "Add Ingredient Clicked", Toast.LENGTH_SHORT).show()
            }
        } else {
            rv.layoutManager = GridLayoutManager(context, 2)
            rv.adapter = RecipeAdapter(FavoritesPage.getAllRecipes())
            fab.visibility = View.GONE
        }
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

    inner class IngredientAdapter(private val items: List<FavoritesPage.Companion.Ingredient>) :
        RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val name: TextView = v.findViewById(R.id.tvIngredientName)
            val image: ImageView = v.findViewById(R.id.ivIngredientImage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.name.text = item.name
            holder.image.setImageResource(item.imageRes)
            
            holder.itemView.setOnClickListener {
                val fragment = PlaceholderFragment.newInstance(item.name)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        override fun getItemCount() = items.size
    }
}
