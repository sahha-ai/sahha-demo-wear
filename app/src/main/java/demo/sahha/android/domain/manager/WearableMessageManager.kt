package demo.sahha.android.domain.manager

interface WearableMessageManager {
    fun sendData(label: String, data: ByteArray)
}