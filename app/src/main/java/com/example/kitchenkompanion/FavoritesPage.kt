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

    // Central Data Repository
    companion object {
        data class Recipe(
            val name: String,
            val time: String,
            val difficulty: String,
            var isFavorited: Boolean,
            val imageRes: Int,
            val missingIngredients: ArrayList<String> = arrayListOf()
        )

        data class Ingredient(val name: String, val imageRes: Int)

        private val recipes = mutableListOf(
            Recipe("Creamy Salmon Pasta", "25 mins", "Medium", false, R.drawable.salmon_pasta, arrayListOf("Heavy Cream", "Basil")),
            Recipe("Tomato Basil Pasta", "20 mins", "Easy", false, R.drawable.tomato_pasta, arrayListOf("Basil")),
            Recipe("Strawberry Salad", "15 mins", "Easy", false, R.drawable.strawberry_salad, arrayListOf("Poppyseed Dressing", "Walnuts")),
            Recipe("Roasted Potatoes", "40 mins", "Easy", false, R.drawable.roasted_potatoes, arrayListOf("Rosemary", "Olive Oil")),
            Recipe("Lemon Herb Salmon", "15 mins", "Easy", false, R.drawable.salmon, arrayListOf("Dill", "Asparagus")),
            Recipe("Classic Grilled Cheese", "10 mins", "Easy", false, R.drawable.cheese, arrayListOf("Sourdough Bread", "Butter"))
        )

        private val ingredients = mutableListOf(
            Ingredient("Salmon", R.drawable.salmon),
            Ingredient("Pasta", R.drawable.pasta),
            Ingredient("Strawberry", R.drawable.strawberry),
            Ingredient("Lemon", R.drawable.lemon),
            Ingredient("Tomato", R.drawable.tomato),
            Ingredient("Lettuce", R.drawable.lettuce),
            Ingredient("Milk", R.drawable.milk),
            Ingredient("Cheese", R.drawable.cheese),
            Ingredient("Potato", R.drawable.potato)
        )

        fun getAllRecipes(): List<Recipe> = recipes
        fun getAllIngredients(): List<Ingredient> = ingredients

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

        refreshFavoritesList()
    }

    private fun refreshFavoritesList() {
        val favoritedItems = getFavoritedRecipes()
        val rvFavorites = view?.findViewById<RecyclerView>(R.id.rvFavorites)
        val tvEmpty = view?.findViewById<TextView>(R.id.tvEmptyMessage)

        if (favoritedItems.isEmpty()) {
            tvEmpty?.visibility = View.VISIBLE
            rvFavorites?.visibility = View.GONE
        } else {
            tvEmpty?.visibility = View.GONE
            rvFavorites?.visibility = View.VISIBLE
            rvFavorites?.adapter = FavoriteAdapter(favoritedItems)
        }
    }

    private fun navigateToRecipeDetail(recipe: Recipe) {
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
            
            holder.heart.setImageResource(android.R.drawable.btn_star_big_on)
            holder.heart.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.yellow_star))
            
            holder.itemView.setOnClickListener {
                navigateToRecipeDetail(item)
            }

            holder.heart.setOnClickListener {
                toggleFavorite(item.name)
                refreshFavoritesList()
            }
        }

        override fun getItemCount() = items.size
    }
}
