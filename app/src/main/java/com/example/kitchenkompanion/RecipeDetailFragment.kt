package com.example.kitchenkompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class RecipeDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString("name") ?: "Recipe"
        val time = arguments?.getString("time") ?: ""
        val difficulty = arguments?.getString("difficulty") ?: ""
        val imageRes = arguments?.getInt("imageRes") ?: 0
        val missingIngredients =
            arguments?.getStringArrayList("missingIngredients") ?: arrayListOf()

        val recipeImage = view.findViewById<ImageView>(R.id.recipeImage)
        val recipeTitle = view.findViewById<TextView>(R.id.recipeTitle)
        val recipeMeta = view.findViewById<TextView>(R.id.recipeMeta)
        val ingredientsText = view.findViewById<TextView>(R.id.ingredientsText)
        val missingText = view.findViewById<TextView>(R.id.missingText)
        val instructionsText = view.findViewById<TextView>(R.id.instructionsText)
        val addMissingButton = view.findViewById<Button>(R.id.addMissingButton)

        val backButton = view.findViewById<ImageView>(R.id.btnBack)

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        if (imageRes != 0) {
            recipeImage.setImageResource(imageRes)
        }

        recipeTitle.text = name
        recipeMeta.text = "$time • $difficulty"

        val ingredientList = when (name) {
            "Creamy Salmon Pasta" -> listOf("Salmon", "Pasta", "Milk", "Cheese", "Lemon")
            "Tomato Basil Pasta" -> listOf("Pasta", "Tomato", "Cheese")
            "Strawberry Salad" -> listOf("Strawberry", "Lettuce")
            "Roasted Potatoes" -> listOf("Potato", "Seasoning")
            else -> listOf("Ingredients unavailable")
        }

        val instructions = when (name) {
            "Creamy Salmon Pasta" -> listOf(
                "Boil the pasta until tender.",
                "Cook the salmon in a pan.",
                "Add milk, cheese, and lemon to make the sauce.",
                "Mix everything together and serve."
            )

            "Tomato Basil Pasta" -> listOf(
                "Boil the pasta.",
                "Cook tomatoes into a sauce.",
                "Mix with pasta and top with cheese."
            )

            "Strawberry Salad" -> listOf(
                "Wash and slice strawberries.",
                "Combine with lettuce.",
                "Toss and serve."
            )

            "Roasted Potatoes" -> listOf(
                "Cut potatoes into pieces.",
                "Season them.",
                "Roast until golden brown."
            )

            else -> listOf("Instructions unavailable")
        }

        ingredientsText.text = ingredientList.joinToString("\n• ", prefix = "• ")

        missingText.text = if (missingIngredients.isEmpty()) {
            "None"
        } else {
            missingIngredients.joinToString("\n• ", prefix = "• ")
        }

        instructionsText.text = instructions.mapIndexed { index, step ->
            "${index + 1}. $step"
        }.joinToString("\n\n")

        addMissingButton.setOnClickListener {
            if (missingIngredients.isNotEmpty()) {
                ShoppingListData.addItems(missingIngredients)
                Toast.makeText(
                    requireContext(),
                    "Missing ingredients added to shopping list",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "No missing ingredients to add",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}