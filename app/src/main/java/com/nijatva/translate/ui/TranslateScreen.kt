package com.nijatva.translate.ui

import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nijatva.translate.R
import com.nijatva.translate.data.Language
import com.nijatva.translate.data.Languages
import kotlinx.coroutines.launch
import java.util.Locale

private enum class PickerTarget { Source, Target }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateScreen(
    viewModel: TranslateViewModel,
    onOpenSettings: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val speak = rememberSpeaker()

    var picker by remember { mutableStateOf<PickerTarget?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = stringResource(R.string.tagline),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            LanguageBar(
                source = state.source,
                target = state.target,
                canSwap = state.canSwap,
                onPickSource = { picker = PickerTarget.Source },
                onPickTarget = { picker = PickerTarget.Target },
                onSwap = viewModel::onSwap,
            )

            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = state.input,
                onValueChange = viewModel::onInputChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 148.dp),
                shape = RoundedCornerShape(20.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.input_hint),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                trailingIcon = {
                    if (state.input.isNotEmpty()) {
                        IconButton(onClick = viewModel::onClearInput) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.clear_input),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = viewModel::translate,
                enabled = state.canTranslate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text = stringResource(R.string.translating),
                        style = MaterialTheme.typography.labelLarge,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Translate,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text = stringResource(R.string.translate),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            state.error?.let { message ->
                Spacer(Modifier.height(14.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(24.dp))

            ResultCard(
                text = state.output,
                targetLanguage = state.target,
                onCopy = {
                    clipboard.setText(AnnotatedString(state.output))
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.copied))
                    }
                },
                onShare = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, state.output)
                    }
                    context.startActivity(Intent.createChooser(send, null))
                },
                onSpeak = { speak(state.output, state.target.code) },
            )
        }
    }

    when (picker) {
        PickerTarget.Source -> LanguagePickerSheet(
            title = stringResource(R.string.source_language),
            languages = Languages.sourceOptions,
            selected = state.source,
            onDismiss = { picker = null },
            onSelect = { language ->
                viewModel.onSourceSelected(language)
                picker = null
            },
        )

        PickerTarget.Target -> LanguagePickerSheet(
            title = stringResource(R.string.target_language),
            languages = Languages.all,
            selected = state.target,
            onDismiss = { picker = null },
            onSelect = { language ->
                viewModel.onTargetSelected(language)
                picker = null
            },
        )

        null -> Unit
    }
}

@Composable
private fun LanguageBar(
    source: Language,
    target: Language,
    canSwap: Boolean,
    onPickSource: () -> Unit,
    onPickTarget: () -> Unit,
    onSwap: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        LanguageSelector(
            modifier = Modifier.weight(1f),
            caption = stringResource(R.string.source_language),
            language = source,
            onClick = onPickSource,
        )

        IconButton(onClick = onSwap, enabled = canSwap) {
            Icon(
                imageVector = Icons.Filled.SwapHoriz,
                contentDescription = stringResource(R.string.swap_languages),
                tint = if (canSwap) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                },
            )
        }

        LanguageSelector(
            modifier = Modifier.weight(1f),
            caption = stringResource(R.string.target_language),
            language = target,
            onClick = onPickTarget,
        )
    }
}

@Composable
private fun LanguageSelector(
    modifier: Modifier = Modifier,
    caption: String,
    language: Language,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Text(
            text = caption.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = language.label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ResultCard(
    text: String,
    targetLanguage: Language,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSpeak: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.result).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = targetLanguage.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(12.dp))

            val placeholder = stringResource(R.string.result_placeholder)
            Text(
                text = text.ifBlank { placeholder },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (text.isBlank()) FontWeight.Normal else FontWeight.Medium,
                color = if (text.isBlank()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )

            if (text.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ActionButton(Icons.Filled.ContentCopy, stringResource(R.string.copy), onCopy)
                    ActionButton(Icons.Filled.Share, stringResource(R.string.share), onShare)
                    ActionButton(Icons.Filled.VolumeUp, stringResource(R.string.speak), onSpeak)
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguagePickerSheet(
    title: String,
    languages: List<Language>,
    selected: Language,
    onDismiss: () -> Unit,
    onSelect: (Language) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var query by remember { mutableStateOf("") }

    val filtered = remember(query, languages) {
        languages.filter { it.matches(query) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.search_languages)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )

            Spacer(Modifier.height(8.dp))

            if (filtered.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_languages),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp),
                )
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 420.dp)) {
                    items(filtered, key = { it.code }) { language ->
                        LanguageRow(
                            language = language,
                            isSelected = language.code == selected.code,
                            onClick = { onSelect(language) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageRow(
    language: Language,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = language.label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
            if (!language.nativeName.equals(language.label, ignoreCase = false)) {
                Text(
                    text = language.englishName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = language.code.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Small helper that speaks a translation with the platform text-to-speech engine. */
@Composable
private fun rememberSpeaker(): (String, String) -> Unit {
    val context = LocalContext.current
    val engine = remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val created = TextToSpeech(context) { _ -> }
        engine.value = created
        onDispose {
            created.stop()
            created.shutdown()
            engine.value = null
        }
    }

    return remember {
        { text: String, languageTag: String ->
            val speaker = engine.value
            if (speaker != null && text.isNotBlank()) {
                speaker.language = Locale.forLanguageTag(languageTag)
                speaker.speak(text, TextToSpeech.QUEUE_FLUSH, null, "nijatva-translate")
            }
        }
    }
}
