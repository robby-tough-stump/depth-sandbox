package com.google.ar.core.examples.java.rawdepth

import android.graphics.Bitmap
import android.graphics.Color
import android.media.Image
import android.util.Log
import android.widget.ImageView
import com.google.ar.core.Frame
import com.google.ar.core.Session
import com.google.ar.core.exceptions.NotYetAvailableException
import java.nio.ByteOrder

object DepthDisplayer {

    const val TAG = "DepthDisplayer"

    fun showDepth(frame: Frame, session: Session, view: ImageView) {
//        Log.d(TAG, "depth displayin")

        var depthImage: Image? = null
        try { depthImage = frame.acquireRawDepthImage16Bits() }catch (e: NotYetAvailableException) { return }
        val depthWidth = depthImage.width
        val depthHeight = depthImage.height

        //ok we're going to try and turn this into a bitmap
        val outputBitmap = Bitmap.createBitmap(depthWidth, depthHeight, Bitmap.Config.ARGB_8888)

        val depthShortBuffer = depthImage.planes[0].buffer.order(ByteOrder.nativeOrder()).asShortBuffer()

        for(row in 0 until depthHeight) {
            for(column in 0 until depthWidth) {
                val depthMillimeters = depthShortBuffer.get(row * depthWidth + column)

                var bitmapColor = Color.BLUE

                //15 meters for now, arbitrary
                val maxDist = 5000f

                val depthProportion = (depthMillimeters/maxDist).coerceIn(0f..1f)

                if (depthMillimeters.toInt() != 0) {
                    val intensity = (depthProportion*0xFF).toInt()
                    bitmapColor = Color.argb(0xFF, intensity, intensity, intensity)

                    Log.d(TAG, depthMillimeters.toString())

                }

                outputBitmap.setPixel(column, row, bitmapColor)
                


            }
        }
        view.setImageBitmap(outputBitmap)
    }
}