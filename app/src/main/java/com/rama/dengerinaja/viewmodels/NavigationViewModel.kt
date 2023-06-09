package com.rama.dengerinaja.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rama.dengerinaja.models.MainNavigationAction
import com.rama.dengerinaja.models.MetaData

class NavigationViewModel: ViewModel() {

    private val _mainNavigationAction = MutableLiveData<MainNavigationAction?>()
    val mainNavigationAction: LiveData<MainNavigationAction?>
        get() = _mainNavigationAction

    private val _homeNavigationAction = MutableLiveData<MetaData?>()
    val homeNavigationAction: LiveData<MetaData?>
        get() = _homeNavigationAction

    fun mainNavigateTo(action: MainNavigationAction) {
        if (_mainNavigationAction.value != null)
            return
        _mainNavigationAction.postValue(action)
    }

    fun finishMainNavigation() {
        _mainNavigationAction.postValue(null)
    }

    fun homeNavigateTo(metaData: MetaData) {
        if (_homeNavigationAction.value != null)
            return
        _homeNavigationAction.postValue(metaData)
    }

    fun finishHomeNavigation() {
        _homeNavigationAction.postValue(null)
    }
}