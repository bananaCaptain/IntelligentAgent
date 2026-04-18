package com.plantain.intelligentagent.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.plantain.intelligentagent.R
import com.plantain.intelligentagent.data.aidl.IntelligentServiceDataSource
import com.plantain.intelligentagent.data.remote.QwenApiProvider
import com.plantain.intelligentagent.data.remote.ZaiApiProvider
import com.plantain.intelligentagent.data.repository.ModelRepository

class MainActivity : AppCompatActivity() {

    private val intelligentServiceDataSource = IntelligentServiceDataSource()

    private val sharedViewModel: MainViewModel by viewModels {
        MainViewModel.getMainViewModelFactory(
            ModelRepository(
                qwenDataSource = QwenApiProvider.createDataSource(apiKey = "YOUR_QWEN_API_KEY"),
                zaiDataSource = ZaiApiProvider.createDataSource(apiKey = "YOUR_ZAI_API_KEY"),
                intelligentServiceDataSource = intelligentServiceDataSource
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //初始化共享ViewModel
        sharedViewModel
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_host_fragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val extFilesDir = getExternalFilesDir(null)
        val qwen2BModel = "Qwen3.5-2B-Q4_K_M.gguf"
        val modelPath = extFilesDir!!.absolutePath + "/" + qwen2BModel
        //检查模型文件是否存在
        val modelFile = java.io.File(modelPath)
        if(modelFile.exists().not()) {
            //打印模型不存在不加载模型
            println("模型文件不存在，路径：$modelPath")
            return
        }
        //加载模型库
        sharedViewModel.loadLocalModel(modelPath)
        sharedViewModel.loadServiceLlamaModel(modelPath)

    }

    override fun onStart() {
        super.onStart()
        intelligentServiceDataSource.bind(this)
    }

    override fun onStop() {
        intelligentServiceDataSource.unbind(this)
        super.onStop()
    }
}
