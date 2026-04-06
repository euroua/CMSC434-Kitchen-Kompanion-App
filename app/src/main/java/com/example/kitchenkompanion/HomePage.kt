package com.example.kitchenkompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
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
        tvGreeting.text = getString(R.string.greeting_text, FavoritesPage.currentUsername)

        val ivProfile = view.findViewById<ImageView>(R.id.ivProfilePic)
        ivProfile.setOnClickListener { 
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        val rvRecommended = view.findViewById<RecyclerView>(R.id.rvRecommendedRecipes)
        val recipes = FavoritesPage.getAllRecipes()
        rvRecommended.adapter = RecipeAdapter(recipes) { recipe ->
            navigateToRecipeDetail(recipe)
        }

        // Your Ingredients
        val rvIngredients = view.findViewById<RecyclerView>(R.id.rvYourIngredients)
        val ingredients = FavoritesPage.getAllIngredients()
        rvIngredients.adapter = IngredientAdapter(ingredients) { ingredient ->
            navigateToIngredientDetail(ingredient)
        }

        // All Ingredients
        view.findViewById<TextView>(R.id.tvSeeAll).setOnClickListener {
            navigateToAllItems(AllItemsPage.TYPE_INGREDIENTS)
        }
    }

    private fun navigateToFavorites() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FavoritesPage())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToShoppingList() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ShoppingListFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToAllItems(type: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AllItemsPage.newInstance(type))
            .addToBackStack(null)
            .commit()
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

    private fun navigateToIngredientDetail(ingredient: FavoritesPage.Companion.Ingredient) {
        val fragment = IngredientDetailFragment.newInstance(ingredient.name, ingredient.category, ingredient.imageRes)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToPlaceholder(pageName: String) {
        val fragment = PlaceholderFragment.newInstance(pageName)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    inner class RecipeAdapter(private val items: List<FavoritesPage.Companion.Recipe>, private val onClick: (FavoritesPage.Companion.Recipe) -> Unit) :
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
                    FavoritesPage.toggleFavorite(recipe.name)
                    notifyItemChanged(adapterPosition)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_horizontal, parent, false)
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

    inner class IngredientAdapter(private val items: List<FavoritesPage.Companion.Ingredient>, private val onClick: (FavoritesPage.Companion.Ingredient) -> Unit) :
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
