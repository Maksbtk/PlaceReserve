package com.onlylemi.mapview.library;

import android.view.MotionEvent;

/**
 * MapViewListener
 *
 * @author: onlylemi
 */
public interface MapViewListener {

    /**
     * when mapview load complete to callback
     */
    void onMapLoadSuccess();

    /**
     * when mapview load error to callback
     */
    void onMapLoadFail();

    /**
     * when on touch event to callback
     */
    void onMapTouch(MotionEvent event);

}
