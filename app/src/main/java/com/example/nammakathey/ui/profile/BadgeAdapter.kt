package com.nammakathey.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nammakathey.data.db.Badge
import com.nammakathey.databinding.ItemBadgeBinding

class BadgeAdapter(
    private val badges: List<Badge>,
    private val isKannada: Boolean
) : RecyclerView.Adapter<BadgeAdapter.BadgeVH>() {

    inner class BadgeVH(val b: ItemBadgeBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BadgeVH(ItemBadgeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = badges.size

    override fun onBindViewHolder(holder: BadgeVH, position: Int) {
        val badge = badges[position]
        with(holder.b) {
            tvBadgeHeroName.text    = badge.heroName
            tvBadgeCategory.text    = badge.heroCategory
            tvBadgeDistrict.text    = badge.districtName

            // Animate each badge entry
            root.alpha = 0f
            root.translationY = 30f
            root.animate()
                .alpha(1f).translationY(0f)
                .setDuration(300)
                .setStartDelay((position * 80).toLong())
                .start()
        }
    }
}