package com.github.gotify.messages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.gotify.Utils
import com.github.gotify.Utils.launchCoroutine
import com.github.gotify.client.model.Message
import com.github.gotify.dialog.PostponeMessageDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime

class PostponeMessageActivity : AppCompatActivity() {

    private lateinit var viewModel: MessagesModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, MessagesModelFactory(this))[MessagesModel::class.java]
        // get message id from extras
        val messageId = intent.getLongExtra("id", -1)
        if (messageId == -1L) {
            finish()
            return
        }

        PostponeMessageDialog(
            this,
            layoutInflater,
            onPositiveButtonClick = { at ->
                launchCoroutine {
                    viewModel.messages.postpone(messageId, at)
                    finish()
                }
            },
            onNegativeButtonClick = {
                finish()
            },
            onNeutralButtonClick = {
                launchCoroutine {
                    viewModel.messages.postpone(messageId, null)
                    finish()
                }
            },
            null,
        ).show()
    }

}