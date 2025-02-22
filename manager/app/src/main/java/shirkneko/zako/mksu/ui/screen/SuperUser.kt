package shirkneko.zako.mksu.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AppProfileScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import shirkneko.zako.mksu.Natives
import shirkneko.zako.mksu.R
import shirkneko.zako.mksu.ui.component.SearchAppBar
import shirkneko.zako.mksu.ui.util.ModuleModify
import shirkneko.zako.mksu.ui.viewmodel.SuperUserViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SuperUserScreen(navigator: DestinationsNavigator) {
    val viewModel = viewModel<SuperUserViewModel>()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    var showBatchActions by remember { mutableStateOf(false) }

    // 添加备份和还原启动器
    val backupLauncher = ModuleModify.rememberAllowlistBackupLauncher(context, snackBarHostState)
    val restoreLauncher = ModuleModify.rememberAllowlistRestoreLauncher(context, snackBarHostState)

    LaunchedEffect(key1 = navigator) {
        viewModel.search = ""
        if (viewModel.appList.isEmpty()) {
            viewModel.fetchAppList()
        }
    }

    LaunchedEffect(viewModel.search) {
        if (viewModel.search.isEmpty()) {
            listState.scrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            SearchAppBar(
                title = { Text(stringResource(R.string.superuser)) },
                searchText = viewModel.search,
                onSearchTextChange = { viewModel.search = it },
                onClearClick = { viewModel.search = "" },
                dropdownContent = {
                    var showDropdown by remember { mutableStateOf(false) }

                    IconButton(
                        onClick = { showDropdown = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(id = R.string.settings)
                        )

                        DropdownMenu(expanded = showDropdown, onDismissRequest = {
                            showDropdown = false
                        }) {
                            DropdownMenuItem(text = {
                                Text(stringResource(R.string.refresh))
                            }, onClick = {
                                scope.launch {
                                    viewModel.fetchAppList()
                                }
                                showDropdown = false
                            })
                            DropdownMenuItem(text = {
                                Text(
                                    if (viewModel.showSystemApps) {
                                        stringResource(R.string.hide_system_apps)
                                    } else {
                                        stringResource(R.string.show_system_apps)
                                    }
                                )
                            }, onClick = {
                                viewModel.showSystemApps = !viewModel.showSystemApps
                                showDropdown = false
                            })
                            DropdownMenuItem(text = {
                                Text(if (showBatchActions) "退出批量操作" else "批量操作")
                            }, onClick = {
                                showBatchActions = !showBatchActions
                                if (!showBatchActions) {
                                    viewModel.clearSelection()
                                }
                                showDropdown = false
                            })
                            if (showBatchActions && viewModel.selectedApps.isNotEmpty()) {
                                DropdownMenuItem(text = {
                                    Text("批量授权")
                                }, onClick = {
                                    scope.launch {
                                        viewModel.updateBatchPermissions(true)
                                    }
                                    showDropdown = false
                                })
                                DropdownMenuItem(text = {
                                    Text("批量取消授权")
                                }, onClick = {
                                    scope.launch {
                                        viewModel.updateBatchPermissions(false)
                                    }
                                    showDropdown = false
                                })
                            }
                            DropdownMenuItem(text = {
                                Text(stringResource(R.string.backup_allowlist))
                            }, onClick = {
                                backupLauncher.launch(ModuleModify.createAllowlistBackupIntent())
                                showDropdown = false
                            })
                            DropdownMenuItem(text = {
                                Text(stringResource(R.string.restore_allowlist))
                            }, onClick = {
                                restoreLauncher.launch(ModuleModify.createAllowlistRestoreIntent())
                                showDropdown = false
                            })
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier.padding(innerPadding),
            onRefresh = {
                scope.launch { viewModel.fetchAppList() }
            },
            isRefreshing = viewModel.isRefreshing
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                items(viewModel.appList, key = { it.packageName + it.uid }) { app ->
                    AppItem(
                        app = app,
                        showBatchActions = showBatchActions,
                        isSelected = viewModel.selectedApps.contains(app.packageName),
                        onToggleSelection = { viewModel.toggleAppSelection(app.packageName) },
                        onSwitchChange = { allowSu ->
                            scope.launch {
                                val profile = Natives.getAppProfile(app.packageName, app.uid)
                                val updatedProfile = profile.copy(allowSu = allowSu)
                                if (Natives.setAppProfile(updatedProfile)) {
                                    viewModel.fetchAppList()
                                }
                            }
                        },
                        onClick = {
                            if (showBatchActions) {
                                viewModel.toggleAppSelection(app.packageName)
                            } else {
                                navigator.navigate(AppProfileScreenDestination(app))
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AppItem(
    app: SuperUserViewModel.AppInfo,
    showBatchActions: Boolean,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onSwitchChange: (Boolean) -> Unit,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(app.label) },
        supportingContent = {
            Column {
                Text(app.packageName)
                FlowRow {
                    if (app.allowSu) {
                        LabelText(label = "ROOT")
                    } else {
                        if (Natives.uidShouldUmount(app.uid)) {
                            LabelText(label = "UMOUNT")
                        }
                    }
                    if (app.hasCustomProfile) {
                        LabelText(label = "CUSTOM")
                    }
                }
            }
        },
        leadingContent = {
            Row {
                if (showBatchActions) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleSelection() },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(app.packageInfo)
                        .crossfade(true)
                        .build(),
                    contentDescription = app.label,
                    modifier = Modifier
                        .padding(4.dp)
                        .width(48.dp)
                        .height(48.dp)
                )
            }
        },
        trailingContent = {
            if (!showBatchActions) {
                Switch(
                    checked = app.allowSu,
                    onCheckedChange = onSwitchChange
                )
            }
        }
    )
}

@Composable
fun LabelText(label: String) {
    Box(
        modifier = Modifier
            .padding(top = 4.dp, end = 4.dp)
            .background(
                Color.Black,
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 5.dp),
            style = TextStyle(
                fontSize = 8.sp,
                color = Color.White,
            )
        )
    }
}