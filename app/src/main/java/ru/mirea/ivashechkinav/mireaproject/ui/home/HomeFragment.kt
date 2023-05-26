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
import com.google.gson.GsonBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mirea.ivashechkinav.mireaproject.databinding.FragmentHomeBinding
import ru.mirea.ivashechkinav.mireaproject.ui.home.retrofit.MyApi
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
        initTestApi()
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
    private fun initTestApi() {
        val BASE_URL = "https://www.timeapi.io/api/"
        val gson = GsonBuilder().create()
        val client = OkHttpClient.Builder().build()

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build().create(MyApi::class.java)

        binding.btnApiTest.setOnClickListener {
            lifecycleScope.launch {
                binding.tvApiString.text = api.testApi().dateTime
            }
        }

    }
}