package com.nammakathey.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nammakathey.data.model.Hero
import com.nammakathey.databinding.ItemHeroCarouselBinding

class HeroCarouselAdapter(
    private val heroes: List<Pair<Hero, String>>,
    private val isKannada: Boolean,
    private val onClick: (Hero) -> Unit
) : RecyclerView.Adapter<HeroCarouselAdapter.CarouselVH>() {

    private val categoryEmojis = mapOf(
        "Warrior" to "⚔️", "Queen" to "👑", "King" to "🏰",
        "Saint" to "🙏", "Artist" to "🎵", "Military" to "🎖️",
        "Engineer" to "⚙️", "Scholar" to "📚", "Explorer" to "🧭",
        "Leader" to "🏛️", "Folk Hero" to "🌟"
    )

    inner class CarouselVH(val b: ItemHeroCarouselBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CarouselVH(ItemHeroCarouselBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = heroes.size

    override fun onBindViewHolder(holder: CarouselVH, position: Int) {
        val (hero, districtName) = heroes[position]
        with(holder.b) {
            tvCarouselHeroName.text = if (isKannada) hero.name_kn else hero.name
            tvCarouselTagline.text  = if (isKannada) hero.tagline_kn else hero.tagline
            tvCarouselCategory.text = hero.category
            tvCarouselDistrict.text = "📍 $districtName"
            tvCarouselEra.text      = "⏳ ${hero.era}"
            tvHeroEmoji.text        = categoryEmojis[hero.category] ?: "⭐"
            btnCarouselRead.text    = if (isKannada) "ಕಥೆ ಓದಿ →" else "Read Story →"
            btnCarouselRead.setOnClickListener { onClick(hero) }
            root.setOnClickListener { onClick(hero) }
        }
    }
}