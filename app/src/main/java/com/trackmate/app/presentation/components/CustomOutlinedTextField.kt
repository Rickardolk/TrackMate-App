package com.trackmate.app.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackmate.app.R

@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier ,
    value: String ,
    onValueChange: (String) -> Unit ,
    label: String ,
    placeHolder: String,
    leadingIconRes: Int ,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false)}

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = if (isPassword) {
            keyboardOptions.copy(keyboardType = KeyboardType.Password)
        } else {
            keyboardOptions
        },
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF141718),
            unfocusedBorderColor = Color.Transparent,
            focusedLeadingIconColor = Color(0xFF141718),
            unfocusedLeadingIconColor = Color(0xFFC2C3CB),
            focusedTextColor = Color(0xFF141718),
            unfocusedTextColor = Color(0xFFC2C3CB),
            unfocusedPlaceholderColor = Color(0xFFC2C3CB),
            focusedLabelColor = Color(0xFF141718),
            unfocusedLabelColor = Color(0xFFC2C3CB)
        ),
        leadingIcon = {
            Icon(
                painter = painterResource(id = leadingIconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingIcon = if (isPassword) {
            {
                val trailingColor = if (passwordVisible) {
                    Color(0xFF141718)
                } else {
                    Color(0xFFC2C3CB)
                }
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_eye),
                        contentDescription = null,
                        tint = trailingColor
                    )
                }
            }
        } else null,
        singleLine = true,
        placeholder = {
            Text(
                text = placeHolder,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }




    )
}