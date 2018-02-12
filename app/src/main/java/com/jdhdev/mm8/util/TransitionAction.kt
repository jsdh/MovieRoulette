package com.jdhdev.mm8.util

import android.transition.Transition


open class TransitionAction : Transition.TransitionListener {
    override fun onTransitionEnd(p0: Transition?) = Unit
    override fun onTransitionResume(p0: Transition?) = Unit
    override fun onTransitionPause(p0: Transition?) = Unit
    override fun onTransitionCancel(p0: Transition?) = Unit
    override fun onTransitionStart(p0: Transition?) = Unit
}