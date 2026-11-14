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
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Asks the user for a date and time to postpone a message to.
 *
 * Exactly one callback is invoked: [onPositiveButtonClick], [onNeutralButtonClick] (only offered
 * if [currentPostponement] is set) or [onCancel], which is also invoked if the dialog is dismissed
 * in any other way.
 */
internal class PostponeMessageDialog(
    private val activity: AppCompatActivity,
    private val layoutInflater: LayoutInflater,
    private val onPositiveButtonClick: (OffsetDateTime) -> Unit,
    private val onNeutralButtonClick: () -> Unit,
    private val onCancel: (() -> Unit)? = null,
    private val currentPostponement: OffsetDateTime? = null
) {
    private lateinit var binding: PostponeMessageDialogBinding
    private val zone = ZoneId.systemDefault()

    // Wall clock time in the local time zone. Keeping it zone-less avoids wrong results around
    // daylight saving changes when the user picks another date.
    private var dateTime: LocalDateTime = initialDateTime()

    private var handled = false

    private fun initialDateTime(): LocalDateTime {
        val now = OffsetDateTime.now()
        // A postponement in the past is already delivered, offer a new time instead.
        val base = currentPostponement?.takeIf { it.isAfter(now) }
            ?: now.plusMinutes(DEFAULT_DELAY_MINUTES)
        return base.atZoneSameInstant(zone).toLocalDateTime().withSecond(0).withNano(0)
    }

    private fun showDatePicker() {
        // MaterialDatePicker works with the selected day at midnight UTC.
        val selection = dateTime.toLocalDate()
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.postpone_select_date)
            .setSelection(selection)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())
                    .build()
            )
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val day = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
            dateTime = dateTime.with(day)
            formatText()
        }

        datePicker.show(activity.supportFragmentManager, "DATE_PICKER")
    }

    private fun showTimePicker() {
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText(R.string.postpone_select_time)
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
        binding.dateEditText.setText(dateTime.format(DATE_FORMAT))
        binding.timeEditText.setText(dateTime.format(TIME_FORMAT))
    }

    fun show(): PostponeMessageDialog {
        binding = PostponeMessageDialogBinding.inflate(layoutInflater)
        binding.dateEditText.setOnClickListener { showDatePicker() }
        binding.timeEditText.setOnClickListener { showTimePicker() }
        formatText()
        MaterialAlertDialogBuilder(activity)
            .setView(binding.root)
            .setTitle(R.string.postpone_message)
            .setPositiveButton(R.string.done) { _, _ ->
                handled = true
                onPositiveButtonClick(dateTime.atZone(zone).toOffsetDateTime())
            }
            .setNegativeButton(R.string.cancel, null)
            .apply {
                if (currentPostponement != null) {
                    setNeutralButton(R.string.postpone_remove) { _, _ ->
                        handled = true
                        onNeutralButtonClick()
                    }
                }
            }
            .setOnDismissListener {
                if (!handled) {
                    onCancel?.invoke()
                }
            }
            .show()
        return this
    }

    private companion object {
        const val DEFAULT_DELAY_MINUTES = 15L
        val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
