package shirkneko.zako.mksu.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Fence
import androidx.compose.material.icons.filled.FolderDelete
import androidx.compose.material.icons.filled.RemoveModerator
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.list.ListDialog
import com.maxkeppeler.sheets.list.models.ListOption
import com.maxkeppeler.sheets.list.models.ListSelection
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AppProfileTemplateScreenDestination
import com.ramcosta.composedestinations.generated.destinations.FlashScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import shirkneko.zako.mksu.BuildConfig
import shirkneko.zako.mksu.Natives
import shirkneko.zako.mksu.R
import shirkneko.zako.mksu.ui.component.AboutDialog
import shirkneko.zako.mksu.ui.component.ConfirmResult
import shirkneko.zako.mksu.ui.component.DialogHandle
import shirkneko.zako.mksu.ui.component.SwitchItem
import shirkneko.zako.mksu.ui.component.rememberConfirmDialog
import shirkneko.zako.mksu.ui.component.rememberCustomDialog
import shirkneko.zako.mksu.ui.component.rememberLoadingDialog
import shirkneko.zako.mksu.ui.util.LocalSnackbarHost
import shirkneko.zako.mksu.ui.util.getBugreportFile
import shirkneko.zako.mksu.ui.util.getSuSFS
import shirkneko.zako.mksu.ui.util.getSuSFSFeatures
import shirkneko.zako.mksu.ui.util.susfsSUS_SU_0
import shirkneko.zako.mksu.ui.util.susfsSUS_SU_2
import shirkneko.zako.mksu.ui.util.susfsSUS_SU_Mode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Wallpaper
import shirkneko.zako.mksu.ui.theme.ThemeConfig
import shirkneko.zako.mksu.ui.theme.saveCustomBackground
import androidx.compose.material3.Slider
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.graphics.Color
import shirkneko.zako.mksu.ui.theme.CardConfig
import androidx.compose.ui.unit.DpSize
import kotlinx.coroutines.CoroutineScope


fun saveCardConfig(context: Context) {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    with(prefs.edit()) {
        putFloat("card_alpha", CardConfig.cardAlpha)
        putBoolean("custom_background_enabled", CardConfig.cardElevation == 0.dp)
        apply()
    }
}


