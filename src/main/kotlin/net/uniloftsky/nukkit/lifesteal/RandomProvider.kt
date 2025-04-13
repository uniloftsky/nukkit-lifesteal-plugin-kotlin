package net.uniloftsky.nukkit.lifesteal

import java.util.concurrent.ThreadLocalRandom

fun interface RandomProvider {

    fun nextInt(bound: Int): Int

}

object DefaultRandomProvider : RandomProvider {

    override fun nextInt(bound: Int): Int {
        return ThreadLocalRandom.current().nextInt(bound)
    }
}