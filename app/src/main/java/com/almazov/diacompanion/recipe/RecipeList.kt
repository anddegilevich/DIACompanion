package com.almazov.diacompanion.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.almazov.diacompanion.R
import kotlinx.android.synthetic.main.fragment_recipe_list.view.*

class RecipeList : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipe_list, container, false)

        view.btn_add_recipe.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_recipeList_to_addRecipe)
        }

        return view
    }
}