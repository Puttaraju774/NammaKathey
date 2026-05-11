package com.nammakathey.data.repository

import android.content.Context
import com.google.gson.Gson
import com.nammakathey.data.model.District
import com.nammakathey.data.model.Hero
import com.nammakathey.data.model.HeroData

class HeroRepository(private val context: Context) {

    private val gson = Gson()
    private var cachedData: HeroData? = null

    private fun getData(): HeroData {
        if (cachedData == null) {
            val json = context.assets.open("heroes.json")
                .bufferedReader().use { it.readText() }
            cachedData = gson.fromJson(json, HeroData::class.java)
        }
        return cachedData!!
    }

    fun getAllDistricts(): List<District> = getData().districts

    fun getDistrictById(id: String): District? =
        getData().districts.find { it.id == id }

    fun getHeroById(heroId: String): Hero? =
        getData().districts.flatMap { it.heroes }.find { it.id == heroId }

    fun getHeroWithDistrict(heroId: String): Pair<Hero, District>? {
        getData().districts.forEach { district ->
            district.heroes.forEach { hero ->
                if (hero.id == heroId) return Pair(hero, district)
            }
        }
        return null
    }

    fun getAllHeroes(): List<Hero> =
        getData().districts.flatMap { it.heroes }
}