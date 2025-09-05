package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.japanesehelper.presentation.viewmodel.VocabViewModel
import com.japanesehelper.presentation.viewmodel.screendata.LevelState
import com.japanesehelper.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelDropdownMenu(
    modifier: Modifier = Modifier,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val state by viewModel.homeState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = MenuAnchorType.PrimaryNotEditable,
                    enabled = true
                ),
            value = state.levelState?.name ?: LevelState.RANDOM.name,
            onValueChange = {
                viewModel.updateLevel(LevelState.valueOf(it))
            },
            readOnly = true,
            label = { Text(stringResource(R.string.home_level_selection_caption)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            LevelState.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        viewModel.updateLevel(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
