package com.ware.jetpack.nav

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ware.R
import kotlinx.android.synthetic.main.fragment_a.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DemoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DemoFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FragmentTest", "onCreate: $this")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("FragmentTest", "onCreateView: ")
        return inflater.inflate(R.layout.fragment_a, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("FragmentTest", "onDestroy: ")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FragmentTest", "onDestroyView: ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goFragmentB.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_fragmentA_to_fragmentB)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DemoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
