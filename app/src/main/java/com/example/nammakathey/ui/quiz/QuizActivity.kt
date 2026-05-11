package com.nammakathey.ui.quiz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.nammakathey.databinding.ActivityQuizBinding
import com.nammakathey.databinding.ActivityQuizResultBinding
import com.nammakathey.data.model.QuizQuestion
import com.nammakathey.ui.home.HomeActivity
import com.nammakathey.ui.profile.ProfileActivity
import com.nammakathey.utils.BaseActivity
import com.nammakathey.utils.PrefManager
import com.nammakathey.viewmodel.MainViewModel

class QuizActivity : BaseActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var viewModel: MainViewModel

    private var currentIndex = 0
    private var score = 0
    private var heroId = ""
    private var questions = listOf<QuizQuestion>()
    private var answered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        heroId = intent.getStringExtra("hero_id") ?: return

        val hero = viewModel.getHeroById(heroId) ?: return
        questions = hero.quiz

        val isKn = isKannada()
        binding.tvHeroName.text = if (isKn) hero.name_kn else hero.name
        binding.btnBack.setOnClickListener { finish() }

        loadQuestion()
    }

    private fun loadQuestion() {
        if (currentIndex >= questions.size) {
            showResult()
            return
        }

        answered = false
        val q = questions[currentIndex]
        val isKn = isKannada()

        // Progress
        binding.tvProgress.text = "${currentIndex + 1} / ${questions.size}"
        val progressPercent = ((currentIndex + 1).toFloat() / questions.size * 100).toInt()
        binding.progressBar.progress = progressPercent

        // Question text
        binding.tvQuestion.text = if (isKn) q.q_kn else q.q_en

        // Options
        val options = if (isKn) q.options_kn else q.options_en
        val buttons = listOf(binding.btnOpt1, binding.btnOpt2,
            binding.btnOpt3, binding.btnOpt4)

        buttons.forEachIndexed { i, btn ->
            btn.text = options.getOrElse(i) { "" }
            btn.isEnabled = true
            // Reset to WHITE background with DARK BLUE text
            btn.setBackgroundColor(Color.WHITE)
            btn.setTextColor(Color.parseColor("#1B3A6B"))
            btn.setOnClickListener {
                onAnswer(i, q.correct_index, buttons)
            }
        }

        // Animate question in
        binding.cardQuestion.translationY = 80f
        binding.cardQuestion.alpha = 0f
        binding.cardQuestion.animate()
            .translationY(0f).alpha(1f)
            .setDuration(350).start()
    }

    private fun onAnswer(selected: Int, correct: Int, buttons: List<Button>) {
        if (answered) return
        answered = true

        // Disable all buttons
        buttons.forEach { it.isEnabled = false }

        if (selected == correct) {
            score++
            // Correct — green background white text
            buttons[selected].setBackgroundColor(Color.parseColor("#27AE60"))
            buttons[selected].setTextColor(Color.WHITE)
        } else {
            // Wrong answer — red
            buttons[selected].setBackgroundColor(Color.parseColor("#C0392B"))
            buttons[selected].setTextColor(Color.WHITE)
            // Show correct — green
            buttons[correct].setBackgroundColor(Color.parseColor("#27AE60"))
            buttons[correct].setTextColor(Color.WHITE)
        }

        // Move to next after 1.2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            currentIndex++
            loadQuestion()
        }, 1200)
    }

    private fun showResult() {
        val isKn = isKannada()
        val (hero, district) = viewModel.getHeroWithDistrict(heroId) ?: return

        val resultBinding = ActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(resultBinding.root)

        val badgeEarned = score >= 2

        resultBinding.tvScore.text = "$score / ${questions.size}"
        resultBinding.tvHeroName.text = if (isKn) hero.name_kn else hero.name

        if (badgeEarned) {
            resultBinding.badgeLayout.visibility = View.VISIBLE
            resultBinding.starsContainer.visibility = View.VISIBLE
            resultBinding.tvResultTitle.text =
                if (isKn) "ಭಲೇ! ಬ್ಯಾಡ್ಜ್ ಗೆದ್ದಿರಿ! 🏅"
                else "Maha-Sadhakal! Badge Earned! 🏅"
            resultBinding.tvResultMessage.text =
                if (isKn) "'${hero.name_kn}'ರ ಹೆರಿಟೇಜ್ ಬ್ಯಾಡ್ಜ್ ಗೆದ್ದಿರಿ!"
                else "You mastered '${hero.name}' and earned the Heritage Badge!"
            resultBinding.tvExperience.text = "850 XP"

            // Save badge
            viewModel.saveBadge(
                heroId, hero.name, hero.category,
                if (isKn) district.name_kn else district.name
            )

            // Animate badge
            resultBinding.badgeIcon.scaleX = 0f
            resultBinding.badgeIcon.scaleY = 0f
            resultBinding.badgeIcon.animate()
                .scaleX(1.2f).scaleY(1.2f).setDuration(400)
                .withEndAction {
                    resultBinding.badgeIcon.animate()
                        .scaleX(1f).scaleY(1f).setDuration(200).start()
                }.start()

        } else {
            resultBinding.badgeLayout.visibility = View.GONE
            resultBinding.starsContainer.visibility = View.GONE
            resultBinding.tvResultTitle.text =
                if (isKn) "ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ! 💪"
                else "Keep Trying! 💪"
            resultBinding.tvResultMessage.text =
                if (isKn) "2/3 ಅಥವಾ ಹೆಚ್ಚು ಸರಿ ಉತ್ತರ ಗೆದ್ದರೆ ಬ್ಯಾಡ್ಜ್ ಸಿಗುತ್ತದೆ!"
                else "Score 2/3 or more to earn the Heritage Badge!"
            resultBinding.tvExperience.text = "250 XP"
        }

        resultBinding.tvTimeTaken.text = "5:24"

        resultBinding.btnPlayAgain.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("hero_id", heroId)
            startActivity(intent)
            finish()
        }

        resultBinding.btnBackToMap.setOnClickListener {
            startActivity(
                Intent(this, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        resultBinding.btnViewBadges.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}