package com.kushalsharma.nasawalli

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import coil.Coil
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.fragment_clicked_patent.view.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ClickedPatentFragment() : Fragment() {

    private val args by navArgs<ClickedPatentFragmentArgs>()
    private var imgUrl: String? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var imageLoader: ImageLoader
    private lateinit var main: MainActivity
    private var btnDownload: CircularProgressButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_clicked_patent, container, false)

        main = MainActivity()
        btnDownload = root.findViewById(R.id.btn_download) as CircularProgressButton

        if (args.patentInfo == "null" && args.patentDecpInfo == "null" && args.patentImgUrlInfo == "null") {
            root.tv_pctitle.text = args.letstryIOTD.title
            root.tv_pcDescription.text = args.letstryIOTD.explanation
            Glide.with(this).load(args.letstryIOTD.url)
                .transform(FitCenter(), RoundedCorners(30))
                .into(root.iv_pc)
            imgUrl = args.letstryIOTD.url

        } else {
            root.tv_pctitle.text = args.patentInfo
            root.tv_pcDescription.text = args.patentDecpInfo
            Glide.with(this).load(args.patentImgUrlInfo).into(root.iv_pc)
            imgUrl = args.patentImgUrlInfo

        }

//        root.img_progressBar.visible(false)
        setPermissionCallback()
        imageLoader = Coil.imageLoader(this.requireContext())

        btnDownload!!.setOnClickListener {

            checkPermissionAndDownloadBitmap(imgUrl.toString())

        }

        return root
    }

    private fun setPermissionCallback() {
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    getBitmapFromUrl(imgUrl.toString())
                }
            }
    }

    //function to check and request storage permission
    private fun checkPermissionAndDownloadBitmap(bitmapURL: String) {
        when {
            ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                getBitmapFromUrl(bitmapURL)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                main.showPermissionRequestDialog(
                    getString(R.string.permission_title),
                    getString(R.string.write_permission_request)
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    //this function will fetch the Bitmap from the given URL
    private fun getBitmapFromUrl(bitmapURL: String) = lifecycleScope.launch {
//        progressbar.visible(true)
        btnDownload!!.startAnimation()
//        imgViewLoader.load(bitmapURL)
        val request = ImageRequest.Builder(requireContext())
            .data(bitmapURL)
            .build()
        try {
            val downloadedBitmap = (imageLoader.execute(request).drawable as BitmapDrawable).bitmap
//            imgViewLoader.setImageBitmap(downloadedBitmap)
            saveMediaToStorage(downloadedBitmap)
        } catch (e: Exception) {
            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
        }

        btnDownload!!.revertAnimation()


//        progressbar.visible(false)
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireActivity().contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)

            Toast.makeText(context, "Saved to Photos", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        btnDownload!!.dispose()
    }
}







