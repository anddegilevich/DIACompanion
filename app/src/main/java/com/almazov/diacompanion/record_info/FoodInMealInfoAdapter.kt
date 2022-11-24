import androidx.core.content.ContextCompat
import com.almazov.diacompanion.R
import com.almazov.diacompanion.meal.FoodInMealItem
import com.almazov.diacompanion.meal.FoodInMealListAdapter
import kotlinx.android.synthetic.main.food_in_meal_row.view.*

class FoodInMealInfoAdapter(foodItemList: MutableList<FoodInMealItem>,
                            mListener: InterfaceFoodInMeal
)
    : FoodInMealListAdapter(foodItemList, mListener) {

    override fun changeWeight(holder: FoodInMealItemViewHolder, position: Int) {
        val color = ContextCompat.getColor(context!!, R.color.purple_dark)
        holder.itemView.tv_food_in_meal_name.setTextColor(color)
        holder.itemView.edit_text_food_in_meal_weight.setTextColor(color)
        holder.itemView.edit_text_food_in_meal_weight.isEnabled = false
        holder.itemView.tv_weight_units.setTextColor(color)
        holder.itemView.food_divider.dividerColor = color
    }

}