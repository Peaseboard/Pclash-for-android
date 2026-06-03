package com.pclash.common.serialization

import android.os.Parcel
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.SerializersModule

object MergedParcels: SerialFormat {
    fun <T> dump(serializer: SerializationStrategy<T>, obj: T, parcel: Parcel) {
        val data = Parcel.obtain()
        val encoder = ParcelsEncoder(data)

        try {
            serializer.serialize(encoder, obj)
            data.setDataPosition(0)
            parcel.writeStringList(encoder.getStringList())
            parcel.appendFrom(data, 0, data.dataSize())
        } finally {
            data.recycle()
        }
    }

    fun <T> load(deserializer: DeserializationStrategy<T>, parcel: Parcel): T {
        val strings = mutableListOf<String>().apply { parcel.readStringList(this) }
        return deserializer.deserialize(ParcelsDecoder(strings, parcel))
    }

    private class ParcelsEncoder(private val parcel: Parcel) : AbstractEncoder() {
        private val strings = mutableMapOf<String, Int>()
        private var stringIndex = 0

        fun getStringList(): List<String> {
            val result = mutableListOf<String>()
            strings.map { it.value to it.key }.sortedBy { it.first }.forEach { result.add(it.second) }
            return result
        }

        override val serializersModule: SerializersModule get() = SerializersModule {}

        override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder = this
        override fun endStructure(descriptor: SerialDescriptor) {}

        override fun encodeBoolean(value: Boolean) = parcel.writeByte(if (value) 1 else 0)
        override fun encodeByte(value: Byte) = parcel.writeByte(value)
        override fun encodeChar(value: Char) = parcel.writeInt(value.toInt())
        override fun encodeDouble(value: Double) = parcel.writeDouble(value)
        override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = parcel.writeInt(index)
        override fun encodeFloat(value: Float) = parcel.writeFloat(value)
        override fun encodeInt(value: Int) = parcel.writeInt(value)
        override fun encodeLong(value: Long) = parcel.writeLong(value)
        override fun encodeShort(value: Short) = parcel.writeInt(value.toInt())
        override fun encodeString(value: String) {
            val index = strings.computeIfAbsent(value) { stringIndex++ }
            parcel.writeInt(index)
        }
    }

    class ParcelsDecoder(private val strings: List<String>, private val parcel: Parcel) : AbstractDecoder() {
        override val serializersModule: SerializersModule get() = SerializersModule {}

        override fun decodeSequentially() = true
        override fun decodeElementIndex(descriptor: SerialDescriptor) = CompositeDecoder.UNKNOWN_NAME
        override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = this
        override fun endStructure(descriptor: SerialDescriptor) {}

        override fun decodeBoolean() = parcel.readByte() != 0.toByte()
        override fun decodeByte() = parcel.readByte()
        override fun decodeChar() = parcel.readInt().toChar()
        override fun decodeDouble() = parcel.readDouble()
        override fun decodeEnum(enumDescriptor: SerialDescriptor) = parcel.readInt()
        override fun decodeFloat() = parcel.readFloat()
        override fun decodeInt() = parcel.readInt()
        override fun decodeLong() = parcel.readLong()
        override fun decodeShort() = parcel.readInt().toShort()
        override fun decodeString(): String {
            val index = parcel.readInt()
            return strings[index]
        }
    }

    override val serializersModule: SerializersModule = SerializersModule {}
}
