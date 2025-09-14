package com.example.myapplication3.ui.notifications

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication3.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class NotificationsFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private lateinit var achievementsLayout: LinearLayout

    // 成就数据列表
    private val achievements = listOf(
        // 任务选项
        Achievement("呜呜，是地缚灵", "task_click_count", 15),
        Achievement("资深牛马", "task_click_count", 50),
        Achievement("我就是邪剑仙！", "task_click_count", 100),
        // 课程选项
        Achievement("数学不会就是不会", "math_click_count", 15),
        Achievement("狼狈的征途", "math_click_count", 50),
        Achievement("高数在上", "math_click_count", 100),

        Achievement("Lee code小能手", "leet_click_count", 15),
        Achievement("脸滚键盘", "code_click_count", 15),
        Achievement("码农不秃", "code_click_count", 50),
        Achievement("新的征程", "code_click_count", 100),

        Achievement("该死的小语种", "language_ClickCount", 15),
        Achievement("我会18种语言！o(´^｀)o", "language_ClickCount", 100),

        Achievement("To-Ma-Toes!", "english_click_count", 15),
        Achievement("雅思,冲冲冲！", "english_click_count", 50),
        Achievement("出国啦！", "english_click_count", 100),
        Achievement("留子圣体", "english_click_count", 200),

        Achievement("呦呦鹿鸣", "chinese_recite_click_count", 10),
        Achievement("臣本布衣", "chinese_recite_click_count", 30),
        Achievement("吾不识青天高，黄地厚", "chinese_recite_click_count", 50),

        // 技能选项
        Achievement("尝百草", "chinese_medicine_click_count", 10),
        Achievement("白芷炖鸡", "chinese_medicine_click_count", 30),
        Achievement("衔云归", "chinese_medicine_click_count", 50),

        Achievement("比划比划", "sign_language_click_count", 10),
        Achievement("默多克在线执法", "sign_language_click_count", 50),

        Achievement("魔术师的第一步", "editing_click_count", 15),
        Achievement("剪辑中成", "editing_click_count", 30),
        Achievement("B站大会员", "editing_click_count", 50),

        Achievement("PS小手", "photoshop_click_count", 15),
        Achievement("神秘力量", "photoshop_click_count", 30),
        Achievement("再也不用怕给女朋友P图了！", "photoshop_click_count", 50),

        Achievement("我太想进步了", "communication_click_count", 10),
        Achievement("我们不熟，但没关系", "communication_click_count", 30),
        Achievement("一人派对", "communication_click_count", 50),

        Achievement("会画直线啦？", "painting_click_count", 10),
        Achievement("猕猴桃还是几维鸟？", "painting_click_count", 50),
        Achievement("德国艺术生", "painting_click_count", 100),

        Achievement("晌午", "wood_click_count", 10),
        Achievement("手中有刀自然神", "wood_click_count", 30),
        Achievement("手办？我自己做！", "wood_click_count", 50),

        Achievement("我要这个这个和这个", "chuangzuo_click_count", 10),
        Achievement("心想事成", "chuangzuo_click_count", 30),
        Achievement("我的世界", "chuangzuo_click_count", 50),

        Achievement("跳棋？五子棋？还是象棋？", "qilei_click_count", 10),
        Achievement("KO村口大爷", "qilei_click_count", 30),
        Achievement("神之一手", "qilei_click_count", 50),

        Achievement("番茄，蛋，再加点耗油！", "cook_click_count", 10),
        Achievement("哥德堡变奏曲", "cook_click_count", 30),
        Achievement("什么？我是特级厨师？", "cook_click_count", 50),

        Achievement("传奇序章", "skill_click_count", 100),
        Achievement("骄傲不死", "skill_click_count", 1000),
        // 作息选项
        Achievement("早起的虫儿", "wake_up_click_count", 15),
        Achievement("睡得早！", "sleep_early_click_count", 15),
        Achievement("清理一新！", "tidy_up_click_count", 15),
        Achievement("小鳄鱼爱洗澡", "shower_click_count", 15),
        Achievement("温暖的尸体", "zuoxi_click_count", 50),
        Achievement("健康的尸体", "zuoxi_click_count", 100),
        Achievement("再也不是脆皮了！", "zuoxi_click_count", 250),
        Achievement("独立生活Everyday", "zuoxi_click_count", 500),

        // 运动选项
        Achievement("小试牛刀", "ba_duan_jin_click_count", 10),
        Achievement("啊打~", "ba_duan_jin_click_count", 25),
        Achievement("八段锦大师", "ba_duan_jin_click_count", 50),

        Achievement("一步，两步", "running_click_count", 10),
        Achievement("跑起来！", "running_click_count", 25),
        Achievement("绝对自由", "running_click_count", 100),

        Achievement("救...咕噜噜", "swimming_click_count", 10),
        Achievement("《漂》", "swimming_click_count", 30),
        Achievement("萨卡班甲鱼", "swimming_click_count", 50),

        Achievement("跳起来打", "ball_games_click_count", 10),
        Achievement("球！球！球！", "ball_games_click_count", 50),

        Achievement("我的拨了盖呀！", "fitness_click_count", 15),
        Achievement("看我的肱二头肌", "fitness_click_count", 30),
        Achievement("利剑同志！∠(°ゝ°)", "fitness_click_count", 50),

        Achievement("大汗天子", "yundong_click_count", 50),
        Achievement("永远少年", "yundong_click_count", 100),

        // 音乐选项
        Achievement("嗯，是驴叫", "whistle_click_count", 10),
        Achievement("小酒馆的小曲儿", "whistle_click_count", 30),
        Achievement("WHO LIVES IN A PINEAPPLE UNDER THE SEA?", "whistle_click_count", 50),

        Achievement("钢的琴", "piano_click_count", 15),
        Achievement("弹起来！唱起来！", "piano_click_count", 30),
        Achievement("钢琴家", "piano_click_count", 50),

        Achievement("嘟嘟嘟~", "harmonica_click_count", 15),
        Achievement("洗洗口琴", "harmonica_click_count", 30),
        Achievement("随身小曲儿", "harmonica_click_count", 50),

        Achievement("来，跟着节奏走！", "yinyue_click_count", 100),

        // 练字选项
        Achievement("恭喜你，会写字了！", "hard_pen_click_count", 15),
        Achievement("回的四样写法", "hard_pen_click_count", 30),
        Achievement("执此锋锐", "hard_pen_click_count", 50),

        Achievement("天地玄黄", "soft_pen_click_count", 15),
        Achievement("嗯，纸不错", "soft_pen_click_count", 30),
        Achievement("浮白载笔", "soft_pen_click_count", 50),

        Achievement("笔走龙蛇", "shufa_click_count", 100)

    )

    data class Achievement(
        val name: String,
        val key: String,
        val threshold: Int
    )


    // 内部视图用数据
    private data class AchievementViewModel(
        val achievement: Achievement,
        val progress: Int,
        val claimed: Boolean,
        val canClaim: Boolean,
        val lastClaimed: Long
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        achievementsLayout = view.findViewById(R.id.achievements_layout)
        prefs = requireActivity().getSharedPreferences(
            "score_data",
            android.content.Context.MODE_PRIVATE
        )
        displayAchievements()
        return view
    }

    private fun displayAchievements() {
        achievementsLayout.removeAllViews()

        val viewModels = achievements.map { ach ->
            val progress = prefs.getInt(ach.key, 0)
            val unique = uniqueKey(ach)
            val claimedLevel = prefs.getInt("${unique}_claimedLevel", 0)
            val lastClaimed = prefs.getLong("${unique}_last_claimed", 0L)

            val level = if (ach.threshold > 0) progress / ach.threshold else 0
            val canClaim = level > claimedLevel

            Triple(ach, Triple(progress, level, claimedLevel), Pair(canClaim, lastClaimed))
        }
            // ★ 过滤：等级为0 且 没有领取过的（lastClaimed == 0）直接隐藏
            .filter { triple ->
                val level = triple.second.second
                val lastClaimed = triple.third.second
                level > 0 || lastClaimed > 0L
            }
        val sorted = viewModels.sortedWith(
            compareByDescending<Triple<Achievement, Triple<Int, Int, Int>, Pair<Boolean, Long>>> { it.third.first }  // canClaim
                .thenByDescending { it.third.second }                                                                // lastClaimed
                .thenBy { achievements.indexOf(it.first) }
        )

        sorted.forEach { (ach, p, state) ->
            val progress = p.first
            val level = p.second
            val claimedLevel = p.third
            val canClaim = state.first
            val lastClaimed = state.second

            val achievementView = LayoutInflater.from(context)
                .inflate(R.layout.achievement_item, achievementsLayout, false)

            val nameTextView: TextView = achievementView.findViewById(R.id.achievement_name)
            val levelTextView: TextView = achievementView.findViewById(R.id.achievement_level)
            val countTextView: TextView = achievementView.findViewById(R.id.click_count_text)
            val timeTextView: TextView = achievementView.findViewById(R.id.last_claimed_time)
            val claimButton: Button = achievementView.findViewById(R.id.claim_button)

            nameTextView.text = ach.name

            // 等级
            levelTextView.text = "等级：$level"

            // 距离下一次领取还需多少次
            val nextTarget = (level + 1) * ach.threshold
            val remain = (nextTarget - progress).coerceAtLeast(0)
            countTextView.text = "距离下一次领取还需 $remain 次"

            // 最近领取时间（如无则显示尚未领取）
            timeTextView.text = if (lastClaimed > 0L) formatTime(lastClaimed) else "尚未领取"

            // 按钮：只要当前等级 > 已领取等级，就可领取
            claimButton.isEnabled = canClaim
            claimButton.text = if (canClaim) "领 取" else "已领取"

            claimButton.setOnClickListener {
                if (canClaim) {
                    claimAchievement(ach, claimedLevel)   // 传入当前已领取等级
                    displayAchievements()                 // 刷新
                }
            }

            achievementsLayout.addView(achievementView)
        }
    }

    private fun claimAchievement(achievement: Achievement, claimedLevel: Int) {
        val unique = uniqueKey(achievement)
        val newTotalScore = prefs.getInt("totalScore", 0) + 5
        val newClaimedLevel = claimedLevel + 1            // 领取到下一个等级

        prefs.edit().apply {
            putInt("totalScore", newTotalScore)
            putInt("${unique}_claimedLevel", newClaimedLevel)
            putLong("${unique}_last_claimed", System.currentTimeMillis())
            apply()
        }
    }

    private fun uniqueKey(achievement: Achievement): String {
        // 独立的 claimedLevel/last_claimed 以“键@阈值”为命名，避免同一计数键不同阈值互相干扰
        return "${achievement.key}@${achievement.threshold}"
    }

    private fun formatTime(timestamp: Long): String {
        if (timestamp == 0L) return "尚未领取"
        return SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(timestamp))
    }
}
