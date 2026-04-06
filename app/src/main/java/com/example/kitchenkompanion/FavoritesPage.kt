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
        const val NO_IMAGE = -1
        
        var currentUsername: String = "User"

        data class Recipe(
            val name: String,
            val time: String,
            val difficulty: String,
            var isFavorited: Boolean,
            val imageRes: Int,
            val missingIngredients: ArrayList<String> = arrayListOf()
        )

        data class Ingredient(
            val name: String, 
            val imageRes: Int, 
            val category: String,
            var quantity: Int = 1
        )

        private val recipes = mutableListOf(
            Recipe("Creamy Salmon Pasta", "25 mins", "Medium", false, R.drawable.salmon_pasta, arrayListOf("Heavy Cream", "Basil")),
            Recipe("Tomato Basil Pasta", "20 mins", "Easy", false, R.drawable.tomato_pasta, arrayListOf("Basil")),
            Recipe("Strawberry Salad", "15 mins", "Easy", false, R.drawable.strawberry_salad, arrayListOf("Poppyseed Dressing", "Walnuts")),
            Recipe("Roasted Potatoes", "40 mins", "Easy", false, R.drawable.roasted_potatoes, arrayListOf("Rosemary", "Olive Oil")),
            Recipe("Lemon Herb Salmon", "15 mins", "Easy", false, R.drawable.lemon_salmon, arrayListOf("Dill", "Asparagus")),
            Recipe("Classic Grilled Cheese", "10 mins", "Easy", false, R.drawable.grilled_cheese, arrayListOf("Sourdough Bread", "Butter"))
        )

        private val ingredients = mutableListOf(
            Ingredient("Salmon", R.drawable.salmon, "Poultry/Meats"),
            Ingredient("Pasta", R.drawable.pasta, "Grains"),
            Ingredient("Strawberry", R.drawable.strawberry, "Produce"),
            Ingredient("Lemon", R.drawable.lemon, "Produce"),
            Ingredient("Tomato", R.drawable.tomato, "Produce"),
            Ingredient("Lettuce", R.drawable.lettuce, "Produce"),
            Ingredient("Milk", R.drawable.milk, "Dairy"),
            Ingredient("Cheese", R.drawable.cheese, "Dairy"),
            Ingredient("Potato", R.drawable.potato, "Produce")
        )

        fun getAllRecipes(): List<Recipe> = recipes
        fun getAllIngredients(): MutableList<Ingredient> = ingredients

        fun getFavoritedRecipes(): List<Recipe> = recipes.filter { it.isFavorited }

        fun toggleFavorite(recipeName: String) {
            recipes.find { it.name == recipeName }?.let {
                it.isFavorited = !it.isFavorited
            }
        }

        fun updateIngredientQuantity(name: String, delta: Int) {
            ingredients.find { it.name == name }?.let {
                it.quantity += delta
                if (it.quantity <= 0) {
                    ingredients.remove(it)
                }
            }
        }

        fun removeIngredient(name: String) {
            ingredients.removeAll { it.name == name }
        }
        
        fun addIngredient(name: String, category: String, quantity: Int, imageRes: Int = NO_IMAGE) {
            val existing = ingredients.find { it.name.lowercase() == name.lowercase() }
            if (existing != null) {
                existing.quantity += quantity
            } else {
                ingredients.add(Ingredient(name, imageRes, category, quantity))
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
