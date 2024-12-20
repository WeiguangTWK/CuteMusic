@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.blacklisted

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAllBlacklistedFolders
import com.sosauce.cutemusic.domain.model.Folder
import com.sosauce.cutemusic.ui.screens.blacklisted.components.AllFoldersBottomSheet
import com.sosauce.cutemusic.ui.shared_components.AppBar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import java.io.File

@Composable
fun BlacklistedScreen(
    navController: NavController,
    folders: List<Folder>,
) {
    var isSheetOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var blacklistedFolders by rememberAllBlacklistedFolders()

    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSheetOpen = false },
            modifier = Modifier.fillMaxHeight()
        ) {
            AllFoldersBottomSheet(
                folders = folders,
                onClick = { path ->
                    if (path in blacklistedFolders) {
                        Toast.makeText(
                            context,
                            context.resources.getText(R.string.alrdy_blacklisted),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        blacklistedFolders = blacklistedFolders.toMutableSet().apply {
                            add(path)
                        }
                        isSheetOpen = false
                        Toast.makeText(
                            context,
                            context.resources.getText(R.string.pls_restart),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    }

    BlacklistedScreenContent(
        onAddFolder = { isSheetOpen = true },
        onPopBackStack = navController::navigateUp
    )
}

@Composable
private fun BlacklistedScreenContent(
    onAddFolder: () -> Unit,
    onPopBackStack: () -> Unit,
) {

    var blacklistedFolders by rememberAllBlacklistedFolders()

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.blacklisted_folders),
                showBackArrow = true,
                onPopBackStack = { onPopBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddFolder() }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { values ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
        ) {
            itemsIndexed(
                items = blacklistedFolders.toList(),
                key = { _, folder -> folder }
            ) { index, folder ->

                val topDp by animateDpAsState(
                    targetValue = if (index == 0) 24.dp else 4.dp,
                    label = "Top Dp",
                    animationSpec = tween(500)
                )
                val bottomDp by animateDpAsState(
                    targetValue = if (index == blacklistedFolders.size - 1) 24.dp else 4.dp,
                    label = "Bottom Dp",
                    animationSpec = tween(500)
                )

                BlackFolderItem(
                    folder = folder,
                    onClick = {
                        blacklistedFolders = blacklistedFolders.toMutableSet().apply {
                            remove(folder)
                        }
                    },
                    topDp = topDp,
                    bottomDp = bottomDp,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}


@Composable
private fun BlackFolderItem(
    modifier: Modifier = Modifier,
    folder: String,
    onClick: () -> Unit,
    topDp: Dp,
    bottomDp: Dp,
) {
    Card(
        modifier = modifier
            .padding(
                start = 13.dp,
                end = 13.dp,
                bottom = 8.dp
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(
            topStart = topDp,
            topEnd = topDp,
            bottomStart = bottomDp,
            bottomEnd = bottomDp
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(33.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                CuteText(
                    text = getFileName(folder),
                    fontSize = 18.sp
                )
                CuteText(
                    text = folder,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.basicMarquee()
                )
            }
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun getFileName(filePath: String): String {
    val file = File(filePath)
    return file.name
}