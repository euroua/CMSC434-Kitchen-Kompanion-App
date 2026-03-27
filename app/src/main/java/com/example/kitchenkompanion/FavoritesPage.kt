package com.example.kitchenkompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class FavoritesPage : Fragment() {

    // Data management moved here and kept static to persist status
    companion object {
        data class Recipe(
            val name: String,
            val time: String,
            val difficulty: String,
            var isFavorited: Boolean,
            val imageRes: Int
        )

        private val recipes = mutableListOf(
            Recipe("Creamy Salmon Pasta", "25 mins", "Medium", false, R.drawable.salmon_pasta),
            Recipe("Tomato Basil Pasta", "20 mins", "Easy", false, R.drawable.tomato_pasta),
            Recipe("Strawberry Salad", "15 mins", "Easy", false, R.drawable.strawberry_salad),
            Recipe("Roasted Potatoes", "40 mins", "Easy", false, R.drawable.roasted_potatoes)
        )

        fun getAllRecipes(): List<Recipe> = recipes

        fun getFavoritedRecipes(): List<Recipe> = recipes.filter { it.isFavorited }

        fun toggleFavorite(recipeName: String) {
            recipes.find { it.name == recipeName }?.let {
                it.isFavorited = !it.isFavorited
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvFavorites = view.findViewById<RecyclerView>(R.id.rvFavorites)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptyMessage)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val favoritedItems = getFavoritedRecipes()
        
        if (favoritedItems.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvFavorites.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvFavorites.visibility = View.VISIBLE
            rvFavorites.adapter = FavoriteAdapter(favoritedItems)
        }
    }

    inner class FavoriteAdapter(private val items: List<Recipe>) :
        RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

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
            
            // In favorites page, they are all favorited
            holder.heart.setImageResource(android.R.drawable.btn_star_big_on)
            holder.heart.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.yellow_star))
            
            holder.heart.setOnClickListener {
                toggleFavorite(item.name)
                // Refresh the list when it's no longer a favorite
                val updatedList = getFavoritedRecipes()
                if (updatedList.isEmpty()) {
                    view?.findViewById<TextView>(R.id.tvEmptyMessage)?.visibility = View.VISIBLE
                    view?.findViewById<RecyclerView>(R.id.rvFavorites)?.visibility = View.GONE
                }
                notifyDataSetChanged() 
            }
        }

        override fun getItemCount() = items.size
    }
}
