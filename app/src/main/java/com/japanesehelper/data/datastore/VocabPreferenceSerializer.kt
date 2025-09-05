package com.japanesehelper.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.japanesehelper.android.datastore.VocabPreferences
import java.io.InputStream
import java.io.OutputStream

object VocabPreferenceSerializer : Serializer<VocabPreferences> {
    override val defaultValue: VocabPreferences
        get() = VocabPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): VocabPreferences {
        try {
            return VocabPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: VocabPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}
