package com.ojeklokal.customer

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {
 private val db=FirebaseFirestore.getInstance(); private var lat=0.0; private var lng=0.0
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_main)
  findViewById<Spinner>(R.id.service).adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,listOf("Pesan Makanan","Titip Belanja","Antar Barang","Ojek"))
  findViewById<Button>(R.id.gps).setOnClickListener{getGps()}
  findViewById<Button>(R.id.order).setOnClickListener{sendOrder()}
 }
 private fun getGps(){if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),10);return}; val lm=getSystemService(LOCATION_SERVICE) as LocationManager; val p=if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER))LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER; val l=object:LocationListener{override fun onLocationChanged(x:Location){lat=x.latitude;lng=x.longitude;findViewById<TextView>(R.id.gpsText).text="Lokasi GPS: %.6f, %.6f".format(lat,lng);lm.removeUpdates(this)}};lm.requestLocationUpdates(p,1000,1f,l)}
 private fun sendOrder(){val pickup=findViewById<EditText>(R.id.pickup).text.toString().trim();val dest=findViewById<EditText>(R.id.destination).text.toString().trim();val note=findViewById<EditText>(R.id.note).text.toString().trim();if(pickup.isEmpty()||dest.isEmpty()||lat==0.0){Toast.makeText(this,"Isi pickup, tujuan, lalu ambil GPS.",Toast.LENGTH_LONG).show();return};val d=hashMapOf<String,Any>("customerId" to "test-user-001","layanan" to findViewById<Spinner>(R.id.service).selectedItem.toString(),"pickupAddress" to pickup,"destinationAddress" to dest,"note" to note,"distanceKm" to 0.0,"ongkir" to 5000L,"payment" to "COD","status" to "waiting","createdAt" to Timestamp.now(),"pickupLat" to lat,"pickupLng" to lng);db.collection("orders").add(d).addOnSuccessListener{findViewById<TextView>(R.id.status).text="Status: pesanan terkirim, menunggu kurir."}.addOnFailureListener{Toast.makeText(this,"Gagal: ${it.message}",Toast.LENGTH_LONG).show()}}
}
