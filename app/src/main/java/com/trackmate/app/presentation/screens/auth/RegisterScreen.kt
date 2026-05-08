package com.trackmate.app.presentation.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackmate.app.R
import com.trackmate.app.presentation.components.CustomTextField
import com.trackmate.app.presentation.theme.TrackMateTheme
import com.trackmate.app.utils.Resource

@Composable
fun RegisterScreenRoute(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is Resource.Success)
            onRegisterSuccess
    }

    RegisterScreen(
        username = viewModel.username,
        email = viewModel.email,
        password = viewModel.password,
        authState = authState,
        onUsernameChange = viewModel::onUsernameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRegisterClick = viewModel::register,
        onNavigateToLogin = {
            viewModel.resetAuthState()
            onNavigateToLogin()
        }
    )
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    username: String,
    authState: Resource<String>?,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier.height(48.dp))

        //logo
        Image(
            painter = painterResource(R.drawable.tiny_blue_logo),
            contentDescription = null,
            modifier = Modifier
                .height(38.dp)
                .align(Alignment.Start)
        )

        Spacer(modifier.height(24.dp))
        //text
        Column(
            modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Buat Akun Baru",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Pantau kendaraan anda dengan TrackMate",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier.height(26.dp))
        //form
        Column(
            modifier
                .fillMaxWidth()
        ){
            //username
            Text(
                text = "Username",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier.height(8.dp))
            CustomTextField(
                value = username,
                onValueChange = onUsernameChange,
                placeholder = "Type Username",
                leadingIcon = painterResource(R.drawable.ic_person)
            )

            Spacer(modifier.height(16.dp))

            //email
            Text(
                text = "Email",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier.height(8.dp))
            CustomTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = "Type Email",
                leadingIcon = painterResource(R.drawable.ic_email)
            )

            Spacer(modifier.height(16.dp))
            //password
            Text(
                text = "Password",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier.height(8.dp))
            CustomTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = "Type Password",
                leadingIcon = painterResource(R.drawable.ic_lock),
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier.height(8.dp))

            //error message
            if (authState is Resource.Error) {
                Row(
                    modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = authState.message,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }


        }

        Spacer(modifier.height(16.dp))
        //term text
        val termsText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                append("Dengan mendaftar, Anda menyetujui")
            }
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)) {
                append("Syarat & Ketentuan")
            }
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                append("serta Kebijakan Privasi kami.")
            }
        }
        Text(
            text = termsText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier.height(32.dp))

        //Button register
        Button(
            onClick = onRegisterClick,
            shape = RoundedCornerShape(24.dp) ,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = authState !is Resource.Loading
        ) {
            Text(
                text = "Daftar",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }


        Spacer(modifier.height(24.dp))

        Row(
            modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sudah punya akun?",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "Login",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable {
                        onNavigateToLogin()
                    }
            )
        }





    }








}


@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun View() {
    TrackMateTheme {
        RegisterScreen(
            username = "",
            email = "",
            password = "",
            authState = null,
            onUsernameChange = { },
            onEmailChange = { },
            onPasswordChange = { },
            onRegisterClick = { },
            onNavigateToLogin = { }
        )
    }
}