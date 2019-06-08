package com.example.placereserve

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class TableIconsCache {
    companion object {
        // images caches
        var freeIconBmp: Bitmap? = null
        var choosedIconBmp: Bitmap? = null
        var busyIconBmp: Bitmap? = null
        var choosedIconBmpAdm: Bitmap? = null

        private var TAG: String = "TableIconsCache"

        /********************************** IMAGES (Glide) **************************************/
        /****************************************************************************************/
        private fun generateFreeIcon(activity: PlaceActivity) {
            if (freeIconBmp != null && !freeIconBmp!!.isRecycled) return
            Glide.with(activity).asBitmap().load(R.drawable.free_1).override(150, 150).into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    freeIconBmp = resource
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
            //Picasso.get().load(R.drawable.free_1).resize(150, 150).into(freeTarget!!)
        }

        private fun generateChoosedIcon(activity: PlaceActivity) {
            if (choosedIconBmp != null && !choosedIconBmp!!.isRecycled) return
            Glide.with(activity).asBitmap().load(R.drawable.choosedtable).override(150, 150).into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    choosedIconBmp = resource
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
            //Picasso.get().load(R.drawable.choosedtable).resize(150, 150).into(choosedTarget!!)
        }
        private fun generateChoosedIconAdm(activity: PlaceActivity) {
            if (choosedIconBmpAdm != null && !choosedIconBmpAdm!!.isRecycled) return
            Glide.with(activity).asBitmap().load(R.drawable.adm_chose_table).override(150, 150).into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    choosedIconBmpAdm = resource
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
            //Picasso.get().load(R.drawable.adm_chose_table).resize(150, 150).into(choosedTargetAdm!!)
        }
        private fun generateBusyIcon(activity: PlaceActivity) {
            if (busyIconBmp != null && !busyIconBmp!!.isRecycled) return
            Glide.with(activity).asBitmap().load(R.drawable.busy_1).override(150, 150).into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    busyIconBmp = resource
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
            //Picasso.get().load(R.drawable.busy_1).resize(150, 150).into(busyTarget!!)
        }

        fun prepareIcons(activity: PlaceActivity) {
            Log.i(TAG, "Preparing...")
            generateChoosedIcon(activity)
            generateChoosedIconAdm(activity)
            generateFreeIcon(activity)
            generateBusyIcon(activity)
            Log.i(TAG, "Prepare done!")
        }

        fun recycleIcons() {
            Log.i(TAG, "Recycling...")
            if(freeIconBmp != null  && !freeIconBmp!!.isRecycled) {
                freeIconBmp!!.recycle()
            }
            if(choosedIconBmp != null && !choosedIconBmp!!.isRecycled) {
                choosedIconBmp!!.recycle()
            }
            if(busyIconBmp != null && !busyIconBmp!!.isRecycled) {
                busyIconBmp!!.recycle()
            }
            if(choosedIconBmpAdm != null && !choosedIconBmpAdm!!.isRecycled) {
                choosedIconBmpAdm!!.recycle()
            }

            Log.i(TAG, "Recycling done!")
        }
        /****************************************************************************************/
    }
}