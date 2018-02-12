package com.jdhdev.mm8.util

import io.reactivex.Scheduler


interface BaseSchedulerProvider {
    fun computation(): Scheduler
    fun io(): Scheduler
    fun ui(): Scheduler
}
