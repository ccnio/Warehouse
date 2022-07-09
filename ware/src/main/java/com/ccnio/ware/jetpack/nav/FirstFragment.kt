package com.ccnio.ware.jetpack.nav

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ccnio.ware.R

private const val TAG = "NavFirstFragment"

class FirstFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: firstHostAc = $activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        /**
         * 界面跳转
         */
        view.findViewById<View>(R.id.btn_to_second_fragment).setOnClickListener {
            /**
            //NavHostFragment.findNavController(this).navigate(R.id.action_first_to_second)
            //findNavController().navigate(R.id.action_first_to_second)
            val bundle = Bundle()
            bundle.putString("KEY_1", "我是从 First 过来的")
            Navigation.findNavController(view).navigate(R.id.action_first_to_second, bundle)
             **/
            val args: SecondFragmentArgs = SecondFragmentArgs.Builder(
                true, "string value"
            ).build()
            Navigation.findNavController(view)
                .navigate(R.id.action_first_to_second, args.toBundle())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
    }

}