package ru.mirea.ivashechkinav.mireaproject.ui.profile

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.mirea.ivashechkinav.mireaproject.R
import ru.mirea.ivashechkinav.mireaproject.databinding.FragmentGalleryBinding
import ru.mirea.ivashechkinav.mireaproject.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var binding: FragmentProfileBinding

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPrefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.btnLoadData.setOnClickListener(this::loadDataFromPrefs)
        binding.btnSaveData.setOnClickListener(this::saveTextInPrefs)
        return binding.root
    }
    private fun loadDataFromPrefs(view: View) {
        val name = sharedPrefs.getString(NAME_KEY, null) ?: return
        val age = sharedPrefs.getString(AGE_KEY, null) ?: return
        binding.tvSavedName.text = name
        binding.tvSavedAge.text = age
    }
    private fun saveTextInPrefs(view: View) {
        with(sharedPrefs.edit()) {
            putString(NAME_KEY, binding.etvName.text.toString())
            putString(AGE_KEY, binding.etvAge.text.toString())
            apply()
        }
    }
    companion object {
        const val PREFS_NAME = "ProfileFragmentPrefs"
        const val NAME_KEY = "ProfileFragmentName"
        const val AGE_KEY = "ProfileFragmentAge"
    }
}