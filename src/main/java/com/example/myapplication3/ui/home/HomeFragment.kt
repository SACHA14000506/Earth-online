package com.example.myapplication3.ui.home
import android.graphics.Typeface
import android.os.Looper
import android.graphics.Color

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication3.R
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity // 添加这一行
import android.util.Log

class HomeFragment : Fragment() {

    // 秒表相关变量
    private var stopwatchHandler: Handler = Handler(Looper.getMainLooper())
    private var stopwatchRunnable: Runnable? = null
    private var elapsedTime: Long = 0 // 秒表时间（秒）
    private var isStopwatchRunning = false // 秒表是否正在运行
    private var stopwatchStartTime: Long = 0 // 秒表开始时间

    // UI 控件
    private lateinit var stopwatchTextView: TextView
    private lateinit var startStopwatchButton: Button
    private lateinit var pauseStopwatchButton: Button
    private lateinit var resetStopwatchButton: Button
    // 每个选项的点击次数键名
    private val option1ClickCount = "option_1_click_count"
    private val option2ClickCount = "option_2_click_count"
    private val option3ClickCount = "option_3_click_count"
    private val taskClickCount = "task_click_count"
    // 课程选项点击次数
    private val mathClickCount = "math_click_count"
    private val englishClickCount = "english_click_count"
    private val language_ClickCount = "language_click_count"
    // 代码选项点击次数
    private val webClickCount = "web_click_count"
    private val leetClickCount = "leet_click_count"
    private val codeClickCount = "code_click_count"
    // 技能选项点击次数
    private val chineseMedicineClickCount = "chinese_medicine_click_count"
    private val signLanguageClickCount = "sign_language_click_count"
    private val editingClickCount = "editing_click_count"
    private val photoshopClickCount = "photoshop_click_count"
    private val communicationClickCount = "communication_click_count"
    private val paintingClickCount = "painting_click_count"
    private val woodClickCount = "wood_click_count"
    private val chuangzaoClickCount = "chuangzuo_click_count"
    private val qileiClickCount = "qilei_click_count"
    private val cookClickCount = "cook_click_count"
    private val skillClickCount = "skill_click_count"

    // 背诵选项点击次数
    private val englishReciteClickCount = "english_recite_click_count"
    private val chineseReciteClickCount = "chinese_recite_click_count"
    // 作息选项点击次数
    private val wakeUpClickCount = "wake_up_click_count"
    private val sleepEarlyClickCount = "sleep_early_click_count"
    private val tidyUpClickCount = "tidy_up_click_count"
    private val showerClickCount = "shower_click_count"
    private val zuoxiClickCount = "zuoxi_click_count"
    // 运动选项点击次数
    private val baDuanJinClickCount = "ba_duan_jin_click_count"
    private val runningClickCount = "running_click_count"
    private val swimmingClickCount = "swimming_click_count"
    private val ballGamesClickCount = "ball_games_click_count"
    private val fitnessClickCount = "fitness_click_count"
    private val yundongClickCount = "yundong_click_count"
    // 练字选项点击次数
    private val hardPenClickCount = "hard_pen_click_count"
    private val softPenClickCount = "soft_pen_click_count"
    private val lianziPenClickCount = "soft_pen_click_count"
    // 音乐选项点击次数
    private val whistleClickCount = "whistle_click_count"
    private val pianoClickCount = "piano_click_count"
    private val harmonicaClickCount = "harmonica_click_count"
    private val yinyueClickCount = "harmonica_click_count"

    private lateinit var amPmLabel: TextView
    private lateinit var timeLabel: TextView
    private lateinit var dateLabel: TextView
    private lateinit var totalScoreLabel: TextView
    private lateinit var dailyScoreLabel: TextView
    private lateinit var levelLabel: TextView
    private lateinit var levelScore: TextView
    private lateinit var rewardContainer: LinearLayout

    private var dailyScore = 0
    private var totalScore = 0
    private var hasClaimedTodayReward = false
    private var currentDate = ""
    private var lastTotalScore = 0
    private var dailyRewardStartTime: Long = -1L
    private var hasUnclaimedDailyReward = false
    private var dailyRewardDescription = ""

    private val pendingRewards = mutableListOf<String>()

