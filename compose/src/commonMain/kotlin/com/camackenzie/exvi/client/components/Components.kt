package com.camackenzie.exvi.client.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.camackenzie.exvi.client.icons.ExviIcons
import com.camackenzie.exvi.client.rendering.RenderedSpinner
import com.camackenzie.exvi.core.model.ExerciseSet
import com.camackenzie.exvi.core.model.SingleExerciseSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun UsernameField(
    username: String,
    onUsernameChange: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val usernameRegex = Regex("([0-9a-z]|[._-])*")
    TextField(
        modifier = modifier,
        value = username,
        onValueChange = {
            val lower = it.lowercase()
            if (lower.length <= 30
                && lower.matches(usernameRegex)
            ) onUsernameChange(lower)
        },
        label = { Text("Username") },
        placeholder = { Text("Username") },
        enabled = enabled
    )
}

@Composable
fun OptIntField(
    modifier: Modifier = Modifier,
    value: Int?,
    onValueChange: (Int?) -> Unit,
    maxDigits: Int = Int.MAX_VALUE,
    label: @Composable () -> Unit = {},
    placeholder: @Composable () -> Unit = {},
    enabled: Boolean = true
) {
    val regex = Regex("[0-9]*")
    TextField(
        modifier = modifier,
        value = value?.toString() ?: "",
        onValueChange = { setStr ->
            if (setStr.matches(regex) && setStr.length <= maxDigits) {
                onValueChange(if (setStr.isBlank()) null else setStr.toInt())
            }
        },
        label = label,
        placeholder = placeholder,
        enabled = enabled
    )
}

@Composable
fun RepField(
    set: SingleExerciseSet?,
    target: SingleExerciseSet?,
    onValueChange: (Int) -> Unit,
    unit: String,
    modifier: Modifier = Modifier.width(70.dp),
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null
) {
    val reps = if ((set?.reps ?: -1) <= 0) null else set?.reps
    val unitHeader = if (unit.length <= 1) unit
    else "${unit.substring(0, 1).uppercase()}${unit.substring(1)}s"
    OptIntField(
        modifier = modifier,
        value = reps,
        maxDigits = 4,
        onValueChange = {
            onValueChange(it ?: 0)
        },
        label = label ?: { Text(unitHeader) },
        placeholder = placeholder ?: {
            if (target != null)
                Text(target.reps.toString())
        },
        enabled = enabled
    )
}

@Composable
fun RepList(
    exercise: ExerciseSet,
    onValueChange: (Int, Int) -> Unit,
    target: ExerciseSet? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(10.dp, Alignment.Start),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    label: @Composable ((Int) -> Unit)? = null,
    placeholder: @Composable ((Int) -> Unit)? = null,
    contents: @Composable (Int, @Composable () -> Unit) -> Unit = { _, repField -> repField() }
) {
    // TODO: Make this a flow row
    LazyRow(
        modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        val nSets = exercise.sets.size
        items(nSets) { setIdx ->
            contents(setIdx) {
                RepField(
                    set = exercise.sets[setIdx],
                    target = target?.sets?.get(setIdx),
                    unit = exercise.unit,
                    onValueChange = { newReps ->
                        onValueChange(setIdx, newReps)
                    },
                    label = if (label != null) {
                        { label(setIdx) }
                    } else null,
                    placeholder = if (placeholder != null) {
                        { placeholder(setIdx) }
                    } else null,
                )
            }
        }
    }
}

@Composable
fun LoadingIcon(
    modifier: Modifier = Modifier.size(45.dp).padding(6.dp)
) = RenderedSpinner(modifier = modifier)

@Composable
fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    val passwordRegex = Regex("([0-9a-zA-Z]|[*.!@#$%^&(){}\\[\\]:;<>,.?/~_+-=|])*")
    TextField(
        value = password,
        onValueChange = {
            if (it.length <= 30
                && it.matches(passwordRegex)
            ) onPasswordChange(it)
        },
        enabled = enabled,
        label = { Text("Password") },
        placeholder = { Text("Password") },
        visualTransformation = if (passwordVisible && enabled) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible && enabled)
                ExviIcons.Visibility
            else ExviIcons.VisibilityOff
            IconButton(onClick = {
                onPasswordVisibleChange(!passwordVisible)
            }, enabled = enabled) {
                Icon(image, "")
            }
        }
    )
}

