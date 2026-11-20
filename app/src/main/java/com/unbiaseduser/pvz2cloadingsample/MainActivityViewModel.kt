package com.unbiaseduser.pvz2cloadingsample

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _isOverlayVisible: MutableStateFlow<Boolean> = savedStateHandle.getMutableStateFlow("isOverlayVisible", true)

    val isOverlayVisibleFlow: StateFlow<Boolean> = _isOverlayVisible.asStateFlow()

    var isOverlayVisible: Boolean by _isOverlayVisible::value

}