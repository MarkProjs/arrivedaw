package dawsoncollege.android.arrivedaw

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import dawsoncollege.android.arrivedaw.databinding.ActivityMainBinding
//data class for the metro data
data class MetroData(val rb1: RadioButton, val metroRadio: RadioButton, val lineMetro: Spinner,
                     val metroNumber: EditText, val dateMetro: String, val timeMetro: String,
                    val studentId: EditText)
//data class for the door data
data class DoorData(val rb1: RadioButton, val landRadio: RadioButton, val dawsonWing: Spinner,
val timeDoor: String, val studentId: EditText)

//data class for the window data
data class WindowData(val rb1: RadioButton, val windowRadio: RadioButton, val wingRoom: EditText,
val requireLadder: CheckBox, val dateWindow: String, val studentId: EditText)


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //declaring the variables
        val genQr: Button = binding.generateButton
        val qrResult: ImageView? = binding.QRResult
        val firstRadioGroup: RadioGroup = binding.firstQuestionRadio
        val secondRadioGroup: RadioGroup = binding.secondQuestionRadio
        val studentId: EditText = binding.studentNumber
        val firstErrorText: TextView? = binding.errorFirstQuestion
        val secondErrorText: TextView? = binding.errorSecondQuestion
        val thirdErrorText: TextView? = binding.errorThirdQuestion
        val metroRadio: RadioButton = binding.entryByMetro
        val landRadio: RadioButton = binding.entryByLand
        val windowRadio: RadioButton = binding.entryByWindow
        val scrollDown: ScrollView = binding.rootLayout

        //variables for the first sub-form
        val lineMetro: Spinner = binding.metroSpinner
        val metroNumber: EditText = binding.metroText
        val metroDate: DatePicker = binding.metroDate
        val metroTime: TimePicker = binding.metroTime

        //variables for the second sub form
        val dawsonWing: Spinner = binding.dawsonWings
        val doorTime: TimePicker = binding.timeSubformDoor

        //variables for the third sub-form
        val wingRoom: EditText = binding.roomNumber
        val requireLadder: CheckBox = binding.stairsCheckbox
        val windowDate: DatePicker = binding.windowDate

        //Real-time error messages
        val metroError: TextView? = binding.metroNumberError
        val roomError : TextView? = binding.roomNumberError

        //setting up the dawson wing dropdown menu
        val dawsonWings = resources.getStringArray(R.array.dawson_wings)
        val dawsonWingsSpinner = binding.dawsonWings
        if (dawsonWingsSpinner != null) {
            val dawsonWingsAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, dawsonWings)
            dawsonWingsSpinner.adapter = dawsonWingsAdapter
        }

        //setting up metro spinner
        val metroLine = resources.getStringArray(R.array.metro_lines)
        val metroLineSpinner = binding.metroSpinner
        if (metroLineSpinner != null) {
            val metroLineAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, metroLine)
            metroLineSpinner.adapter = metroLineAdapter
        }

        //Listener for showing the sub-forms
        if (roomError != null && metroError != null) {
            subFormListener(genQr, metroError, roomError , metroRadio, landRadio, windowRadio)
        }

        //setting up the date pickers to be default to tomorrow
        val metroDatePicker: DatePicker = binding.metroDate
        val windowDatePicker: DatePicker = binding.windowDate
        metroDatePicker.minDate = System.currentTimeMillis() + 24*60*60*1000
        windowDatePicker.minDate = System.currentTimeMillis() + 24*60*60*1000


        //event listener for the qr button
        genQr.setOnClickListener {
            qrResult?.visibility = View.GONE
            firstErrorText?.visibility = View.GONE
            secondErrorText?.visibility = View.GONE
            thirdErrorText?.visibility = View.GONE


            if (firstRadioGroup.checkedRadioButtonId == -1) {
                firstErrorText?.visibility = View.VISIBLE
            }
            if (secondRadioGroup.checkedRadioButtonId == -1) {
                secondErrorText?.visibility = View.VISIBLE
            }
            if (studentId.text.length < 7) {
                thirdErrorText?.visibility = View.VISIBLE
            }

            if (firstRadioGroup.checkedRadioButtonId != -1 && secondRadioGroup.checkedRadioButtonId != -1 && studentId.text.length == 7) {
                //functions to generate QR code depending on the user's entry
                modifyQrCode(
                    firstRadioGroup,
                    metroRadio,
                    lineMetro,
                    metroNumber,
                    metroDate,
                    metroTime,
                    studentId,
                    landRadio,
                    dawsonWing,
                    doorTime,
                    windowRadio,
                    wingRoom,
                    requireLadder,
                    windowDate
                )
                qrResult?.visibility = View.VISIBLE
            }

            scrollDown.post{
                scrollDown.fullScroll(View.FOCUS_DOWN)
            }
        }

        //event listener for the number of metro
        metroNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                metroError?.visibility = View.GONE
                 if (s.isNullOrEmpty() || s.startsWith('0')) {
                    metroError?.visibility = View.VISIBLE
                     genQr.visibility = View.GONE
                }
                else {
                     genQr.visibility = View.VISIBLE
                }
            }
        })

        //event listener for the wing room
        wingRoom.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                roomError?.visibility = View.GONE

                if (s != null && !s.matches("[1-8][A-H][1-9][0-9]?".toRegex())) {
                    roomError?.visibility = View.VISIBLE
                    genQr.visibility = View.GONE
                }
                else if (s.isNullOrEmpty()) {
                    roomError?.visibility = View.VISIBLE
                    genQr.visibility = View.GONE
                }
                else {
                    genQr.visibility = View.VISIBLE
                }

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun modifyQrCode(
        firstRadioGroup: RadioGroup,
        metroRadio: RadioButton,
        lineMetro: Spinner,
        metroNumber: EditText,
        metroDate: DatePicker,
        metroTime: TimePicker,
        studentId: EditText,
        landRadio: RadioButton,
        dawsonWing: Spinner,
        doorTime: TimePicker,
        windowRadio: RadioButton,
        wingRoom: EditText,
        requireLadder: CheckBox,
        windowDate: DatePicker
    ) {
        val radioButtonID = firstRadioGroup.checkedRadioButtonId
        var rb1: RadioButton  = firstRadioGroup.findViewById(radioButtonID);
        var stringInput = ""

        if (metroRadio.isChecked) {
            val dateMetro = "${metroDate.year} - ${metroDate.month + 1} - ${metroDate.dayOfMonth}"
            val timeMetro = "${metroTime.hour}: ${metroTime.minute}"
            val metroData = MetroData(rb1, metroRadio, lineMetro, metroNumber, dateMetro,
                timeMetro, studentId)
            stringInput = metroData.toString()

        } else if (landRadio.isChecked) {
            val timeDoor = "${doorTime.hour}: ${doorTime.minute}"
            val doorData = DoorData(rb1, landRadio, dawsonWing, timeDoor, studentId)
            stringInput =  doorData.toString()
        } else if (windowRadio.isChecked) {
            val dateWindow = "${windowDate.year} - ${windowDate.month + 1} - ${windowDate.dayOfMonth}"
            val windowData = WindowData(rb1, windowRadio, wingRoom, requireLadder, dateWindow, studentId)
            stringInput = windowData.toString()
        }
        val gson = Gson()
        val stringFormat = gson.toJson((stringInput))
        binding.QRResult?.setImageBitmap(encodeStringToBitmap(stringFormat))
    }
    private fun subFormListener(
        genQr: Button,
        metroError: TextView,
        roomError: TextView,
        metroRadio: RadioButton,
        landRadio: RadioButton,
        windowRadio: RadioButton
    ) {
        val metroLayout: LinearLayout = binding.subformMetro
        val landLayout: LinearLayout = binding.subformDoors
        val windowLayout: LinearLayout = binding.subformWindow
        metroRadio.setOnClickListener {
            metroError.visibility = View.GONE
            roomError.visibility = View.GONE
            metroLayout.visibility = View.VISIBLE
            landLayout.visibility = View.GONE
            windowLayout.visibility = View.GONE
            genQr.visibility = View.VISIBLE
        }

        landRadio.setOnClickListener {
            metroError.visibility = View.GONE
            roomError.visibility = View.GONE
            metroLayout.visibility = View.GONE
            landLayout.visibility = View.VISIBLE
            windowLayout.visibility = View.GONE
            genQr.visibility = View.VISIBLE

        }

        windowRadio.setOnClickListener {
            metroError.visibility = View.GONE
            roomError.visibility = View.GONE
            metroLayout.visibility = View.GONE
            landLayout.visibility = View.GONE
            windowLayout.visibility = View.VISIBLE
            genQr.visibility = View.VISIBLE
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
