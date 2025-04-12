package net.uniloftsky.nukkit.lifesteal.config

import cn.nukkit.item.Item
import cn.nukkit.plugin.PluginLogger
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import net.uniloftsky.nukkit.lifesteal.LifestealPlugin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@ExtendWith(MockKExtension::class)
class LifestealConfigTest {

    companion object {
        private const val VALID_WEAPON_NAME = "Wooden Sword"
        private const val INVALID_WEAPON_NAME = "unknown"
        private const val MOCKED_JSON = "{\"chance\":25,\"weapons\":[{\"id\":268,\"lifesteal\":10}]}"

        /* These values should be defined respectively to MOCKED_JSON */
        private const val WEAPON_ID = 268
        private const val LIFESTEAL_POTENTIAL = 10
        private const val LIFESTEAL_CHANCE = 25
    }

    @MockK
    private lateinit var plugin: LifestealPlugin

    @MockK
    private lateinit var pluginDataFolder: File

    @MockK
    private lateinit var logger: PluginLogger

    @InjectMockKs
    private lateinit var config: LifestealConfig

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockLogger()
        every { plugin.saveResource(LifestealConfig.MAIN_CONFIG) } returns true
    }

    @Test
    fun testInit() {

        // given
        mockGetConfigContents()
        mockRegisterWeapon(WEAPON_ID)

        // when
        val result = config.init()

        // then
        assertTrue { result }
        verify(exactly = 3) { logger.info(any<String>()) }
        verify { plugin.saveResource(LifestealConfig.MAIN_CONFIG) }
        assertTrue(config.weapons.isNotEmpty())
        assertTrue(config.weapons.size == 1)
        assertEquals(LIFESTEAL_CHANCE, config.lifestealChance)
    }

    @Test
    fun testInitCannotGetConfig() {

        // given
        mockGetConfigContents(true)

        // when
        val result = config.init()

        // then
        assertFalse(result)
        verify(exactly = 1) { logger.error(any<String>()) }
    }

    @Test
    fun testInitConfigInvalidWeapon() {

        // given
        mockGetConfigContents()
        mockRegisterWeapon(WEAPON_ID, false)

        // when
        val result = config.init()

        // then
        assertTrue(result)
        assertTrue(config.weapons.isEmpty())
    }

    @Test
    fun testGetWeapon() {

        // given
        mockGetConfigContents()
        mockRegisterWeapon(WEAPON_ID)
        config.init()

        // when
        val result = config.getWeapon(WEAPON_ID)

        // then
        assertNotNull(result)
        assertEquals(WEAPON_ID, result?.id)
        assertEquals(LIFESTEAL_POTENTIAL, result?.lifesteal)
    }

    @Test
    fun testGetWeaponConfigNotInitialized() {
        assertThrows(RuntimeException::class.java) { config.getWeapon(WEAPON_ID) }
    }

    @Test
    fun testGetWeapons() {

        // given
        mockGetConfigContents(false)
        mockRegisterWeapon(WEAPON_ID)
        config.init()

        val result = config.weapons

        assertNotNull(result)
        assertFalse(result.isEmpty())
        assertTrue(result.size == 1)
    }

    @Test
    fun testGetWeaponsConfigNotInitialized() {
        assertThrows(RuntimeException::class.java) { config.weapons }
    }

    private fun mockGetConfigContents(withException: Boolean = false) {
        val mockedPath = mockk<Path>()
        every { pluginDataFolder.toPath() } returns mockedPath
        every { mockedPath.resolve(LifestealConfig.MAIN_CONFIG) } returns mockedPath

        mockkStatic(Files::class)
        if(withException) {
            every { Files.readAllBytes(mockedPath) } throws IOException("Some exception")
        } else {
            every { Files.readAllBytes(mockedPath) } returns MOCKED_JSON.toByteArray()
        }
    }

    private fun mockRegisterWeapon(weaponId: Int, validWeapon: Boolean = true) {
        mockkStatic(Item::class)
        val mockedItem = mockk<Item>()
        every { Item.get(weaponId) } returns mockedItem
        every { mockedItem.id } returns WEAPON_ID
        every { mockedItem.isNull } returns false
        every { mockedItem.name } returns if(validWeapon) VALID_WEAPON_NAME else INVALID_WEAPON_NAME
        every { mockedItem.isSword } returns true
        every { mockedItem.isAxe } returns false
    }

    // Invoke if logger should be mocked
    private fun mockLogger() {
        every { plugin.logger } returns logger
        every { logger.info(any<String>()) } just Runs
        every { logger.warning(any<String>()) } just Runs
        every { logger.error(any<String>()) } just Runs
    }
}