    private val rewardSteps = listOf(
        RewardRule(150, "读会儿书"),
        RewardRule(600, "做个手工"),
        RewardRule(700, "一部电影"),
        RewardRule(1000, "吃吃吃！"),
        RewardRule(1200, "睡懒觉一次"),
        RewardRule(1500, "一杯奶茶？"),
        RewardRule(1800, "1小时游戏"),
        RewardRule(2000, "偷得半日闲"),
        RewardRule(3000, "美丽废物")
    )

    data class RewardRule(val step: Int, val description: String)

    private val handler = Handler()
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateTime()
            handler.postDelayed(this, 1000)
        }
    }

    private val prefs by lazy { requireActivity().getSharedPreferences("score_data", Activity.MODE_PRIVATE) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 初始化视图
        amPmLabel = view.findViewById(R.id.am_pm_label)
        timeLabel = view.findViewById(R.id.time_label)
        dateLabel = view.findViewById(R.id.date_label)
        totalScoreLabel = view.findViewById(R.id.total_score_label)
        dailyScoreLabel = view.findViewById(R.id.daily_score_label)
        levelLabel = view.findViewById(R.id.level_label)
        levelScore = view.findViewById(R.id.level_score)
        rewardContainer = view.findViewById(R.id.reward_container)
        // 初始化秒表相关视图
        stopwatchTextView = view.findViewById(R.id.stopwatch_text_view)
        startStopwatchButton = view.findViewById(R.id.start_stopwatch_button)
        pauseStopwatchButton = view.findViewById(R.id.pause_stopwatch_button)
        resetStopwatchButton = view.findViewById(R.id.reset_stopwatch_button)

        // 设置秒表按钮点击事件
        startStopwatchButton.setOnClickListener {
            startStopwatch()
        }

        pauseStopwatchButton.setOnClickListener {
            pauseStopwatch()
        }

        resetStopwatchButton.setOnClickListener {
            resetStopwatch()
        }

        val deleteButton: Button = view.findViewById(R.id.delete_button)
        deleteButton.setOnClickListener {
            // 显示删除确认对话框
            showDeleteConfirmationDialog()
        }
        view.findViewById<Button>(R.id.task_button).apply {
            // 设置按钮的字体
            this.setTypeface(Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf"))
        }
        view.findViewById<Button>(R.id.task_button).setOnClickListener {
            val options = arrayOf("1小时", "2小时", "3小时")
            val scores = arrayOf(10, 20, 30)

            val builder = AlertDialog.Builder(requireContext())
                .setTitle("任务")
                .setItems(options) { _, which ->
                    val score = scores[which]
                    incrementScore(score)
                    // 根据选项更新点击次数
                    when (which) {
                        0 -> {
                            incrementClickCount(option1ClickCount)  // 更新 option1 的点击次数
                            incrementClickCount(taskClickCount)  // 更新 taskClickCount 的点击次数
                        }
                        1 -> {
                            incrementClickCount(option2ClickCount)  // 更新 option2 的点击次数
                            incrementClickCount(taskClickCount)  // 更新 taskClickCount 的点击次数
                            incrementClickCount(taskClickCount)  // 更新 taskClickCount 的点击次数

                        }
                        2 -> {
                            incrementClickCount(option3ClickCount)  // 更新 option3 的点击次数
                            incrementClickCount(taskClickCount)  // 更新 taskClickCount 的点击次数
                            incrementClickCount(taskClickCount)  // 更新 taskClickCount 的点击次数
                            incrementClickCount(taskClickCount)  // 更新 taskClickCount 的点击次数

                        }
//                        3 -> {
//                            // 更新多个选项和 taskClickCount
//                            incrementClickCount(option1ClickCount)
//                            incrementClickCount(option2ClickCount)
//                            incrementClickCount(option3ClickCount)
//                            incrementClickCount(taskClickCount)  // 更新 taskClickCount 的点击次数
//
//                        }
                    }

                }
            // 创建对话框
            val dialog = builder.create()

            // 设置监听器：在对话框显示时更新字体
            dialog.setOnShowListener {
                // 获取 AlertDialog 中的所有选项，并为每个选项设置字体大小和颜色
                for (i in 0 until dialog.listView.childCount) {
                    val textView = dialog.listView.getChildAt(i) as TextView
                    textView.typeface = Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf")
                    textView.textSize = 20f // 设置字体大小（单位：sp）
                }
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)  // 使用自定义背景 drawable

            }
            dialog.show()

        }
        view.findViewById<Button>(R.id.course_button).apply {
            // 设置按钮的字体
            this.setTypeface(Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf"))
        }
        view.findViewById<Button>(R.id.course_button).setOnClickListener {
            val options = arrayOf("数学", "英语", "计算机", "小语种")
            val scores = arrayOf(10, 10, 10, 10)

            val builder = AlertDialog.Builder(requireContext())
                .setTitle("课程")
                .setItems(options) { _, which ->
                    val score = scores[which]
                    incrementScore(score)
                    // 根据选项更新点击次数
                    when (which) {
                        0 -> {
                            incrementClickCount(mathClickCount)
                        }
                        1 -> {
                            incrementClickCount(englishClickCount)
                        }
                        2 -> {
                            incrementClickCount(codeClickCount)
                            incrementClickCount(webClickCount)

                        }
                        3 -> {
                            incrementClickCount(language_ClickCount)
                        }
                    }
                }
            // 创建对话框
            val dialog = builder.create()

            // 设置监听器：在对话框显示时更新字体
            dialog.setOnShowListener {
                // 获取 AlertDialog 中的所有选项，并为每个选项设置字体大小和颜色
                for (i in 0 until dialog.listView.childCount) {
                    val textView = dialog.listView.getChildAt(i) as TextView
                    textView.typeface = Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf")
                    textView.textSize = 20f // 设置字体大小（单位：sp）
                }
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)  // 使用自定义背景 drawable

            }
            dialog.show()

        }
        view.findViewById<Button>(R.id.code_button).apply {
            // 设置按钮的字体
            this.setTypeface(Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf"))
        }
        view.findViewById<Button>(R.id.code_button).setOnClickListener {
            val options = arrayOf("CODE", "LEECODE")
            val scores = arrayOf(10, 10)

            val builder = AlertDialog.Builder(requireContext())
                .setTitle("代码")
                .setItems(options) { _, which ->
                    val score = scores[which]
                    incrementScore(score)
                    // 根据选项更新点击次数
                    when (which) {
                        0 -> {incrementClickCount(codeClickCount)
                            incrementClickCount(webClickCount)
                        }
                        1 -> {incrementClickCount(leetClickCount)
                            incrementClickCount(webClickCount)
                        }

                    }
                }
            // 创建对话框
            val dialog = builder.create()

            // 设置监听器：在对话框显示时更新字体
            dialog.setOnShowListener {
                // 获取 AlertDialog 中的所有选项，并为每个选项设置字体大小和颜色
                for (i in 0 until dialog.listView.childCount) {
                    val textView = dialog.listView.getChildAt(i) as TextView
                    textView.typeface = Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf")
                    textView.textSize = 20f // 设置字体大小（单位：sp）
                }
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)  // 使用自定义背景 drawable

            }
            dialog.show()

        }
        view.findViewById<Button>(R.id.skill_button).apply {
            // 设置按钮的字体
            this.setTypeface(Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf"))
        }
        view.findViewById<Button>(R.id.skill_button).setOnClickListener {
            val options = arrayOf("中医", "手语", "剪辑", "PS", "沟通", "绘画", "木雕", "创作", "棋类", "烹饪")
            val scores = arrayOf(10, 10, 10, 10, 10, 10, 10, 10, 10, 10)

            val builder = AlertDialog.Builder(requireContext())
                .setTitle("技能")
                .setItems(options) { _, which ->
                    val score = scores[which]
                    incrementScore(score)
                    when (which) {
                        0 -> {incrementClickCount("chinese_medicine_click_count")
                            incrementClickCount("skill_click_count")
                        }
                        1 -> {incrementClickCount("sign_language_click_count")
                            incrementClickCount("skill_click_count")
                        }
                        2 -> {incrementClickCount("editing_click_count")
                            incrementClickCount("skill_click_count")
                        }
                        3 -> {incrementClickCount("photoshop_click_count")
                            incrementClickCount("skill_click_count")
                        }
                        4 -> {incrementClickCount("communication_click_count")
                            incrementClickCount("skill_click_count")

                        }
                        5 -> {incrementClickCount("painting_click_count")
                            incrementClickCount("skill_click_count")
                        }
                        6 -> {incrementClickCount("wood_click_count")
                            incrementClickCount("skill_click_count")
                        }
                        7 -> {incrementClickCount("chuangzuo_click_count")
                            incrementClickCount("skill_click_count")
                        }
                        8 -> {incrementClickCount("qilei_click_count")
                            incrementClickCount("skill_click_count")
                        }
                        9 -> {incrementClickCount("cook_click_count")
                            incrementClickCount("skill_click_count")
                        }
                    }
                }
            // 创建对话框
            val dialog = builder.create()

            // 设置监听器：在对话框显示时更新字体
            dialog.setOnShowListener {
                // 获取 AlertDialog 中的所有选项，并为每个选项设置字体大小和颜色
                for (i in 0 until dialog.listView.childCount) {
                    val textView = dialog.listView.getChildAt(i) as TextView
                    textView.typeface = Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf")
                    textView.textSize = 20f // 设置字体大小（单位：sp）
                }
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)  // 使用自定义背景 drawable

            }
            dialog.show()

        }
        view.findViewById<Button>(R.id.review_button).apply {
            // 设置按钮的字体
            this.setTypeface(Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf"))
        }
        view.findViewById<Button>(R.id.review_button).setOnClickListener {
            val options = arrayOf("英文", "中文")
            val scores = arrayOf(10, 10)
            val builder = AlertDialog.Builder(requireContext())
                .setTitle("背诵")
                .setItems(options) { _, which ->
                    val score = scores[which]
                    incrementScore(score)
                    // 根据选项更新点击次数
                    when (which) {
                        0 ->
                        {
                            incrementClickCount("english_recite_click_count")  // 英文
                            incrementClickCount("english_count")
                        }
                        1 ->{
                            incrementClickCount("chinese_recite_click_count")  // 中文
                        }
                    }
                }
            // 创建对话框
            val dialog = builder.create()

            // 设置监听器：在对话框显示时更新字体
            dialog.setOnShowListener {
                // 获取 AlertDialog 中的所有选项，并为每个选项设置字体大小和颜色
                for (i in 0 until dialog.listView.childCount) {
                    val textView = dialog.listView.getChildAt(i) as TextView
                    textView.typeface = Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf")
                    textView.textSize = 20f // 设置字体大小（单位：sp）
                }
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)  // 使用自定义背景 drawable

            }
            dialog.show()

        }
        view.findViewById<Button>(R.id.schedule_button).apply {
            // 设置按钮的字体
            this.setTypeface(Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf"))
        }
        view.findViewById<Button>(R.id.schedule_button).setOnClickListener {
            val options = arrayOf("早起", "早睡", "收拾东西", "洗澡")
            val scores = arrayOf(10, 10, 3, 5)

            val builder = AlertDialog.Builder(requireContext())
                .setTitle("作息")
                .setItems(options) { _, which ->
                    val score = scores[which]
                    incrementScore(score)
                    // 根据选项更新点击次数
                    when (which) {
                        0 ->{
                            incrementClickCount("zuoxi_click_count")
                            incrementClickCount("wake_up_click_count")  // 早起
                        }


                        1 -> {
                            incrementClickCount("zuoxi_click_count")
                            incrementClickCount("sleep_early_click_count")  // 早睡
                        }
                        2 -> {
                            incrementClickCount("zuoxi_click_count")
                            incrementClickCount("tidy_up_click_count")  // 收拾东西
                        }
                        3 -> {
                            incrementClickCount("shower_click_count")  // 洗澡
                            incrementClickCount("zuoxi_click_count")
                        }
                    }
                }
            // 创建对话框
            val dialog = builder.create()

            // 设置监听器：在对话框显示时更新字体
            dialog.setOnShowListener {
                // 获取 AlertDialog 中的所有选项，并为每个选项设置字体大小和颜色
                for (i in 0 until dialog.listView.childCount) {
                    val textView = dialog.listView.getChildAt(i) as TextView
                    textView.typeface = Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf")
                    textView.textSize = 20f // 设置字体大小（单位：sp）
                }
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)  // 使用自定义背景 drawable

            }

            // 显示对话框
            dialog.show()
        }
        view.findViewById<Button>(R.id.exercise_button).apply {
            // 设置按钮的字体
            this.setTypeface(Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf"))
        }
        view.findViewById<Button>(R.id.exercise_button).setOnClickListener {
            val options = arrayOf("八段锦", "跑步", "游泳", "球类", "健身")
            val scores = arrayOf(10, 10, 10, 10, 10)

            val builder = AlertDialog.Builder(requireContext())
                .setTitle("运动")
                .setItems(options) { _, which ->
                    val score = scores[which]
                    incrementScore(score)
                    // 根据选项更新点击次数
                    when (which) {
                        0 ->{
                            incrementClickCount("ba_duan_jin_click_count")  // 八段锦
                            incrementClickCount("yundong_click_count")
                        }

                        1 -> {
                            incrementClickCount("running_click_count")  // 跑步
                            incrementClickCount("yundong_click_count")
                        }
                        2 -> {
                            incrementClickCount("swimming_click_count")  // 游泳
                            incrementClickCount("yundong_click_count")
                        }
                        3 -> {
                            incrementClickCount("ball_games_click_count")  // 球类
                            incrementClickCount("yundong_click_count")
                        }
                        4 -> {
                            incrementClickCount("fitness_click_count")  // 健身
                            incrementClickCount("yundong_click_count")
                        }
                    }
                }
            // 创建对话框
            val dialog = builder.create()

            // 设置监听器：在对话框显示时更新字体
            dialog.setOnShowListener {
                // 获取 AlertDialog 中的所有选项，并为每个选项设置字体大小和颜色
                for (i in 0 until dialog.listView.childCount) {
                    val textView = dialog.listView.getChildAt(i) as TextView
                    textView.typeface = Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf")
                    textView.textSize = 20f // 设置字体大小（单位：sp）
                }
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)  // 使用自定义背景 drawable

            }

            // 显示对话框
            dialog.show()
        }
        view.findViewById<Button>(R.id.writing_button).apply {
            // 设置按钮的字体
            this.setTypeface(Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf"))
        }
        view.findViewById<Button>(R.id.writing_button).setOnClickListener {
            val options = arrayOf("硬笔", "软笔")
            val scores = arrayOf(10, 10)

            val builder = AlertDialog.Builder(requireContext())
                .setTitle("练字")
                .setItems(options) { _, which ->
                    val score = scores[which]
                    incrementScore(score)
                    // 根据选项更新点击次数
                    when (which) {
                        0 ->{
                            incrementClickCount("hard_pen_click_count")  // 硬笔
                            incrementClickCount("shufa_click_count")

                        }
                        1 -> {
                            incrementClickCount("soft_pen_click_count")  // 软笔
                            incrementClickCount("shufa_click_count")
                        }
                    }
                }

            // 创建对话框
            val dialog = builder.create()

            // 设置监听器：在对话框显示时更新字体
            dialog.setOnShowListener {
                // 获取 AlertDialog 中的所有选项，并为每个选项设置字体大小和颜色
                for (i in 0 until dialog.listView.childCount) {
                    val textView = dialog.listView.getChildAt(i) as TextView
                    textView.typeface = Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf")
                    textView.textSize = 20f // 设置字体大小（单位：sp）
                }
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)  // 使用自定义背景 drawable

            }
            dialog.show()

        }

        view.findViewById<Button>(R.id.music_button).apply {
            // 设置按钮的字体
            this.setTypeface(Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf"))
        }
        view.findViewById<Button>(R.id.music_button).setOnClickListener {
            val options = arrayOf("哨笛", "钢琴", "口琴")
            val scores = arrayOf(10, 10, 10)


            val builder = AlertDialog.Builder(requireContext())
                .setTitle("乐器")
                .setItems(options) { _, which ->
                    val score = scores[which]
                    incrementScore(score)
                    // 根据选项更新点击次数
                    when (which) {
                        0 -> {
                            incrementClickCount("whistle_click_count")  // 哨笛
                            incrementClickCount("yinyue_click_count")
                        }
                        1 -> {
                            incrementClickCount("piano_click_count")  // 钢琴
                            incrementClickCount("yinyue_click_count")
                        }
                        2 -> {
                            incrementClickCount("harmonica_click_count")  // 口琴
                            incrementClickCount("yinyue_click_count")
                        }
                    }
                }
            // 创建对话框
            val dialog = builder.create()

            // 设置监听器：在对话框显示时更新字体
            dialog.setOnShowListener {
                // 获取 AlertDialog 中的所有选项，并为每个选项设置字体大小和颜色
                for (i in 0 until dialog.listView.childCount) {
                    val textView = dialog.listView.getChildAt(i) as TextView
                    textView.typeface = Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf")
                    textView.textSize = 20f // 设置字体大小（单位：sp）
                }
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)  // 使用自定义背景 drawable

            }
            dialog.show()

        }
        view.findViewById<Button>(R.id.penalty_button).apply {
            // 设置按钮的字体
            this.setTypeface(Typeface.createFromAsset(requireContext().assets, "fonts/ZCOOLKuaiLe-Regular.ttf"))
        }
        view.findViewById<Button>(R.id.penalty_button).setOnClickListener {
            decrementScore(10)
        }
        loadData()
        updateScoreDisplays()

        // 恢复未领取的总积分奖励
        pendingRewards.forEach {
            addReward(it, save = false)
        }


        if (hasUnclaimedDailyReward && dailyRewardDescription.isNotEmpty()) {
            val rewardView = LayoutInflater.from(context).inflate(R.layout.reward_item, rewardContainer, false)
            val rewardText = rewardView.findViewById<TextView>(R.id.reward_text)
            val claimButton = rewardView.findViewById<Button>(R.id.claim_button)
            rewardText.text = dailyRewardDescription
            claimButton.text = "领取"
            rewardContainer.addView(rewardView)

            // 如果用户点击领取，开始倒计时
            claimButton.setOnClickListener {
                dailyRewardStartTime = System.currentTimeMillis()
                hasUnclaimedDailyReward = true
                saveData()
                startCountdown(rewardView, 30 * 60 * 1000)  // 启动倒计时
            }
        }
        handler.post(updateTimeRunnable)
        return view
    }

    private fun startStopwatch() {
        if (!isStopwatchRunning) {
            isStopwatchRunning = true
            stopwatchStartTime = System.currentTimeMillis() - elapsedTime * 1000
            stopwatchRunnable = object : Runnable {
                override fun run() {
                    val elapsedMillis = System.currentTimeMillis() - stopwatchStartTime
                    elapsedTime = elapsedMillis / 1000
                    updateStopwatchUI()
                    stopwatchHandler.postDelayed(this, 1000) // 每秒更新一次
                }
            }
            stopwatchHandler.post(stopwatchRunnable!!)
        }
    }

    // 暂停秒表
    private fun pauseStopwatch() {
        if (isStopwatchRunning) {
            isStopwatchRunning = false
            stopwatchHandler.removeCallbacks(stopwatchRunnable!!) // 移除更新操作
        }
    }

    // 清零秒表
    private fun resetStopwatch() {
        isStopwatchRunning = false
        elapsedTime = 0
        updateStopwatchUI()
        stopwatchHandler.removeCallbacks(stopwatchRunnable!!) // 移除更新操作
    }

    // 更新秒表 UI
    private fun updateStopwatchUI() {
        val minutes = (elapsedTime / 60).toInt()
        val seconds = (elapsedTime % 60).toInt()
        stopwatchTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun incrementClickCount(key: String) {
        val currentCount = prefs.getInt(key, 0)
        prefs.edit().putInt(key, currentCount + 1).apply()
    }

    private fun deleteAllData() {
        // 清空所有记录
        prefs.edit().clear().apply()
        // 重新加载数据（这里会加载清空后的默认值）
        loadData()
        // 更新视图，显示所有数据为零
        updateScoreDisplays()
    }
    private fun showDeleteConfirmationDialog() {
        // 创建一个确认对话框
        AlertDialog.Builder(requireContext())
            .setTitle("确认删除")
            .setMessage("确定要删除所有记录吗？此操作不可恢复！")
            .setPositiveButton("确定") { _, _ ->
                // 用户点击确认，执行删除操作
                deleteAllData()
            }
            .setNegativeButton("取消", null)  // 用户点击取消，不做任何操作
            .show()
    }

    private fun updateTime() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val amPm = if (hourOfDay < 12) "上午" else "下午"
        val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
        val timeText = String.format("%02d:%02d:%02d", hour, minute, second)
        val dateText = getCurrentDate()

        amPmLabel.text = amPm
        timeLabel.text = timeText
        dateLabel.text = dateText

        if (dateText != currentDate) {
            currentDate = dateText
            dailyScore = 0
            hasClaimedTodayReward = false
            dailyRewardStartTime = -1L
            hasUnclaimedDailyReward = false
            dailyRewardDescription = ""
            rewardContainer.removeAllViews()
            pendingRewards.clear()
            updateScoreDisplays()
            saveData()
        }
    }

    private fun saveDailyScore() {
        val date = getCurrentDate()  // 获取当前日期
        prefs.edit().apply {
            putInt("score_$date", dailyScore)  // 使用当前日期作为键保存分数
            apply()  // 提交更改
        }
        Log.d("HomeFragment", "Saved score for $date: $dailyScore")  // 确认分数是否保存
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
    }

    private fun incrementScore(amount: Int) {
        dailyScore += amount
        if (dailyScore >= 100) {
            dailyScore = 100
            if (!hasClaimedTodayReward) {
                hasClaimedTodayReward = true
                addRewardWithTimer("手机玩半小时")
            }
        }

        totalScore += amount
        for (rule in rewardSteps) {
            val prev = lastTotalScore / rule.step
            val now = totalScore / rule.step
            if (now > prev) {
                val times = now - prev
                repeat(times) {
                    addReward(rule.description)
                }
            }
        }

        lastTotalScore = totalScore
        saveData()  // 保存总分数和每日分数
        saveDailyScore()  // 保存当天的分数
        updateScoreDisplays()

    }

    private fun decrementScore(amount: Int) {
        dailyScore -= amount
        if (dailyScore < 0) dailyScore = 0
        totalScore -= amount
        if (totalScore < 0) totalScore = 0
        saveData()  // 保存总分数和每日分数
        saveDailyScore()  // 保存当天的分数
        updateScoreDisplays()
    }

    private fun updateScoreDisplays() {
        dailyScoreLabel.text = "每日积分: $dailyScore/100"
        totalScoreLabel.text = "Score: $totalScore"
        val level = totalScore / 1000
        levelLabel.text = "LV$level"
        val remainingScore = 1000 - (totalScore % 1000)
        levelScore.text = "距离晋级还差: $remainingScore"
    }

    private fun addReward(rewardDescription: String, save: Boolean = true) {
        if (!pendingRewards.contains(rewardDescription)) {
            pendingRewards.add(rewardDescription)
            if (save) saveData()
        }

        val rewardView = LayoutInflater.from(context).inflate(R.layout.reward_item, rewardContainer, false)
        val rewardText = rewardView.findViewById<TextView>(R.id.reward_text)
        val claimButton = rewardView.findViewById<Button>(R.id.claim_button)
        rewardText.text = rewardDescription
        claimButton.text = "领取"

        claimButton.setOnClickListener {
            rewardContainer.removeView(rewardView)
            pendingRewards.remove(rewardDescription)
            saveData()
        }

        rewardContainer.addView(rewardView)
    }

    private fun addRewardWithTimer(rewardDescription: String) {
        // 每日奖励被添加到待领取成就列表中
        if (!pendingRewards.contains(rewardDescription)) {
            pendingRewards.add(rewardDescription)
            saveData()  // 保存数据
        }

        val rewardView = LayoutInflater.from(context).inflate(R.layout.reward_item, rewardContainer, false)
        val rewardText = rewardView.findViewById<TextView>(R.id.reward_text)
        val claimButton = rewardView.findViewById<Button>(R.id.claim_button)
        rewardText.text = rewardDescription
        claimButton.text = "领取"

        claimButton.setOnClickListener {
            dailyRewardStartTime = System.currentTimeMillis()  // 重置每日奖励开始时间
            hasUnclaimedDailyReward = true  // 标记为未领取的每日奖励
            dailyRewardDescription = rewardDescription  // 保存奖励描述
            saveData()  // 保存数据
            startCountdown(rewardView, 30 * 60 * 1000)  // 启动倒计时
        }

        rewardContainer.addView(rewardView)  // 添加奖励视图
    }

    private fun startCountdown(rewardView: View, duration: Long) {
        val rewardText = rewardView.findViewById<TextView>(R.id.reward_text)
        val claimButton = rewardView.findViewById<Button>(R.id.claim_button)

        claimButton.isEnabled = false  // 禁用领取按钮

        val countDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                rewardText.text = String.format("倒计时: %02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                rewardContainer.removeView(rewardView)
                dailyRewardStartTime = -1L  // 重置奖励开始时间
                hasUnclaimedDailyReward = false  // 重置奖励状态
                dailyRewardDescription = ""  // 清空奖励描述
                saveData()  // 保存数据
            }
        }

        countDownTimer.start()
    }


