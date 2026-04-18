package com.plantain.intelligentservice

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.plantain.llamakotlin.LlamaKotlin
import java.io.File

class MainActivity : AppCompatActivity() {

    private val llamaKotlin by lazy { LlamaKotlin() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnLoadModel = findViewById<Button>(R.id.btnLoadModel)
        val tvLoadResult = findViewById<TextView>(R.id.tvLoadResult)

        btnLoadModel.setOnClickListener {
            val extFilesDir = getExternalFilesDir(null)
            val modelName = "Qwen3.5-2B-Q4_K_M.gguf"
            val modelPath = extFilesDir?.absolutePath + "/" + modelName
            val modelFile = File(modelPath)

            if (!modelFile.exists()) {
                tvLoadResult.text = "模型不存在: $modelName"
                return@setOnClickListener
            }

            runCatching {
                llamaKotlin.initBackend()
                val loadCode = llamaKotlin.loadModel(modelPath)
                if (loadCode != 0) {
                    tvLoadResult.text = "加载失败 code=$loadCode"
                    return@runCatching
                }

                val ctxCode = llamaKotlin.prepareContext(2048)
                tvLoadResult.text = if (ctxCode == 0) {
                    "加载成功: $modelName"
                } else {
                    "上下文初始化失败 code=$ctxCode"
                }
            }.onFailure { error ->
                tvLoadResult.text = "加载异常: ${error.message}"
            }
        }
    }

    override fun onDestroy() {
        runCatching {
            llamaKotlin.release()
            llamaKotlin.shutdown()
        }
        super.onDestroy()
    }
}
