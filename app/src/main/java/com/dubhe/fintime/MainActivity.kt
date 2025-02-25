package com.dubhe.fintime

import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.dubhe.fintime.ui.theme.FInTimeTheme

class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView  // WebView 인스턴스를 저장
    private var backPressedTime: Long = 0  // 뒤로 가기 버튼을 누른 시간 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 뒤로 가기 버튼 동작 설정
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (::webView.isInitialized && webView.canGoBack()) {
                    val currentUrl = webView.url ?: ""
                    if (currentUrl == "https://fin-time.com/main") {
                        exitApp()
                    } else {
                        webView.goBack()  // WebView에서 뒤로 가기
                    }
                } else {
                    exitApp()
                }
            }
        })

        setContent {
            FInTimeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebViewComponent(
                        modifier = Modifier.padding(innerPadding),
                        webViewProvider = { webView = it } // WebView 인스턴스 저장
                    )
                }
            }
        }
    }

    fun exitApp() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            finish()  // 2초 이내에 두 번 누르면 앱 종료
        } else {
            backPressedTime = currentTime
            Toast.makeText(
                this@MainActivity,
                "한 번 더 누르면 종료됩니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
fun WebViewComponent(modifier: Modifier = Modifier, webViewProvider: (WebView) -> Unit) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.javaScriptEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.domStorageEnabled = true
            settings.setSupportZoom(false)
            settings.builtInZoomControls = false

            setWebContentsDebuggingEnabled(true)

            settings.allowContentAccess = true
            settings.allowFileAccess = true

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    view?.loadUrl(request?.url.toString())
                    return true
                }
            }

            webChromeClient = WebChromeClient()
            loadUrl("https://fin-time.com")
        }
    }

    // WebView 인스턴스를 MainActivity에 전달
    LaunchedEffect(Unit) {
        webViewProvider(webView)
    }

    AndroidView(
        factory = { webView },
        modifier = modifier
    )
}