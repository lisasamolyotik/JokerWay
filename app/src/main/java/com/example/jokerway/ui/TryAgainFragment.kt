package com.example.jokerway.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.jokerway.R
import com.example.jokerway.databinding.TryAgainFragmentBinding

class TryAgainFragment : Fragment(R.layout.try_again_fragment) {
    private var _binding: TryAgainFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TryAgainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tryAgainButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<LevelFragment>(R.id.fragment_container_view)
                addToBackStack(null)
            }
        }


        binding.optionsButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}