package com.example.ahmatynov

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class homeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<Button>(R.id.button_news).setOnClickListener{view.findNavController().navigate(R.id.action_homeFragment_to_newsFragment)}
        view.findViewById<Button>(R.id.button_resp).setOnClickListener {view.findNavController().navigate(R.id.action_homeFragment_to_scheduleFragment)}
        view.findViewById<Button>(R.id.button_table).setOnClickListener {view.findNavController().navigate(R.id.action_homeFragment_to_gradesFragment)}

        return view
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            homeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}