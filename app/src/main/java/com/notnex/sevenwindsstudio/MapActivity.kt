package com.notnex.sevenwindsstudio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.MapKitFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import com.notnex.sevenwindsstudio.data.model.Location
import com.notnex.sevenwindsstudio.R
import android.util.Log

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var mapObjects: MapObjectCollection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.WHITE
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapview)
        mapObjects = mapView.map.mapObjects

        findViewById<android.widget.ImageButton>(R.id.backButton)?.setOnClickListener {
            finish()
        }

        // Очищаем карту от тестовых маркеров и логов, отображаем только реальные кофейни
        val cafesJson = intent.getStringExtra("cafes_json")
        if (!cafesJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<Location>>() {}.type
            val cafes: List<Location> = Gson().fromJson(cafesJson, type)
            for (cafe in cafes) {
                val point = Point(cafe.point.latitude, cafe.point.longitude)
                val placemark = mapObjects.addPlacemark(point)
                placemark.setIcon(ImageProvider.fromResource(this, R.drawable.marker))
            }
            // Центрируем карту на первой кофейне, если есть
            if (cafes.isNotEmpty()) {
                mapView.map.move(
                    com.yandex.mapkit.map.CameraPosition(
                        Point(cafes[0].point.latitude, cafes[0].point.longitude),
                        14.0f, 0.0f, 0.0f
                    )
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
} 