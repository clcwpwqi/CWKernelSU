package shirkneko.zako.mksu.ui.screen

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Wysiwyg
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ExecuteModuleActionScreenDestination
import com.ramcosta.composedestinations.generated.destinations.FlashScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import shirkneko.zako.mksu.Natives
import shirkneko.zako.mksu.R
import shirkneko.zako.mksu.ui.component.ConfirmResult
import shirkneko.zako.mksu.ui.component.SearchAppBar
import shirkneko.zako.mksu.ui.component.rememberConfirmDialog
import shirkneko.zako.mksu.ui.component.rememberLoadingDialog
import shirkneko.zako.mksu.ui.util.DownloadListener
import shirkneko.zako.mksu.ui.util.LocalSnackbarHost
import shirkneko.zako.mksu.ui.util.download
import shirkneko.zako.mksu.ui.util.hasMagisk
import shirkneko.zako.mksu.ui.util.reboot
import shirkneko.zako.mksu.ui.util.restoreModule
import shirkneko.zako.mksu.ui.util.toggleModule
import shirkneko.zako.mksu.ui.util.uninstallModule
import shirkneko.zako.mksu.ui.viewmodel.ModuleViewModel
import shirkneko.zako.mksu.ui.webui.WebUIActivity
import okhttp3.OkHttpClient
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.zip.ZipInputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CompletableDeferred
import java.io.IOException

