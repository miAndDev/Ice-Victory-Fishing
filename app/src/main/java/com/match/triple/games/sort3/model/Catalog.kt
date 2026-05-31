package com.match.triple.games.sort3.model

import androidx.annotation.DrawableRes
import com.match.triple.games.sort3.R

/** Rarity buckets drive spawn weighting, reward, and whether a catch is a trophy. */
enum class Rarity { COMMON, RARE, LEGENDARY }

/**
 * Static definition of a fish species. These are immutable catalog entries — the live,
 * moving instances on screen are [com.match.triple.games.sort3.game.Fish] objects that
 * merely reference a [FishType] by [id].
 *
 * Every species maps to one of the migrated `fish_sprite*` drawables (originally the
 * `Rectangle*` mipmaps).
 */
data class FishType(
    val id: String,
    val displayName: String,
    @param:DrawableRes val spriteRes: Int,
    val rarity: Rarity,
    /** Base reward in points and coins. */
    val points: Int,
    val coins: Int,
    /** Multiplies base size — bigger fish are easier to hit but worth more. */
    val sizeFactor: Float,
    /** Multiplies base horizontal swim speed — faster fish are harder to catch. */
    val speedFactor: Float,
    /** Relative likelihood of spawning (before luck adjustments). */
    val spawnWeight: Float
) {
    val isTrophy: Boolean get() = rarity != Rarity.COMMON
}

/** The nine fish species, one per `fish_sprite*` asset. */
object FishCatalog {
    val all: List<FishType> = listOf(
        FishType("anchovy", "Anchovy", R.drawable.fish_sprite, Rarity.COMMON, 10, 1, 0.85f, 1.10f, 5f),
        FishType("sardine", "Sardine", R.drawable.fish_sprite_1, Rarity.COMMON, 15, 1, 0.90f, 1.05f, 5f),
        FishType("mackerel", "Mackerel", R.drawable.fish_sprite_2, Rarity.COMMON, 20, 2, 1.00f, 1.00f, 4f),
        FishType("bass", "Sea Bass", R.drawable.fish_sprite_3, Rarity.COMMON, 30, 3, 1.05f, 0.95f, 3.5f),
        FishType("snapper", "Red Snapper", R.drawable.fish_sprite_4, Rarity.RARE, 60, 6, 1.10f, 1.15f, 1.6f),
        FishType("tuna", "Bluefin Tuna", R.drawable.fish_sprite_5, Rarity.RARE, 90, 9, 1.30f, 1.25f, 1.2f),
        FishType("pufferfish", "Pufferfish", R.drawable.fish_sprite_6, Rarity.RARE, 120, 12, 0.95f, 1.40f, 0.9f),
        FishType("swordfish", "Swordfish", R.drawable.fish_sprite_7, Rarity.LEGENDARY, 200, 20, 1.45f, 1.55f, 0.5f),
        FishType("goldenkoi", "Golden Koi", R.drawable.fish_sprite_8, Rarity.LEGENDARY, 350, 35, 1.20f, 1.70f, 0.25f),
    )

    private val byId = all.associateBy { it.id }
    fun byId(id: String): FishType? = byId[id]
}

/**
 * A purchasable, multi-level upgrade. [effectPerLevel] is applied additively to a base
 * gameplay parameter in the ViewModel, so buying instantly changes how the game plays.
 */
data class Booster(
    val id: String,
    val displayName: String,
    val description: String,
    val maxLevel: Int,
    val baseCost: Int,
    /** Cost of the next level grows linearly: baseCost * (currentLevel + 1). */
    val effectPerLevel: Float
) {
    fun costForLevel(currentLevel: Int): Int = baseCost * (currentLevel + 1)
}

object BoosterCatalog {
    const val REEL_SPEED = "reel_speed"
    const val HOOK_SIZE = "hook_size"
    const val LUCK = "luck"

    val all: List<Booster> = listOf(
        Booster(
            id = REEL_SPEED,
            displayName = "Reel Speed",
            description = "Raise the hook faster after a bite.",
            maxLevel = 5,
            baseCost = 25,
            effectPerLevel = 0.20f // +20% reel speed per level
        ),
        Booster(
            id = HOOK_SIZE,
            displayName = "Bigger Hook",
            description = "Wider catch radius — snag fish more easily.",
            maxLevel = 5,
            baseCost = 30,
            effectPerLevel = 0.15f // +15% hook radius per level
        ),
        Booster(
            id = LUCK,
            displayName = "Lucky Lure",
            description = "Attract more rare fish and bonus coins.",
            maxLevel = 5,
            baseCost = 40,
            effectPerLevel = 0.25f // +25% rare weighting per level
        ),
    )

    private val byId = all.associateBy { it.id }
    fun byId(id: String): Booster? = byId[id]
}
