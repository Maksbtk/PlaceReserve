package com.example.placereserve

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class TableIconsCache {
    companion object {
        private var TAG: String = "TableIconsCache"

        // images caches
        var freeIconBmp: Bitmap? = null
        var choosedIconBmp: Bitmap? = null
        var busyIconBmp: Bitmap? = null

        /********************************* IMAGES (Picasso) *************************************/
        /****************************************************************************************/
        private var freeTarget: Target? = object: com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap == null) {
                    Log.w(TAG, "Image is null!")
                } else {
                    freeIconBmp = bitmap
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.e(TAG, "Ah shit, here we go again")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        }

        private var choosedTarget: Target? = object: com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap == null) {
                    Log.w(TAG, "Image is null!")
                } else {
                    choosedIconBmp = bitmap
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.e(TAG, "Ah shit, here we go again")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        }

        private var busyTarget: Target? = object: com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap == null) {
                    Log.w(TAG, "Image is null!")
                } else {
                    busyIconBmp = bitmap
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.e(TAG, "Ah shit, here we go again")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        }

        private fun generateFreeIcon() {
            if(freeIconBmp != null) return
            Picasso.get().load(R.drawable.free_1).resize(150,150).into(freeTarget!!)
        }

        private fun generateChoosedIcon() {
            if(choosedIconBmp != null) return
            Picasso.get().load(R.drawable.choosedtable).resize(150,150).into(choosedTarget!!)
        }

        private fun generateBusyIcon() {
            if(busyIconBmp != null) return
            Picasso.get().load(R.drawable.busy_1).resize(150,150).into(busyTarget!!)
        }

        fun prepareIcons() {
            Log.i(TAG, "Preparing...")
            generateChoosedIcon()
            generateFreeIcon()
            generateBusyIcon()
            Log.i(TAG, "Prepare done!")
        }
        /****************************************************************************************/
    }
}