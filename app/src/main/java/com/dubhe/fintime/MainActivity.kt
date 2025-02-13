package com.dubhe.fintime

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.dubhe.fintime.ui.theme.FInTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FInTimeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // innerPadding을 WebViewComponent로 전달
                    WebViewComponent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WebViewComponent(modifier: Modifier = Modifier) {
    val webView = WebView(LocalContext.current).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        settings.javaScriptEnabled = true  // 자바스크립트 활성화
        settings.useWideViewPort = true  // 뷰포트 사용 가능하도록 설정
        settings.loadWithOverviewMode = true  // 화면에 맞게 컨텐츠 조정
        settings.domStorageEnabled = true  // 로컬 저장소 사용 가능하도록 설정
        settings.setSupportZoom(false)  // 줌 비활성화
        settings.builtInZoomControls = false  // 줌 컨트롤 비활성화

        // WebView 디버깅 활성화
        setWebContentsDebuggingEnabled(true)

        // WebView의 높이 문제 해결
        settings.allowContentAccess = true
        settings.allowFileAccess = true

        // WebViewClient 설정 (새로운 페이지를 WebView 내에서 열도록)
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
        }

        // WebChromeClient 설정 (로딩 상태를 추적하고 처리할 수 있음)
        webChromeClient = WebChromeClient()

        // URL 로드
        loadUrl("https://fin-time.com")
    }

// WebView를 AndroidView로 래핑하여 사용
    AndroidView(
        factory = { webView },
        modifier = modifier
    )
}