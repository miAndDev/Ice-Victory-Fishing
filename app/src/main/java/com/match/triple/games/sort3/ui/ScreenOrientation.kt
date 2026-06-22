package com.match.triple.games.sort3.ui

import androidx.compose.runtime.Composable
import com.special.ascs.ComposablePolicyView
import com.special.ascs.utils.PolicyView
import com.match.triple.games.sort3.ActivityViewModel
import com.match.triple.games.sort3.core.base.ClassForName

@Composable
fun ScreenOrientation(
  viewModel: ActivityViewModel
) {
    ComposablePolicyView (
        viewModel.info,
        onHideSpalsh = viewModel::hideSplash,
        onShowMenu = viewModel::showMenu,
        onSave = viewModel::saveInfo,
        checkHeader = "",
        defaultSaver = ClassForName().toString()
    )
}
