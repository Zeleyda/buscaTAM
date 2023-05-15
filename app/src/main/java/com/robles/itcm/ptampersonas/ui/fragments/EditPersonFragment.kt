package com.robles.itcm.ptampersonas.ui.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.robles.itcm.ptampersonas.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EditPersonFragment : Fragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var currentBackground: Drawable
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var org1: ImageView
    private lateinit var org2: ImageView
    private lateinit var org3: ImageView
    private lateinit var intent: Intent

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
        val view = inflater.inflate(R.layout.fragment_edit_person, container, false)
        val toolbar = requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        currentBackground = toolbar.background
        toolbar.setBackgroundResource(R.drawable.banner3)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        org1 = view.findViewById(R.id.img_org1)
        org2 = view.findViewById(R.id.img_org2)
        org3 = view.findViewById(R.id.img_org3)
        org1.setOnClickListener {
            intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+52 834 688 5075"))
            startActivity(intent)
        }

        org2.setOnClickListener {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=100064439063925"))
            startActivity(intent)
        }

        org3.setOnClickListener {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=100067415101708"))
            startActivity(intent)
        }
        super.onViewCreated(view, savedInstanceState)
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditPersonFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditPersonFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toolbar.background = currentBackground
    }
}