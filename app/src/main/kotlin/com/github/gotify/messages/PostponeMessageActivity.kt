package com.github.gotify.messages

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.gotify.R
import com.github.gotify.Utils
import com.github.gotify.Utils.launchCoroutine
import com.github.gotify.dialog.PostponeMessageDialog
import java.time.OffsetDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Transparent activity which shows the postpone dialog for the "Postpone" notification action. */
internal class PostponeMessageActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_MESSAGE_ID = "id"
    }

    private lateinit var viewModel: MessagesModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, MessagesModelFactory(this))[MessagesModel::class.java]
        showDialog(intent)
    }

    // launchMode is singleTask: a second action is delivered here and not to a new instance.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        showDialog(intent)
    }

    private fun showDialog(intent: Intent) {
        val messageId = intent.getLongExtra(EXTRA_MESSAGE_ID, -1L)
        if (messageId == -1L) {
            finish()
            return
        }

        PostponeMessageDialog(
            this,
            layoutInflater,
            onPositiveButtonClick = { at -> postpone(messageId, at) },
            onNeutralButtonClick = { postpone(messageId, null) },
            onCancel = { finish() }
        ).show()
    }

    private fun postpone(messageId: Long, at: OffsetDateTime?) {
        // finish only after the request is done, finishing cancels the lifecycle scope
        launchCoroutine {
            val success = viewModel.messages.postpone(messageId, at)
            withContext(Dispatchers.Main) {
                if (success) {
                    val manager = getSystemService(NotificationManager::class.java)
                    manager.cancel(Utils.longToInt(messageId))
                } else {
                    Toast.makeText(
                        this@PostponeMessageActivity,
                        R.string.postpone_failed,
                        Toast.LENGTH_LONG
                    ).show()
                }
                finish()
            }
        }
    }
}
