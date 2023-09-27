package com.github.mikanichinose.rop.ui.theme

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.github.mikanichinose.rop.LoadingState
import com.github.mikanichinose.rop.MainViewModel
import com.github.mikanichinose.rop.domain.model.RopError

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val result by viewModel.result.collectAsState()
        val loading by viewModel.loading.collectAsState()
        var color by remember {
            mutableStateOf(Color.Black)
        }
        var errorColor by remember {
            mutableStateOf(Color.Red)
        }

        LaunchedEffect(loading) {
            color = if (loading == LoadingState.LOADING) Color.Gray else Color.Black
            errorColor = if (loading == LoadingState.LOADING) Color.Gray else Color.Red
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            result.onSuccess {
                Text(
                    color = color,
                    text = it.title,
                )
            }.onFailure {
                ErrorScreen(error = it, errorColor = errorColor)
            }

            if (loading == LoadingState.LOADING) {
                CircularProgressIndicator()
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            var text by remember {
                mutableStateOf("")
            }
            val context = LocalContext.current
            val keyboardController = LocalSoftwareKeyboardController.current
            SearchField(
                text = text,
                onTextChanged = { text = it },
                onSearchButtonClick = {
                    keyboardController?.hide()
                    viewModel.getRandomIssue(
                        query = text,
                        onValidationError = {
                            Toast.makeText(
                                context,
                                "リポジトリ名を入力してください",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                },
            )
        }
    }
}

@Composable
fun SearchField(
    text: String,
    onTextChanged: (String) -> Unit,
    onSearchButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { onSearchButtonClick() }
            ),
            placeholder = {
                Text(text = "リポジトリ名")
            }
        )
        Spacer(modifier = Modifier.size(16.dp))
        IconButton(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            onClick = onSearchButtonClick,
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.White)
        }
    }

}

@Composable
fun ErrorScreen(
    error: RopError,
    errorColor: Color
) {
    when (error) {
        RopError.RepositoryNotFoundError -> Text(
            color = errorColor,
            text = "リポジトリが見つかりませんでした",
        )

        RopError.IssueNotFoundError -> Text(
            color = errorColor,
            text = "Issueが見つかりませんでした",
        )

        is RopError.UnknownError -> Text(
            color = errorColor,
            text = "予期せぬエラーが発生しました: ${error.cause}",
        )

        RopError.BlankQueryError -> return
    }
}
