package com.alialfayed.locationreminder.ui.home.features.dashboardReminds.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.alialfayed.locationreminder.R
import com.alialfayed.locationreminder.core.common.app.BaseApp
import com.alialfayed.locationreminder.databinding.FragmentDashboardRemindsBinding
import com.alialfayed.locationreminder.ui.auth.view.AuthActivity
import com.alialfayed.locationreminder.ui.home.viewModel.OneSingleViewModel
import com.alialfayed.locationreminder.utils.AppConstant


class DashboardRemindsFragment : Fragment() {

    private var _binding: FragmentDashboardRemindsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val mViewModel by activityViewModels<OneSingleViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardRemindsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fragment = this@DashboardRemindsFragment
            lifecycleOwner = this@DashboardRemindsFragment
        }

        setMenu()
        setUpViewModelStateObservers()
    }

    private fun setUpViewModelStateObservers() {
        mViewModel.logoutState.observe(viewLifecycleOwner ){
            if (it == false) {
                isLoading(false)
                BaseApp.appPreferences.removeKey(AppConstant.USER)
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
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