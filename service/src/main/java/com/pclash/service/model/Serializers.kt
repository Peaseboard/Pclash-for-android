package com.pclash.service.model

import android.net.Uri
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

class UriSerializer : KSerializer<Uri> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Uri {
        return Uri.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Uri) {
        encoder.encodeString(value.toString())
    }
}