package com.nammakathey.data.model

data class HeroData(val districts: List<District>)

data class District(
    val id: String,
    val name: String,
    val name_kn: String,
    val theme: String,
    val image: String,
    val heroes: List<Hero>
)

data class Hero(
    val id: String,
    val name: String,
    val name_kn: String,
    val era: String,
    val category: String,
    val tagline: String,
    val tagline_kn: String,
    val image: String,
    val story_pages_en: List<String>,
    val story_pages_kn: List<String>,
    val quiz: List<QuizQuestion>,
    val statue: StatueInfo
)

data class QuizQuestion(
    val q_en: String,
    val q_kn: String,
    val options_en: List<String>,
    val options_kn: List<String>,
    val correct_index: Int
)

data class StatueInfo(
    val name: String,
    val address: String,
    val distance: String
)