//    private fun startCountdown(rewardView: View, duration: Long) {
//        val rewardText = rewardView.findViewById<TextView>(R.id.reward_text)
//        val claimButton = rewardView.findViewById<Button>(R.id.claim_button)
//
//        claimButton.isEnabled = false
//
//        val countDownTimer = object : CountDownTimer(duration, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val minutes = (millisUntilFinished / 1000) / 60
//                val seconds = (millisUntilFinished / 1000) % 60
//                rewardText.text = String.format("倒计时: %02d:%02d", minutes, seconds)
//            }
//
//            override fun onFinish() {
//                rewardContainer.removeView(rewardView)
//                dailyRewardStartTime = -1L
//                hasUnclaimedDailyReward = false
//                dailyRewardDescription = ""
//                saveData()
//            }
//        }
//
//        countDownTimer.start()
//    }

//        private fun saveData() {
//
//        prefs.edit().apply {
//            putInt("dailyScore", dailyScore)
//            putInt("totalScore", totalScore)
//            putInt("lastTotalScore", lastTotalScore)
//            putBoolean("hasClaimedTodayReward", hasClaimedTodayReward)
//            putLong("dailyRewardStartTime", dailyRewardStartTime)
//            putString("currentDate", currentDate)
//            putString("pendingRewards", pendingRewards.joinToString("#"))
//            putBoolean("hasUnclaimedDailyReward", hasUnclaimedDailyReward)
//            putString("dailyRewardDescription", dailyRewardDescription)
//            apply()
//        }
//    }
    private fun saveData() {
        prefs.edit().apply {
            putInt("dailyScore", dailyScore)
            putInt("totalScore", totalScore)
            putInt("lastTotalScore", lastTotalScore)
            putBoolean("hasClaimedTodayReward", hasClaimedTodayReward)
            putLong("dailyRewardStartTime", dailyRewardStartTime)  // 保存每日奖励的开始时间
            putString("currentDate", currentDate)
            putString("pendingRewards", pendingRewards.joinToString("#"))
            putBoolean("hasUnclaimedDailyReward", hasUnclaimedDailyReward)  // 保存每日奖励状态
            putString("dailyRewardDescription", dailyRewardDescription)  // 保存奖励描述
            apply()  // 提交更改
        }
    }




