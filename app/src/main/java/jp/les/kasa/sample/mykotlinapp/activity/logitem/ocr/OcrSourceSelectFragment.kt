package jp.les.kasa.sample.mykotlinapp.activity.logitem.ocr

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemViewModel
import jp.les.kasa.sample.mykotlinapp.databinding.FragmentSelectOcrsrcBinding
import jp.les.kasa.sample.mykotlinapp.databinding.ItemBitmapBinding
import jp.les.kasa.sample.mykotlinapp.getSrcBitmaps
import kotlinx.android.synthetic.main.fragment_select_ocrsrc.view.*


class OcrSourceSelectFragment : Fragment() {

    companion object {
        const val TAG = "OcrSourceSelectFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewModel = ViewModelProviders.of(activity!!).get(LogItemViewModel::class.java)

        val binding: FragmentSelectOcrsrcBinding =
            DataBindingUtil.inflate(
                inflater,
                jp.les.kasa.sample.mykotlinapp.R.layout.fragment_select_ocrsrc,
                container,
                false
            )

        val root = binding.root

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        // RecyclyerView
        root.bitmap_list.layoutManager = GridLayoutManager(requireContext(), 2)

        val adapter = BitmapRecyclerAdapter(viewModel.bitmapSourceList.value!!, viewModel)
        root.bitmap_list.adapter = adapter

        Handler().post {
            val list = getSrcBitmaps()
            viewModel.bitmapSourceList.postValue(list)
        }

        viewModel.ocrBitmapSource.observe(this, Observer {
            fragmentManager?.popBackStack()
        })

        return binding.root
    }

}

class BitmapRecyclerAdapter(
    private var list: List<Bitmap>,
    private val viewModel: LogItemViewModel
) :
    RecyclerView.Adapter<BitmapRecyclerAdapter.LogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding: ItemBitmapBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), jp.les.kasa.sample.mykotlinapp.R.layout.item_bitmap, parent, false
        )
        return LogViewHolder(binding)
    }

    fun setList(newList: List<Bitmap>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        if (position >= list.size) return
        holder.binding.bitmap = list[position]
        holder.binding.viewmodel = viewModel
    }

    class LogViewHolder(val binding: ItemBitmapBinding) : RecyclerView.ViewHolder(binding.root)
}