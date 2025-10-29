//package com.example.appasistencia.utils
//
//import android.content.Context
//import androidx.biometric.BiometricManager
//import androidx.biometric.BiometricPrompt
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.FragmentActivity
//
//object BiometricUtils {
//
//    fun canAuthenticate(context: Context): Boolean {
//        val biometricManager = BiometricManager.from(context)
//        return biometricManager.canAuthenticate(
//            BiometricManager.Authenticators.BIOMETRIC_WEAK or
//                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
//        ) == BiometricManager.BIOMETRIC_SUCCESS
//    }
//
//    fun showBiometricPrompt(
//        activity: FragmentActivity,
//        title: String,
//        subtitle: String,
//        onSuccess: () -> Unit,
//        onFailure: () -> Unit
//    ) {
//        val executor = ContextCompat.getMainExecutor(activity)
//        val promptInfo = BiometricPrompt.PromptInfo.Builder()
//            .setTitle(title)
//            .setSubtitle(subtitle)
//            .setAllowedAuthenticators(
//                BiometricManager.Authenticators.BIOMETRIC_WEAK or
//                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
//            )
//            .build()
//
//        val biometricPrompt = BiometricPrompt(activity, executor,
//            object : BiometricPrompt.AuthenticationCallback() {
//                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//                    onSuccess()
//                }
//                override fun onAuthenticationFailed() {
//                    onFailure()
//                }
//            })
//
//        biometricPrompt.authenticate(promptInfo)
//    }
//}
