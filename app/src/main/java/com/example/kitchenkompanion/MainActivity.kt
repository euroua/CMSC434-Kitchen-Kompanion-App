package com.example.kitchenkompanion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kitchenkompanion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(HomePage())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomePage())
                    true
                }
                R.id.nav_ingredients -> {
                    loadFragment(AllItemsPage.newInstance(AllItemsPage.TYPE_INGREDIENTS))
                    true
                }
                R.id.nav_recipes -> {
                    loadFragment(AllItemsPage.newInstance(AllItemsPage.TYPE_RECIPES))
                    true
                }
                R.id.nav_favorites -> {
                    loadFragment(FavoritesPage())
                    true
                }
                R.id.nav_shopping_list -> {
                    loadFragment(ShoppingListFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
