package net.uniloftsky.nukkit.lifesteal.listener

import cn.nukkit.Player
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.plugin.PluginLogger
import net.uniloftsky.nukkit.lifesteal.LifestealCore

class EventListener(
    private val logger: PluginLogger,
    private val lifestealCore: LifestealCore
) : Listener {

    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        if (event.damager is Player) {
            val player = event.damager as Player
            val itemInHand = player.inventory.itemInHand
            lifestealCore.healPlayer(player, itemInHand)
        }
    }


}