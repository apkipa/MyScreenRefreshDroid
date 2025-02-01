@file:OptIn(ExperimentalMaterial3Api::class)

package com.apkipa.myscreenrefreshdroid

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.apkipa.myscreenrefreshdroid.ui.theme.MyScreenRefreshDroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MyScreenRefreshDroidTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text("MyScreenRefreshDroid")
                            },
                            actions = {
                                AppTopBarDropdownMenuButton()
                            }
                        )
                    },
                ) { innerPadding ->
                    MainScreenContent(
                        modifier = Modifier.padding(innerPadding)
                    )

//                    CheckIsSystemSettingsWritable()
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val refreshEntryList = GetDefaultRefreshEntryList()
            for (entry in refreshEntryList) {
                RefreshListItem(entry)
            }
        }
    }
}

@Composable
fun RefreshListItem(entry: RefreshEntry) {
    val appContext = LocalContext.current

    var isErrorDialogOpen by remember { mutableStateOf(false) }
    var errorDialogMsg by remember { mutableStateOf("") }

    Button(onClick = {
        var toastMsg = ""
        var toastIsLong: Boolean
        try {
            entry.ApplySetting(appContext)
            toastMsg = "${entry.title} 设置成功"
            toastIsLong = false
        } catch (e: Exception) {
            toastMsg = "${entry.title} 设置失败\n${e.message}"
            toastIsLong = true
        }
        if (toastIsLong) {
            // Show as dialog
            errorDialogMsg = toastMsg
            isErrorDialogOpen = true
        } else {
            Toast.makeText(
                appContext,
                toastMsg,
                Toast.LENGTH_SHORT
            ).show()
        }
    }) {
        Column {
            Text(entry.title)
            Text(entry.description)
        }
    }

    if (isErrorDialogOpen) {
        AlertDialog(
            onDismissRequest = { isErrorDialogOpen = false },
            title = { Text("错误") },
            text = {
                Column {
                    Text(errorDialogMsg)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { isErrorDialogOpen = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun AppTopBarDropdownMenuButton() {
    var expanded by remember { mutableStateOf(false) }
    var openAboutDialog by remember { mutableStateOf(false) }
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text("更多选项")
            }
        },
        state = rememberTooltipState(),
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("自定义配置 (未实现)") },
                onClick = {
                    expanded = false
                    /* Do something... */
                }
            )
            DropdownMenuItem(
                text = { Text("关于...") },
                onClick = {
                    expanded = false
                    openAboutDialog = true
                }
            )
        }
    }
    if (openAboutDialog) {
        AlertDialog(
            onDismissRequest = { openAboutDialog = false },
            title = { Text("关于") },
            text = {
                Column {
                    Text("MyScreenRefreshDroid v0.1.0")
                    Text("")
                    Text("作者: apkipa")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openAboutDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

//@Composable
//fun CheckIsSystemSettingsWritable() {
//    val context = LocalContext.current
//    var isDialogOpen by remember { mutableStateOf(false) }
//    var userForceDismissedDialog by remember { mutableStateOf(false) }
//    val lifecycleOwner = LocalLifecycleOwner.current
//    LaunchedEffect(lifecycleOwner.lifecycle) {
//        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//            isDialogOpen = false
//        }
//    }
//
//    isDialogOpen = !Settings.System.canWrite(context)
//    isDialogOpen = isDialogOpen && !userForceDismissedDialog
//
//    if (isDialogOpen) {
//        // Show dialog
//        AlertDialog(
//            onDismissRequest = { isDialogOpen = false },
//            title = { Text("权限不足") },
//            text = {
//                Column {
//                    Text("请授予 MyScreenRefreshDroid 修改系统设置的权限")
//                }
//            },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        context.startActivity(
//                            Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
//                        )
//                    }
//                ) {
//                    Text("前往设置")
//                }
//            },
//            dismissButton = {
//                TextButton(
//                    onClick = { userForceDismissedDialog = true }
//                ) {
//                    Text("取消")
//                }
//            }
//        )
//    }
//}
