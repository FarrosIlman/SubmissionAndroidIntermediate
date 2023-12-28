package com.parrosz.storyu.adapter

import android.app.Activity
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.parrosz.storyu.R
import com.parrosz.storyu.databinding.StoryInfoWindowBinding


class StoryInfoWindowAdapter(private val context: Activity) :
    GoogleMap.InfoWindowAdapter {
    private val binding: StoryInfoWindowBinding by lazy {
        StoryInfoWindowBinding.inflate(context.layoutInflater)
    }

    override fun getInfoContents(marker: Marker): View {
        binding.tvUserName.text = context.getString(R.string.story_map_title).format(marker.title)
        binding.tvDescription.text = marker.snippet

        return binding.root
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}