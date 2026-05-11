package com.nammakathey.ui.story

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.nammakathey.R
import com.nammakathey.databinding.ActivityStoryBinding
import com.nammakathey.ui.quiz.QuizActivity
import com.nammakathey.utils.BaseActivity
import com.nammakathey.utils.PrefManager
import com.nammakathey.viewmodel.MainViewModel
import java.util.Locale

class StoryActivity : BaseActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityStoryBinding
    private lateinit var viewModel: MainViewModel
    private var tts: TextToSpeech? = null
    private var isSpeaking = false
    private var heroId = ""
    private var currentPages = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        tts = TextToSpeech(this, this)

        heroId = intent.getStringExtra("hero_id") ?: return
        setupStory()
    }

    private fun setupStory() {
        val (hero, district) = viewModel.getHeroWithDistrict(heroId) ?: return
        val isKn = isKannada()

        currentPages = if (isKn) hero.story_pages_kn else hero.story_pages_en

        // Header
        binding.tvHeroName.text   = if (isKn) hero.name_kn else hero.name
        binding.tvHeroEra.text    = hero.era
        binding.tvDistrict.text   = if (isKn) district.name_kn else district.name
        binding.tvCategory.text   = hero.category

        // ViewPager2 — REQUIRED by project spec
        val adapter = StoryPagerAdapter(currentPages)
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 1

        // Dot indicator
        setupDots(currentPages.size, 0)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setupDots(currentPages.size, position)
                updatePageNavigation(position, currentPages.size)
                if (isSpeaking) speakPage(currentPages[position])
            }
        })

        updatePageNavigation(0, currentPages.size)


        // Buttons
        binding.btnBack.setOnClickListener { finish() }

        binding.btnTts.setOnClickListener { toggleTts() }

        binding.btnTakeQuiz.setOnClickListener {
            tts?.stop()
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("hero_id", heroId)
            startActivity(intent)
        }

        binding.btnPrev.setOnClickListener {
            val cur = binding.viewPager.currentItem
            if (cur > 0) binding.viewPager.currentItem = cur - 1
        }
        binding.btnNext.setOnClickListener {
            val cur = binding.viewPager.currentItem
            if (cur < currentPages.size - 1) binding.viewPager.currentItem = cur + 1
        }

        // Language toggle
        binding.btnLanguage.text = if (isKn) "EN" else "ಕನ್ನಡ"
        binding.btnLanguage.setOnClickListener {
            tts?.stop()
            tts?.shutdown()
            PrefManager.toggleLanguage(this)
            val intent = Intent(this, StoryActivity::class.java)
            intent.putExtra("hero_id", heroId)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun updatePageNavigation(position: Int, total: Int) {
        binding.tvPageNum.text = "${position + 1} / $total"
        binding.btnPrev.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
        binding.btnNext.visibility = if (position < total - 1) View.VISIBLE else View.INVISIBLE
        binding.btnTakeQuiz.visibility = if (position == total - 1) View.VISIBLE else View.GONE
    }

    private fun setupDots(total: Int, selected: Int) {
        binding.dotsContainer.removeAllViews()
        for (i in 0 until total) {
            val dot = View(this).apply {
                val size = if (i == selected) 14 else 8
                val params = android.widget.LinearLayout.LayoutParams(
                    dpToPx(size), dpToPx(size)
                ).apply { marginStart = dpToPx(4); marginEnd = dpToPx(4) }
                layoutParams = params
                background = if (i == selected)
                    getDrawable(R.drawable.dot_selected)
                else getDrawable(R.drawable.dot_unselected)
            }
            binding.dotsContainer.addView(dot)
        }
    }

    private fun dpToPx(dp: Int) =
        (dp * resources.displayMetrics.density).toInt()

    // TTS
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val lang = if (isKannada()) Locale("kn", "IN") else Locale.ENGLISH
            val result = tts?.setLanguage(lang)
            binding.btnTts.isEnabled =
                result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    private fun toggleTts() {
        if (isSpeaking) {
            tts?.stop()
            isSpeaking = false
            binding.btnTts.setImageResource(R.drawable.ic_volume_off)
            binding.wavesView.visibility = View.GONE
        } else {
            isSpeaking = true
            binding.btnTts.setImageResource(R.drawable.ic_volume_on)
            binding.wavesView.visibility = View.VISIBLE
            speakPage(currentPages[binding.viewPager.currentItem])
        }
    }

    private fun speakPage(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "story_tts")
    }

    override fun onPause()  { tts?.stop(); super.onPause() }
    override fun onDestroy() { tts?.shutdown(); super.onDestroy() }
}