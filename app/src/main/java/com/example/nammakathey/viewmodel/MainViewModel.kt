package com.nammakathey.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.nammakathey.data.db.Badge
import com.nammakathey.data.model.District
import com.nammakathey.data.model.Hero
import com.nammakathey.data.repository.BadgeRepository
import com.nammakathey.data.repository.HeroRepository
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val heroRepo = HeroRepository(app)
    private val badgeRepo = BadgeRepository(app)

    val allBadges: LiveData<List<Badge>> = badgeRepo.allBadges
    val badgeCount: LiveData<Int> = badgeRepo.badgeCount

    fun getAllDistricts(): List<District> = heroRepo.getAllDistricts()

    fun getDistrictById(id: String): District? = heroRepo.getDistrictById(id)

    fun getHeroById(id: String): Hero? = heroRepo.getHeroById(id)

    fun getHeroWithDistrict(id: String) = heroRepo.getHeroWithDistrict(id)

    fun saveBadge(heroId: String, heroName: String, category: String, district: String) {
        viewModelScope.launch {
            badgeRepo.saveBadge(
                Badge(heroId = heroId, heroName = heroName,
                    heroCategory = category, districtName = district)
            )
        }
    }

    suspend fun badgeExists(heroId: String) = badgeRepo.badgeExists(heroId)
}