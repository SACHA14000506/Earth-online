package com.example.myapplication3.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication3.R
import android.content.SharedPreferences
import java.util.*
import android.util.Log

class DashboardFragment : Fragment() {

    private lateinit var gridView: GridView
    private lateinit var yearText: TextView
    private lateinit var monthText: TextView
    private lateinit var totalScoreLabel: TextView
    private lateinit var dailyScoreLabel: TextView

    private lateinit var prefs: SharedPreferences

    private val calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentMonth = calendar.get(Calendar.MONTH) + 1  // 月份从 0 开始，所以加 1
    private val dates = getDatesInMonth(currentMonth, currentYear) // 获取当前月份的所有日期
    private val scores = MutableList(dates.size) { 0 }  // 初始化所有日期的得分为 0

    // Declare the adapter variable here
    private lateinit var dayScoreAdapter: DayScoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        gridView = view.findViewById(R.id.gridView)
        yearText = view.findViewById(R.id.yearText)
        monthText = view.findViewById(R.id.monthText)
        totalScoreLabel = view.findViewById(R.id.total_score_label)
        dailyScoreLabel = view.findViewById(R.id.daily_score_label)

        // Initialize SharedPreferences
        prefs = requireActivity().getSharedPreferences("score_data", android.content.Context.MODE_PRIVATE)

        // Set year and month
        yearText.text = currentYear.toString()
        monthText.text = getMonthName(currentMonth)

        // Load data from SharedPreferences
        val dailyScore = prefs.getInt("dailyScore", 0)
        val totalScore = prefs.getInt("totalScore", 0)

        // Display scores
        dailyScoreLabel.text = "每日积分: $dailyScore/100"
        totalScoreLabel.text = "总得分: $totalScore"

        // Initialize the adapter and set it to the GridView
        dayScoreAdapter = DayScoreAdapter(requireContext(), dates, scores)
        gridView.adapter = dayScoreAdapter

        // Load daily scores from SharedPreferences
        loadScoresFromSharedPreferences()
        return view
    }


    private fun loadScoresFromSharedPreferences() {
        // 获取每一天的分数并传递给适配器
        for (i in dates.indices) {
            val date = dates[i]
            val formattedDate = formatDate(date)  // 格式化日期为 yyyy-MM-dd
            scores[i] = prefs.getInt("score_$formattedDate", 0) // 获取每个日期的分数
            Log.d("DashboardFragment", "Loaded score for date $formattedDate: ${scores[i]}")  // 输出日志查看分数
        }

        // 刷新适配器
        dayScoreAdapter.notifyDataSetChanged()
    }

    // 格式化日期为 yyyy-MM-dd
    private fun formatDate(date: Int): String {
        val year = currentYear
        val month = if (currentMonth < 10) "0$currentMonth" else currentMonth.toString()  // 格式化月份为两位
        val day = if (date < 10) "0$date" else date.toString()  // 格式化日期为两位
        return "$year-$month-$day"
    }
    private fun getMonthName(month: Int): String {
        val monthNames = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        return monthNames[month - 1]  // month 从 0 开始
    }

    private fun getDatesInMonth(month: Int, year: Int): List<Int> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1) // 设置到指定月份的第一天
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) // 获取当月的最大天数
        return (1..daysInMonth).toList()
    }
}
