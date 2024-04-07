package com.github.gotify.dialog

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.github.gotify.R
import com.github.gotify.databinding.PostponeMessageDialogBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

internal class PostponeMessageDialog(
    private val activity: AppCompatActivity,
    private val layoutInflater: LayoutInflater,
    private val onPositiveButtonClick: (OffsetDateTime) -> Unit,
    private val onNegativeButtonClick: (() -> Unit)? = null,
    private val onNeutralButtonClick: () -> Unit,
    private val defaultDateTime: OffsetDateTime?,
) {
    private lateinit var binding: PostponeMessageDialogBinding
    private var dateTime: OffsetDateTime = defaultDateTime ?: OffsetDateTime.now(ZoneOffset.UTC)

    init {
        val utcDateTime = dateTime.withOffsetSameInstant(ZoneOffset.UTC)
        val localDateTime = utcDateTime.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime()

        dateTime = localDateTime.withSecond(0).withNano(0).plusMinutes(15)
    }

    private fun showDatePicker() {
        val currentDateMillis = dateTime.toInstant().toEpochMilli()

        val constraintsBuilder = CalendarConstraints.Builder()
        val currentDate = Calendar.getInstance()
        constraintsBuilder.setStart(currentDate.timeInMillis) // Start from current date
        constraintsBuilder.setValidator(DateValidatorPointForward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(currentDateMillis)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val selectedDateTime = OffsetDateTime.ofInstant(
                Instant.ofEpochMilli(it),
                ZoneId.of("UTC"),
            ) // idk
            dateTime = dateTime.withYear(selectedDateTime.year)
                .withMonth(selectedDateTime.monthValue)
                .withDayOfMonth(selectedDateTime.dayOfMonth)
            formatText()
        }

        datePicker.show(activity.supportFragmentManager, "DATE_PICKER")
    }

    private fun showTimePicker() {
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText("Select Time")
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(dateTime.hour)
            .setMinute(dateTime.minute)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            dateTime = dateTime.withHour(timePicker.hour).withMinute(timePicker.minute)
            formatText()
        }

        timePicker.show(activity.supportFragmentManager, "TIME_PICKER")
    }

    private fun formatText() {
        val formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val formattedTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        binding.dateEditText.setText(formattedDate)
        binding.timeEditText.setText(formattedTime)
    }

    fun show(): PostponeMessageDialog {
        binding = PostponeMessageDialogBinding.inflate(layoutInflater)
        binding.dateEditText.setOnClickListener { showDatePicker() }
        binding.timeEditText.setOnClickListener { showTimePicker() }
        formatText()
        MaterialAlertDialogBuilder(activity)
            .setView(binding.root)
            .setTitle(R.string.postpone_message)
            .setPositiveButton(activity.getString(R.string.done)) { _, _ ->
                onPositiveButtonClick(
                    dateTime.atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime()
                )
            }
            .setNegativeButton(activity.getString(R.string.cancel), onNegativeButtonClick?.let { { _, _ -> it() } })
            .apply {
                if (defaultDateTime != null) {
                    setNeutralButton("Delete postponement") { _, _ -> onNeutralButtonClick() }
                }
            }
            .show()
        return this
    }

}