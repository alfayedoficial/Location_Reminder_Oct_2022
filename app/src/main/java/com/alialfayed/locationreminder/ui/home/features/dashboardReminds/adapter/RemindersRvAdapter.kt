package com.alialfayed.locationreminder.ui.home.features.dashboardReminds.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import com.alfayedoficial.kotlinutils.kuGetBindingRow
import com.alialfayed.locationreminder.BR
import com.alialfayed.locationreminder.R
import com.alialfayed.locationreminder.core.common.adapter.BaseAdapter
import com.alialfayed.locationreminder.core.common.adapter.BaseViewHolder
import com.alialfayed.locationreminder.core.common.adapter.DiffCallBack
import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.data.dto.Reminders
import com.alialfayed.locationreminder.databinding.ItemRvReminderBinding


/**
 * Created by ( Eng Ali Al Fayed)
 * Class do :
 * Date 9/13/2021 - 3:28 PM
 */
class RemindersRvAdapter : BaseAdapter<ReminderDTO>() {

    private var mDiffer = AsyncListDiffer(this, DiffCallBack<ReminderDTO>())
    private var dataList: Reminders = arrayListOf()


    override fun setDataList(dataList: Reminders) {
        this.dataList = dataList
        mDiffer.submitList(dataList)
    }

    override fun addDataList(dataList: Reminders) {
        clearDataList()
        setDataList(dataList)
        notifyDataSetChanged()
    }

    override fun clearDataList() {
        this.dataList = arrayListOf()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ReminderDTO> {
        return ViewHolderStore(kuGetBindingRow(parent, R.layout.item_rv_reminder) as ItemRvReminderBinding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ReminderDTO>, position: Int) {
        val model = mDiffer.currentList[position]
        holder.apply {
            bind(model)

        }
    }

    override fun getItemCount(): Int = mDiffer.currentList.size

    inner class ViewHolderStore(binding: ItemRvReminderBinding) : BaseViewHolder<ReminderDTO>(binding) {

        override var itemRowBinding: ViewDataBinding = binding

        override fun bind(result: ReminderDTO) {
            itemRowBinding.apply {
                setVariable(BR.model, result)
                executePendingBindings()
            }
        }
    }
}



