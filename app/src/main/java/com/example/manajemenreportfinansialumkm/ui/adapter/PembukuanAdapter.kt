import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.manajemenreportfinansialumkm.databinding.ItemPemasukanBinding
import com.example.manajemenreportfinansialumkm.databinding.ItemPengeluaranBinding
import com.example.manajemenreportfinansialumkm.helper.Pemasukan
import com.example.manajemenreportfinansialumkm.helper.PembukuanItem
import com.example.manajemenreportfinansialumkm.helper.Pengeluaran
import com.example.manajemenreportfinansialumkm.helper.currencyToRupiah

class PembukuanAdapter : ListAdapter<PembukuanItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    class PemasukanViewHolder(private val bindingPemasukan:ItemPemasukanBinding): RecyclerView.ViewHolder(bindingPemasukan.root) {
        fun bind(data: Pemasukan) {
            bindingPemasukan.tanggalPemasukan.text = data.date
            bindingPemasukan.textHargaTotalPemasukan.text = "+ ${data.totalPemasukan?.toDouble()
                ?.let { currencyToRupiah(it) }}"
        }
    }

    class PengeluaranViewHolder(private val bindingPengeluaran:ItemPengeluaranBinding): RecyclerView.ViewHolder(bindingPengeluaran.root) {
        fun bind(data: Pengeluaran) {
            bindingPengeluaran.textHargaTotalPengeluaran.text = "- ${data.totalPengeluaran?.toDouble()
                ?.let { currencyToRupiah(it) }}"
            bindingPengeluaran.tanggalPengeluaran.text = data.date

        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PembukuanItem.PemasukanItem -> TYPE_PEMASUKAN
            is PembukuanItem.PengeluaranItem -> TYPE_PENGELUARAN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_PEMASUKAN -> {
                val view = ItemPemasukanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PemasukanViewHolder(view)
            }
            TYPE_PENGELUARAN -> {
                val view = ItemPengeluaranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PengeluaranViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PembukuanItem.PemasukanItem -> (holder as PemasukanViewHolder).bind(item.data)
            is PembukuanItem.PengeluaranItem -> (holder as PengeluaranViewHolder).bind(item.data)
        }
    }

    companion object {
        private const val TYPE_PEMASUKAN = 0
        private const val TYPE_PENGELUARAN = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PembukuanItem>() {
            override fun areItemsTheSame(oldItem: PembukuanItem, newItem: PembukuanItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PembukuanItem, newItem: PembukuanItem): Boolean {
                return oldItem == newItem
            }
        }
    }


}
