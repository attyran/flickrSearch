package com.attyran.flickrsearch

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

class FlickrAdapter(private val interactor: FlickrAdapterViewHolder.Interactor?) : ListAdapter<Photo, FlickrAdapter.FlickrAdapterViewHolder>(FlickrItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.flickr_rv_item, parent, false)
        return FlickrAdapterViewHolder(view, interactor)
    }

    override fun onBindViewHolder(holder: FlickrAdapterViewHolder, position: Int) {
        val photo = getItem(position)
        holder.bind(photo)
    }

    class FlickrAdapterViewHolder
    constructor(itemView: View, private val interactor: Interactor?) : RecyclerView.ViewHolder(itemView) {
        fun bind(photo: Photo) {
            val imageUrl = String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg",
                photo.farm, photo.server, photo.id, photo.secret)
            Glide.with(itemView.item_image)
                .load(imageUrl)
                .apply(RequestOptions().centerInside().transform(RoundedCorners(25)))
                .into(itemView.item_image)

            itemView.setOnClickListener {
                interactor?.onItemSelected(photo, itemView.item_image)
            }
        }

        interface Interactor {
            fun onItemSelected(photo: Photo, image: ImageView)
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
