package com.nammakathey.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nammakathey.databinding.ActivityProfileBinding
import com.nammakathey.ui.home.HomeActivity
import com.nammakathey.utils.BaseActivity
import com.nammakathey.utils.PrefManager
import com.nammakathey.viewmodel.MainViewModel

class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val isKn = isKannada()

        setupLabels(isKn)
        setupBadges(isKn)
        setupButtons(isKn)
    }

    private fun setupLabels(isKn: Boolean) {
        binding.tvScreenTitle.text =
            if (isKn) "ನಿಮ್ಮ ಬ್ಯಾಡ್ಜ್‌ಗಳು" else "Your Heritage Badges"
        binding.tvBadgesTitle.text =
            if (isKn) "ನಿಮ್ಮ ಹೆರಿಟೇಜ್ ಬ್ಯಾಡ್ಜ್‌ಗಳು" else "Your Heritage Badges"
        binding.tvBadgesDesc.text =
            if (isKn) "ಕ್ವಿಜ್‌ನಲ್ಲಿ 2/3 ಗಳಿಸಿ ಬ್ಯಾಡ್ಜ್ ಗೆಲ್ಲಿ!"
            else "Score 2/3 in any quiz to earn a badge!"

        binding.btnLanguage.text = if (isKn) "EN" else "ಕನ್ನಡ"
        binding.btnLanguage.setOnClickListener {
            PrefManager.toggleLanguage(this)
            val intent = Intent(this, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    private fun setupBadges(isKn: Boolean) {
        viewModel.allBadges.observe(this) { badges ->
            val count = badges.size
            binding.tvBadgeCount.text =
                if (isKn) "$count ಬ್ಯಾಡ್ಜ್‌ಗಳು" else "$count Badges"
            binding.tvExperienceTotal.text = "${count * 850} XP"

            if (badges.isEmpty()) {
                binding.tvEmptyBadges.visibility  = View.VISIBLE
                binding.recyclerBadges.visibility = View.GONE
                binding.tvEmptyBadges.text =
                    if (isKn) "ಇನ್ನೂ ಯಾವ ಬ್ಯಾಡ್ಜ್ ಇಲ್ಲ! ಕ್ವಿಜ್ ಆಡಿ ಗೆಲ್ಲಿ."
                    else "No badges yet! Complete quizzes to earn them."
            } else {
                binding.tvEmptyBadges.visibility  = View.GONE
                binding.recyclerBadges.visibility = View.VISIBLE
                val adapter = BadgeAdapter(badges, isKn)
                binding.recyclerBadges.layoutManager = GridLayoutManager(this, 3)
                binding.recyclerBadges.adapter = adapter
            }
        }
    }

    private fun setupButtons(isKn: Boolean) {
        binding.btnBack.setOnClickListener { finish() }

        // Swipe/slide back to home button
        binding.btnBackToHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            finish()
        }
    }
}