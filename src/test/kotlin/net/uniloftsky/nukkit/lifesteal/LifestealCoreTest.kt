package net.uniloftsky.nukkit.lifesteal

import cn.nukkit.Player
import cn.nukkit.item.Item
import cn.nukkit.level.Level
import cn.nukkit.level.Location
import cn.nukkit.level.particle.GenericParticle
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import net.uniloftsky.nukkit.lifesteal.config.LifestealConfig
import net.uniloftsky.nukkit.lifesteal.config.LifestealWeapon
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class LifestealCoreTest {

    @MockK
    private lateinit var config: LifestealConfig

    @MockK
    private lateinit var randomProvider: RandomProvider

    @InjectMockKs
    private lateinit var lifestealCore: LifestealCore

    @Test
    fun testHealPlayer() {

        // given
        val lifestealChance = 25
        every { randomProvider.nextInt(100) } returns lifestealChance
        every { config.lifestealChance } returns lifestealChance

        val mockedPlayer: Player = buildMockedPlayer().also {
            every { it.heal(any(Float::class)) } just Runs

            // Mocking for the spawn of particles
            every { it.add(any(Double::class), any(Double::class), any(Double::class)) } returns mockk<Location>()
            val level: Level =
                mockk<Level>().also { level -> every { level.addParticle(any(GenericParticle::class)) } just Runs }
            every { it.getLevel() } returns level
        }

        val weaponId = 123
        val mockedItemInHand: Item = mockk<Item>().also {
            every { it.id } returns weaponId
            every { it.attackDamage } returns 15
        }

        val lifestealWeapon: LifestealWeapon = mockk<LifestealWeapon>().also {
            every { it.lifesteal } returns 100
            every { config.getWeapon(weaponId) } returns it
        }

        // when
        val result = lifestealCore.healPlayer(mockedPlayer, mockedItemInHand)

        // then
        assertTrue(result)

        val healAmount = (mockedItemInHand.attackDamage.toFloat() / 100 * lifestealWeapon.lifesteal) * 2
        verify(exactly = 1) { mockedPlayer.heal(healAmount) }
    }

    @Test
    fun testHealPlayerOffline() {

        // given
        val mockedPlayer = buildMockedPlayer(isOnline = false)

        // when
        val result = lifestealCore.healPlayer(mockedPlayer, mockk())

        // then
        assertFalse(result)
        verify(exactly = 0) { mockedPlayer.heal(any(Float::class)) }
    }

    @Test
    fun testHealPlayerIsNotAlive() {

        // given
        val mockedPlayer = buildMockedPlayer(isAlive = false)

        // when
        val result = lifestealCore.healPlayer(mockedPlayer, mockk())

        // then
        assertFalse(result)
        verify(exactly = 0) { mockedPlayer.heal(any(Float::class)) }
    }

    @Test
    fun testHealPlayerNoPermission() {

        // given
        val mockedPlayer = buildMockedPlayer(hasPermission = false)

        // when
        val result = lifestealCore.healPlayer(mockedPlayer, mockk())

        // then
        assertFalse(result)
        verify(exactly = 0) { mockedPlayer.heal(any(Float::class)) }
    }

    @Test
    fun testHealPlayerBadChance() {

        // given
        val mockedPlayer = buildMockedPlayer()
        every { randomProvider.nextInt(100) } returns 50
        every { config.lifestealChance } returns 25

        // when
        val result = lifestealCore.healPlayer(mockedPlayer, mockk())

        // then
        assertFalse(result)
        verify(exactly = 0) { mockedPlayer.heal(any(Float::class)) }
    }

    private fun buildMockedPlayer(
        isOnline: Boolean = true,
        isAlive: Boolean = true,
        hasPermission: Boolean = true
    ): Player {
        return mockk<Player>().also {
            every { it.isOnline } returns isOnline
            every { it.isAlive } returns isAlive
            every { it.hasPermission(Permissions.LIFESTEAL_ABILITY_PERMISSION.permission) } returns hasPermission
        }
    }
}