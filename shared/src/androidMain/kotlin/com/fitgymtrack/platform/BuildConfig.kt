package com.fitgymtrack.platform

import com.fitgymtrack.androidApp.BuildConfig

object AndroidBuildConfig {
    val versionName: String
        get() = BuildConfig.VERSION_NAME

    val versionCode: Int
        get() = BuildConfig.VERSION_CODE
}