fun getModuleNameFromUri(context: Context, uri: Uri): String {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri) ?: return "Unknown Module"

    val zipInputStream = ZipInputStream(inputStream)
    var entry = zipInputStream.nextEntry
    while (entry != null) {
        if (entry.name == "module.prop") { // 假设模块名称存储在 module.prop 文件中
            val reader = BufferedReader(InputStreamReader(zipInputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line?.startsWith("name=") == true) {
                    return line?.substringAfter("=") ?: "Unknown Module"
                }
            }
        }
        entry = zipInputStream.nextEntry
    }
    return "Unknown Module"
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun ModuleScreen(navigator: DestinationsNavigator) {
    val viewModel = viewModel<ModuleViewModel>()
    val context = LocalContext.current
    val snackBarHost = LocalSnackbarHost.current
    val scope = rememberCoroutineScope()
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    // 添加文件选择器
    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                scope.launch {
                    backupModules(context, snackBarHost, uri)
                }
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                scope.launch {
                    restoreModules(context, snackBarHost, uri)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (viewModel.moduleList.isEmpty() || viewModel.isNeedRefresh) {
            viewModel.sortEnabledFirst = prefs.getBoolean("module_sort_enabled_first", false)
            viewModel.sortActionFirst = prefs.getBoolean("module_sort_action_first", false)
            viewModel.fetchModuleList()
        }
    }

    val isSafeMode = Natives.isSafeMode
    val hasMagisk = hasMagisk()

    val hideInstallButton = isSafeMode || hasMagisk

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val webUILauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { viewModel.fetchModuleList() }

    Scaffold(
        topBar = {
            SearchAppBar(
                title = { Text(stringResource(R.string.module)) },
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
                                Text(stringResource(R.string.module_sort_action_first))
                            }, trailingIcon = {
                                Checkbox(viewModel.sortActionFirst, null)
                            }, onClick = {
                                viewModel.sortActionFirst = !viewModel.sortActionFirst
                                prefs.edit()
                                    .putBoolean("module_sort_action_first", viewModel.sortActionFirst)
                                    .apply()
                                scope.launch {
                                    viewModel.fetchModuleList()
                                }
                            })
                            DropdownMenuItem(text = {
                                Text(stringResource(R.string.module_sort_enabled_first))
                            }, trailingIcon = {
                                Checkbox(viewModel.sortEnabledFirst, null)
                            }, onClick = {
                                viewModel.sortEnabledFirst = !viewModel.sortEnabledFirst
                                prefs.edit()
                                    .putBoolean("module_sort_enabled_first", viewModel.sortEnabledFirst)
                                    .apply()
                                scope.launch {
                                    viewModel.fetchModuleList()
                                }
                            })

                            HorizontalDivider()

                            // 修改备份选项
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.backup_modules)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Download,
                                        contentDescription = "备份"
                                    )
                                },
                                onClick = {
                                    showDropdown = false
                                    // 打开目录选择器
                                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                        addCategory(Intent.CATEGORY_OPENABLE)
                                        type = "application/zip"
                                        // 设置默认文件名
                                        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                        putExtra(Intent.EXTRA_TITLE, "modules_backup_$timestamp.zip")
                                    }
                                    backupLauncher.launch(intent)
                                }
                            )

                            // 修改还原选项
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.restore_modules)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Refresh,
                                        contentDescription = "还原"
                                    )
                                },
                                onClick = {
                                    showDropdown = false
                                    // 打开文件选择器
                                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                        addCategory(Intent.CATEGORY_OPENABLE)
                                        type = "application/zip"
                                    }
                                    restoreLauncher.launch(intent)
                                }
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (!hideInstallButton) {
                val moduleInstall = stringResource(id = R.string.module_install)
                val selectZipLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == RESULT_OK) {
                        val uri = result.data?.data ?: return@rememberLauncherForActivityResult
                        val moduleName = getModuleNameFromUri(context, uri) // 获取模块名称
                        // 弹出确认对话框
                        scope.launch {
                            val userConfirmed = showInstallConfirmation(context, moduleName)
                            if (userConfirmed) {
                                navigator.navigate(FlashScreenDestination(FlashIt.FlashModule(uri)))
                            }
                        }
                    }
                }

                ExtendedFloatingActionButton(
                    onClick = {
                        // 打开文件选择器
                        selectZipLauncher.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "application/zip"
                        })
                    },
                    icon = { Icon(Icons.Filled.Add, moduleInstall) },
                    text = { Text(text = moduleInstall) },
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        snackbarHost = { SnackbarHost(hostState = snackBarHost) }
    ) { innerPadding ->
        when {
            hasMagisk -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.module_magisk_conflict),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            else -> {
                ModuleList(
                    navigator,
                    viewModel = viewModel,
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    boxModifier = Modifier.padding(innerPadding),
                    onInstallModule = {
                        navigator.navigate(FlashScreenDestination(FlashIt.FlashModule(it)))
                    },
                    onClickModule = { id, name, hasWebUi ->
                        if (hasWebUi) {
                            webUILauncher.launch(
                                Intent(context, WebUIActivity::class.java)
                                    .setData(Uri.parse("kernelsu://webui/$id"))
                                    .putExtra("id", id)
                                    .putExtra("name", name)
                            )
                        }
                    },
                    context = context,
                    snackBarHost = snackBarHost
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModuleList(
    navigator: DestinationsNavigator,
    viewModel: ModuleViewModel,
    modifier: Modifier = Modifier,
    boxModifier: Modifier = Modifier,
    onInstallModule: (Uri) -> Unit,
    onClickModule: (id: String, name: String, hasWebUi: Boolean) -> Unit,
    context: Context,
    snackBarHost: SnackbarHostState
) {
    val failedEnable = stringResource(R.string.module_failed_to_enable)
    val failedDisable = stringResource(R.string.module_failed_to_disable)
    val failedUninstall = stringResource(R.string.module_uninstall_failed)
    val successUninstall = stringResource(R.string.module_uninstall_success)
    val reboot = stringResource(R.string.reboot)
    val rebootToApply = stringResource(R.string.reboot_to_apply)
    val moduleStr = stringResource(R.string.module)
    val uninstall = stringResource(R.string.uninstall)
    val cancel = stringResource(android.R.string.cancel)
    val moduleUninstallConfirm = stringResource(R.string.module_uninstall_confirm)
    val updateText = stringResource(R.string.module_update)
    val changelogText = stringResource(R.string.module_changelog)
    val downloadingText = stringResource(R.string.module_downloading)
    val startDownloadingText = stringResource(R.string.module_start_downloading)
    val fetchChangeLogFailed = stringResource(R.string.module_changelog_failed)

    val loadingDialog = rememberLoadingDialog()
    val confirmDialog = rememberConfirmDialog()

    suspend fun onModuleUpdate(
        module: ModuleViewModel.ModuleInfo,
        changelogUrl: String,
        downloadUrl: String,
        fileName: String
    ) {
        val changelogResult = loadingDialog.withLoading {
            withContext(Dispatchers.IO) {
                runCatching {
                    OkHttpClient().newCall(
                        okhttp3.Request.Builder().url(changelogUrl).build()
                    ).execute().body!!.string()
                }
            }
        }

        val showToast: suspend (String) -> Unit = { msg ->
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    msg,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val changelog = changelogResult.getOrElse {
            showToast(fetchChangeLogFailed.format(it.message))
            return
        }.ifBlank {
            showToast(fetchChangeLogFailed.format(module.name))
            return
        }

        // changelog is not empty, show it and wait for confirm
        val confirmResult = confirmDialog.awaitConfirm(
            changelogText,
            content = changelog,
            markdown = true,
            confirm = updateText,
        )

        if (confirmResult != ConfirmResult.Confirmed) {
            return
        }

        showToast(startDownloadingText.format(module.name))

        val downloading = downloadingText.format(module.name)
        withContext(Dispatchers.IO) {
            download(
                context,
                downloadUrl,
                fileName,
                downloading,
                onDownloaded = onInstallModule,
                onDownloading = {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, downloading, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    suspend fun onModuleUninstallClicked(module: ModuleViewModel.ModuleInfo) {
        val isUninstall = !module.remove
        if (isUninstall) {
            val confirmResult = confirmDialog.awaitConfirm(
                moduleStr,
                content = moduleUninstallConfirm.format(module.name),
                confirm = uninstall,
                dismiss = cancel
            )
            if (confirmResult != ConfirmResult.Confirmed) {
                return
            }
        }

        val success = loadingDialog.withLoading {
            withContext(Dispatchers.IO) {
                if (isUninstall) {
                    uninstallModule(module.dirId)
                } else {
                    restoreModule(module.dirId)
                }
            }
        }

        if (success) {
            viewModel.fetchModuleList()
        }
        if (!isUninstall) return
        val message = if (success) {
            successUninstall.format(module.name)
        } else {
            failedUninstall.format(module.name)
        }
        val actionLabel = if (success) {
            reboot
        } else {
            null
        }
        val result = snackBarHost.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Long
        )
        if (result == SnackbarResult.ActionPerformed) {
            reboot()
        }
    }
    PullToRefreshBox(
        modifier = boxModifier,
        onRefresh = {
            viewModel.fetchModuleList()
        },
        isRefreshing = viewModel.isRefreshing
    ) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = remember {
                PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp + 56.dp + 16.dp + 48.dp + 6.dp /* Scaffold Fab Spacing + Fab container height + SnackBar height */
                )
            },
        ) {
            when {
                viewModel.moduleList.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(R.string.module_empty),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    items(viewModel.moduleList) { module ->
                        val scope = rememberCoroutineScope()
                        val updatedModule by produceState(initialValue = Triple("", "", "")) {
                            scope.launch(Dispatchers.IO) {
                                value = viewModel.checkUpdate(module)
                            }
                        }

                        ModuleItem(
                            navigator = navigator,
                            module = module,
                            updateUrl = updatedModule.first,
                            onUninstallClicked = {
                                scope.launch { onModuleUninstallClicked(module) }
                            },
                            onCheckChanged = {
                                scope.launch {
                                    val success = loadingDialog.withLoading {
                                        withContext(Dispatchers.IO) {
                                            toggleModule(module.dirId, !module.enabled)
                                        }
                                    }
                                    if (success) {
                                        viewModel.fetchModuleList()

                                        val result = snackBarHost.showSnackbar(
                                            message = rebootToApply,
                                            actionLabel = reboot,
                                            duration = SnackbarDuration.Long
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            reboot()
                                        }
                                    } else {
                                        val message = if (module.enabled) failedDisable else failedEnable
                                        snackBarHost.showSnackbar(message.format(module.name))
                                    }
                                }
                            },
                            onUpdate = {
                                scope.launch {
                                    onModuleUpdate(
                                        module,
                                        updatedModule.third,
                                        updatedModule.first,
                                        "${module.name}-${updatedModule.second}.zip"
                                    )
                                }
                            },
                            onClick = {
                                onClickModule(it.dirId, it.name, it.hasWebUi)
                            }
                        )

                        // fix last item shadow incomplete in LazyColumn
                        Spacer(Modifier.height(1.dp))
                    }
                }
            }
        }

        DownloadListener(context, onInstallModule)

    }
}

@Composable
fun ModuleItem(
    navigator: DestinationsNavigator,
    module: ModuleViewModel.ModuleInfo,
    updateUrl: String,
    onUninstallClicked: (ModuleViewModel.ModuleInfo) -> Unit,
    onCheckChanged: (Boolean) -> Unit,
    onUpdate: (ModuleViewModel.ModuleInfo) -> Unit,
    onClick: (ModuleViewModel.ModuleInfo) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        val textDecoration = if (!module.remove) null else TextDecoration.LineThrough
        val interactionSource = remember { MutableInteractionSource() }
        val indication = LocalIndication.current
        val viewModel = viewModel<ModuleViewModel>()

        Column(
            modifier = Modifier
                .run {
                    if (module.hasWebUi) {
                        toggleable(
                            value = module.enabled,
                            enabled = !module.remove && module.enabled,
                            interactionSource = interactionSource,
                            role = Role.Button,
                            indication = indication,
                            onValueChange = { onClick(module) }
                        )
                    } else {
                        this
                    }
                }
                .padding(22.dp, 18.dp, 22.dp, 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val moduleVersion = stringResource(id = R.string.module_version)
                val moduleAuthor = stringResource(id = R.string.module_author)

                Column(
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text(
                        text = module.name,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                        fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
                        textDecoration = textDecoration,
                    )

                    Text(
                        text = "$moduleVersion: ${module.version}",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                        fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                        textDecoration = textDecoration
                    )

                    Text(
                        text = "$moduleAuthor: ${module.author}",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                        fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                        textDecoration = textDecoration
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Switch(
                        enabled = !module.update,
                        checked = module.enabled,
                        onCheckedChange = onCheckChanged,
                        interactionSource = if (!module.hasWebUi) interactionSource else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = module.description,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4,
                textDecoration = textDecoration
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = Dp.Hairline)

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (module.hasActionScript) {
                    FilledTonalButton(
                        modifier = Modifier.defaultMinSize(52.dp, 32.dp),
                        enabled = !module.remove && module.enabled,
                        onClick = {
                            navigator.navigate(ExecuteModuleActionScreenDestination(module.dirId))
                            viewModel.markNeedRefresh()
                        },
                        contentPadding = ButtonDefaults.TextButtonContentPadding
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Outlined.PlayArrow,
                            contentDescription = null
                        )
                        if (!module.hasWebUi && updateUrl.isEmpty()) {
                            Text(
                                modifier = Modifier.padding(start = 7.dp),
                                text = stringResource(R.string.action),
                                fontFamily = MaterialTheme.typography.labelMedium.fontFamily,
                                fontSize = MaterialTheme.typography.labelMedium.fontSize
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.1f, true))
                }

                if (module.hasWebUi) {
                    FilledTonalButton(
                        modifier = Modifier.defaultMinSize(52.dp, 32.dp),
                        enabled = !module.remove && module.enabled,
                        onClick = { onClick(module) },
                        interactionSource = interactionSource,
                        contentPadding = ButtonDefaults.TextButtonContentPadding
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.AutoMirrored.Outlined.Wysiwyg,
                            contentDescription = null
                        )
                        if (!module.hasActionScript && updateUrl.isEmpty()) {
                            Text(
                                modifier = Modifier.padding(start = 7.dp),
                                fontFamily = MaterialTheme.typography.labelMedium.fontFamily,
                                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                text = stringResource(R.string.open)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f, true))

                if (updateUrl.isNotEmpty()) {
                    Button(
                        modifier = Modifier.defaultMinSize(52.dp, 32.dp),
                        enabled = !module.remove,
                        onClick = { onUpdate(module) },
                        shape = ButtonDefaults.textShape,
                        contentPadding = ButtonDefaults.TextButtonContentPadding
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Outlined.Download,
                            contentDescription = null
                        )
                        if (!module.hasActionScript || !module.hasWebUi) {
                            Text(
                                modifier = Modifier.padding(start = 7.dp),
                                fontFamily = MaterialTheme.typography.labelMedium.fontFamily,
                                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                text = stringResource(R.string.module_update)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.1f, true))
                }

                FilledTonalButton(
                    modifier = Modifier.defaultMinSize(52.dp, 32.dp),
                    onClick = { onUninstallClicked(module) },
                    contentPadding = ButtonDefaults.TextButtonContentPadding
                ) {
                    if (!module.remove) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                        )
                    } else {
                        Icon(
                            modifier = Modifier.size(20.dp).rotate(180f),
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = null,
                        )
                    }
                    if (!module.hasActionScript && !module.hasWebUi && updateUrl.isEmpty()) {
                        Text(
                            modifier = Modifier.padding(start = 7.dp),
                            fontFamily = MaterialTheme.typography.labelMedium.fontFamily,
                            fontSize = MaterialTheme.typography.labelMedium.fontSize,
                            text = stringResource(if (!module.remove) R.string.uninstall else R.string.restore)
                        )
                    }
                }
            }
        }
    }
}

// 修改备份功能
private suspend fun backupModules(context: Context, snackBarHost: SnackbarHostState, uri: Uri) {
    withContext(Dispatchers.IO) {
        try {
            // 1. 定义路径
            val busyboxPath = "/data/adb/ksu/bin/busybox"
            val moduleDir = "/data/adb/modules"
            val tempFile = File(context.cacheDir, "backup_${System.currentTimeMillis()}.tar.gz")
            val tempPath = tempFile.absolutePath

            // 2. 执行 tar 备份命令
            val command = """
                cd "$moduleDir" &&
                $busyboxPath tar -czvf "$tempPath" ./*
            """.trimIndent()

            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            process.waitFor()

            // 3. 检查错误
            val error = BufferedReader(InputStreamReader(process.errorStream)).readText()
            if (process.exitValue() != 0) {
                throw IOException("命令执行失败: $error")
            }

            // 4. 复制到用户选择的 URI
            context.contentResolver.openOutputStream(uri)?.use { output ->
                tempFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            // 5. 清理临时文件
            tempFile.delete()

            // 6. 提示成功
            withContext(Dispatchers.Main) {
                snackBarHost.showSnackbar("备份成功 (tar.gz)", duration = SnackbarDuration.Long)
            }

        } catch (e: Exception) {
            Log.e("Backup", "备份失败", e)
            withContext(Dispatchers.Main) {
                snackBarHost.showSnackbar("备份失败: ${e.message}", duration = SnackbarDuration.Long)
            }
        }
    }
}

private suspend fun showInstallConfirmation(context: Context, moduleName: String): Boolean {
    val result = CompletableDeferred<Boolean>()
    withContext(Dispatchers.Main) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.module_install_confirm))
            .setMessage(context.getString(R.string.module_install_confirm_message, moduleName))
            .setPositiveButton(context.getString(R.string.install)) { _, _ -> result.complete(true) }
            .setNegativeButton(context.getString(android.R.string.cancel)) { _, _ -> result.complete(false) }
            .setOnCancelListener { result.complete(false) }
            .show()
    }
    return result.await()
}

private suspend fun showRestoreConfirmation(context: Context): Boolean {
    val result = CompletableDeferred<Boolean>()
    withContext(Dispatchers.Main) {
        AlertDialog.Builder(context)
            .setTitle("确认还原模块")
            .setMessage("此操作将覆盖所有现有模块，是否继续？")
            .setPositiveButton("确定") { _, _ -> result.complete(true) }
            .setNegativeButton("取消") { _, _ -> result.complete(false) }
            .setOnCancelListener { result.complete(false) }
            .show()
    }
    return result.await()
}

// 修改还原功能
private suspend fun restoreModules(
    context: Context,
    snackBarHost: SnackbarHostState,
    uri: Uri
) {
    val userConfirmed = showRestoreConfirmation(context)
    if (!userConfirmed) return

    withContext(Dispatchers.IO) {
        try {
            // 1. 定义关键路径和命令
            val busyboxPath = "/data/adb/ksu/bin/busybox"
            val moduleDir = "/data/adb/modules"
            val tempFile = File(context.cacheDir, "temp_restore.tar.gz").apply {
                if (exists()) delete()
            }

            // 2. 将用户选择的文件复制到临时目录
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // 3. 执行还原命令（使用 tar 解压）
            val command = """
                cd "$moduleDir" &&
                rm -rf * && 
                $busyboxPath tar -xzvf "${tempFile.absolutePath}"
            """.trimIndent()

            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            process.waitFor()

            // 4. 检查命令错误
            val error = BufferedReader(InputStreamReader(process.errorStream)).readText()
            if (process.exitValue() != 0) {
                throw IOException("命令执行失败: $error")
            }

            // 5. 清理临时文件
            tempFile.delete()

            // 6. 提示成功并要求重启
            withContext(Dispatchers.Main) {
                val snackbarResult = snackBarHost.showSnackbar(
                    message = "模块已成功还原，需重启生效",
                    actionLabel = "立即重启",
                    duration = SnackbarDuration.Long
                )
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    reboot()
                }
            }

        } catch (e: Exception) {
            Log.e("Restore", "还原失败", e)
            withContext(Dispatchers.Main) {
                snackBarHost.showSnackbar(
                    message = "还原失败: ${e.message ?: "未知错误"}",
                    duration = SnackbarDuration.Long
                )
            }
        }
    }
}

@Preview
@Composable
fun ModuleItemPreview() {
    val module = ModuleViewModel.ModuleInfo(
        id = "id",
        name = "name",
        version = "version",
        versionCode = 1,
        author = "author",
        description = "I am a test module and i do nothing but show a very long description",
        enabled = true,
        update = true,
        remove = false,
        updateJson = "",
        hasWebUi = false,
        hasActionScript = false,
        dirId = "dirId"
    )
    ModuleItem(EmptyDestinationsNavigator, module, "", {}, {}, {}, {})
}
