package com.lisowski.clientapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.lisowski.clientapp.R
import com.lisowski.clientapp.adapters.PlaceArrayAdapter
import kotlinx.android.synthetic.main.activity_order.*

class OrderActivity : AppCompatActivity() {
    private var placeAdapter: PlaceArrayAdapter? = null
    private lateinit var mPlacesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)



        Places.initialize(this, "AIzaSyCbPoPSc5SRZy21nq5I7sfKrc8MJGazcBg")
        mPlacesClient = Places.createClient(this)

        placeAdapter = PlaceArrayAdapter(this, R.layout.layout_item_places, mPlacesClient)
        originAutoCompleteTV.setAdapter(placeAdapter)
        destinationAutoCompleteTV.setAdapter(placeAdapter)

        originAutoCompleteTV.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val place = parent.getItemAtPosition(position)
            originAutoCompleteTV.apply {
                setText(place.toString())
                setSelection(originAutoCompleteTV.length())
            }
        }
        destinationAutoCompleteTV.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val place = parent.getItemAtPosition(position)
            destinationAutoCompleteTV.apply {
                setText(place.toString())
                setSelection(destinationAutoCompleteTV.length())
            }
        }
    }
}