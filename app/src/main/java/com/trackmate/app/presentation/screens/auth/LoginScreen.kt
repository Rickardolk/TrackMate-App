package com.trackmate.app.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackmate.app.R
import com.trackmate.app.presentation.components.CustomOutlinedTextField
import com.trackmate.app.utils.Resource
import com.trackmate.app.utils.myShadow

@Composable
fun LoginScreenRoute(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.authUiState.collectAsState()

    LaunchedEffect(uiState.authState) {
        if (uiState.authState is Resource.Success)
            onLoginSuccess()
    }

    LoginScreen(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::login,
        onNavigateToRegister = {
            viewModel.resetAuthState()
            onNavigateToRegister()
        },
        onBackClick = onBackClick
    )

}


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
            .padding(top = 44.dp)
        ) {
            //button back
            Button(
                onClick = onBackClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF141718)
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(0.dp),
                modifier = Modifier
                    .size(45.dp)
                    .myShadow(
                        color = Color(0xFFD3D1D8).copy(alpha = 0.3f) ,
                        borderRadius = 12.dp ,
                        offsetY = 12.dp ,
                        offsetX = 6.dp ,
                        blurRadius = 24.dp
                    ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_keyboard_left),
                    contentDescription = "ic keyboard left",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            Text(
                text = "Masuk dengan\nAkun Anda",
                fontSize = 36.sp,
                lineHeight = 50.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF323142)
            )

            Spacer(modifier = Modifier.height(32.dp))
            Column(modifier.wrapContentSize()) {
                CustomOutlinedTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    label = "Email",
                    placeHolder = "Masukan Email",
                    leadingIconRes = R.drawable.ic_outlined_email
                )
                Spacer(modifier = Modifier.height(12.dp))

                CustomOutlinedTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    label = "Password",
                    placeHolder = "Masukan password",
                    leadingIconRes = R.drawable.ic_outlined_lock,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

                TextButton(
                    onClick = {},
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Lupa Password?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF323142)
                    )
                }

            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onLoginClick,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF141718),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Login",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )

            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Belum memiliki akun?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFACADB9)
                )

                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = onNavigateToRegister,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF323142)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier
                    .height(0.6.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFC2C3CB) ,
                        shape = RoundedCornerShape(16)
                    )
            )

            Spacer(modifier = Modifier.height(18.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Masuk dengan Google",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFACADB9)
                )

                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD44638).copy(0.25f),
                        contentColor = Color(0xFFD44638)
                    ),
                    modifier = Modifier
                        .wrapContentSize(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "GOOGLE",
                        letterSpacing = 2.8.sp,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFD44638),
                        modifier = Modifier
                            .padding(vertical = 12.dp, horizontal = 28.dp)
                    )
                }
            }


        }
    }
}

@Preview
@Composable
private fun View() {
    LoginScreen(
        uiState = AuthUiState(),
        onPasswordChange = {},
        onEmailChange = {},
        onLoginClick = {},
        onNavigateToRegister = {},
        onBackClick = {}
    )

}