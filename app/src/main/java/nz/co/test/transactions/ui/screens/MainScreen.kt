package nz.co.test.transactions.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Routes.TransactionList.destination
    ) {
        composable (route = Routes.TransactionList.destination) {
            TransactionListScreen() { transitionId ->
                navController.navigate("${Routes.TransactionDetails.destination}/$transitionId")
            }
        }

        composable (
            route = "${Routes.TransactionDetails.destination}/{${Arguments.TransactionId.argName}}",
            arguments = listOf(
                navArgument(Arguments.TransactionId.argName) {
                    type = NavType.IntType
                }
            )
        ) {
            it.arguments?.getInt(Arguments.TransactionId.argName)?.let { transactionId ->
                TransactionDetailsScreen(
                    transactionId = transactionId
                ) {
                    navController.popBackStack()
                }
            }
        }
    }
}

private enum class Routes(val destination: String) {
    TransactionList(destination = "TransactionList"),
    TransactionDetails(destination = "TransactionDetails"),
}

private enum class Arguments(val argName: String) {
    TransactionId(argName = "transactionId")
}