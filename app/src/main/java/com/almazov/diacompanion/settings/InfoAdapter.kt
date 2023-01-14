import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.base.slideView
import com.almazov.diacompanion.databinding.InfoBlockBinding
import com.almazov.diacompanion.settings.InfoBlock
import kotlinx.android.synthetic.main.fragment_meal_add_record.*

class InfoAdapter(private val infoBlocks: List<InfoBlock>)
    : RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = InfoBlockBinding.inflate(from, parent, false)
        return InfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {

        holder.bind(infoBlocks[position])
    }

    override fun getItemCount(): Int = infoBlocks.size

    class InfoViewHolder(
        private val infoBlockBinding: InfoBlockBinding
    ) : RecyclerView.ViewHolder(infoBlockBinding.root) {
        fun bind(infoBlock: InfoBlock) {
            infoBlockBinding.infoBlockTitle.text = infoBlock.title
            infoBlockBinding.vfInfoBlock.displayedChild = infoBlock.infoBlockId
                infoBlockBinding.infoBlockTitle.setOnClickListener{
                slideView(infoBlockBinding.vfInfoBlock)
            }
        }
    }
}