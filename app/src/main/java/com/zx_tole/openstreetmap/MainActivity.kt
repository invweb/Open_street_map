package com.zx_tole.openstreetmap

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.zx_tole.openstreetmap.data.City
import com.zx_tole.openstreetmap.viewModels.MainViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.zx_tole.openstreetmap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel.loadJSONFromAsset(this, "items.json")
        setContentView(binding.root)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val ctx: Context = applicationContext

        Configuration.getInstance().userAgentValue = packageName
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        
        mapView = binding.mapview
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setLayerType(View.LAYER_TYPE_HARDWARE,null )
        mapView?.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView?.setMultiTouchControls(true)
        mapView?.zoomController?.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        val mapController = mapView?.controller
        mapController?.setZoom(6.0)

        mapView?.setUseDataConnection(true)
        mapView?.setTileSource(TileSourceFactory.MAPNIK)

//        val aMoscow = City(
//            55.751244,
//            37.618423,
//            getString(R.string.moscow),
//        )
//
//        val moscowPoint = GeoPoint(aMoscow.lat, aMoscow.lon)
//        val moscowMarker = Marker(mapView)
//        moscowMarker.setTextIcon(aMoscow.name)
//        moscowMarker.position = moscowPoint
//        moscowMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//        mapView?.overlays?.add(moscowMarker)
//
//        val aSaintPete = City(
//            59.937500,
//            30.308611,
//            getString(R.string.spb)
//        )
//
//        val saintPetePoint = GeoPoint(aSaintPete.lat, aSaintPete.lon)
//        val saintPeteMarker = Marker(mapView)
//        saintPeteMarker.setTextIcon(aSaintPete.name)
//        saintPeteMarker.position = saintPetePoint
//        saintPeteMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//
//        mapView?.overlays?.add(saintPeteMarker)
//
//        mapView?.controller?.setCenter(saintPetePoint)
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