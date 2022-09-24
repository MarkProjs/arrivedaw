package dawsoncollege.android.arrivedaw

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import dawsoncollege.android.arrivedaw.databinding.ActivityMainBinding
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val qrTestString = "Hello this is a test string! It will be used to generate a QR code"
//        binding.QRResult.setImageBitmap(encodeStringToBitmap(qrTestString))


        //setting up the dawson wing dropdown menu
        val dawsonWings = resources.getStringArray(R.array.dawson_wings)
        val dawsonWingsSpinner = findViewById<Spinner>(R.id.dawson_wings)
        if (dawsonWingsSpinner != null) {
            val dawsonWingsAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, dawsonWings)
            dawsonWingsSpinner.adapter = dawsonWingsAdapter
        }

        //setting up metro spinner
        val metroLine = resources.getStringArray(R.array.metro_lines)
        val metroLineSpinner = findViewById<Spinner>(R.id.metro_spinner)
        if (metroLineSpinner != null) {
            val metroLineAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, metroLine)
            metroLineSpinner.adapter = metroLineAdapter
        }
        subFormListener();

        val metroDatePicker: DatePicker = findViewById<DatePicker>(R.id.metro_date)
        val windowDatePicker: DatePicker = findViewById<DatePicker>(R.id.window_date)
        val daysAdded = 1
        metroDatePicker.minDate = System.currentTimeMillis() + 1000
        windowDatePicker.minDate = System.currentTimeMillis() + 1000
        
    }

    private fun subFormListener() {
        val metroRadio: RadioButton = findViewById<RadioButton>(R.id.entry_by_metro)
        val landRadio: RadioButton = findViewById<RadioButton>(R.id.entry_by_land)
        val windowRadio: RadioButton = findViewById<RadioButton>(R.id.entry_by_window)
        metroRadio.setOnClickListener {
            val metroLayout: LinearLayout = findViewById<LinearLayout>(R.id.subform_metro)
            val landLayout: LinearLayout = findViewById<LinearLayout>(R.id.subform_doors)
            val windowLayout: LinearLayout = findViewById<LinearLayout>(R.id.subform_window)
            metroLayout.visibility = View.VISIBLE
            landLayout.visibility = View.GONE
            windowLayout.visibility = View.GONE
        }

        landRadio.setOnClickListener {
            val metroLayout: LinearLayout = findViewById<LinearLayout>(R.id.subform_metro)
            val landLayout: LinearLayout = findViewById<LinearLayout>(R.id.subform_doors)
            val windowLayout: LinearLayout = findViewById<LinearLayout>(R.id.subform_window)
            metroLayout.visibility = View.GONE
            landLayout.visibility = View.VISIBLE
            windowLayout.visibility = View.GONE

        }

        windowRadio.setOnClickListener {
            val metroLayout: LinearLayout = findViewById<LinearLayout>(R.id.subform_metro)
            val landLayout: LinearLayout = findViewById<LinearLayout>(R.id.subform_doors)
            val windowLayout: LinearLayout = findViewById<LinearLayout>(R.id.subform_window)
            metroLayout.visibility = View.GONE
            landLayout.visibility = View.GONE
            windowLayout.visibility = View.VISIBLE
        }
    }
}

    @Throws(WriterException::class)
    fun encodeStringToBitmap(str: String): Bitmap {
        /* First encode the string into a bit matrix... */
        val bitMatrix: BitMatrix = QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, 400, 400)

        val pixels = IntArray(bitMatrix.width * bitMatrix.height)

        /* ... then translate the bit matrix into a grid of pixels ... */
        for (y in 0 until bitMatrix.height) {
            val offset = y * bitMatrix.width

            for (x in 0 until bitMatrix.width) {
                if (bitMatrix[x, y]) {
                    pixels[offset + x] = Color.BLACK
                } else {
                    pixels[offset + x] = Color.WHITE
                }
            }
        }

        /* ... finally put the grid of pixels into a bitmap image */
        val bitmap: Bitmap =
            Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, bitMatrix.width, 0, 0, bitMatrix.width, bitMatrix.height)

        return bitmap
    }
