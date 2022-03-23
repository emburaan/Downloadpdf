package com.sumit.downloadpdf

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sumit.downloadpdf.databinding.ActivityMainBinding

const val URL =
    "https://mu.ac.in/wp-content/uploads/2021/07/FYBCOM-Foundation-Course-I.pdf" //dummy url

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission()
    }

    fun submitBtnClick() {
        binding.btnDownloadPdf.setOnClickListener {
            val timeStamp = System.currentTimeMillis() / 1000
            val ts = timeStamp.toString()

            val request = DownloadManager.Request(Uri.parse(URL))
                .setTitle("File")
                .setDescription("Downloading...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)

            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS.toString(),
                "${ts}.pdf"
            )


            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }
    }

    private fun checkPermission() {
        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                    when {
                        p0!!.areAllPermissionsGranted() -> {
                            submitBtnClick()
                            Toast.makeText(
                                applicationContext,
                                "Permission Granted",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        p0.isAnyPermissionPermanentlyDenied -> {
                            showRationalDialogForPermission()
                        }
                        else -> {
                            Toast.makeText(
                                applicationContext,
                                "Permission Required to use this feature",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }

            }).onSameThread().check()
    }

    internal fun showRationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("Permission Is Required For Accessing Location! You can enable it from App Settings")
            .setPositiveButton("Settings") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
                dialog.dismiss()
            }
            .show()
    }
}