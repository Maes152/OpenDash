package info.nightscout.androidaps.plugins.general.automation.elements

import android.app.TimePickerDialog
import android.graphics.Typeface
import android.text.format.DateFormat
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import info.nightscout.androidaps.automation.R
import info.nightscout.androidaps.interfaces.Profile
import info.nightscout.androidaps.utils.DateUtil
import info.nightscout.androidaps.utils.MidnightTime
import info.nightscout.androidaps.utils.resources.ResourceHelper
import java.util.*

class InputTime(private val resourceHelper: ResourceHelper, private val dateUtil: DateUtil) : Element() {

    var value: Int = getMinSinceMidnight(dateUtil.now())

    override fun addToLayout(root: LinearLayout) {
        val label = TextView(root.context)
        val startButton = TextView(root.context)
        startButton.text = dateUtil.timeString(toMills(value))

        val startTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            value = 60 * hour + minute
            startButton.text = dateUtil.timeString(toMills(value))
        }

        startButton.setOnClickListener {
            root.context?.let {
                val cal = Calendar.getInstance()
                cal.timeInMillis = toMills(value)
                TimePickerDialog(it, startTimeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(it)
                ).show()
            }
        }

        val px = resourceHelper.dpToPx(10)
        label.text = resourceHelper.gs(R.string.atspecifiedtime, "")
        label.setTypeface(label.typeface, Typeface.BOLD)
        startButton.setPadding(px, px, px, px)
        val l = LinearLayout(root.context)
        l.orientation = LinearLayout.HORIZONTAL
        l.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        l.addView(label)
        l.addView(startButton)
        root.addView(l)
    }

    private fun toMills(minutesSinceMidnight: Int): Long = MidnightTime.calcPlusMinutes(minutesSinceMidnight)

    private fun getMinSinceMidnight(time: Long): Int = Profile.secondsFromMidnight(time) / 60
}