/**
 *
 * Crypto Facilities Ltd REST API v3
 *
 * Copyright (c) 2018 Crypto Facilities
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import khttp.get
import khttp.post
import khttp.responses.Response
import org.json.JSONObject
import util.Util
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import util.Util.RequestType.GET
import util.Util.RequestType.POST

/* Public endpoints */
fun getInstruments() {
    val endpoint = "/api/v3/instruments"
    val response = get(BASE_URL + endpoint, timeout = TIMEOUT)
    println("getInstruments():\n\t" + response.text)
}

fun getTickers() {
    val endpoint = "/api/v3/tickers"
    val response = get(BASE_URL + endpoint, timeout = TIMEOUT)
    println("getTickers():\n\t" + response.text)
}

fun getOrderBook(productId: String) {
    val endpoint = "/api/v3/orderbook"
    val response = get(BASE_URL + endpoint,
            params = mapOf("symbol" to productId),
            timeout = TIMEOUT)
    println("getOrderbook():\n\t" + response.text)
}

fun getHistory(productId: String) {
    val endpoint = "/api/v3/history"
    val response = get(BASE_URL + endpoint,
            params = mapOf("symbol" to productId),
            timeout = TIMEOUT)
    println("getHistory():\n\t" + response.text)
}

/* Private endpoints */

fun getAccounts() {
    val endpoint = "/api/v3/accounts"
    val response = authRequest(GET, endpoint)
    println("getAccounts():\n\t" + response.text)
}

fun sendOrder(params: Map<String, Any>) {
    val endpoint = "/api/v3/sendorder"
    val response = authRequest(POST, endpoint, params = params)
    println("sendOrder():\n\t" + response.text)

}

fun editOrder(params: Map<String, Any>) {
    val endpoint = "/api/v3/editorder"
    val response = authRequest(POST, endpoint, params = params)
    println("editorder():\n\t" + response.text)

}

fun batchOrder(orders: List<Map<String,Any>>) {
    val endpoint = "/api/v3/batchorder"
    val batchOrderJson = JSONObject(mapOf("batchOrder" to orders))
    val response = authRequest(POST, endpoint, params = mapOf("json" to batchOrderJson.toString()))
    println("batchOrder():\n\t" + response.text)

}

fun cancelOrder(params: Map<String, String>) {
    val endpoint = "/api/v3/cancelorder"
    val response = authRequest(POST, endpoint, params = params)
    println("cancelOrder():\n\t" + response.text)
}

fun cancelAllOrders(params: Map<String, String> = mapOf()) {
    val endpoint = "/api/v3/cancelallorders"
    val response = authRequest(POST, endpoint, params = params)
    println("cancelAllOrders():\n\t" + response.text)
}

fun cancelAllOrdersAfter(timeout: Int) {
    val endpoint = "/api/v3/cancelallordersafter"
    val response = authRequest(POST, endpoint, params = mapOf("timeout" to timeout))
    println("cancelAllOrdersAfter():\n\t" + response.text)
}

fun getOpenOrders() {
    val endpoint = "/api/v3/openorders"
    val response = authRequest(GET, endpoint)
    println("getOpenOrders():\n\t" + response.text)
}

fun getOpenPositions() {
    val endpoint = "/api/v3/openpositions"
    val response = authRequest(GET, endpoint)
    println("getOpenPositions():\n\t" + response.text)
}

fun getRecentOrders(params: Map<String, String> = mapOf()) {
    val endpoint = "/api/v3/recentorders"
    val response = authRequest(GET, endpoint, params = params)
    println("getRecentOrders():\n\t" + response.text)
}

fun getFills(params: Map<String, String> = mapOf()) {
    val endpoint = "/api/v3/fills"
    val response = authRequest(GET, endpoint, params)
    println("getFills():\n\t" + response.text)
}

fun getNotifications() {
    val endpoint = "/api/v3/notifications"
    val response = authRequest(GET, endpoint)
    println("getNotifications():\n\t" + response.text)
}

fun getTransfers(params: Map<String, String> = mapOf()) {
    val endpoint = "/api/v3/transfers"
    val response = authRequest(GET, endpoint, params)
    println("getTransfers():\n\t" + response.text)
}

fun sendWithdrawal(params: Map<String, String>) {
    val endpoint = "/api/v3/withdrawal"
    val response = authRequest(POST, endpoint, params = params)
    println("sendWithdrawal():\n\t" + response.text)
}

fun transfer(params: Map<String, String>) {
    val endpoint = "/api/v3/transfer"
    val response = authRequest(POST, endpoint, params = params)
    println("transfer():\n\t" + response.text)
}

private fun authRequest(type: Util.RequestType, endpoint: String, params: Map<String, Any> = mapOf()): Response {
    val parameters = params.entries.joinToString(separator = "&")
    val authent : String
    val headers : Map<String,String>
    if (USE_NONCE) {
        val nonce = Util.getNonce()
        authent = signRequest(endpoint, parameters, nonce=nonce)
        headers = mapOf(
                "APIKey" to API_KEY,
                "nonce" to nonce,
                "authent" to authent)
    } else {
        authent = signRequest(endpoint, parameters)
        headers = mapOf(
                "APIKey" to API_KEY,
                "authent" to authent)
    }

    return when (type) {
        GET -> get(BASE_URL + endpoint, params = params.mapValues { it.value.toString() }, headers = headers, timeout = TIMEOUT)
        POST -> post(BASE_URL + endpoint, params = params.mapValues { it.value.toString() }, headers = headers, timeout = TIMEOUT)
        else -> throw UnsupportedOperationException()
    }
}

fun signRequest(endpoint: String, postData: String, nonce: String? = null): String {
    // Step 1: concatenate postData, nonce + endpoint
    val message = if (USE_NONCE) postData + nonce + endpoint else postData + endpoint

    // Step 2: hash the result of step 1 with SHA256
    val hash = MessageDigest.getInstance("SHA-256").digest(message.toByteArray(StandardCharsets.UTF_8))

    // step 3: base64 decode api secret
    val secretDecoded = Base64.getDecoder().decode(API_SECRET)

    // step 4: use result of step 3 to hash the result of step 2 with
    // HMAC-SHA512
    val hmacsha512 = Mac.getInstance("HmacSHA512")
    hmacsha512.init(SecretKeySpec(secretDecoded, "HmacSHA512"))
    val hash2 = hmacsha512.doFinal(hash)

    // step 5: base64 encode the result of step 4 and return
    return Base64.getEncoder().encodeToString(hash2)
}
