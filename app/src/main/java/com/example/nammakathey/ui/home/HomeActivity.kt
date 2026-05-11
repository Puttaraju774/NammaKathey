package com.nammakathey.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.nammakathey.R
import com.nammakathey.databinding.ActivityHomeBinding
import com.nammakathey.ui.map.DistrictMapActivity
import com.nammakathey.ui.profile.ProfileActivity
import com.nammakathey.ui.story.StoryActivity
import com.nammakathey.utils.BaseActivity
import com.nammakathey.utils.PrefManager
import com.nammakathey.viewmodel.MainViewModel
import kotlin.math.abs

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupLanguageToggle()
        setupHeroCarousel()
        setupClickListeners()
        updateBadgeCount()
        setupAnimations()
    }

    private fun setupLanguageToggle() {
        val isKn = isKannada()

        binding.btnLanguage.text =
            if (isKn) "EN" else "ಕನ್ನಡ"

        binding.tvAppTitleKn.text =
            if (isKn)
                "ಕರ್ನಾಟಕದ ಧೀರ ವೀರರು"
            else
                "Karnataka's Brave Heroes"

        binding.btnLanguage.setOnClickListener {

            PrefManager.toggleLanguage(this)

            val intent = Intent(
                this,
                HomeActivity::class.java
            )

            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            )

            startActivity(intent)
            finish()

            overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
    }

    private fun setupHeroCarousel() {

        val isKn = isKannada()

        val districts = viewModel.getAllDistricts()

        if (districts.isNullOrEmpty()) {
            binding.heroCarousel.visibility = View.GONE
            return
        }

        // Null-safe flatten
        val allHeroes = districts.flatMap { district ->

            val heroList = district.heroes ?: emptyList()

            heroList.map { hero ->

                Pair(
                    hero,
                    if (isKn)
                        district.name_kn ?: district.name
                    else
                        district.name
                )
            }
        }

        if (allHeroes.isEmpty()) {
            binding.heroCarousel.visibility = View.GONE
            return
        }

        binding.heroCarousel.visibility = View.VISIBLE

        val adapter = HeroCarouselAdapter(
            allHeroes,
            isKn
        ) { hero ->

            val intent = Intent(
                this,
                StoryActivity::class.java
            )

            intent.putExtra(
                "hero_id",
                hero.id
            )

            startActivity(intent)

            overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.fade_out
            )
        }

        binding.heroCarousel.adapter = adapter
        binding.heroCarousel.offscreenPageLimit = 3

        val recyclerView =
            binding.heroCarousel.getChildAt(0) as RecyclerView

        recyclerView.clipToPadding = false
        recyclerView.overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        setup3DEffect()

        setupCarouselDots(
            allHeroes.size,
            0
        )

        binding.heroCarousel.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    setupCarouselDots(
                        allHeroes.size,
                        position
                    )
                }
            }
        )
    }

    private fun setup3DEffect() {

        val transformer =
            CompositePageTransformer()

        transformer.addTransformer(
            MarginPageTransformer(24)
        )

        transformer.addTransformer { page, position ->

            val ratio =
                1 - abs(position)

            page.scaleX =
                0.80f + ratio * 0.20f

            page.scaleY =
                0.80f + ratio * 0.20f

            page.rotationY =
                position * -15f

            page.alpha =
                0.5f + ratio * 0.5f

            page.translationZ =
                ratio * 8f
        }

        binding.heroCarousel.setPageTransformer(
            transformer
        )
    }

    private fun setupCarouselDots(
        total: Int,
        selected: Int
    ) {

        binding.carouselDots.removeAllViews()

        val visibleDots =
            minOf(total, 8)

        for (i in 0 until visibleDots) {

            val dot = View(this)

            val size =
                if (i == selected % visibleDots)
                    12
                else
                    7

            val params =
                android.widget.LinearLayout.LayoutParams(
                    dpToPx(size),
                    dpToPx(size)
                )

            params.marginStart =
                dpToPx(3)

            params.marginEnd =
                dpToPx(3)

            dot.layoutParams =
                params

            dot.background =
                if (i == selected % visibleDots)
                    getDrawable(
                        R.drawable.dot_selected
                    )
                else
                    getDrawable(
                        R.drawable.dot_unselected
                    )

            binding.carouselDots.addView(
                dot
            )
        }
    }

    private fun setupAnimations() {

        binding.btnExplore.animate()
            .scaleX(1.03f)
            .scaleY(1.03f)
            .setDuration(900)
            .withEndAction {

                binding.btnExplore.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(900)
                    .withEndAction {
                        setupAnimations()
                    }
                    .start()
            }
            .start()
    }

    private fun setupClickListeners() {

        binding.btnExplore.setOnClickListener { button ->

            button.animate()
                .scaleX(0.92f)
                .scaleY(0.92f)
                .setDuration(100)
                .withEndAction {

                    button.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()

                    startActivity(
                        Intent(
                            this,
                            DistrictMapActivity::class.java
                        )
                    )

                    overridePendingTransition(
                        android.R.anim.slide_in_left,
                        android.R.anim.fade_out
                    )
                }
                .start()
        }

        binding.navHome.setOnClickListener { }

        binding.navMap.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    DistrictMapActivity::class.java
                )
            )
        }

        binding.navRewards.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }
    }

    private fun updateBadgeCount() {

        viewModel.badgeCount.observe(this) { count ->

            binding.tvBadgeCount.text =
                "🏅 $count"
        }
    }

    private fun dpToPx(
        dp: Int
    ): Int {

        return (
                dp * resources.displayMetrics.density
                ).toInt()
    }
}