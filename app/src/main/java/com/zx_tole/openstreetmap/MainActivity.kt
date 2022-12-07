package com.zx_tole.openstreetmap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.OverlayItem
import java.util.*


class MainActivity : AppCompatActivity() {
    private var mapView: MapView? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val ctx: Context = applicationContext

        Configuration.getInstance().userAgentValue = packageName
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        
        mapView = findViewById(R.id.mapview)
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setLayerType(View.LAYER_TYPE_HARDWARE,null )
        mapView?.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView?.setMultiTouchControls(true)
        mapView?.zoomController?.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        val mapController = mapView?.controller
        mapController?.setZoom(12.0)

        mapView?.setUseDataConnection(true)
        mapView?.setTileSource(TileSourceFactory.MAPNIK)

        val moscowPoint = GeoPoint(55.751244, 37.618423)
        val moscowMarker = Marker(mapView)
        moscowMarker.setTextIcon(getString(R.string.moscow))
        moscowMarker.position = moscowPoint
        moscowMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView?.overlays?.add(moscowMarker)

        val saintPetePoint = GeoPoint(59.937500, 30.308611)
        val saintPeteMarker = Marker(mapView)
        saintPeteMarker.setTextIcon(getString(R.string.spb))
        saintPeteMarker.position = saintPetePoint
        saintPeteMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView?.overlays?.add(saintPeteMarker)

        mapView?.controller?.setCenter(saintPetePoint)
    }

    override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        if (mapView != null) {
            mapView?.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        Configuration.getInstance().save(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        if (mapView != null) {
            mapView?.onPause()
        }
    }
}