package com.ccnio.sec

import android.app.Application
import android.app.AppInteractionAttribution
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appfunctions.AppFunctionData
import androidx.appfunctions.AppFunctionManager
import androidx.appfunctions.AppFunctionSearchSpec
import androidx.appfunctions.ExecuteAppFunctionRequest
import androidx.appfunctions.ExecuteAppFunctionResponse
import androidx.appfunctions.metadata.AppFunctionArrayTypeMetadata
import androidx.appfunctions.metadata.AppFunctionBooleanTypeMetadata
import androidx.appfunctions.metadata.AppFunctionComponentsMetadata
import androidx.appfunctions.metadata.AppFunctionDataTypeMetadata
import androidx.appfunctions.metadata.AppFunctionDoubleTypeMetadata
import androidx.appfunctions.metadata.AppFunctionFloatTypeMetadata
import androidx.appfunctions.metadata.AppFunctionIntTypeMetadata
import androidx.appfunctions.metadata.AppFunctionLongTypeMetadata
import androidx.appfunctions.metadata.AppFunctionMetadata
import androidx.appfunctions.metadata.AppFunctionObjectTypeMetadata
import androidx.appfunctions.metadata.AppFunctionParameterMetadata
import androidx.appfunctions.metadata.AppFunctionReferenceTypeMetadata
import androidx.appfunctions.metadata.AppFunctionStringTypeMetadata
import androidx.appfunctions.metadata.AppFunctionUnitTypeMetadata
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "AppFunctionBrowser"
private const val PERMISSION_EXECUTE_APP_FUNCTIONS = "android.permission.EXECUTE_APP_FUNCTIONS"

class ViewTestActivity : ComponentActivity() {
    private val viewModel: AppFunctionBrowserViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AppFunctionBrowserViewModel(application) as T
                }
            },
        )[AppFunctionBrowserViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val uiState by viewModel.uiState.collectAsState()
                AppFunctionBrowserScreen(
                    uiState = uiState,
                    onRefresh = viewModel::refresh,
                    onPackageSelected = viewModel::selectPackage,
                    onInputChanged = viewModel::updateInputValue,
                    onExecute = viewModel::executeFunction,
                )
            }
        }
    }
}

class AppFunctionBrowserViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(AppFunctionBrowserUiState())
    internal val uiState: StateFlow<AppFunctionBrowserUiState> = _uiState
    private var observeJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }
            val appContext = getApplication<Application>()
            val manager = AppFunctionManager.getInstance(appContext)
            if (manager == null) {
                _uiState.update {
                    it.copy(
                        isSupported = false,
                        isLoading = false,
                        errorMessage = "未获取到 AppFunctionManager，设备可能未启用 AppFunctions 接口。",
                    )
                }
                return@launch
            }
            val executePermissionState = appContext.checkSelfPermission(PERMISSION_EXECUTE_APP_FUNCTIONS)
            if (executePermissionState != PackageManager.PERMISSION_GRANTED) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        functions = emptyList(),
                        packageNames = emptyList(),
                        selectedPackage = null,
                        errorMessage = "缺少 $PERMISSION_EXECUTE_APP_FUNCTIONS，无法发现和执行其他应用的 AppFunctions。",
                    )
                }
                return@launch
            }

            manager.observeVisibleAppFunctions()
                .catch { throwable ->
                    Log.e(TAG, "scan failed", throwable)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            functions = emptyList(),
                            packageNames = emptyList(),
                            selectedPackage = null,
                            errorMessage = throwable.message ?: throwable.javaClass.simpleName,
                        )
                    }
                }
                .collect { functions ->
                    val packages = functions.map { it.packageName }.distinct()
                    val demoFunctions = functions.filter { it.packageName == "com.ccino.demo" }
                    Log.d(
                        TAG,
                        "scan success: packageCount=${packages.size}, functionCount=${functions.size}, " +
                            "demoFunctionCount=${demoFunctions.size}, demoFunctionIds=${demoFunctions.map { it.functionId }}",
                    )
                    val selectedPackage = _uiState.value.selectedPackage?.takeIf { it in packages }
                    _uiState.value = AppFunctionBrowserUiState(
                        isSupported = true,
                        isLoading = false,
                        packageNames = packages,
                        selectedPackage = selectedPackage,
                        functions = functions,
                    )
                }
        }
    }

    fun selectPackage(packageName: String?) {
        _uiState.update { it.copy(selectedPackage = packageName) }
    }

    fun updateInputValue(functionKey: String, inputPath: String, value: String) {
        _uiState.update { state ->
            state.copy(
                functions = state.functions.map { function ->
                    if (function.stableKey != functionKey) {
                        function
                    } else {
                        function.copy(
                            inputFields = function.inputFields.map { input ->
                                if (input.path == inputPath) input.copy(value = value) else input
                            },
                        )
                    }
                },
            )
        }
    }

    fun executeFunction(functionKey: String) {
        val appContext = getApplication<Application>()
        val manager = AppFunctionManager.getInstance(appContext) ?: return
        val function = _uiState.value.functions.firstOrNull { it.stableKey == functionKey } ?: return

        viewModelScope.launch {
            markExecuting(functionKey, true, null, null)
            runCatching {
                function.execute(manager)
            }.onSuccess { result ->
                Log.d(TAG, "execute success: ${function.functionId}")
                markExecuting(
                    functionKey = functionKey,
                    isExecuting = false,
                    result = result,
                    error = null,
                )
            }.onFailure { throwable ->
                Log.e(TAG, "execute failed: ${function.functionId}", throwable)
                markExecuting(
                    functionKey = functionKey,
                    isExecuting = false,
                    result = null,
                    error = throwable.message ?: throwable.javaClass.simpleName,
                )
            }
        }
    }

    private fun markExecuting(
        functionKey: String,
        isExecuting: Boolean,
        result: String?,
        error: String?,
    ) {
        _uiState.update { state ->
            state.copy(
                functions = state.functions.map { function ->
                    if (function.stableKey != functionKey) {
                        function
                    } else {
                        function.copy(
                            isExecuting = isExecuting,
                            executionResult = result,
                            executionError = error,
                        )
                    }
                },
            )
        }
    }
}

data class AppFunctionBrowserUiState(
    val isSupported: Boolean = true,
    val isLoading: Boolean = false,
    val packageNames: List<String> = emptyList(),
    val selectedPackage: String? = null,
    val functions: List<ScannedFunction> = emptyList(),
    val errorMessage: String? = null,
)

data class ScannedFunction(
    val packageName: String,
    val functionId: String,
    val displayName: String,
    val description: String,
    val metadata: AppFunctionMetadata,
    val parameters: List<FunctionParameter>,
    val inputFields: List<FunctionInputField>,
    val isEnabled: Boolean,
    val isExecuting: Boolean = false,
    val executionResult: String? = null,
    val executionError: String? = null,
) {
    val stableKey: String = "$packageName::$functionId"
    val canExecute: Boolean = isEnabled && parameters.all { it.isExecutable() }
}

data class FunctionParameter(
    val name: String,
    val description: String,
    val required: Boolean,
    val kind: ParameterKind,
    val typeLabel: String,
    val dataType: AppFunctionDataTypeMetadata,
    val children: List<FunctionParameter> = emptyList(),
) {
    fun isExecutable(): Boolean {
        return when (kind) {
            ParameterKind.String,
            ParameterKind.Int,
            ParameterKind.Long,
            ParameterKind.Float,
            ParameterKind.Double,
            ParameterKind.Boolean -> true
            ParameterKind.Document -> children.isNotEmpty() && children.all { it.isExecutable() }
            ParameterKind.Unit -> false
            ParameterKind.Unknown -> false
        }
    }
}

data class FunctionInputField(
    val path: String,
    val label: String,
    val description: String,
    val typeLabel: String,
    val required: Boolean,
    val value: String = "",
)

