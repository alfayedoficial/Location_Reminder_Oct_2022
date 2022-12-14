package com.udacity.project4.locationreminders.features.reminderList.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.alfayedoficial.kotlinutils.*
import com.udacity.project4.core.common.app.MyApp
import com.udacity.project4.locationreminders.data.dto.Reminders
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.authentication.view.AuthenticationActivity
import com.udacity.project4.locationreminders.features.reminderList.adapter.RemindersRvAdapter
import com.udacity.project4.locationreminders.features.reminderList.viewModel.RemindersListViewModel
import com.udacity.project4.utils.AppConstant
import org.koin.android.ext.android.inject


class ReminderListFragment : Fragment() {

    private var _binding: FragmentRemindersBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val mViewModel : RemindersListViewModel by inject()
    private val adapterRemindersRv : RemindersRvAdapter by lazy { RemindersRvAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fragment = this@ReminderListFragment
            lifecycleOwner = this@ReminderListFragment
        }

        setMenu()
        setUpViewModelStateObservers()
    }

    private fun setUpViewModelStateObservers() {
        mViewModel.logoutState.observe(viewLifecycleOwner ){
            if (it == false) {
                isLoading(false)
                MyApp.appPreferences.removeKey(AppConstant.USER)
                startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
            }
        }

        mViewModel.showLoadingLiveData.observe(viewLifecycleOwner){isLoading(it)}
        mViewModel.reminders.observe(viewLifecycleOwner){ initRemindersList(it) }
        mViewModel.errorLiveData.observe(viewLifecycleOwner){ showError(it) }

    }

    override fun onResume() {
        super.onResume()
        mViewModel.getReminders()
    }

    private fun showError(it: String?) {
        if (it != null && it.isNotEmpty()) {
            kuSnackBarError(it,kuRes.getColor(R.color.white , kuRes.newTheme()),kuRes.getColor(R.color.TemplateRed , kuRes.newTheme()))
        }
    }


    private fun initRemindersList(reminders : Reminders){
        binding.apply {
           if (reminders.isNotEmpty()){

               if (reminders.size == adapterRemindersRv.itemCount) return

               remindersRv.apply {
                   kuShow()
                   kuInitLinearLayoutManager(RecyclerView.VERTICAL, adapterRemindersRv)
               }
               tvNoRemindersIndicator.kuHide()
               adapterRemindersRv.apply {
                   setDataList(reminders)
               }
           }else{
               tvNoRemindersIndicator.kuShow()
               remindersRv.kuHide()

           }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.dashboard, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.menuLogout -> {
                        isLoading(true)
                        mViewModel.logout(requireContext())
                        return true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun isLoading(b: Boolean) {
        if (b){
            binding.lyContainerIsLoading.root.visibility = View.VISIBLE
        }else{
            binding.lyContainerIsLoading.root.visibility = View.GONE
        }
    }


    fun onAddRemindClicked() {
        findNavController().navigate(R.id.action_DashboardRemindsFragment_to_saveRemindFragment)
    }
}