package com.zx_tole.openstreetmap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.zx_tole.openstreetmap.data.Attraction
import com.zx_tole.openstreetmap.databinding.ActivityMainBinding
import com.zx_tole.openstreetmap.viewModels.MainViewModel
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var road: Road? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val markers = viewModel.loadJSONFromAsset(this, "CitiesAndAttractions.json")
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

        val centerGeoPoint = GeoPoint(markers!!.centerLat, markers.centerLon)
        mapController?.setCenter(centerGeoPoint)

        mapView?.setUseDataConnection(true)
        mapView?.setTileSource(TileSourceFactory.MAPNIK)


        var attractionFirst: Attraction? = null
        var attractionSecond: Attraction? = null
        for (city in markers.items) {
            for(attraction in city.attractions) {
                val attractionPoint = GeoPoint(attraction.lat, attraction.lon)
                val attractionMarker = Marker(mapView)
                attractionMarker.title = attraction.name
                attractionMarker.position = attractionPoint
                attractionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView?.overlays?.add(attractionMarker)

                if(attractionFirst == null){
                    attractionFirst = attraction
                } else if(attractionSecond == null) {
                    attractionSecond = attraction
                }

                if(attractionFirst != null && attractionSecond != null) {
                    addRoad(
                        attractionFirst.lat,
                        attractionFirst.lon,
                        attractionSecond.lat,
                        attractionSecond.lon
                    )

                    attractionFirst = null
                    attractionSecond = null
                }
            }

            val cityPoint = GeoPoint(city.lat, city.lon)
            val cityMarker = Marker(mapView)
            cityMarker.title = city.name
            cityMarker.position = cityPoint
            cityMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView?.overlays?.add(cityMarker)
        }
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

    fun addRoad(source_lat: Double, source_lng: Double, dst_lat: Double, dst_lng: Double){
        Thread {
            val roadManager: RoadManager = OSRMRoadManager(this, "")
            val waypoints = ArrayList<GeoPoint>()
            val startPoint = GeoPoint(source_lat, source_lng)
            waypoints.add(startPoint)
            val endPoint = GeoPoint(dst_lat, dst_lng)
            waypoints.add(endPoint)
            try {
                road = roadManager.getRoad(waypoints)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            road?.let {
                runOnUiThread {
                    if (it.mStatus !== Road.STATUS_OK) {
                        Timber.d("handle error... warn the user, etc.")
                    }
                    val roadOverlay: Polyline =
                        RoadManager.buildRoadOverlay(it, Color.RED, 8f)
                    mapView?.overlays?.add(roadOverlay)
                }
            }
        }.start()
    }
}