enum class ParameterKind {
    String,
    Int,
    Long,
    Float,
    Double,
    Boolean,
    Document,
    Unit,
    Unknown,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppFunctionBrowserScreen(
    uiState: AppFunctionBrowserUiState,
    onRefresh: () -> Unit,
    onPackageSelected: (String?) -> Unit,
    onInputChanged: (String, String, String) -> Unit,
    onExecute: (String) -> Unit,
) {
    val visibleFunctions = if (uiState.selectedPackage == null) {
        uiState.functions
    } else {
        uiState.functions.filter { it.packageName == uiState.selectedPackage }
    }
    val packageOptions = listOf<String?>(null) + uiState.packageNames
    var packageMenuExpanded by remember(uiState.packageNames, uiState.selectedPackage) { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AppFunction 扫描器") },
                actions = {
                    TextButton(onClick = onRefresh) {
                        Text("重新扫描")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box {
                Button(
                    onClick = { packageMenuExpanded = true },
                    enabled = packageOptions.isNotEmpty(),
                ) {
                    Text(uiState.selectedPackage ?: "全部包名")
                }
                DropdownMenu(
                    expanded = packageMenuExpanded,
                    onDismissRequest = { packageMenuExpanded = false },
                ) {
                    packageOptions.forEach { packageName ->
                        DropdownMenuItem(
                            text = { Text(packageName ?: "全部包名") },
                            onClick = {
                                packageMenuExpanded = false
                                onPackageSelected(packageName)
                            },
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator()
                    Text("正在扫描 AppFunction...")
                }
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Text(
                text = "已发现 ${visibleFunctions.size} 个函数 / ${uiState.packageNames.size} 个包",
                style = MaterialTheme.typography.bodySmall,
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(visibleFunctions, key = { it.stableKey }) { function ->
                    AppFunctionCard(
                        function = function,
                        onInputChanged = { path, value ->
                            onInputChanged(function.stableKey, path, value)
                        },
                        onExecute = { onExecute(function.stableKey) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppFunctionCard(
    function: ScannedFunction,
    onInputChanged: (String, String) -> Unit,
    onExecute: () -> Unit,
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = function.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(function.packageName, style = MaterialTheme.typography.labelMedium)
            Text("标识：${function.functionId}", style = MaterialTheme.typography.bodySmall)
            Text(
                text = if (function.description.isNotBlank()) function.description else "无描述",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text("参数", style = MaterialTheme.typography.titleSmall)
            if (function.parameters.isEmpty()) {
                Text("无参数", style = MaterialTheme.typography.bodySmall)
            } else {
                function.parameters.flatMap(::flattenParameterLines).forEach { line ->
                    Text(line, style = MaterialTheme.typography.bodySmall)
                }
            }

            if (function.inputFields.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("执行输入", style = MaterialTheme.typography.titleSmall)
                function.inputFields.forEach { input ->
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = input.value,
                        onValueChange = { onInputChanged(input.path, it) },
                        label = { Text("${input.label} (${input.typeLabel})") },
                        supportingText = {
                            val suffix = if (input.required) "必填" else "可选"
                            Text(input.description.ifBlank { suffix })
                        },
                        singleLine = true,
                    )
                }
            }

            if (!function.isEnabled) {
                Text(
                    text = "当前函数已禁用。",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            } else if (!function.canExecute) {
                Text(
                    text = "当前仅支持基础类型和可展开对象参数的执行。",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onExecute,
                    enabled = function.canExecute && !function.isExecuting,
                ) {
                    Text(if (function.isExecuting) "执行中..." else "执行")
                }
                if (function.isExecuting) {
                    Spacer(modifier = Modifier.width(12.dp))
                    CircularProgressIndicator(modifier = Modifier.height(20.dp))
                }
            }

            function.executionResult?.let { result ->
                Text("执行结果\n$result", style = MaterialTheme.typography.bodySmall)
            }
            function.executionError?.let { error ->
                Text(
                    text = "执行失败\n$error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun flattenParameterLines(
    parameter: FunctionParameter,
    depth: Int = 0,
): List<String> {
    val indent = "  ".repeat(depth)
    val header = buildString {
        append(indent)
        append("- ")
        append(parameter.name)
        append(": ")
        append(parameter.typeLabel)
        if (parameter.required) {
            append(" [required]")
        }
        if (parameter.description.isNotBlank()) {
            append(" - ")
            append(parameter.description)
        }
    }
    return listOf(header) + parameter.children.flatMap { child ->
        flattenParameterLines(child, depth + 1)
    }
}

private fun AppFunctionManager.observeVisibleAppFunctions() =
    observeAppFunctions(AppFunctionSearchSpec()).map { packageMetadataList ->
        packageMetadataList
            .flatMap { packageMetadata ->
                packageMetadata.appFunctions.map { metadata ->
                    toScannedFunction(packageMetadata.packageName, metadata)
                }
            }
            .sortedWith(compareBy<ScannedFunction> { it.packageName }.thenBy { it.displayName })
    }

private fun toScannedFunction(
    packageName: String,
    metadata: AppFunctionMetadata,
): ScannedFunction {
    val parameters = metadata.parameters.map { it.toFunctionParameter(metadata.components) }
    return ScannedFunction(
        packageName = packageName,
        functionId = metadata.id,
        displayName = metadata.schema?.name?.takeIf { it.isNotBlank() }
            ?: metadata.id.substringAfterLast('#', metadata.id),
        description = metadata.description,
        metadata = metadata,
        parameters = parameters,
        inputFields = buildInputFields(parameters),
        isEnabled = metadata.isEnabled,
    )
}

private fun AppFunctionParameterMetadata.toFunctionParameter(
    components: AppFunctionComponentsMetadata,
): FunctionParameter {
    return dataType.resolve(components).toFunctionParameter(
        name = name,
        description = description,
        required = isRequired,
        components = components,
    )
}

private fun AppFunctionDataTypeMetadata.toFunctionParameter(
    name: String,
    description: String,
    required: Boolean,
    components: AppFunctionComponentsMetadata,
): FunctionParameter {
    val resolved = resolve(components)
    return when (resolved) {
        is AppFunctionStringTypeMetadata -> FunctionParameter(
            name = name,
            description = description,
            required = required,
            kind = ParameterKind.String,
            typeLabel = "String",
            dataType = resolved,
        )
        is AppFunctionIntTypeMetadata -> FunctionParameter(
            name = name,
            description = description,
            required = required,
            kind = ParameterKind.Int,
            typeLabel = "Int",
            dataType = resolved,
        )
        is AppFunctionLongTypeMetadata -> FunctionParameter(
            name = name,
            description = description,
            required = required,
            kind = ParameterKind.Long,
            typeLabel = "Long",
            dataType = resolved,
        )
        is AppFunctionFloatTypeMetadata -> FunctionParameter(
            name = name,
            description = description,
            required = required,
            kind = ParameterKind.Float,
            typeLabel = "Float",
            dataType = resolved,
        )
        is AppFunctionDoubleTypeMetadata -> FunctionParameter(
            name = name,
            description = description,
            required = required,
            kind = ParameterKind.Double,
            typeLabel = "Double",
            dataType = resolved,
        )
        is AppFunctionBooleanTypeMetadata -> FunctionParameter(
            name = name,
            description = description,
            required = required,
            kind = ParameterKind.Boolean,
            typeLabel = "Boolean",
            dataType = resolved,
        )
        is AppFunctionUnitTypeMetadata -> FunctionParameter(
            name = name,
            description = description,
            required = required,
            kind = ParameterKind.Unit,
            typeLabel = "Unit",
            dataType = resolved,
        )
        is AppFunctionObjectTypeMetadata -> FunctionParameter(
            name = name,
            description = description,
            required = required,
            kind = ParameterKind.Document,
            typeLabel = "Object",
            dataType = resolved,
            children = resolved.properties.map { (childName, childType) ->
                childType.toFunctionParameter(
                    name = childName,
                    description = childType.description,
                    required = childName in resolved.required,
                    components = components,
                )
            },
        )
        else -> FunctionParameter(
            name = name,
            description = description,
            required = required,
            kind = ParameterKind.Unknown,
            typeLabel = resolved.javaClass.simpleName.removePrefix("AppFunction").removeSuffix("TypeMetadata"),
            dataType = resolved,
        )
    }
}

private fun AppFunctionDataTypeMetadata.resolve(
    components: AppFunctionComponentsMetadata,
): AppFunctionDataTypeMetadata {
    return if (this is AppFunctionReferenceTypeMetadata) {
        components.dataTypes[referenceDataType]
            ?: throw IllegalStateException("Reference to $referenceDataType not found.")
    } else {
        this
    }
}

private fun buildInputFields(parameters: List<FunctionParameter>): List<FunctionInputField> {
    return parameters.flatMap { parameter -> parameter.toInputFields() }
}

private fun FunctionParameter.toInputFields(prefix: String = ""): List<FunctionInputField> {
    val currentPath = if (prefix.isBlank()) name else "$prefix.$name"
    return when (kind) {
        ParameterKind.String,
        ParameterKind.Int,
        ParameterKind.Long,
        ParameterKind.Float,
        ParameterKind.Double,
        ParameterKind.Boolean -> {
            listOf(
                FunctionInputField(
                    path = currentPath,
                    label = currentPath,
                    description = description,
                    typeLabel = typeLabel,
                    required = required,
                ),
            )
        }
        ParameterKind.Document -> children.flatMap { child ->
            child.toInputFields(currentPath)
        }
        ParameterKind.Unit,
        ParameterKind.Unknown -> emptyList()
    }
}

private suspend fun ScannedFunction.execute(
    manager: AppFunctionManager,
): String {
    val request = buildExecuteRequest()
    return when (val response = manager.executeAppFunction(request)) {
        is ExecuteAppFunctionResponse.Success -> formatExecuteSuccess(response, metadata)
        is ExecuteAppFunctionResponse.Error -> {
            val error = response.error
            throw IllegalStateException(
                "Execution failed: ${error.errorMessage} (${error.javaClass.simpleName})",
                error,
            )
        }
    }
}

private fun ScannedFunction.buildExecuteRequest(): ExecuteAppFunctionRequest {
    val inputMap = inputFields.associate { it.path to it.value }
    val parametersData = buildParametersData(metadata, parameters, inputMap)
    if (Build.VERSION.SDK_INT >= 37) {
        return ExecuteAppFunctionRequest(
            functionIdentifier = functionId,
            targetPackageName = packageName,
            functionParameters = parametersData,
            attribution = AppInteractionAttribution.Builder(
                AppInteractionAttribution.INTERACTION_TYPE_USER_QUERY,
            )
                .setInteractionUri(buildInteractionUri())
                .build(),
        )
    }
    return ExecuteAppFunctionRequest(
        functionIdentifier = functionId,
        targetPackageName = packageName,
        functionParameters = parametersData,
    )
}

private fun ScannedFunction.buildInteractionUri(): Uri {
    return Uri.Builder()
        .scheme("appfunctions-browser")
        .authority("execute")
        .appendQueryParameter("package", packageName)
        .appendQueryParameter("function", functionId)
        .build()
}

private fun buildParametersData(
    metadata: AppFunctionMetadata,
    parameters: List<FunctionParameter>,
    inputMap: Map<String, String>,
): AppFunctionData {
    val builder = AppFunctionData.Builder(metadata.parameters, metadata.components)
    parameters.forEach { parameter ->
        val value = buildParameterValue(parameter, parameter.name, metadata.components, inputMap) ?: return@forEach
        applyValue(builder, parameter.name, parameter.kind, value)
    }
    return builder.build()
}

private fun buildObjectData(
    parameter: FunctionParameter,
    path: String,
    components: AppFunctionComponentsMetadata,
    inputMap: Map<String, String>,
): AppFunctionData? {
    val objectMetadata = parameter.dataType as? AppFunctionObjectTypeMetadata
        ?: throw IllegalStateException("${parameter.name} 不是对象类型")
    val builder = AppFunctionData.Builder(objectMetadata, components)
    var hasAnyValue = false
    parameter.children.forEach { child ->
        val childPath = "$path.${child.name}"
        val value = buildParameterValue(child, childPath, components, inputMap) ?: return@forEach
        hasAnyValue = true
        applyValue(builder, child.name, child.kind, value)
    }
    if (!hasAnyValue && !parameter.required) {
        return null
    }
    return builder.build()
}

private fun buildParameterValue(
    parameter: FunctionParameter,
    currentPath: String,
    components: AppFunctionComponentsMetadata,
    inputMap: Map<String, String>,
): Any? {
    return when (parameter.kind) {
        ParameterKind.String -> parseRequiredText(inputMap[currentPath], parameter)
        ParameterKind.Int -> parseRequiredInt(inputMap[currentPath], parameter)
        ParameterKind.Long -> parseRequiredLong(inputMap[currentPath], parameter)
        ParameterKind.Float -> parseRequiredFloat(inputMap[currentPath], parameter)
        ParameterKind.Double -> parseRequiredDouble(inputMap[currentPath], parameter)
        ParameterKind.Boolean -> parseRequiredBoolean(inputMap[currentPath], parameter)
        ParameterKind.Document -> buildObjectData(parameter, currentPath, components, inputMap)
        ParameterKind.Unit -> null
        ParameterKind.Unknown -> throw IllegalStateException("存在未知参数类型，无法执行：${parameter.name}")
    }
}

private fun applyValue(
    builder: AppFunctionData.Builder,
    name: String,
    kind: ParameterKind,
    value: Any,
) {
    when (kind) {
        ParameterKind.String -> builder.setString(name, value as String)
        ParameterKind.Int -> builder.setInt(name, value as Int)
        ParameterKind.Long -> builder.setLong(name, value as Long)
        ParameterKind.Float -> builder.setFloat(name, value as Float)
        ParameterKind.Double -> builder.setDouble(name, value as Double)
        ParameterKind.Boolean -> builder.setBoolean(name, value as Boolean)
        ParameterKind.Document -> builder.setAppFunctionData(name, value as AppFunctionData)
        ParameterKind.Unit,
        ParameterKind.Unknown -> Unit
    }
}

private fun parseRequiredText(value: String?, parameter: FunctionParameter): String? {
    val text = value?.trim().orEmpty()
    if (text.isEmpty()) {
        if (parameter.required) {
            throw IllegalArgumentException("${parameter.name} 为必填参数")
        }
        return null
    }
    return text
}

private fun parseRequiredInt(value: String?, parameter: FunctionParameter): Int? {
    val text = parseRequiredText(value, parameter) ?: return null
    return text.toIntOrNull()
        ?: throw IllegalArgumentException("${parameter.name} 需要 Int，当前值为 $text")
}

private fun parseRequiredLong(value: String?, parameter: FunctionParameter): Long? {
    val text = parseRequiredText(value, parameter) ?: return null
    return text.toLongOrNull()
        ?: throw IllegalArgumentException("${parameter.name} 需要 Long，当前值为 $text")
}

private fun parseRequiredFloat(value: String?, parameter: FunctionParameter): Float? {
    val text = parseRequiredText(value, parameter) ?: return null
    return text.toFloatOrNull()
        ?: throw IllegalArgumentException("${parameter.name} 需要 Float，当前值为 $text")
}

private fun parseRequiredDouble(value: String?, parameter: FunctionParameter): Double? {
    val text = parseRequiredText(value, parameter) ?: return null
    return text.toDoubleOrNull()
        ?: throw IllegalArgumentException("${parameter.name} 需要 Double，当前值为 $text")
}

private fun parseRequiredBoolean(value: String?, parameter: FunctionParameter): Boolean? {
    val text = parseRequiredText(value, parameter) ?: return null
    return when (text.lowercase()) {
        "true", "1", "yes" -> true
        "false", "0", "no" -> false
        else -> throw IllegalArgumentException("${parameter.name} 需要 Boolean，当前值为 $text")
    }
}

private fun formatExecuteSuccess(
    response: ExecuteAppFunctionResponse.Success,
    metadata: AppFunctionMetadata,
): String {
    val returnValueKey = ExecuteAppFunctionResponse.Success.PROPERTY_RETURN_VALUE
    if (!response.returnValue.containsKey(returnValueKey)) {
        return "无返回值"
    }
    return formatValue(
        data = response.returnValue,
        key = returnValueKey,
        dataType = metadata.response.valueType,
        components = metadata.components,
        depth = 0,
    ) ?: "无返回值"
}

private fun formatValue(
    data: AppFunctionData,
    key: String,
    dataType: AppFunctionDataTypeMetadata,
    components: AppFunctionComponentsMetadata,
    depth: Int,
): String? {
    return when (val resolved = dataType.resolve(components)) {
        is AppFunctionStringTypeMetadata -> data.getStringOrNull(key)
        is AppFunctionIntTypeMetadata -> data.getIntOrNull(key)?.toString()
        is AppFunctionLongTypeMetadata -> data.getLongOrNull(key)?.toString()
        is AppFunctionFloatTypeMetadata -> data.getFloatOrNull(key)?.toString()
        is AppFunctionDoubleTypeMetadata -> data.getDoubleOrNull(key)?.toString()
        is AppFunctionBooleanTypeMetadata -> data.getBooleanOrNull(key)?.toString()
        is AppFunctionUnitTypeMetadata -> "Unit"
        is AppFunctionObjectTypeMetadata -> {
            data.getAppFunctionDataOrNull(key)?.formatObject(resolved, components, depth)
        }
        is AppFunctionArrayTypeMetadata -> {
            when (val itemType = resolved.itemType.resolve(components)) {
                is AppFunctionStringTypeMetadata -> data.getStringListOrNull(key)?.joinToString(prefix = "[", postfix = "]")
                is AppFunctionIntTypeMetadata -> data.getIntArrayOrNull(key)?.joinToString(prefix = "[", postfix = "]")
                is AppFunctionLongTypeMetadata -> data.getLongArrayOrNull(key)?.joinToString(prefix = "[", postfix = "]")
                is AppFunctionObjectTypeMetadata -> data.getAppFunctionDataListOrNull(key)?.joinToString(
                    prefix = "[\n",
                    postfix = "\n${"  ".repeat(depth)}]",
                    separator = ",\n",
                ) { item ->
                    item.formatObject(itemType, components, depth + 1)
                }
                else -> "暂不支持展示数组返回类型：${itemType.javaClass.simpleName}"
            }
        }
        else -> "暂不支持展示返回类型：${resolved.javaClass.simpleName}"
    }
}

private fun AppFunctionData.formatObject(
    schema: AppFunctionObjectTypeMetadata,
    components: AppFunctionComponentsMetadata,
    depth: Int,
): String {
    val indent = "  ".repeat(depth)
    val childIndent = "  ".repeat(depth + 1)
    val entries = schema.properties.keys.sorted().mapNotNull { propertyName ->
        val propertyType = schema.properties[propertyName] ?: return@mapNotNull null
        val value = formatValue(this, propertyName, propertyType, components, depth + 1) ?: return@mapNotNull null
        "$childIndent$propertyName = $value"
    }
    return buildString {
        append("{")
        if (entries.isNotEmpty()) {
            append('\n')
            append(entries.joinToString("\n"))
            append('\n')
            append(indent)
        }
        append("}")
    }
}

private fun AppFunctionData.getStringListOrNull(key: String): List<String>? = runCatching { getStringList(key) }.getOrNull()

private fun AppFunctionData.getIntArrayOrNull(key: String): IntArray? = runCatching { getIntArray(key) }.getOrNull()

private fun AppFunctionData.getLongArrayOrNull(key: String): LongArray? = runCatching { getLongArray(key) }.getOrNull()

private fun AppFunctionData.getAppFunctionDataOrNull(key: String): AppFunctionData? = runCatching { getAppFunctionData(key) }.getOrNull()

private fun AppFunctionData.getAppFunctionDataListOrNull(key: String): List<AppFunctionData>? = runCatching { getAppFunctionDataList(key) }.getOrNull()
