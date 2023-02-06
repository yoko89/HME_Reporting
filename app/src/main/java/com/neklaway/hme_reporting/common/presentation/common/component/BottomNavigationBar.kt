package com.neklaway.hme_reporting.common.presentation.common.component

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.neklaway.hme_reporting.common.presentation.Screen

private const val TAG = "BottomNavigationBar"

@Composable
fun BottomNavigationBar(
    screenList: List<Screen>,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onClick: (Screen) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()

    NavigationBar(
        modifier = modifier,
    ) {
        screenList.forEach { screen ->
            val selected =
                screen.route == backStackEntry.value?.destination?.route?.split("?")?.get(0)

            NavigationBarItem(
                selected = selected,
                onClick = {
                    Log.d(TAG, "BottomNavigationBar: Screen Clicked $screen")
                    if (!selected) onClick(screen)
                },
                icon = {
                    screen.imageVector?.let { image ->
                        Icon(imageVector = image, contentDescription = screen.name)
                    }
                    screen.imageId?.let {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = it),
                            contentDescription = screen.name,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(text = screen.name, textAlign = TextAlign.Center)
                },
                alwaysShowLabel = false
            )
        }
    }
}