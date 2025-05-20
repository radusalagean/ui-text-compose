package com.radusalagean.uitextcompose.multiplatform.sample.util

import platform.Foundation.NSLocale
import platform.Foundation.NSURL
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

class LanguageManagerIOS : LanguageManager {
    override fun getCurrentLanguageCode(): String {
        return NSLocale.currentLocale.languageCode()
    }

    override fun onLanguageSelected(code: String) {
        val url = NSURL(string = UIApplicationOpenSettingsURLString)
        val application = UIApplication.sharedApplication
        if (application.canOpenURL(url)) {
            application.openURL(
                url,
                options = emptyMap<Any?, Any>(),
                completionHandler = null
            )
        }
    }
}