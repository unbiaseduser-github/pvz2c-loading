package com.unbiaseduser.pvz2cloadingsample

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.unbiaseduser.pvz2cloadingsample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.toggle.setOnClickListener {
            viewModel.isOverlayVisible = !viewModel.isOverlayVisible
        }
        binding.pause.setOnClickListener {
            binding.overlay.pause()
        }
        binding.resume.setOnClickListener {
            binding.overlay.resume()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isOverlayVisibleFlow.collect { isVisible ->
                    if (isVisible) {
                        binding.pause.isVisible = true
                        binding.resume.isVisible = true
                        binding.overlay.isVisible = true
                    } else {
                        binding.pause.isVisible = false
                        binding.resume.isVisible = false
                        binding.overlay.isVisible = false
                    }
                }
            }
        }
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.content) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }
}