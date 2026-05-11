package com.nammakathey.ui.map

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nammakathey.data.model.District
import com.nammakathey.databinding.ItemDistrictBinding

class DistrictAdapter(
    private val allDistricts: List<District>,
    private val isKannada: Boolean,
    private val onClick: (District) -> Unit
) : RecyclerView.Adapter<DistrictAdapter.DistrictVH>() {

    private var filteredList =
        allDistricts.toMutableList()

    private val cardColors = listOf(
        "#E8A020",
        "#1B3A6B",
        "#C0392B",
        "#27AE60",
        "#8E44AD",
        "#2980B9",
        "#D35400",
        "#16A085"
    )

    inner class DistrictVH(
        val binding: ItemDistrictBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DistrictVH {

        return DistrictVH(
            ItemDistrictBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(
        holder: DistrictVH,
        position: Int
    ) {

        val district =
            filteredList[position]

        with(holder.binding) {

            // Safe district name
            tvDistrictName.text =
                if (isKannada)
                    district.name_kn ?: district.name
                else
                    district.name

            // Safe theme
            tvDistrictTheme.text =
                district.theme ?: ""

            // Safe heroes count
            val heroCount =
                district.heroes?.size ?: 0

            tvHeroCount.text =
                "$heroCount ${
                    if (isKannada)
                        "ವೀರರು"
                    else
                        "Heroes"
                }"

            // Card accent colors
            val color =
                Color.parseColor(
                    cardColors[
                        position % cardColors.size
                    ]
                )

            accentBar.setBackgroundColor(
                color
            )

            tvDistrictTheme.setTextColor(
                color
            )

            root.setOnClickListener {

                root.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(80)
                    .withEndAction {

                        root.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(80)
                            .start()

                        onClick(
                            district
                        )
                    }
                    .start()
            }
        }
    }

    fun filter(
        query: String
    ) {

        filteredList =
            if (query.isEmpty()) {

                allDistricts.toMutableList()

            } else {

                allDistricts.filter {

                    (it.name ?: "").contains(
                        query,
                        true
                    ) ||

                            (it.name_kn ?: "").contains(
                                query,
                                true
                            )

                }.toMutableList()
            }

        notifyDataSetChanged()
    }
}