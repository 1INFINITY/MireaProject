package ru.mirea.ivashechkinav.mireaproject.ui.slideshow

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.mirea.ivashechkinav.mireaproject.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment(), SensorEventListener {

    private lateinit var binding: FragmentSlideshowBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometerSensor: Sensor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)
        binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this, accelerometerSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            val light = event.values[0]

            binding.textSlideshow.text = "Сенсор света для допроса говорит: \n" +
                if (light < 3f)
                    "Преступнику все равно"
                else if (light < 8f)
                    "Преступник готов к допросу"
                else
                    "Преступник сознается"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

}