@Composable
fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit,
    enabled: Boolean = true
) {
    val emailRegex = Regex("([0-9a-zA-Z]|[.\\-_])*@?([0-9a-zA-Z]|[.\\-_])*")
    TextField(
        enabled = enabled,
        value = email,
        onValueChange = {
            if (it.length <= 40
                && it.matches(emailRegex)
            ) onEmailChange(it)
        },
        label = { Text("Email") },
        placeholder = { Text("user@example.com") }
    )
}

@Composable
fun PhoneField(phone: String, onPhoneChange: (String) -> Unit, enabled: Boolean = true) {
    val phoneRegex = Regex("\\+?([0-9])*")
    TextField(
        enabled = enabled,
        value = phone,
        onValueChange = {
            if (it.length <= 15
                && it.matches(phoneRegex)
            ) onPhoneChange(it)
        },
        label = { Text("Phone Number") },
        placeholder = { Text("Ex. +01234567890") }
    )
}

@Composable
fun VerificationCodeField(code: String, onCodeChange: (String) -> Unit, enabled: Boolean = true) {
    val codeRegex = Regex("\\+?([0-9])*")
    TextField(
        enabled = enabled,
        value = code,
        onValueChange = {
            if (it.length <= 6
                && it.matches(codeRegex)
            ) onCodeChange(it)
        },
        label = { Text("Verification Code") },
        placeholder = { Text("Ex. 123456") }
    )
}

@Composable
fun Expandable(
    modifier: Modifier = Modifier.fillMaxWidth(),
    header: @Composable () -> Unit = {},
    body: @Composable () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val onExpandedChanged: (Boolean) -> Unit = { expanded = it }
    Expandable(expanded, onExpandedChanged, modifier, header, body)
}

@Composable
fun Expandable(
    expanded: Boolean,
    onExpandedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    header: @Composable () -> Unit = {},
    body: @Composable () -> Unit
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.border(1.dp, Color.Black)
                .padding(5.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!expanded) {
                        IconButton(onClick = {
                            onExpandedChanged(true)
                        }) { Icon(Icons.Default.KeyboardArrowDown, "Expand") }
                    } else {
                        IconButton(onClick = {
                            onExpandedChanged(false)
                        }) { Icon(Icons.Default.KeyboardArrowUp, "Retract") }
                    }
                    header()
                }
                if (expanded) {
                    Divider(Modifier.fillMaxWidth())
                    body()
                }
            }
        }
    }
}

@Composable
fun StringSelectionView(
    views: Map<String, @Composable () -> Unit>,
    onCurrentViewChange: (String) -> Unit,
    currentView: String,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier.fillMaxSize(),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top
) = SelectionView(
    views,
    views.keys.associateWith {
        { selected ->
            Button(onClick = {
                onCurrentViewChange(it)
            }, enabled = !selected) { Text(it) }
        }
    },
    currentView,
    coroutineScope,
    modifier,
    horizontalAlignment,
    verticalArrangement
)

@Composable
fun <T> SelectionView(
    views: Map<T, @Composable () -> Unit>,
    headers: Map<T, @Composable (Boolean) -> Unit>,
    currentView: T,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier.fillMaxSize(),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        // TODO: Make this a flow row
        // TODO: Improve selected page styling
        Row(Modifier.fillMaxWidth()) {
            val scrollDiff = 80f
            IconButton(onClick = {
                coroutineScope.launch {
                    scrollState.animateScrollBy(-scrollDiff)
                }
            }) { Icon(ExviIcons.ArrowLeft, "Scroll left") }
            IconButton(onClick = {
                coroutineScope.launch {
                    scrollState.animateScrollBy(scrollDiff)
                }
            }) { Icon(ExviIcons.ArrowRight, "Scroll right") }
            Row(
                Modifier.fillMaxWidth().horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                verticalAlignment = Alignment.Top
            ) {
                for ((view, comp) in headers)
                    Box(contentAlignment = Alignment.Center) { comp(view == currentView) }
            }
        }
        views[currentView]?.invoke()
    }
}

@Composable
fun ExviBox(
    modifier: Modifier? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier?.then(
            Modifier.border(1.dp, Color.Black)
                .padding(10.dp)
        ) ?: Modifier.border(1.dp, Color.Black).padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}