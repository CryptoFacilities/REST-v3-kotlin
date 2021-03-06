import java.math.BigDecimal

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

const val BASE_URL = "https://www.cryptofacilities.com/derivatives"
const val API_KEY = "..."    // accessible on your Account page under Settings -> API Keys
const val API_SECRET = "..." // accessible on your Account page under Settings -> API Key
const val TIMEOUT = 10.0
const val USE_NONCE = false

const val ETH_SYMBOL = "pi_ethusd"
const val XBT_SYMBOL = "pi_xbtusd"
const val MARGIN_ACCOUNT = "fi_xbtusd"


fun main(args: Array<String>) {
    runExamples()
}

fun runExamples() {
    /** public endpoint examples */

    getInstruments()

    getTickers()

    getOrderBook(ETH_SYMBOL)

    getHistory(ETH_SYMBOL)

    /** private endpoint examples */

    getAccounts()

    //send a limit order with client order id
    sendOrder(
            mapOf("orderType" to "lmt",
                    "symbol" to XBT_SYMBOL,
                    "side" to "buy",
                    "size" to 1,
                    "limitPrice" to 1,
                    "cliOrdId" to "my_client_id_a"))

    //edit order with client order id
    editOrder(
            mapOf("size" to 1,
                  "limitPrice" to 2,
                  "cliOrdId" to "my_client_id_a"))

    //send a stop order
    sendOrder(
            mapOf("orderType" to "stp",
                    "symbol" to XBT_SYMBOL,
                    "side" to "buy",
                    "size" to 1,
                    "limitPrice" to 1.00,
                    "stopPrice" to 2.00,
                    "cliOrdId" to "dd2bd9f9-fb23-46cf-a49b-334a05aa6400"))

    // batchOrder
    batchOrder(listOf(
            mapOf(
                    "order" to "send",
                    "order_tag" to "1",
                    "orderType" to "lmt", //stp, post
                    "symbol" to XBT_SYMBOL,
                    "side" to "buy",
                    "size" to BigDecimal(100),
                    "limitPrice" to BigDecimal(1.0),
                    "cliOrdId" to "my_client_id_b"),
            mapOf(
                    "order" to "send",
                    "order_tag" to "2",
                    "orderType" to "post", //stp, post
                    "symbol" to ETH_SYMBOL,
                    "side" to "buy",
                    "size" to BigDecimal(100),
                    "limitPrice" to BigDecimal(1.0),
                    "cliOrdId" to "my_client_id_c"),
            mapOf(
                    "order" to "cancel",
                    "cliOrdId" to "dd2bd9f9-fb23-46cf-a49b-334a05aa6400"),
            mapOf(
                    "order" to "cancel",
                    "order_id" to "4f399721-5abe-4983-a09c-f9c0b35cd10f")
    ))


    // cancel order using client order id
    cancelOrder(
            mapOf("cliOrdId" to "4f399721-5abe-4983-a09c-f9c0b35cd10f"))

    // cancel all orders on a margin account
    cancelAllOrders(
            mapOf("symbol" to MARGIN_ACCOUNT)
    )

    // enable dead man's switch
    cancelAllOrdersAfter(timeout = 60)

    getOpenOrders()

    // cancel all orders
    cancelAllOrders()

    getOpenPositions()

    getRecentOrders(mapOf("symbol" to XBT_SYMBOL))

    getFills(mapOf("lastFillTime" to "2018-08-15T14:50:47.759Z"))

    getNotifications()

    getTransfers(mapOf("lastTransferTime" to "2018-08-15T14:50:47.759Z"))

    sendWithdrawal(
            mapOf(
                    "targetAddress" to "0x2DgTDWB50Db8c3EcA13Bf8c3Ec9408D6EEc24D81",
                    "currency" to "eth",
                    "amount" to "0.1"))

    transfer(
            mapOf("fromAccount" to "fi_ethusd",
                    "toAccount" to "cash",
                    "unit" to "eth",
                    "amount" to "0.1"))

}