/**
 * @author weishu
 * @date 2023/1/1.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SettingScreen(navigator: DestinationsNavigator) {
    // region 界面基础设置
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val snackBarHost = LocalSnackbarHost.current
    // endregion

    Scaffold(
        topBar = {
            TopBar(
                onBack = {
                    navigator.popBackStack()
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackBarHost) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { paddingValues ->
        // region 对话框状态管理
        val aboutDialog = rememberCustomDialog { AboutDialog(it) }
        val loadingDialog = rememberLoadingDialog()
        val shrinkDialog = rememberConfirmDialog()
        // endregion

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            // region 上下文与协程
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            // endregion

            // region 日志导出功能
            val exportBugreportLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.CreateDocument("application/gzip")
            ) { uri: Uri? ->
                if (uri == null) return@rememberLauncherForActivityResult
                scope.launch(Dispatchers.IO) {
                    loadingDialog.show()
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        getBugreportFile(context).inputStream().use {
                            it.copyTo(output)
                        }
                    }
                    loadingDialog.hide()
                    snackBarHost.showSnackbar(context.getString(R.string.log_saved))
                }
                // endregion
            }
            // region 配置项列表
            // 配置文件模板入口
            val profileTemplate = stringResource(id = R.string.settings_profile_template)
            ListItem(
                leadingContent = { Icon(Icons.Filled.Fence, profileTemplate) },
                headlineContent = { Text(profileTemplate) },
                supportingContent = { Text(stringResource(id = R.string.settings_profile_template_summary)) },
                modifier = Modifier.clickable {
                    navigator.navigate(AppProfileTemplateScreenDestination)
                }
            )
            // 卸载模块开关
            var umountChecked by rememberSaveable {
                mutableStateOf(Natives.isDefaultUmountModules())
            }
            SwitchItem(
                icon = Icons.Filled.FolderDelete,
                title = stringResource(id = R.string.settings_umount_modules_default),
                summary = stringResource(id = R.string.settings_umount_modules_default_summary),
                checked = umountChecked
            ) {
                if (Natives.setDefaultUmountModules(it)) {
                    umountChecked = it
                }
            }
            // SU 禁用开关（仅在兼容版本显示）
            if (Natives.version >= Natives.MINIMAL_SUPPORTED_SU_COMPAT) {
                var isSuDisabled by rememberSaveable {
                    mutableStateOf(!Natives.isSuEnabled())
                }
                SwitchItem(
                    icon = Icons.Filled.RemoveModerator,
                    title = stringResource(id = R.string.settings_disable_su),
                    summary = stringResource(id = R.string.settings_disable_su_summary),
                    checked = isSuDisabled,
                ) { checked ->
                    val shouldEnable = !checked
                    if (Natives.setSuEnabled(shouldEnable)) {
                        isSuDisabled = !shouldEnable
                    }
                }
            }

            val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

            // 初始化时从 SharedPreferences 加载配置到 CardConfig
            LaunchedEffect(Unit) {
                CardConfig.apply {
                    cardAlpha = prefs.getFloat("card_alpha", 0.85f)
                    cardElevation = if (prefs.getBoolean("custom_background_enabled", false)) 0.dp else defaultElevation
                }
            }


            var cardAlpha by rememberSaveable {
                mutableStateOf(CardConfig.cardAlpha)
            }

            LaunchedEffect(CardConfig.cardAlpha) {
                cardAlpha = CardConfig.cardAlpha
            }

            // 从 SharedPreferences 中读取卡片阴影
            var cardElevation by remember {
                val enabled = prefs.getBoolean("custom_background_enabled", false)
                mutableStateOf(if (enabled) 0.dp else CardConfig.defaultElevation)
            }
            // region SUSFS 配置（仅在支持时显示）
            val suSFS = getSuSFS()
            val isSUS_SU = getSuSFSFeatures()
            if (suSFS == "Supported") {
                if (isSUS_SU == "CONFIG_KSU_SUSFS_SUS_SU") {
                    var isEnabled by rememberSaveable {
                        mutableStateOf(susfsSUS_SU_Mode() == "2")
                    }

                    LaunchedEffect(Unit) {
                        isEnabled = susfsSUS_SU_Mode() == "2"
                    }

                    SwitchItem(
                        icon = Icons.Filled.VisibilityOff,
                        title = stringResource(id = R.string.settings_susfs_toggle),
                        summary = stringResource(id = R.string.settings_susfs_toggle_summary),
                        checked = isEnabled
                    ) {
                        if (it) {
                            susfsSUS_SU_2()
                        } else {
                            susfsSUS_SU_0()
                        }
                        prefs.edit().putBoolean("enable_sus_su", it).apply()
                        isEnabled = it
                    }
                }
            }
            // endregion

            // region 自定义背景设置
            var isCustomBackgroundEnabled by rememberSaveable { mutableStateOf(ThemeConfig.customBackgroundUri != null) }

            // 图片选择器定义
            val pickImageLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let {
                    context.saveCustomBackground(it)
                    // 选择图片成功后，更新开关状态为开启
                    isCustomBackgroundEnabled = true
                }
            }

            SwitchItem(
                icon = Icons.Filled.Wallpaper,
                title = stringResource(id = R.string.settings_custom_background),
                summary = stringResource(id = R.string.settings_custom_background_summary),
                checked = isCustomBackgroundEnabled
            ) { isChecked ->
                if (isChecked) {
                    pickImageLauncher.launch("image/*")
                } else {
                    context.saveCustomBackground(null)
                    isCustomBackgroundEnabled = false

                    // 关闭时恢复默认值
                    CardConfig.cardElevation = CardConfig.defaultElevation
                    CardConfig.cardAlpha = 1f
                }
                if (isChecked) {
                    CardConfig.cardElevation = 0.dp
                }
                CardConfig.save(context)
            }
            // endregion

            // region 卡片设置展开控制
            var showCardSettings by remember { mutableStateOf(false) }

            // 当自定义背景启用时显示卡片设置
            if (ThemeConfig.customBackgroundUri != null) {
                ListItem(
                    leadingContent = {
                        Icon(
                            Icons.Filled.ExpandMore,
                            stringResource(id = R.string.settings_card_manage)
                        )
                    },
                    headlineContent = {
                        Text(stringResource(id = R.string.settings_card_manage))
                    },
                    modifier = Modifier.clickable {
                        showCardSettings = !showCardSettings
                    }
                )

                if (showCardSettings) {
                    // 卡片透明度设置
                    ListItem(
                        leadingContent = {
                            Icon(
                                Icons.Filled.Opacity,
                                stringResource(id = R.string.settings_card_alpha)
                            )
                        },
                        headlineContent = {
                            Text(stringResource(id = R.string.settings_card_alpha))
                        },
                        supportingContent = {
                            Slider(
                                value = cardAlpha,
                                onValueChange = { newValue ->
                                    // 同时更新本地状态和配置对象
                                    cardAlpha = newValue
                                    CardConfig.cardAlpha = newValue
                                    // 立即保存到SharedPreferences（主线程安全操作）
                                    prefs.edit().putFloat("card_alpha", newValue).apply()
                                },
                                onValueChangeFinished = {
                                    // 异步保存完整配置
                                    CoroutineScope(Dispatchers.IO).launch {
                                        saveCardConfig(context)
                                    }
                                },
                                valueRange = 0f..1f,
                                thumb = {
                                    SliderDefaults.Thumb(
                                        interactionSource = remember { MutableInteractionSource() },
                                        thumbSize = DpSize(0.dp, 0.dp)
                                    )
                                },
                                colors = getSliderColors(cardAlpha)
                            )
                        }

                    )

                    // 阴影设置说明注释
                    /*
                    当启用自定义背景时：
                    1. 强制设置阴影为0.dp
                    2. 立即保存配置
                    */
                    if (ThemeConfig.customBackgroundUri != null) {
                        CardConfig.cardElevation = 0.dp
                    } else {
                        // 未启用自定义背景时恢复默认阴影
                        CardConfig.cardElevation = CardConfig.defaultElevation
                    }
                    CardConfig.save(context) // 修改保存调用
                }
            }// endregion

            // 更新检查开关
            var checkUpdate by rememberSaveable {
                mutableStateOf(
                    prefs.getBoolean("check_update", true)
                )
            }
            SwitchItem(
                icon = Icons.Filled.Update,
                title = stringResource(id = R.string.settings_check_update),
                summary = stringResource(id = R.string.settings_check_update_summary),
                checked = checkUpdate
            ) {
                prefs.edit().putBoolean("check_update", it).apply()
                checkUpdate = it
            }

            // Web调试开关
            var enableWebDebugging by rememberSaveable {
                mutableStateOf(
                    prefs.getBoolean("enable_web_debugging", false)
                )
            }
            SwitchItem(
                icon = Icons.Filled.DeveloperMode,
                title = stringResource(id = R.string.enable_web_debugging),
                summary = stringResource(id = R.string.enable_web_debugging_summary),
                checked = enableWebDebugging
            ) {
                prefs.edit().putBoolean("enable_web_debugging", it).apply()
                enableWebDebugging = it
            }
            // endregion

            var showBottomsheet by remember { mutableStateOf(false) }

            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Filled.BugReport,
                        stringResource(id = R.string.send_log)
                    )
                },
                headlineContent = { Text(stringResource(id = R.string.send_log)) },
                modifier = Modifier.clickable {
                    showBottomsheet = true
                }
            )
            if (showBottomsheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomsheet = false },
                    content = {
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterHorizontally)

                        ) {
                            Box {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .clickable {
                                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm")
                                            val current = LocalDateTime.now().format(formatter)
                                            exportBugreportLauncher.launch("KernelSU_bugreport_${current}.tar.gz")
                                            showBottomsheet = false
                                        }
                                ) {
                                    Icon(
                                        Icons.Filled.Save,
                                        contentDescription = null,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.save_log),
                                        modifier = Modifier.padding(top = 16.dp),
                                        textAlign = TextAlign.Center.also {
                                            LineHeightStyle(
                                                alignment = LineHeightStyle.Alignment.Center,
                                                trim = LineHeightStyle.Trim.None
                                            )
                                        }

                                    )
                                }
                            }
                            Box {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .clickable {
                                            scope.launch {
                                                val bugreport = loadingDialog.withLoading {
                                                    withContext(Dispatchers.IO) {
                                                        getBugreportFile(context)
                                                    }
                                                }

                                                val uri: Uri =
                                                    FileProvider.getUriForFile(
                                                        context,
                                                        "${BuildConfig.APPLICATION_ID}.fileprovider",
                                                        bugreport
                                                    )

                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    putExtra(Intent.EXTRA_STREAM, uri)
                                                    setDataAndType(uri, "application/gzip")
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }

                                                context.startActivity(
                                                    Intent.createChooser(
                                                        shareIntent,
                                                        context.getString(R.string.send_log)
                                                    )
                                                )
                                            }
                                        }
                                ) {
                                    Icon(
                                        Icons.Filled.Share,
                                        contentDescription = null,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.send_log),
                                        modifier = Modifier.padding(top = 16.dp),
                                        textAlign = TextAlign.Center.also {
                                            LineHeightStyle(
                                                alignment = LineHeightStyle.Alignment.Center,
                                                trim = LineHeightStyle.Trim.None
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }

            val lkmMode = Natives.version >= Natives.MINIMAL_SUPPORTED_KERNEL_LKM && Natives.isLkmMode
            if (lkmMode) {
                UninstallItem(navigator) {
                    loadingDialog.withLoading(it)
                }
            }

            val about = stringResource(id = R.string.about)
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Filled.ContactPage,
                        about
                    )
                },
                headlineContent = { Text(about) },
                modifier = Modifier.clickable {
                    aboutDialog.show()
                }
            )
        }
    }
}

