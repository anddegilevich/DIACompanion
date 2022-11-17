package com.almazov.diacompanion.recipe

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import kotlinx.android.synthetic.main.fragment_recipe_list.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecipeList : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private lateinit var adapter: RecipeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipe_list, container, false)

        val recyclerView = view.recycler_view_recipes

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        adapter = RecipeListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch{
            appDatabaseViewModel.readRecipePaged().collectLatest {
                recyclerView.smoothScrollToPosition(0)
                adapter.submitData(it)
            }
        }

        view.btn_add_recipe.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_recipeList_to_addRecipe)
        }

        return view
    }
}