//        private fun loadData() {
//        dailyScore = prefs.getInt("dailyScore", 0)
//        totalScore = prefs.getInt("totalScore", 0)
//        lastTotalScore = prefs.getInt("lastTotalScore", 0)
//        hasClaimedTodayReward = prefs.getBoolean("hasClaimedTodayReward", false)
//        currentDate = prefs.getString("currentDate", getCurrentDate()) ?: getCurrentDate()
//        dailyRewardStartTime = prefs.getLong("dailyRewardStartTime", -1L)
//        val rewardString = prefs.getString("pendingRewards", "") ?: ""
//        pendingRewards.clear()
//        if (rewardString.isNotEmpty()) {
//            pendingRewards.addAll(rewardString.split("#"))
//        }
//    }
    private fun loadData() {
        dailyScore = prefs.getInt("dailyScore", 0)
        totalScore = prefs.getInt("totalScore", 0)
        lastTotalScore = prefs.getInt("lastTotalScore", 0)
        hasClaimedTodayReward = prefs.getBoolean("hasClaimedTodayReward", false)
        currentDate = prefs.getString("currentDate", getCurrentDate()) ?: getCurrentDate()
        dailyRewardStartTime = prefs.getLong("dailyRewardStartTime", -1L)  // 恢复每日奖励开始时间
        hasUnclaimedDailyReward = prefs.getBoolean("hasUnclaimedDailyReward", false)  // 恢复每日奖励状态
        dailyRewardDescription = prefs.getString("dailyRewardDescription", "") ?: ""  // 恢复奖励描述

        // 恢复成就列表
        val rewardString = prefs.getString("pendingRewards", "") ?: ""
        pendingRewards.clear()
        if (rewardString.isNotEmpty()) {
            pendingRewards.addAll(rewardString.split("#"))  // 以 "#" 分隔恢复多个成就
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateTimeRunnable)
    }
}
