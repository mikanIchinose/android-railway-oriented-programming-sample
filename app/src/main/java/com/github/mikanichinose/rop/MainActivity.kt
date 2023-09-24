package com.github.mikanichinose.rop

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.mikanichinose.rop.ui.theme.ResultSampleTheme
import io.getstream.result.extractCause

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResultSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {
    val repositories = viewModel.repositories.collectAsState()
    val issues = viewModel.issues.collectAsState()
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LaunchButtonRow(
            onClickRepositoryButton = {
                viewModel.clearIssues()
                viewModel.getMyRepositories(
                    onStart = {
                        Toast.makeText(
                            context,
                            "Start",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Success",
                            Toast.LENGTH_SHORT
                        ).show()

                    },
                    onError = {
                        it.extractCause()?.printStackTrace()
                        Toast.makeText(
                            context,
                            "Error ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                )
            },
            onClickIssueButton = {
                viewModel.clearRepositories()
                viewModel.getIssueFlow(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Success",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onError = {
                        Toast.makeText(
                            context,
                            "Error ${it.javaClass.simpleName} ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                )
//                viewModel.getIssues(
//                    onLog1 = {
//                        Toast.makeText(
//                            context,
//                            "start $it",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    },
//                    onLog2 = {
//                        Toast.makeText(
//                            context,
//                            "start $it",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    },
//                    onLog3 = {
//                        Toast.makeText(
//                            context,
//                            "start $it",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    },
//                    onSuccess = {
//                        Toast.makeText(
//                            context,
//                            "Success",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    },
//                    onError = {
//                        when (it) {
//                            is Error.GenericError -> {}
//                            is Error.NetworkError -> {}
//                            is Error.ThrowableError -> {}
//                        }
//                        Toast.makeText(
//                            context,
//                            "Error ${it.message}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    },
//                )
            }
        )

        when {
            repositories.value.isNotEmpty() -> {
                RepositoryList(repositories = repositories.value)
            }

            issues.value.isNotEmpty() -> {
                IssueList(issues = issues.value)
            }
        }
    }
}

@Composable
fun LaunchButtonRow(
    onClickRepositoryButton: () -> Unit,
    onClickIssueButton: () -> Unit,
) {
    Row {
        LaunchButton(
            text = "Get My Repositories",
            onClick = onClickRepositoryButton,
        )
        LaunchButton(
            text = "Get My Issues",
            onClick = onClickIssueButton,
        )
    }
}

@Composable
fun LaunchButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(onClick = onClick) {
        Text(text = text)
    }
}

@Composable
fun RepositoryList(
    repositories: List<RepositoryModel>
) {
    LazyColumn {
        items(repositories, { it.id }) { repository ->
            Card(modifier = Modifier.padding(8.dp)) {
                AsyncImage(
                    model = repository.ogpImageUrl,
                    contentDescription = "${repository.name}'s OGP",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            }
        }
    }
}

@Composable
fun IssueList(
    issues: List<GithubIssueJson>
) {
    LazyColumn {
        items(issues, { it.id }) { issue ->
            Card(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = issue.title,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun LaunchButtonPreview() {
    Surface {
        LaunchButton("launch", onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ResultSampleTheme {
        Greeting("Android")
    }
}