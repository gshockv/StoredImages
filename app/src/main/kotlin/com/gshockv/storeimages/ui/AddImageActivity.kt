package com.gshockv.storeimages.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.gshockv.storeimages.R
import com.gshockv.storeimages.data.ImageItem
import com.gshockv.storeimages.utils.*
import kotlinx.android.synthetic.main.activity_add_image.*
import java.io.ByteArrayOutputStream
import java.util.*

class AddImageActivity : BaseActivity() {

    lateinit var database: DatabaseReference
    lateinit var storage: FirebaseStorage

    companion object {
        val CHOOSE_FROM_GALLERY_REQUEST = 11
        val IMAGES_ROOT_PATH = "storeImages"

        fun start(context: Context) {
            context.startActivity(Intent(context, AddImageActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)

        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        buttonUpload.isEnabled = false
    }

    override fun onResume() {
        super.onResume()

        initButtons()
        initEditMetaData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == CHOOSE_FROM_GALLERY_REQUEST) {
            displayImage(data?.data)
        }
    }

    private fun displayImage(data: Uri?) {
        imagePreview.showImage(data)
        buttonUpload.isEnabled = true
    }

    private fun initButtons() {
        buttonChoose.setOnClickListener {
            chooseFromGallery()
        }

        buttonUpload.setOnClickListener {
            uploadImage()
        }
    }

    private fun chooseFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSE_FROM_GALLERY_REQUEST)
    }

    private fun uploadImage() {
        buttonUpload.isEnabled = false
        progressUpload.setVisible(true)

        val metadataText = editMetadata.text.toString()

        imagePreview.isDrawingCacheEnabled = true
        imagePreview.buildDrawingCache()

        val bitmap = imagePreview.getDrawingCache()
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        imagePreview.isDrawingCacheEnabled = false
        val data = baos.toByteArray()

        val path = "$IMAGES_ROOT_PATH/${UUID.randomUUID()}.png"
        val imagesReference = storage.getReference(path)

        val metadata = StorageMetadata.Builder()
                .setCustomMetadata("text", metadataText)
                .build()
        val uploadTask = imagesReference.putBytes(data, metadata)
        uploadTask.addOnSuccessListener {
            val url = it.downloadUrl

            buttonUpload.isEnabled = true
            progressUpload.setVisible(false)
            registerImageAtDatabase(url, metadataText)
        }.addOnFailureListener {
            Toast.makeText(this, "Error uploading image to cloud: " + it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerImageAtDatabase(url: Uri?, metadataText: String) {
        val newRecord = database.child(Constants.FIREBASE_ITEM).push()
        newRecord.setValue(ImageItem(newRecord.key, metadataText, url.toString()))

        Toast.makeText(this, "Item saved with ID: " + newRecord.key, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun initEditMetaData() {
        editMetadata.afterTextChanged {
            textMetadata.setText(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> consumeItem { finish() }
        }
        return super.onOptionsItemSelected(item)
    }
}
