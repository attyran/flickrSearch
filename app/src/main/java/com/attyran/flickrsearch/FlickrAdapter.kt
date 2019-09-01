package com.attyran.flickrsearch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

import java.util.ArrayList

class FlickrAdapter(private val mPhotos: List<Photo>) : RecyclerView.Adapter<FlickrAdapter.FlickrAdapterViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.flickr_rv_item, parent, false)
        context = parent.context
        return FlickrAdapterViewHolder(view, mPhotos)
    }

    override fun onBindViewHolder(holder: FlickrAdapterViewHolder, position: Int) {
        val photo = mPhotos[position]
        val imageUrl = String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg", photo.farm, photo.server, photo.id, photo.secret)//"https://farm" + photo.farm + ".staticflickr.com/" + photo.server + "/" + photo.id + "_" + photo.secret + ".jpg"
        Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions().centerInside().transform(RoundedCorners(25)))
                .into(holder.mPhoto)
    }

    override fun getItemCount(): Int {
        return mPhotos.size
    }

    class FlickrAdapterViewHolder(itemView: View, photos: List<Photo>) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal val mPhoto: ImageView
        private var mPhotos: List<Photo> = ArrayList()

        init {
            this.mPhotos = photos

            mPhoto = itemView.findViewById(R.id.item_image)

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
//        val position = adapterPosition
//        val photo = mPhotos[position]
//        val imageUrl = "http://farm" + photo.farm + ".staticflickr.com/" + photo.server + "/" + photo.id + "_" + photo.secret + ".jpg"
//
//        val detailIntent = Intent(this.context, DetailActivity::class.java)
//        detailIntent.putExtra(context.getString(R.string.image_url), imageUrl)
//        this.context.startActivity(detailIntent)
        }
    }
}
