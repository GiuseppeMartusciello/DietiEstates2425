package com.example.dietiestates.ui.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.theme.RobotoMono
import com.example.dietiestates.ui.theme.RobotoSlab


@Composable
fun CustomButton(
    onClick: () -> Unit,
    text: String? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    style: String,
    enabled: Boolean = true
) {
    val backgroundColor = if (style == "white") Color.White else Color(0xFF3F51B5)
    val contentColor = if (style == "white") Color(0xFF3F51B5) else Color.White
    val border = if (style == "white") BorderStroke(1.dp, Color(0xFF9E9E9E)) else null

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
        shape = RoundedCornerShape(8.dp),
        border = border,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(28.dp))
        }
        if (text != null) {
            if (icon != null) Spacer(modifier = Modifier.width(4.dp))
            Text(text, fontFamily = RobotoMono, fontWeight = FontWeight.Medium, lineHeight = 28.sp, fontSize = 18.sp)
        }
    }
}

@Composable
fun dropDownMenu(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.featureTitle,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        SimpleDropdownSelector(
            options = options,
            selectedOption = value,
            onOptionSelected = { onValueChange(it) },
            modifier = modifier
        )
    }
}

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    maxChars: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp)

    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.featureTitle,
            fontSize = 16.sp,
        )

        TextField(
            value = value,
            onValueChange = {
                if (it.length <= maxChars) {
                    onValueChange(it)
                }
            },
            textStyle = TextStyle(
                fontFamily = RobotoSlab,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            ),
            isError = isError,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            modifier = modifier
                .height(48.dp)
                .fillMaxWidth()
        )
        Text(
            text = "${value.length} / $maxChars",
            fontSize = 12.sp,
            color = if (value.length >= maxChars) Color.Red else Color.Gray,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun LabeledNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    maxChars: Int,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.featureTitle,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Column() {
            TextField(
                value = value,
                onValueChange = { input ->
                    if (input.all { it.isDigit() } && input.length <= maxChars) {
                        onValueChange(input)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = RobotoSlab,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp
                ),
                isError = isError,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                modifier = modifier
                    .height(48.dp)
                    .width(120.dp)
            )
            Text(
                text = "${value.length} / $maxChars",
                fontSize = 12.sp,
                color = if (value.length >= maxChars) Color.Red else Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
@Composable
fun LabeledCheckBoxField(
    label: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit
){
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.featureTitle,
            fontSize = 16.sp
        )
        Spacer(Modifier.weight(1f))
        Checkbox(
            checked = value,
            onCheckedChange = { onValueChange(it) },
        )
    }
}


@Composable
fun BottomBar(
    navController: NavController,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Surface(
        modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(bottom = navBarPadding)
            .border(1.dp, Color(0xFF9E9E9E), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = Color.White,
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            CustomButton(
                text = "Annulla",
                style = "white",
                icon = Icons.Outlined.Close,
                onClick = { navController.navigateUp() },
                modifier = Modifier.width(140.dp)
            )

            CustomButton(
                text = "Salva",
                icon = Icons.Outlined.Save,
                style = "blue",
                onClick = { onClick() },
                modifier = Modifier.width(140.dp)
            )

        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SimpleDropdownSelector(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },

        ) {
        OutlinedTextField(
            value = selectedOption.toString(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = modifier
                .menuAnchor()
                .width(80.dp)
                .height(50.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}



