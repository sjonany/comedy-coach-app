package com.comedy.suggester.ui

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.comedy.suggester.R
import com.comedy.suggester.ui.appsetting.AppSettingScreen
import com.comedy.suggester.ui.charactereditor.CharacterEditorScreen
import com.comedy.suggester.ui.characterselection.CharacterSelectionScreen
import kotlinx.coroutines.launch

/**
 * Enum values that represent the screens in the app
 */
enum class AppScreen(val title: String, val route: String) {
    AppSetting(title = "App settings", route = "app_settings"),
    CharacterSelection(title = "Character selection", route = "character_selection"),
    CharacterEditor(title = "Character editor", route = "character_edit"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    onItemSelected = { screen ->
                        scope.launch { drawerState.close() }
                        navController.navigate(screen.route)
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Comedy Coach") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun DrawerContent(onItemSelected: (AppScreen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Screen selection", modifier = Modifier.padding(16.dp))
        HorizontalDivider()
        DrawerItem(
            text = AppScreen.AppSetting.title,
            onClick = { onItemSelected(AppScreen.AppSetting) }
        )
        DrawerItem(
            text = AppScreen.CharacterSelection.title,
            onClick = { onItemSelected(AppScreen.CharacterSelection) }
        )
    }
}

@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = text,
            color = colorResource(R.color.on_primary),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = AppScreen.CharacterSelection.route) {
        composable(AppScreen.AppSetting.route) {
            AppSettingScreen(navController, modifier)
        }
        composable(AppScreen.CharacterSelection.route) {
            CharacterSelectionScreen(navController, modifier)
        }
        composable(
            route = "${AppScreen.CharacterEditor.route}/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterName =
                backStackEntry.arguments?.getString("characterId")?.let { Uri.decode(it) }
            CharacterEditorScreen(modifier, navController, characterName!!)
        }
    }
}

// Navigation functions

// characterId = [CharacterProfile.id]
fun navigateToCharacterEditor(navController: NavHostController, characterId: String) {
    val encodedCharacterName = Uri.encode(characterId)
    navController.navigate("${AppScreen.CharacterEditor.route}/$encodedCharacterName")
}