package com.almazov.diacompanion.recipe

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.FoodEntity
import com.almazov.diacompanion.meal.FoodInMealItem
import com.almazov.diacompanion.meal.FoodInMealListAdapter
import com.almazov.diacompanion.meal.SelectWeightDialog
import com.almazov.diacompanion.meal.SwipeDeleteFood
import kotlinx.android.synthetic.main.fragment_add_recipe.*
import kotlinx.android.synthetic.main.fragment_add_recipe.view.*
import java.math.BigDecimal

class AddRecipe : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel

    var foodList = mutableListOf<FoodInMealItem>()
    var lastFood: String = ""
    lateinit var adapter: FoodInMealListAdapter

    var updateBool: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_recipe, container, false)

        view.edit_text_recipe_name.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        view.spinner_recipe.adapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.RecipeSpinner,
            R.layout.spinner_item
        )

        val recyclerView = view.recycler_view_food_in_recipe
        adapter = FoodInMealListAdapter(foodList)

        val swipeDeleteFood = object : SwipeDeleteFood(requireContext(),R.color.red_dark) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        foodList.removeAt(viewHolder.adapterPosition)
                        adapter.notifyItemRemoved(viewHolder.adapterPosition)
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeDeleteFood)
        touchHelper.attachToRecyclerView(recyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        view.btn_add_food.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_addRecipe_to_foodList)
        }

        view.btn_save.setOnClickListener {

            if (edit_text_recipe_name.text.toString().isNotBlank() and !foodList.isNullOrEmpty()) {
                if (updateBool) { updateRecipe()
                } else { addRecipe()
                }
                findNavController().popBackStack()
            }
        }

        return view
    }

    private fun updateRecipe() {
        val recipe = createRecipe()
    }

    private fun addRecipe() {
        val recipe = createRecipe()
        appDatabaseViewModel.addRecord(recipe,foodList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Navigation.findNavController(view).currentBackStackEntry?.savedStateHandle
            ?.getLiveData<FoodEntity>("foodKey")?.observe(viewLifecycleOwner) {

                var foodAlreadyInList = false
                for (food in foodList) {
                    if (it.name == food.foodEntity.name) foodAlreadyInList = true
                }
                val lastFoodBool = lastFood == it.name

                if (!foodAlreadyInList and !lastFoodBool) {
                    lastFood = it.name!!
                    val selectWeightDialog = SelectWeightDialog(requireContext())
                    selectWeightDialog.isCancelable = false
                    selectWeightDialog.show(requireFragmentManager(), "weight select dialog")

                    setFragmentResultListener("requestKey") { key, bundle ->
                        val result = bundle.getString("resultKey")
                        foodList.add(FoodInMealItem(it, result!!.toDouble()))
                        adapter.notifyItemInserted(foodList.size)
                    }
                }
            }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun createRecipe():FoodEntity {

        val name = edit_text_recipe_name.text.toString()
        val category = spinner_recipe.selectedItem.toString()
        val additional = false
        val favourite = false
        val recipe = true

        var carbo = 0.0
        var prot = 0.0
        var fat = 0.0
        var ec = 0.0
        var gi = 0.0
        var water = 0.0
        var nzhk = 0.0
        var hol = 0.0
        var pv = 0.0
        var zola = 0.0
        var na = 0.0
        var k = 0.0
        var ca = 0.0
        var mg = 0.0
        var p = 0.0
        var fe = 0.0
        var a = 0.0
        var b1 = 0.0
        var b2 = 0.0
        var rr = 0.0
        var c = 0.0
        var re = 0.0
        var kar = 0.0
        var mds = 0.0
        var kr = 0.0
        var te = 0.0
        var ok = 0.0
        var ne = 0.0
        var zn = 0.0
        var cu = 0.0
        var mn = 0.0
        var se = 0.0
        var b5 = 0.0
        var b6 = 0.0
        var fol = 0.0
        var b9 = 0.0
        var dfe = 0.0
        var holin = 0.0
        var b12 = 0.0
        var ear = 0.0
        var a_kar = 0.0
        var b_kript = 0.0
        var likopin = 0.0
        var lut_z = 0.0
        var vit_e = 0.0
        var vit_d = 0.0
        var d_mezd = 0.0
        var vit_k = 0.0
        var mzhk = 0.0
        var pzhk = 0.0
        var w_1ed = 0.0
        var op_1ed = 0.0
        var w_2ed = 0.0
        var op_2ed = 0.0
        var proc_pot = 0.0

        var weightSum = 0.0
        for (food in foodList) weightSum += food.weight
        
        for (food in foodList)
        {
            if (food.foodEntity.carbo != null) carbo += food.foodEntity.carbo * food.weight
            if (food.foodEntity.prot != null) prot += food.foodEntity.prot * food.weight
            if (food.foodEntity.fat != null) fat += food.foodEntity.fat * food.weight
            if (food.foodEntity.ec != null) ec += food.foodEntity.ec * food.weight
            if (food.foodEntity.gi != null) gi += food.foodEntity.gi * food.weight
            if (food.foodEntity.water != null) water += food.foodEntity.water * food.weight
            if (food.foodEntity.nzhk != null) nzhk += food.foodEntity.nzhk * food.weight
            if (food.foodEntity.hol != null) hol += food.foodEntity.hol * food.weight
            if (food.foodEntity.pv != null) pv += food.foodEntity.pv * food.weight
            if (food.foodEntity.zola != null) zola += food.foodEntity.zola * food.weight
            if (food.foodEntity.na != null) na += food.foodEntity.na * food.weight
            if (food.foodEntity.k != null) k += food.foodEntity.k * food.weight
            if (food.foodEntity.ca != null) ca += food.foodEntity.ca * food.weight
            if (food.foodEntity.mg != null) mg += food.foodEntity.mg * food.weight
            if (food.foodEntity.p != null) p += food.foodEntity.p * food.weight
            if (food.foodEntity.fe != null) fe += food.foodEntity.fe * food.weight
            if (food.foodEntity.a != null) a += food.foodEntity.a * food.weight
            if (food.foodEntity.b1 != null) b1 += food.foodEntity.b1 * food.weight
            if (food.foodEntity.b2 != null) b2 += food.foodEntity.b2 * food.weight
            if (food.foodEntity.rr != null) rr += food.foodEntity.rr * food.weight
            if (food.foodEntity.c != null) c += food.foodEntity.c * food.weight
            if (food.foodEntity.re != null) re += food.foodEntity.re * food.weight
            if (food.foodEntity.kar != null) kar += food.foodEntity.kar * food.weight
            if (food.foodEntity.mds != null) mds += food.foodEntity.mds * food.weight
            if (food.foodEntity.kr != null) kr += food.foodEntity.kr * food.weight
            if (food.foodEntity.te != null) te += food.foodEntity.te * food.weight
            if (food.foodEntity.ok != null) ok += food.foodEntity.ok * food.weight
            if (food.foodEntity.ne != null) ne += food.foodEntity.ne * food.weight
            if (food.foodEntity.zn != null) zn += food.foodEntity.zn * food.weight
            if (food.foodEntity.cu != null) cu += food.foodEntity.cu * food.weight
            if (food.foodEntity.mn != null) mn += food.foodEntity.mn * food.weight
            if (food.foodEntity.se != null) se += food.foodEntity.se * food.weight
            if (food.foodEntity.b5 != null) b5 += food.foodEntity.b5 * food.weight
            if (food.foodEntity.b6 != null) b6 += food.foodEntity.b6 * food.weight
            if (food.foodEntity.fol != null) fol += food.foodEntity.fol * food.weight
            if (food.foodEntity.b9 != null) b9 += food.foodEntity.b9 * food.weight
            if (food.foodEntity.dfe != null) dfe += food.foodEntity.dfe * food.weight
            if (food.foodEntity.holin != null) holin += food.foodEntity.holin * food.weight
            if (food.foodEntity.b12 != null) b12 += food.foodEntity.b12 * food.weight
            if (food.foodEntity.ear != null) ear += food.foodEntity.ear * food.weight
            if (food.foodEntity.a_kar != null) a_kar += food.foodEntity.a_kar * food.weight
            if (food.foodEntity.b_kript != null) b_kript += food.foodEntity.b_kript * food.weight
            if (food.foodEntity.likopin != null) likopin += food.foodEntity.likopin * food.weight
            if (food.foodEntity.lut_z != null) lut_z += food.foodEntity.lut_z * food.weight
            if (food.foodEntity.vit_e != null) vit_e += food.foodEntity.vit_e * food.weight
            if (food.foodEntity.vit_d != null) vit_d += food.foodEntity.vit_d * food.weight
            if (food.foodEntity.d_mezd != null) d_mezd += food.foodEntity.d_mezd * food.weight
            if (food.foodEntity.vit_k != null) vit_k += food.foodEntity.vit_k * food.weight
            if (food.foodEntity.mzhk != null) mzhk += food.foodEntity.mzhk * food.weight
            if (food.foodEntity.pzhk != null) pzhk += food.foodEntity.pzhk * food.weight
            if (food.foodEntity.w_1ed != null) w_1ed += food.foodEntity.w_1ed * food.weight
            if (food.foodEntity.op_1ed != null) op_1ed += food.foodEntity.op_1ed * food.weight
            if (food.foodEntity.w_2ed != null) w_2ed += food.foodEntity.w_2ed * food.weight
            if (food.foodEntity.op_2ed != null) op_2ed += food.foodEntity.op_2ed * food.weight
            if (food.foodEntity.proc_pot != null) proc_pot += food.foodEntity.proc_pot * food.weight
        }

        carbo = BigDecimal(carbo / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        prot = BigDecimal(prot / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        fat = BigDecimal(fat / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        ec = BigDecimal(ec / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        gi = BigDecimal(gi / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        water = BigDecimal(water / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        nzhk = BigDecimal(nzhk / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        hol = BigDecimal(hol / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        pv = BigDecimal(pv / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        zola = BigDecimal(zola / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        na = BigDecimal(na / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        k = BigDecimal(k / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        ca = BigDecimal(ca / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        mg = BigDecimal(mg / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        p = BigDecimal(p / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        fe = BigDecimal(fe / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        a = BigDecimal(a / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        b1 = BigDecimal(b1 / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        b2 = BigDecimal(b2 / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        rr = BigDecimal(rr / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        c = BigDecimal(c / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        re = BigDecimal(re / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        kar = BigDecimal(kar / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        mds = BigDecimal(mds / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        kr = BigDecimal(kr / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        te = BigDecimal(te / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        ok = BigDecimal(ok / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        ne = BigDecimal(ne / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        zn = BigDecimal(zn / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        cu = BigDecimal(cu / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        mn = BigDecimal(mn / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        se = BigDecimal(se / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        b5 = BigDecimal(b5 / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        b6 = BigDecimal(b6 / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        fol = BigDecimal(fol / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        b9 = BigDecimal(b9 / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        dfe = BigDecimal(dfe / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        holin = BigDecimal(holin / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        b12 = BigDecimal(b12 / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        ear = BigDecimal(ear / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        a_kar = BigDecimal(a_kar / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        b_kript = BigDecimal(b_kript / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        likopin = BigDecimal(likopin / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        lut_z = BigDecimal(lut_z / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        vit_e = BigDecimal(vit_e / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        vit_d = BigDecimal(vit_d / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        d_mezd = BigDecimal(d_mezd / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        vit_k = BigDecimal(vit_k / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        mzhk = BigDecimal(mzhk / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        pzhk = BigDecimal(pzhk / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        w_1ed = BigDecimal(w_1ed / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        op_1ed = BigDecimal(op_1ed / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        w_2ed = BigDecimal(w_2ed / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        op_2ed = BigDecimal(op_2ed / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
        proc_pot = BigDecimal(proc_pot / weightSum).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()

        return FoodEntity(null,name, category, carbo,prot,fat,ec,gi,water,nzhk,hol,pv,zola,
            na,k,ca,mg,p,fe,a,b1,b2,rr,c,re,kar,mds,kr,te,ok,ne,zn,cu,mn,se,b5,b6,fol,b9,dfe,holin,
            b12,ear,a_kar,b_kript,likopin,lut_z,vit_e,vit_d,d_mezd,vit_k,mzhk,pzhk,w_1ed,op_1ed,
            w_2ed,op_2ed,proc_pot.toInt(),additional,favourite,recipe)
    }

}