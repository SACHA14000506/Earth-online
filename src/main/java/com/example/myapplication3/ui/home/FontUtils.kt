package com.example.myapplication3.ui.home

import android.content.Context
import android.graphics.Typeface
import android.util.Log

object FontUtils {
    private val typefaceCache = mutableMapOf<String, Typeface>()
    private const val TAG = "FontUtils"

    fun getTypeface(context: Context, fontPath: String): Typeface {
        return typefaceCache[fontPath] ?: run {
            try {
                Typeface.createFromAsset(context.assets, fontPath).also {
                    typefaceCache[fontPath] = it
                }
            } catch (e: Exception) {
                Log.e(TAG, "加载字体失败: ${e.message}")
                Typeface.DEFAULT
            }
        }
    }

    // 可选：应用启动时预加载
    fun preloadFonts(context: Context) {
        listOf("fonts/ZCOOLKuaiLe-Regular.ttf").forEach {
            getTypeface(context, it)
        }
    }
}