@Composable
fun UninstallItem(
    navigator: DestinationsNavigator,
    withLoading: suspend (suspend () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uninstallConfirmDialog = rememberConfirmDialog()
    val showTodo = {
        Toast.makeText(context, "TODO", Toast.LENGTH_SHORT).show()
    }
    val uninstallDialog = rememberUninstallDialog { uninstallType ->
        scope.launch {
            val result = uninstallConfirmDialog.awaitConfirm(
                title = context.getString(uninstallType.title),
                content = context.getString(uninstallType.message)
            )
            if (result == ConfirmResult.Confirmed) {
                withLoading {
                    when (uninstallType) {
                        UninstallType.TEMPORARY -> showTodo()
                        UninstallType.PERMANENT -> navigator.navigate(
                            FlashScreenDestination(FlashIt.FlashUninstall)
                        )
                        UninstallType.RESTORE_STOCK_IMAGE -> navigator.navigate(
                            FlashScreenDestination(FlashIt.FlashRestore)
                        )
                        UninstallType.NONE -> Unit
                    }
                }
            }
        }
    }
    val uninstall = stringResource(id = R.string.settings_uninstall)
    ListItem(
        leadingContent = {
            Icon(
                Icons.Filled.Delete,
                uninstall
            )
        },
        headlineContent = { Text(uninstall) },
        modifier = Modifier.clickable {
            uninstallDialog.show()
        }
    )
}

enum class UninstallType(val title: Int, val message: Int, val icon: ImageVector) {
    TEMPORARY(
        R.string.settings_uninstall_temporary,
        R.string.settings_uninstall_temporary_message,
        Icons.Filled.Delete
    ),
    PERMANENT(
        R.string.settings_uninstall_permanent,
        R.string.settings_uninstall_permanent_message,
        Icons.Filled.DeleteForever
    ),
    RESTORE_STOCK_IMAGE(
        R.string.settings_restore_stock_image,
        R.string.settings_restore_stock_image_message,
        Icons.AutoMirrored.Filled.Undo
    ),
    NONE(0, 0, Icons.Filled.Delete)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberUninstallDialog(onSelected: (UninstallType) -> Unit): DialogHandle {
    return rememberCustomDialog { dismiss ->
        val options = listOf(
            // UninstallType.TEMPORARY,
            UninstallType.PERMANENT,
            UninstallType.RESTORE_STOCK_IMAGE
        )
        val listOptions = options.map {
            ListOption(
                titleText = stringResource(it.title),
                subtitleText = if (it.message != 0) stringResource(it.message) else null,
                icon = IconSource(it.icon)
            )
        }

        var selection = UninstallType.NONE
        ListDialog(state = rememberUseCaseState(visible = true, onFinishedRequest = {
            if (selection != UninstallType.NONE) {
                onSelected(selection)
            }
        }, onCloseRequest = {
            dismiss()
        }), header = Header.Default(
            title = stringResource(R.string.settings_uninstall),
        ), selection = ListSelection.Single(
            showRadioButtons = false,
            options = listOptions,
        ) { index, _ ->
            selection = options[index]
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onBack: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = { Text(stringResource(R.string.settings)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        scrollBehavior = scrollBehavior
    )
}

@Preview
@Composable
private fun SettingsPreview() {
    SettingScreen(EmptyDestinationsNavigator)
}

@Composable
fun getSliderColors(value: Float): SliderColors {
    val activeColor = Color.Magenta.copy(alpha = value)
    val inactiveColor = Color.Gray.copy(alpha = 1 - value)
    return SliderDefaults.colors(
        activeTrackColor = activeColor,
        inactiveTrackColor = inactiveColor
    )
}

