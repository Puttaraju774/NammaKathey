package com.nammakathey.ui.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nammakathey.databinding.ItemStoryPageBinding

class StoryPagerAdapter(
    private val pages: List<String>
) : RecyclerView.Adapter<StoryPagerAdapter.StoryVH>() {

    // Chapter titles to display above story text
    private val chapterTitles = listOf("Chapter 1", "Chapter 2", "Chapter 3", "Chapter 4", "Chapter 5")

    inner class StoryVH(val b: ItemStoryPageBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StoryVH(ItemStoryPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = pages.size

    override fun onBindViewHolder(holder: StoryVH, position: Int) {
        with(holder.b) {
            tvStoryText.text = pages[position]
            tvChapter.text   = chapterTitles.getOrElse(position) { "Chapter ${position + 1}" }

            // Animate text fade in on each page
            tvStoryText.alpha = 0f
            tvStoryText.animate().alpha(1f).setDuration(400).start()
        }
    }
}