package net.uniloftsky.nukkit.lifesteal

import cn.nukkit.Player
import cn.nukkit.item.Item
import cn.nukkit.level.particle.GenericParticle
import cn.nukkit.level.particle.Particle
import io.netty.util.internal.ThreadLocalRandom
import net.uniloftsky.nukkit.lifesteal.config.LifestealConfig

/**
 * Lifesteal core holds the logic regarding the lifesteal feature (incl. healing calculations, spawning particles etc.)
 */
class LifestealCore(
    private val config: LifestealConfig,
    private val randomProvider: RandomProvider = DefaultRandomProvider
) {

    companion object {

        /**
         * ID of Happy Villager particle
         */
        private const val PARTICLE_ID: Int = Particle.TYPE_VILLAGER_HAPPY

        /**
         * Amount of particles around the player
         */
        private const val PARTICLES_AMOUNT: Int = 20

        /**
         * Heal multiplier. It is needed because a player has 10 hearts but 20 HP
         */
        private const val HEAL_MULTIPLIER: Int = 2
    }

    fun healPlayer(target: Player, itemInHand: Item): Boolean {
        if (target.isOnline && target.isAlive && target.hasPermission(Permissions.LIFESTEAL_ABILITY_PERMISSION.permission)) {
            val randomLifestealChance = randomProvider.nextInt(100)
            val lifestealChance = config.lifestealChance
            if (randomLifestealChance <= lifestealChance) {
                val lifestealWeapon = config.getWeapon(itemInHand.id)
                lifestealWeapon?.let {
                    val dealtDamage = itemInHand.attackDamage
                    if (dealtDamage > 0) {
                        val amountOfHeal = calculateHealAmount(dealtDamage, lifestealWeapon.lifesteal)
                        target.heal(amountOfHeal)

                        // spawn healing particles
                        spawnHealingParticles(target)
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun spawnHealingParticles(target: Player) {
        for (i in 0 until PARTICLES_AMOUNT) {
            val x = ThreadLocalRandom.current().nextDouble(-1.5, 1.5)
            val z = ThreadLocalRandom.current().nextDouble(-1.5, 1.5)
            val y = ThreadLocalRandom.current().nextDouble(1.0, 2.0)
            val locationToSpawnParticles = target.add(x, y, z)
            target.getLevel().addParticle(GenericParticle(locationToSpawnParticles, PARTICLE_ID))
        }
    }

    private fun calculateHealAmount(dealtDamage: Int, lifesteal: Int): Float {
        return (dealtDamage.toFloat() / 100 * lifesteal) * HEAL_MULTIPLIER
    }

}