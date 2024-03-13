package com.attyran.flickrsearch

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.attyran.flickrsearch.databinding.FlickrRvItemBinding
import com.attyran.flickrsearch.network.Photo
import coil.load
import coil.transform.RoundedCornersTransformation

class FlickrAdapter(private val interactor: FlickrAdapterViewHolder.Interactor?) : ListAdapter<Photo, FlickrAdapter.FlickrAdapterViewHolder>(FlickrItemCallback()) {

    private var _binding: FlickrRvItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        _binding = FlickrRvItemBinding.inflate(inflater, parent, false)
        val view = binding.root
        return FlickrAdapterViewHolder(binding, view, interactor)
    }

    override fun onBindViewHolder(holder: FlickrAdapterViewHolder, position: Int) {
        val photo = getItem(position)
        holder.bind(photo)
    }

    class FlickrAdapterViewHolder(private val binding: FlickrRvItemBinding, itemView: View, private val interactor: Interactor?) : RecyclerView.ViewHolder(itemView) {
        fun bind(photo: Photo) {
            val imageUrl = String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg",
                photo.farm, photo.server, photo.id, photo.secret)
            binding.itemImage.load(imageUrl) {
                crossfade(true)
                transformations(RoundedCornersTransformation(25f))
            }
            itemView.setOnClickListener {
                interactor?.onItemSelected(photo, binding.itemImage)
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
