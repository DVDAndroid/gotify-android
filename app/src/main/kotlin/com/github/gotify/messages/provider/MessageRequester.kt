package com.github.gotify.messages.provider

import com.github.gotify.api.Api
import com.github.gotify.api.ApiException
import com.github.gotify.api.Callback
import com.github.gotify.client.api.MessageApi
import com.github.gotify.client.model.Message
import com.github.gotify.client.model.PagedMessages
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.tinylog.kotlin.Logger

internal class MessageRequester(private val messageApi: MessageApi) {
    fun loadMore(state: MessageState): PagedMessages? {
        return try {
            Logger.info("Loading more messages for ${state.appId}")
            if (MessageState.ALL_MESSAGES == state.appId) {
                Api.execute(messageApi.getMessages(LIMIT, state.nextSince))
            } else {
                Api.execute(messageApi.getAppMessages(state.appId, LIMIT, state.nextSince))
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

    fun postponeMessage(messageId: Long, postponeAt: OffsetDateTime?): Boolean {
        return try {
            if (postponeAt == null) {
                messageApi.unpostponeMessage(messageId).enqueue(Callback.call())
                return true
            }

            val rfc3339String = postponeAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"))
            messageApi.postponeMessage(messageId, rfc3339String).enqueue(Callback.call())
            true
        } catch (e: ApiException) {
            false
        }
    }

    companion object {
        private const val LIMIT = 100
    }
}
