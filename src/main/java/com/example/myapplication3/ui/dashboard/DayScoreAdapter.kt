package com.example.myapplication3.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.myapplication3.R
import java.util.Calendar  // 导入 Calendar 类

class DayScoreAdapter(
    private val context: Context,
    private val dates: List<Int>,  // 日期列表
    private val scores: MutableList<Int>  // 可变分数列表，方便在适配器中更新
) : BaseAdapter() {

    private val today = Calendar.getInstance()
    private val todayDay = today.get(Calendar.DAY_OF_MONTH)  // 获取今天的日期
    private val todayMonth = today.get(Calendar.MONTH) + 1   // 获取当前月份 (1-12)
    private val todayYear = today.get(Calendar.YEAR)         // 获取当前年份 (YYYY)

    override fun getCount(): Int = dates.size

    override fun getItem(position: Int): Any = dates[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // 获取视图，如果复用视图为空，则创建新的视图
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.day_score_item, parent, false)

        // 获取日期和分数的 TextView 控件
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)

        // 获取当前日期和对应的分数
        val date = dates[position]
        val score = scores[position]

        // 设置日期和分数
        dateTextView.text = date.toString()  // 显示日期
        scoreTextView.text = score.toString()  // 显示分数

        // 判断是否是今天
        if (date == todayDay && todayMonth == Calendar.getInstance().get(Calendar.MONTH) + 1 && todayYear == Calendar.getInstance().get(Calendar.YEAR)) {
            // 如果是今天，设置背景色为浅绿色
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.orangelight))  // 使用 ContextCompat 获取颜色
        } else {
            // 如果不是今天，恢复默认背景颜色
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        return view
    }
}
