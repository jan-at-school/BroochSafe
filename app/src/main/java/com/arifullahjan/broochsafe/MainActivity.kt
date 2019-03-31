package com.arifullahjan.broochsafe

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.arifullahjan.broochsafe.firebase.Report
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener , OnMapReadyCallback {

    private val MY_LOCATION_REQUEST_CODE = 9899

    private val TAG = "Main"

    private lateinit var mMap: GoogleMap
    private lateinit var currLocation:LatLng
    private var reportsList:List<Report> = ArrayList()
    val db = FirebaseFirestore.getInstance()

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->

            if(mMap!=null){

                val intent = Intent(this, ReportActivity::class.java)

                mFusedLocationClient?.lastLocation?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {

                        intent.putExtra("lat",task.result!!.latitude)
                        intent.putExtra("long",task.result!!.longitude)

                        startActivity(intent)

                    } else {

                        Toast.makeText(this,"Couldn't get location",Toast.LENGTH_LONG).show()
                    }
                }
                // start your next activity


            }
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)







        // Location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        retrieveReports()

    }



    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.size == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }


    fun retrieveReports(){

        db.collection("reports")
                .get()
                .addOnSuccessListener { result ->
                    reportsList = ArrayList()
                    for (document in result) {
                        (reportsList as ArrayList<Report>).add(document.toObject(Report::class.java))
                    }
                    if(mMap!=null){
                        showReportsOnMapNow()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
    }

    private fun showReportsOnMapNow() {
        reportsList.forEach{report ->
            mMap.addMarker(MarkerOptions()
                    .position(LatLng(report.location.latitude,report.location.longitude))
                    .title(report.category).snippet(report.details)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            moveToCurrentLocation()
            showReportsOnMapNow()
        } else {
            val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions,0)
        }


//        // Add a marker in Sydney, Australia, and move the camera.
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }



    @SuppressLint("MissingPermission")
    private fun moveToCurrentLocation() {
        mFusedLocationClient?.lastLocation?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(task.result!!.latitude, task.result!!.longitude)))

            } else {

                Toast.makeText(this,"Couldn't get location",Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_map -> {
                // Handle the camera action
            }
            R.id.nav_cyber_portal -> {

            }
            R.id.nav_reports -> {

            }
            R.id.nav_settings -> {

            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
