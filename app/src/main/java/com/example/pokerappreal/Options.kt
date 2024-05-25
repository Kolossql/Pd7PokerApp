package com.example.pokerappreal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pokerappreal.databinding.FragmentFirstBinding
import com.example.pokerappreal.databinding.OptionsBinding
import com.example.pokerappreal.databinding.TitleBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Options : Fragment() {

    private var _binding: OptionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = OptionsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startGame.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putInt("numOpponents", binding.seekBar.progress)
            findNavController().navigate(R.id.action_Options_to_Game, bundle)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}