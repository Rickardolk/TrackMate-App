package com.trackmate.app.presentation.screens.profile

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Colors ───────────────────────────────────────────────────────────────────

private val BackgroundGray  = Color(0xFFF2F2F7)
private val CardWhite       = Color(0xFFFFFFFF)
private val TextPrimary     = Color(0xFF1C1C1E)
private val TextSecondary   = Color(0xFF8E8E93)
private val IconBg          = Color(0xFFEEEEEE)
private val DividerColor    = Color(0xFFD1D1D6)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    userName: String = "Praboros Subiantolol",
    userEmail: String = "praboros@gmail.com",
    onEditProfile: () -> Unit = {},
    onNotificationPermission: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onShareApp: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
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

            // ── Section: Pengaturan Akun ──────────────────────────────────
            SectionLabel(text = "Pengaturan Akun")
            Spacer(modifier = Modifier.height(10.dp))

            ProfileUserCard(
                name = userName,
                email = userEmail,
                onClick = onEditProfile
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Section: Pengaturan Aplikasi ──────────────────────────────
            SectionLabel(text = "Pengaturan Aplikasi")
            Spacer(modifier = Modifier.height(10.dp))

            SettingItemCard(
                icon = Icons.Default.Notifications,
                label = "Izin Notifikasi",
                onClick = onNotificationPermission
            )
            Spacer(modifier = Modifier.height(10.dp))
            SettingItemCard(
                icon = Icons.Default.Lock,
                label = "Ubah Kata Sandi",
                onClick = onChangePassword
            )
            Spacer(modifier = Modifier.height(10.dp))
            SettingItemCard(
                icon = Icons.Filled.Share,
                label = "Bagikan Aplikasi",
                onClick = onShareApp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Divider ───────────────────────────────────────────────────
            HorizontalDivider(color = DividerColor, thickness = 1.dp)

            Spacer(modifier = Modifier.height(24.dp))

            // ── Log Out Button ────────────────────────────────────────────
            Button(
                onClick = onLogOut,
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
                Text(
                    text = "Log Out",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar() {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Profil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
    )
}

// ─── Section Label ────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    )
}

// ─── Profile User Card ────────────────────────────────────────────────────────

@Composable
private fun ProfileUserCard(
    name: String,
    email: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar placeholder — replace painter with your actual image resource
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(IconBg),
                contentAlignment = Alignment.Center
            ) {
                // Uncomment when you have a real image resource:
                // Image(
                //     painter = painterResource(id = R.drawable.profile_photo),
                //     contentDescription = "Foto Profil",
                //     modifier = Modifier.fillMaxSize(),
                //     contentScale = ContentScale.Crop
                // )
                Text(
                    text = name.take(1).uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = email,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
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

// ─── Setting Item Card ────────────────────────────────────────────────────────

@Composable
private fun SettingItemCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(IconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
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

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen()
    }
}