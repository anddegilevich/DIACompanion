import androidx.core.content.ContextCompat
import com.almazov.diacompanion.R
import com.almazov.diacompanion.meal.FoodInMealItem
import com.almazov.diacompanion.meal.FoodInMealListAdapter
import kotlinx.android.synthetic.main.food_in_meal_row.view.*

class FoodInMealInfoAdapter(
                            mListener: InterfaceFoodInMeal
)
    : FoodInMealListAdapter(mListener) {

    override fun changeWeight(holder: FoodInMealItemViewHolder, position: Int) {

    }

}