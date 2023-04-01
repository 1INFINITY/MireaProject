package ru.mirea.ivashechkinav.mireaproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import ru.mirea.ivashechkinav.mireaproject.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment() {

    private lateinit var binding: FragmentWebViewBinding
    private val defaultUrl = "https://www.google.com/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebViewBinding.inflate(inflater)
        binding.webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        binding.webView.loadUrl(defaultUrl)
        return binding.root
    }

}