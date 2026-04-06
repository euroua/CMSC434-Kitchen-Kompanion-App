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

class IngredientDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_ingredient_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString("name") ?: ""
        val category = arguments?.getString("category") ?: ""
        val imageRes = arguments?.getInt("imageRes") ?: 0
        
        val ingredient = FavoritesPage.getAllIngredients().find { it.name == name }

        val ivLargeImage = view.findViewById<ImageView>(R.id.ivLargeImage)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvCategory = view.findViewById<TextView>(R.id.tvCategory)
        val tvQuantity = view.findViewById<TextView>(R.id.tvQuantity)
        val btnPlus = view.findViewById<Button>(R.id.btnPlus)
        val btnMinus = view.findViewById<Button>(R.id.btnMinus)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)

        tvName.text = name
        tvCategory.text = "Category: $category"
        
        if (imageRes == FavoritesPage.NO_IMAGE || imageRes == 0) {
            ivLargeImage.visibility = View.GONE
        } else {
            ivLargeImage.visibility = View.VISIBLE
            ivLargeImage.setImageResource(imageRes)
        }
        
        fun updateDisplay() {
            if (ingredient == null || ingredient.quantity <= 0) {
                FavoritesPage.removeIngredient(name)
                parentFragmentManager.popBackStack()
            } else {
                tvQuantity.text = ingredient.quantity.toString()
            }
        }
        
        updateDisplay()

        btnPlus.setOnClickListener {
            ingredient?.let { it.quantity++ }
            updateDisplay()
        }

        btnMinus.setOnClickListener {
            ingredient?.let {
                it.quantity--
                updateDisplay()
            }
        }

        btnDelete.setOnClickListener {
            FavoritesPage.removeIngredient(name)
            Toast.makeText(context, "$name removed", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(name: String, category: String, imageRes: Int): IngredientDetailFragment {
            val fragment = IngredientDetailFragment()
            val args = Bundle()
            args.putString("name", name)
            args.putString("category", category)
            args.putInt("imageRes", imageRes)
            fragment.arguments = args
            return fragment
        }
    }
}
