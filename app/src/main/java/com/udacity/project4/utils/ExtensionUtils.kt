package com.udacity.project4.utils

import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.alfayedoficial.kotlinutils.kuChangeBackgroundTint
import com.alfayedoficial.kotlinutils.kuHide
import com.alfayedoficial.kotlinutils.kuShow
import com.udacity.project4.R
import com.udacity.project4.domain.entity.ReminderEntity
import com.udacity.project4.ui.reminderDescription.view.ReminderDescriptionActivity
import com.udacity.project4.utils.AppConstant.EXTRA_ReminderDataItem
import com.google.android.material.button.MaterialButton

fun Fragment.setDisplayHomeAsUpEnabled(bool: Boolean) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            bool
        )
    }
}

@BindingAdapter("app:address")
fun TextView.setAddress(address: MutableLiveData<String>) {
    if (address.value != null) {
        text = address.value
        kuShow()
    }else{
        text = ""
        kuHide()
    }
}



@BindingAdapter("app:saveEnable")
fun MaterialButton.setSaveEnable(bool: MutableLiveData<Boolean>) {
    isEnabled = if (bool.value != null) {
        bool.value!!
    }else{
        false
    }
    kuChangeBackgroundTint(if (bool.value == true) R.color.TemplateGreen else R.color.gray)

}


fun newIntent(context: Context, reminderEntity: ReminderEntity): Intent {
    val intent = Intent(context, ReminderDescriptionActivity::class.java)
    intent.putExtra(EXTRA_ReminderDataItem, reminderEntity)
    return intent
}
