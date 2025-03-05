package demo.sahha.android.framework.manager

import android.util.Log
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import demo.sahha.android.domain.manager.WearableMessageManager
import javax.inject.Inject

private const val TAG = "WearableMessageManagerImpl"
private const val NO_NODE_ERROR = "Could not find connected device node"

class WearableMessageManagerImpl @Inject constructor(
    private val nodeClient: NodeClient,
    private val messageClient: MessageClient,
) : WearableMessageManager {
    override fun sendData(label: String, data: ByteArray) {
        nodeClient.connectedNodes
            .addOnSuccessListener { nodes ->
                for (node in nodes) {
                    messageClient.sendMessage(
                        node.id, "/$label", data
                    )
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, e.message ?: NO_NODE_ERROR)
            }
    }
}