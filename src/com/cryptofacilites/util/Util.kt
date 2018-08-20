package util

import java.util.Date

object Util {

    enum class RequestType {
        GET, POST, PUT, DELETE, PATCH
    }

    private var nonce = 0

    fun getNonce(): String {
        return String.format("%s%04d", Date().time, nonce++)
    }
}
