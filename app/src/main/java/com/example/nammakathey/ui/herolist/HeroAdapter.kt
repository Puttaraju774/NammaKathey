package com.nammakathey.ui.herolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nammakathey.data.model.Hero
import com.nammakathey.databinding.ItemHeroBinding

class HeroAdapter(
    private val allHeroes: List<Hero>,
    private val isKannada: Boolean,
    private val onClick: (Hero) -> Unit
) : RecyclerView.Adapter<HeroAdapter.HeroVH>() {

    private var displayList = allHeroes.toMutableList()

    private val categoryEmojis = mapOf(
        "Warrior"          to "⚔️",
        "Queen"            to "👑",
        "King"             to "🏰",
        "Saint"            to "🙏",
        "Artist"           to "🎵",
        "Military"         to "🎖️",
        "Engineer"         to "⚙️",
        "Scholar"          to "📚",
        "Explorer"         to "🧭",
        "Leader"           to "🏛️",
        "Folk Hero"        to "🌟",
        "Saints"           to "🙏",
        "Freedom Fighters" to "🇮🇳"
    )

    inner class HeroVH(val b: ItemHeroBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HeroVH(
            ItemHeroBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount() = displayList.size

    override fun onBindViewHolder(holder: HeroVH, position: Int) {
        val hero = displayList[position]
        with(holder.b) {
            tvHeroName.text    = if (isKannada) hero.name_kn else hero.name
            tvHeroTagline.text = "\"${if (isKannada) hero.tagline_kn else hero.tagline}\""
            tvHeroEra.text     = "📍 ${hero.era}"
            tvCategory.text    = hero.category
            tvHeroEmoji.text   = categoryEmojis[hero.category] ?: "⭐"
            btnReadStory.setOnClickListener { onClick(hero) }
            root.setOnClickListener { onClick(hero) }
        }
    }

    fun filterByCategory(category: String?) {
        displayList = if (category == null) {
            allHeroes.toMutableList()
        } else {
            allHeroes.filter {
                it.category.contains(category, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}