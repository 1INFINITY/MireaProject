package ru.mirea.ivashechkinav.mireaproject.ui.places

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.mirea.ivashechkinav.mireaproject.databinding.FragmentPlacesBinding

class PlacesFragment : Fragment() {
    private lateinit var mapView: MapView
    private lateinit var binding: FragmentPlacesBinding
    private lateinit var locationNewOverlay: MyLocationNewOverlay
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        binding = FragmentPlacesBinding.inflate(inflater, container, false)
        mapView = binding.mapView
        mapView.setZoomRounding(true)
        mapView.setMultiTouchControls(true)
        val mapController = mapView.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(55.692241, 37.528039)
        mapController.setCenter(startPoint)

        if (checkPermissions().isEmpty())
            initMain()
        else
            checkAndRequestPermissions()
        return binding.root
    }

    fun initMain() {
        locationNewOverlay =
            MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
        locationNewOverlay.enableMyLocation()
        mapView.overlays.add(this.locationNewOverlay)

        val compassOverlay = CompassOverlay(
            requireContext(), InternalCompassOrientationProvider(
                requireContext()
            ), mapView
        )
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)


        val context: Context = requireContext()
        val dm: DisplayMetrics = context.resources.displayMetrics
        val scaleBarOverlay = ScaleBarOverlay(mapView)
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10)
        mapView.overlays.add(scaleBarOverlay)
        addMarker(GeoPoint(55.504584, 36.976272), "VKYSNO AND DOT")
        addMarker(GeoPoint(55.692241, 37.528039), "ROSTICS")
        addMarker(GeoPoint(55.650771, 37.595480), "BLACK STAR BURGER")
    }

    fun addMarker(geoPoint: GeoPoint, text: String) {
        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.setOnMarkerClickListener { marker, mapView ->
            Toast.makeText(
                requireContext(), text,
                Toast.LENGTH_SHORT
            ).show()
            true
        }
        mapView.overlays.add(marker)

        marker.icon = ResourcesCompat.getDrawable(
            resources, org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null
        )

        marker.title = text
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && !grantResults.contains(PackageManager.PERMISSION_DENIED)) {
            initMain()
        }
    }

    override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        Configuration.getInstance().save(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        mapView.onPause()
    }

    fun checkPermissions(): List<String> {
        val permissionsToRequest = mutableListOf<String>()
        for (permission in LOCATION_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }
        return permissionsToRequest
    }

    fun checkAndRequestPermissions() {
        val permissionsToRequest = checkPermissions()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                123
            )
        }
    }

    companion object {
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
}