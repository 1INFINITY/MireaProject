package ru.mirea.ivashechkinav.mireaproject.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mirea.ivashechkinav.mireaproject.databinding.FragmentHomeBinding
import ru.mirea.ivashechkinav.mireaproject.workers.WorkerCounter

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        initWorker()
        return binding.root
    }

    private fun initWorker() {
        binding.btnStartWorker.setOnClickListener {
            val workRequest = OneTimeWorkRequest.Builder(WorkerCounter::class.java).build()
            WorkManager
                .getInstance(requireContext())
                .enqueue(workRequest)
        }
       lifecycleScope.launch {
           repeat(Int.MAX_VALUE) {
               binding.tvProgress.text = "Current counter is ${getWorkerMessage()}"
               delay(1000)
           }
       }
    }
    private fun getWorkerMessage(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt(WorkerCounter.WORKER_MSG, 0)
    }
}