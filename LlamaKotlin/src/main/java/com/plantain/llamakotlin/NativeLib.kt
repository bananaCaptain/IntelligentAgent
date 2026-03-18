package com.plantain.llamakotlin

class NativeLib {

    /**
     * A native method that is implemented by the 'llamakotlin' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'llamakotlin' library on application startup.
        init {
            System.loadLibrary("llamakotlin")
        }
    }
}