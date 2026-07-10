package com.trackmate.app.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trackmate.app.presentation.screens.auth.AuthViewModel
import com.trackmate.app.utils.Resource
import com.trackmate.app.utils.myShadow

private val BackgroundGray = Color(0xFFF2F2F7)
private val CardWhite      = Color(0xFFFFFFFF)
private val TextPrimary    = Color(0xFF1C1C1E)
private val TextSecondary  = Color(0xFF8E8E93)
private val TextHint       = Color(0xFFAAAAAA)
private val DividerColor   = Color(0xFFD1D1D6)
private val AvatarBg       = Color(0xFFDDDDDD)

@Composable
fun ProfileEditScreenRoute(
    onBackClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.profileUiState.collectAsStateWithLifecycle()

    // Kembali otomatis setelah berhasil update
    LaunchedEffect(uiState.updateUsernameState) {
        if (uiState.updateUsernameState is Resource.Success) {
            viewModel.resetUpdateUsernameState()
            onBackClick()
        }
    }

    ProfileEditScreen(
        initialName = uiState.userName,
        onBackClick = onBackClick,
        onPickPhoto = {  },
        onSaveChanges = { newName -> viewModel.updateUsername(newName) }
    )
}

@Composable
fun ProfileEditScreen(
    initialName: String = "Praboros Subiantolol",
    onBackClick: () -> Unit = {},
    onPickPhoto: () -> Unit = {},
    onSaveChanges: (name: String) -> Unit = {}
) {
    var fullName by remember { mutableStateOf(initialName) }

    Scaffold(
        topBar = {
            ProfileEditTopBar(onBackClick = onBackClick)
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // ── Avatar + Camera Button ────────────────────────────────────
            AvatarPicker(
                initials = fullName.take(1).uppercase(),
                onPickPhoto = onPickPhoto
            )

            Spacer(modifier = Modifier.height(36.dp))

            //Nama Lengkap Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nama Lengkap",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = {
                        Text(text = "Masukkan nama lengkap", color = TextHint)
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite,
                        focusedBorderColor = TextPrimary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            HorizontalDivider(color = DividerColor, thickness = 1.dp)

            Spacer(modifier = Modifier.height(28.dp))

            //Save Button
            Button(
                onClick = { onSaveChanges(fullName) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextPrimary,
                    contentColor = CardWhite
                )
            ) {
                Text(
                    text = "Simpan Perubahan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

//Top Bar
@Composable
private fun ProfileEditTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .myShadow(
                color = Color(0xFF000000).copy(alpha = 0.05f),
                offsetY = 4.dp,
                blurRadius = 8.dp
            )
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 24.dp, bottom = 12.dp)
    ) {
        //button back
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp)
                .size(44.dp)
                .myShadow(
                    color = Color(0xFF000000).copy(alpha = 0.06f),
                    offsetY = 4.dp,
                    blurRadius = 12.dp,
                    borderRadius = 12.dp
                )
                .clip(RoundedCornerShape(12.dp))
                .background(CardWhite)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = "Edit Profil",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

//Avatar Picker

@Composable
private fun AvatarPicker(
    initials: String,
    onPickPhoto: () -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        // Avatar circle
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(AvatarBg),
            contentAlignment = Alignment.Center
        ) {
            // Replace with a real Image composable when you have a photo resource:
            // Image(
            //     painter = painterResource(id = R.drawable.profile_photo),
            //     contentDescription = "Foto Profil",
            //     modifier = Modifier.fillMaxSize(),
            //     contentScale = ContentScale.Crop
            // )
            Text(
                text = initials,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Camera button badge
        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(elevation = 4.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(CardWhite)
                .clickable { onPickPhoto() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountBox,
                contentDescription = "Ganti Foto",
                tint = TextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

//Preview

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileEditScreenPreview() {
    MaterialTheme {
        ProfileEditScreen()
    }
}