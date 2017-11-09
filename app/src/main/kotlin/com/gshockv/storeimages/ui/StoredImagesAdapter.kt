package com.gshockv.storeimages.ui

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gshockv.storeimages.R
import com.gshockv.storeimages.data.ImageItem
import com.gshockv.storeimages.utils.showImage
import kotlinx.android.synthetic.main.item_stored_image.view.*

class StoredImagesAdapter(context: Context, val images: MutableList<ImageItem>)
    : RecyclerView.Adapter<StoredImagesAdapter.ImageViewHolder> () {

    private val inflater : LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ImageViewHolder {
        return ImageViewHolder(inflater.inflate(R.layout.item_stored_image, parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder?, position: Int) {
        holder?.bind(images[position])
    }

    override fun getItemCount() = images.size

    class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: ImageItem) {
            itemView.imageView.showImage(Uri.parse(item.url))
        }
    }

}
