package com.attyran.flickrsearch

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.attyran.flickrsearch.network.Photo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.flickr_rv_item.view.*

class FlickrAdapter : ListAdapter<Photo, FlickrAdapter.FlickrAdapterViewHolder>(FlickrItemCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.flickr_rv_item, parent, false)
        context = parent.context
        return FlickrAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlickrAdapterViewHolder, position: Int) {
        val photo = getItem(position)
        val imageUrl = String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg",
            photo.farm, photo.server, photo.id, photo.secret)
        Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions().centerInside().transform(RoundedCorners(25)))
                .into(holder.mPhoto)
    }

    class FlickrAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal val mPhoto: ImageView = itemView.item_image

        override fun onClick(v: View?) {
        }
    }

    class FlickrItemCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
}
