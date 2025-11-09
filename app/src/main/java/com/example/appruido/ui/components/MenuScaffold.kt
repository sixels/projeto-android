package com.example.appruido.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.appruido.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScaffold(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    currentRoute: String,
    currentScreenTitle: String,
    onSignOutClicked: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))
                    Text("App Ruído", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()

                    Spacer(Modifier.height(12.dp))

                    NavigationDrawerItem(
                        label = { Text("Home") },
                        selected = currentRoute == Screen.Home.route,
                        icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Histórico") },
                        selected = currentRoute == Screen.Historico.route,
                        icon = { Icon(Icons.Outlined.Analytics, contentDescription = null) },
                        onClick = {
                            navController.navigate(Screen.Historico.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = currentRoute == Screen.Settings.route,
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                        onClick = {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        }
                    )

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))

                    NavigationDrawerItem(
                        label = { Text("Sair") },
                        selected = false,
                        icon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Sair") },
                        onClick = {
                            onSignOutClicked()
                            scope.launch { drawerState.close() }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(currentScreenTitle, fontWeight = FontWeight.Medium) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}
