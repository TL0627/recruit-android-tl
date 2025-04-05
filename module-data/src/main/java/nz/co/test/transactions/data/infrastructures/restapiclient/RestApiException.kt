package nz.co.test.transactions.data.infrastructures.restapiclient

sealed class RestApiException(message: String) : Exception(message) {
    object  TransactionResourceNotFoundException : RestApiException("Transaction resource not found") {
        private fun readResolve(): Any = TransactionResourceNotFoundException
    }

    object RetrieveTransactionFailedException : RestApiException("Failed to retrieve transaction") {
        private fun readResolve(): Any = RetrieveTransactionFailedException
    }

    class UnknownException(message: String) : RestApiException(message)
}

