package net.uniloftsky.nukkit.lifesteal.listener

import cn.nukkit.Player
import cn.nukkit.entity.passive.EntityJumpingAnimal
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.inventory.PlayerInventory
import cn.nukkit.item.Item
import cn.nukkit.plugin.PluginLogger
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import net.uniloftsky.nukkit.lifesteal.LifestealCore
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class EventListenerTest {

    @MockK
    private lateinit var logger: PluginLogger

    @MockK
    private lateinit var lifestealCore: LifestealCore

    @InjectMockKs
    private lateinit var eventListener: EventListener

    @Test
    fun testOnAttack() {

        // given
        val event: EntityDamageByEntityEvent = mockk()
        val mockedPlayer: Player = mockk()
        every { event.damager } returns mockedPlayer

        val playerInventory: PlayerInventory = mockk()
        every { mockedPlayer.inventory } returns playerInventory

        val itemInHand: Item = mockk()
        every { playerInventory.itemInHand } returns itemInHand

        every { lifestealCore.healPlayer(mockedPlayer, itemInHand) } returns true

        // when
        eventListener.onAttack(event)

        // then
        verify(exactly = 1) { lifestealCore.healPlayer(mockedPlayer, itemInHand) }
    }

    @Test
    fun testOnAttackNotPlayer() {

        // given
        val event: EntityDamageByEntityEvent = mockk()
        val mockedEntity: EntityJumpingAnimal = mockk()
        every { event.damager } returns mockedEntity

        // when
        eventListener.onAttack(event)

        // then
        verify(exactly = 0) { lifestealCore.healPlayer(any(Player::class), any(Item::class)) }
    }
}