package com.nammakathey.ui.herolist

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammakathey.databinding.ActivityHeroListBinding
import com.nammakathey.ui.story.StoryActivity
import com.nammakathey.utils.BaseActivity
import com.nammakathey.utils.PrefManager
import com.nammakathey.viewmodel.MainViewModel

class HeroListActivity : BaseActivity() {

    private lateinit var binding: ActivityHeroListBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeroListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val districtId = intent.getStringExtra("district_id") ?: return
        val district = viewModel.getDistrictById(districtId) ?: return
        val isKn = isKannada()

        // Top bar title
        binding.tvSectionTitle.text = if (isKn) "ವೀರ ಗ್ಯಾಲರಿ" else "Hero Gallery"

        // Hero count
        binding.tvHeroCount.text =
            "${district.heroes.size} ${if (isKn) "ವೀರರು" else "Heroes"}"

        // Back button
        binding.btnBack.setOnClickListener { finish() }

        // Filter chips
        binding.chipAll.text      = if (isKn) "ಎಲ್ಲರೂ" else "All"
        binding.chipWarriors.text = if (isKn) "ಯೋಧರು" else "Warriors"
        binding.chipQueens.text   = if (isKn) "ರಾಣಿಯರು" else "Queens"
        binding.chipKings.text    = if (isKn) "ರಾಜರು" else "Kings"

        // Adapter
        val adapter = HeroAdapter(district.heroes, isKn) { hero ->
            val intent = Intent(this, StoryActivity::class.java)
            intent.putExtra("hero_id", hero.id)
            startActivity(intent)
            overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.fade_out
            )
        }
        binding.recyclerHeroes.layoutManager = LinearLayoutManager(this)
        binding.recyclerHeroes.adapter = adapter

        // Chip filters
        binding.chipAll.setOnClickListener      { adapter.filterByCategory(null) }
        binding.chipWarriors.setOnClickListener { adapter.filterByCategory("Warrior") }
        binding.chipQueens.setOnClickListener   { adapter.filterByCategory("Queen") }
        binding.chipKings.setOnClickListener    { adapter.filterByCategory("King") }

        // Language toggle
        binding.btnLanguage.text = if (isKn) "EN" else "ಕನ್ನಡ"
        binding.btnLanguage.setOnClickListener {
            PrefManager.toggleLanguage(this)
            val intent = Intent(this, HeroListActivity::class.java)
            intent.putExtra("district_id", districtId)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
    }
}