package com.trackmate.app.presentation.screens.profile

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trackmate.app.presentation.screens.auth.AuthViewModel
import com.trackmate.app.utils.Resource
import com.trackmate.app.utils.myShadow

private val BackgroundGray  = Color(0xFFF2F2F7)
private val CardWhite       = Color(0xFFFFFFFF)
private val TextPrimary     = Color(0xFF1C1C1E)
private val TextSecondary   = Color(0xFF8E8E93)
private val IconBg          = Color(0xFFEEEEEE)
private val DividerColor    = Color(0xFFD1D1D6)

@Composable
fun ProfileScreenRoute(
    onNavigateToLogin: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.profileUiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.logoutState) {
        if (uiState.logoutState is Resource.Success) {
            viewModel.resetLogoutState()
            onNavigateToLogin()
        }
    }

    ProfileScreen(
        uiState = uiState,
        onEditProfile = onNavigateToEditProfile,
        onLogOut = viewModel::logout
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onEditProfile: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    //AlertDialog logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar Akun") },
            text = { Text("Apakah Anda yakin ingin keluar dari akun ini?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogOut()
                }) {
                    Text("Ya", color = Color(0xFFD94F3D), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Tidak")
                }
            }
        )
    }

    Scaffold(
        topBar = { ProfileTopBar() },
        containerColor = BackgroundGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            SectionLabel(text = "Pengaturan Akun")
            Spacer(modifier = Modifier.height(10.dp))

            ProfileUserCard(
                name = uiState.userName,
                email = uiState.userEmail,
                onClick = onEditProfile
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionLabel(text = "Pengaturan Aplikasi")
            Spacer(modifier = Modifier.height(10.dp))

            SettingItemCard(
                icon = Icons.Default.Notifications,
                label = "Izin Notifikasi",
                onClick = {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SettingItemCard(
                icon = Icons.Default.Lock,
                label = "Ubah Kata Sandi",
                onClick = {  }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SettingItemCard(
                icon = Icons.Filled.Share,
                label = "Bagikan Aplikasi",
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Yuk pantau kendaraanmu secara real-time dengan TrackMate!"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Bagikan TrackMate"))
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(color = DividerColor, thickness = 1.dp)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextPrimary,
                    contentColor = CardWhite
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Log Out",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Log Out", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// Top Bar
@Composable
private fun ProfileTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .myShadow(
                color = Color(0xFF000000).copy(alpha = 0.05f),
                offsetY = 4.dp,
                blurRadius = 8.dp
            )
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 24.dp, bottom = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Profil",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

//Section Label
@Composable
private fun SectionLabel(text: String) {
    Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
}

//Profile User Card
@Composable
private fun ProfileUserCard(
    name: String,
    email: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .myShadow(
                color = Color(0xFF000000).copy(alpha = 0.06f),
                offsetY = 4.dp,
                offsetX = 2.dp,
                blurRadius = 12.dp
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(IconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = email, fontSize = 14.sp, color = TextSecondary)
            }

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Edit Profil",
                tint = TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

//Setting Item Card
@Composable
private fun SettingItemCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .myShadow(
                color = Color(0xFF000000).copy(alpha = 0.06f),
                offsetY = 4.dp,
                offsetX = 2.dp,
                blurRadius = 12.dp
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(IconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = TextPrimary, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}