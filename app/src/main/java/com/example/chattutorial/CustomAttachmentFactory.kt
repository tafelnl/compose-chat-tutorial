package com.example.chattutorial

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.compose.ui.attachments.AttachmentFactory
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.Attachment

enum class CustomAttachmentFactoryType : AttachmentFactory.Type {
    CUSTOM
}

object CustomAttachmentType {
    const val CUSTOM: String = "custom"
}

object CustomAttachmentFactory : AttachmentFactory(
    type = CustomAttachmentFactoryType.CUSTOM,
    canHandle = { attachments ->
        attachments.all { it.type == CustomAttachmentType.CUSTOM }
    },
    previewContent = { modifier, attachments, onAttachmentRemoved ->
        attachments.firstOrNull()?.let {
            CustomAttachmentContent(it)
        }
    },
    content = @Composable { modifier, state ->
        val attachment = state.message.attachments.firstOrNull {
            it.type == CustomAttachmentType.CUSTOM
        }
        attachment?.let {
            Box(modifier = Modifier.padding(8.dp)) {
                CustomAttachmentContent(it)
            }
        }
    },
)

@Composable
fun CustomAttachmentContent(attachment: Attachment) {
    Column {
        val latitude = attachment.extraData["latitude"]
        val longitude = attachment.extraData["longitude"]
        BasicText(
            text = "latitude: $latitude",
            style = ChatTheme.typography.body
        )
        BasicText(
            text = "longitude: $longitude",
            style = ChatTheme.typography.body
        )

        val latitudeDouble = attachment.extraData["latitudeDouble"]
        val longitudeDouble = attachment.extraData["longitudeDouble"]
        BasicText(
            text = "latitudeDouble: $latitudeDouble",
            style = ChatTheme.typography.body
        )
        BasicText(
            text = "longitudeDouble: $longitudeDouble",
            style = ChatTheme.typography.body
        )
    }
}