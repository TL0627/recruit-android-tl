package nz.co.test.transactions.data.infrastructures.restapiclient

import nz.co.test.transactions.data.BuildConfig

object HttpConfig {
    const val REST_ENDPOINT_BASE_URL = BuildConfig.REST_ENDPOINT_BASE_URL
    const val CONNECTION_TIME_OUT_IN_SECOND: Long = 10L
    const val READ_WRITE_TIME_OUT_IN_SECOND: Long = 30L
}