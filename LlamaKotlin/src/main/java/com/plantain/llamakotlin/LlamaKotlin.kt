package com.plantain.llamakotlin

class LlamaKotlin {

    /**
     * A native method that is implemented by the 'llamakotlin' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun initBackend()

    external fun systemInfo(): String

    external fun loadModel(modelPath: String): Int

    external fun prepareContext(nCtx: Int): Int

    external fun chat(prompt: String): String

    external fun release()

    external fun shutdown()

    companion object {
        // Used to load the 'llamakotlin' library on application startup.
        init {
            System.loadLibrary("llamakotlin")
        }
    }
}
