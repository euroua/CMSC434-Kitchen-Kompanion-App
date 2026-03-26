package com.example.kitchenkompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class HomePage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Greeting
        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        val username: String? = null
        tvGreeting.text = getString(R.string.greeting_text, username ?: "User!")

        // Hamburger Menu
        val ivMenu = view.findViewById<ImageView>(R.id.ivHamburgerMenu)
        ivMenu.setOnClickListener { showMenu(it) }

        // Profile Picture
        val ivProfile = view.findViewById<ImageView>(R.id.ivProfilePic)
        ivProfile.setOnClickListener { navigateToPlaceholder("Profile") }

        // Recommended Recipes (Setting all to false so they start "empty")
        val rvRecommended = view.findViewById<RecyclerView>(R.id.rvRecommendedRecipes)
        val recipes = listOf(
            Recipe("Creamy Salmon Pasta", "25 mins", "Medium", false, R.drawable.salmon_pasta),
            Recipe("Tomato Basil Pasta", "20 mins", "Easy", false, R.drawable.pasta),
            Recipe("Strawberry Salad", "15 mins", "Easy", false, R.drawable.strawberry),
            Recipe("Roasted Potatoes", "40 mins", "Easy", false, R.drawable.potato)
        )
        rvRecommended.adapter = RecipeAdapter(recipes) { recipe ->
            navigateToPlaceholder(recipe.name)
        }

        // Your Ingredients - 3x3 Grid with your provided images
        val rvIngredients = view.findViewById<RecyclerView>(R.id.rvYourIngredients)
        val ingredients = listOf(
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
        rvIngredients.adapter = IngredientAdapter(ingredients) { ingredient ->
            navigateToPlaceholder(ingredient.name)
        }

        // See All -> All Ingredients
        view.findViewById<TextView>(R.id.tvSeeAll).setOnClickListener {
            navigateToPlaceholder("All Ingredients")
        }
    }

    private fun showMenu(view: View) {
        val popup = PopupMenu(context, view)
        popup.menu.add("All Ingredients")
        popup.menu.add("All Recipes")
        popup.menu.add("Favorites")
        popup.menu.add("Shopping List")
        
        popup.setOnMenuItemClickListener { item ->
            navigateToPlaceholder(item.title.toString())
            true
        }
        popup.show()
    }

    private fun navigateToPlaceholder(pageName: String) {
        val fragment = PlaceholderFragment.newInstance(pageName)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Data Classes
    data class Recipe(val name: String, val time: String, val difficulty: String, var isFavorited: Boolean, val imageRes: Int)
    data class Ingredient(val name: String, val imageRes: Int)

    // Adapters
    inner class RecipeAdapter(private val items: List<Recipe>, private val onClick: (Recipe) -> Unit) :
        RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val name: TextView = v.findViewById(R.id.tvRecipeName)
            val time: TextView = v.findViewById(R.id.tvRecipeTime)
            val difficulty: TextView = v.findViewById(R.id.tvRecipeDifficulty)
            val heart: ImageView = v.findViewById(R.id.ivHeart)
            val image: ImageView = v.findViewById(R.id.ivRecipeImage)
            init {
                v.setOnClickListener { onClick(items[adapterPosition]) }
                heart.setOnClickListener {
                    val recipe = items[adapterPosition]
                    recipe.isFavorited = !recipe.isFavorited
                    notifyItemChanged(adapterPosition)
                }
            }
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
        }

        override fun getItemCount() = items.size
    }

    inner class IngredientAdapter(private val items: List<Ingredient>, private val onClick: (Ingredient) -> Unit) :
        RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val name: TextView = v.findViewById(R.id.tvIngredientName)
            val image: ImageView = v.findViewById(R.id.ivIngredientImage)
            init {
                v.setOnClickListener { onClick(items[adapterPosition]) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.name.text = item.name
            holder.image.setImageResource(item.imageRes)
        }

        override fun getItemCount() = items.size
    }
}
