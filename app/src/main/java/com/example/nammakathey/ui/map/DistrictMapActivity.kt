package com.nammakathey.ui.map

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nammakathey.databinding.ActivityDistrictMapBinding
import com.nammakathey.ui.herolist.HeroListActivity
import com.nammakathey.utils.BaseActivity
import com.nammakathey.viewmodel.MainViewModel
import com.nammakathey.utils.PrefManager

class DistrictMapActivity : BaseActivity() {

    private lateinit var binding: ActivityDistrictMapBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: DistrictAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDistrictMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupToolbar()
        setupRecycler()
        setupSearch()
        setupLanguageToggle()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { finish() }
        binding.tvTitle.text = if (isKannada()) "ಜಿಲ್ಲೆ ಆಯ್ಕೆ ಮಾಡಿ" else "Pick Your Journey!"
        binding.tvSubtitle.text = if (isKannada())
            "ನಿಮ್ಮ ಜಿಲ್ಲೆ ಆಯ್ಕೆ ಮಾಡಿ"
        else "Which district shall we explore today?"
    }

    private fun setupRecycler() {
        val districts = viewModel.getAllDistricts()
        val isKn = isKannada()

        adapter = DistrictAdapter(districts, isKn) { district ->
            val intent = Intent(this, HeroListActivity::class.java)
            intent.putExtra("district_id", district.id)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
        }

        binding.recyclerDistricts.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerDistricts.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.hint = if (isKannada()) "ಜಿಲ್ಲೆ ಹುಡುಕಿ..." else "Search district..."
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupLanguageToggle() {
        binding.btnLanguage.text = if (isKannada()) "EN" else "ಕನ್ನಡ"
        binding.btnLanguage.setOnClickListener {
            PrefManager.toggleLanguage(this)
            val intent = Intent(this, DistrictMapActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}