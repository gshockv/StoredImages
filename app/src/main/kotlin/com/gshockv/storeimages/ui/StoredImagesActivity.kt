package com.gshockv.storeimages.ui

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gshockv.storeimages.R
import com.gshockv.storeimages.data.ImageItem
import com.gshockv.storeimages.utils.Constants
import com.gshockv.storeimages.utils.consumeItem
import com.gshockv.storeimages.utils.setVisible
import kotlinx.android.synthetic.main.activity_stored_images.*

class StoredImagesActivity : BaseActivity() {

    lateinit var imagesAdapter : StoredImagesAdapter
    lateinit var storedImages : MutableList<ImageItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stored_images)

        setEmptyStateVisible(false)

        storedImages = mutableListOf<ImageItem>()
        imagesAdapter = StoredImagesAdapter(this, storedImages!!)
        recyclerImages.layoutManager = GridLayoutManager(this, 2)
        recyclerImages.adapter = imagesAdapter

        buttonAddImage.setOnClickListener {
            openAddImageScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        loadStoredImages()
    }

    private fun loadStoredImages() {
        storedImages.clear()
        FirebaseDatabase.getInstance()
                .getReference(Constants.FIREBASE_ITEM)
                .orderByKey()
                .addValueEventListener(imageListener)
    }

    var imageListener : ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.mapNotNullTo(storedImages) {
                it.getValue<ImageItem>(ImageItem::class.java)
            }
            setEmptyStateVisible(storedImages.isEmpty())
            imagesAdapter.notifyDataSetChanged()
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("StoredImagesActivity", "loadStoredImages:onCanceled", error.toException())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.itemAdd -> consumeItem { openAddImageScreen() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openAddImageScreen() {
        AddImageActivity.start(this)
    }

    private fun setEmptyStateVisible(visible: Boolean) {
        recyclerImages.setVisible(!visible)
        textEmptyMessage.setVisible(visible)
        buttonAddImage.setVisible(visible)
    }
}
