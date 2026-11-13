package com.github.gotify.messages.provider

import com.github.gotify.api.Api
import com.github.gotify.api.ApiException
import com.github.gotify.api.Callback
import com.github.gotify.client.api.MessageApi
import com.github.gotify.client.model.Message
import com.github.gotify.client.model.PagedMessages
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import org.tinylog.kotlin.Logger

internal class MessageRequester(private val messageApi: MessageApi) {
    fun loadMore(state: MessageState): PagedMessages? {
        return try {
            Logger.info("Loading more messages for ${state.appId}")
            if (MessageState.ALL_MESSAGES == state.appId) {
                Api.execute(messageApi.getMessages(LIMIT, state.nextSince, null))
            } else {
                Api.execute(messageApi.getAppMessages(state.appId, LIMIT, state.nextSince, null))
            }
        } catch (apiException: ApiException) {
            Logger.error(apiException, "failed requesting messages")
            null
        }
    }

    fun asyncRemoveMessage(message: Message) {
        Logger.info("Removing message with id ${message.id}")
        messageApi.deleteMessage(message.id).enqueue(Callback.call())
    }

    fun deleteAll(appId: Long): Boolean {
        return try {
            Logger.info("Deleting all messages for $appId")
            if (MessageState.ALL_MESSAGES == appId) {
                Api.execute(messageApi.deleteMessages())
            } else {
                Api.execute(messageApi.deleteAppMessages(appId))
            }
            true
        } catch (e: ApiException) {
            Logger.error(e, "Could not delete messages")
            false
        }
    }

    /** Postpones the message to [postponeAt] or, if it is null, removes the postponement. */
    fun postponeMessage(messageId: Long, postponeAt: OffsetDateTime?): Boolean {
        return try {
            if (postponeAt == null) {
                Logger.info("Removing postponement of message with id $messageId")
                Api.execute(messageApi.deleteMessagePostponement(messageId))
            } else {
                Logger.info("Postponing message with id $messageId to $postponeAt")
                // The server parses RFC3339 and requires the seconds, OffsetDateTime.toString()
                // omits them when they are zero.
                Api.execute(messageApi.postponeMessage(messageId, postponeAt.format(RFC3339)))
            }
            true
        } catch (e: ApiException) {
            Logger.error(e, "Could not postpone message")
            false
        }
    }

    companion object {
        private const val LIMIT = 100
        private val RFC3339 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
    }
}
