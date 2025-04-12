package net.uniloftsky.nukkit.lifesteal.config

import cn.nukkit.item.Item
import com.google.gson.Gson
import com.google.gson.JsonParser
import net.uniloftsky.nukkit.lifesteal.LifestealPlugin
import java.io.File
import java.io.IOException
import java.nio.file.Files

/**
 * Class to hold configurable data. It holds information about lifesteal chance, registered weapons and its lifesteal potential
 */
class LifestealConfig(
    private val plugin: LifestealPlugin,
    private val gson: Gson = Gson(),
    private val pluginDataFolder: File = plugin.dataFolder,
    private val _weapons: MutableMap<Int, LifestealWeapon> = mutableMapOf(),
    private var _lifestealChance: Int = 0,
    private var configInitialized: Boolean = false
) {

    val lifestealChance: Int
        get() {
            isInitialized()
            return _lifestealChance
        }

    val weapons: List<LifestealWeapon>
        get() {
            isInitialized()
            return _weapons.values.toList()
        }

    companion object {
        private const val UNKNOWN_ITEM = "unknown"

        const val MAIN_CONFIG = "config.json"
    }

    fun init(): Boolean {
        plugin.logger.info("Loading configuration...")
        configInitialized = processMainConfig()
        return configInitialized
    }

    fun getWeapon(weaponId: Int): LifestealWeapon? {
        isInitialized()
        return _weapons[weaponId]
    }

    private fun processMainConfig(): Boolean {
        plugin.logger.info("Loading $MAIN_CONFIG")
        plugin.saveResource(MAIN_CONFIG)

        val configContents = try {
            getConfigContents(MAIN_CONFIG)
        } catch (ex: IOException) {
            plugin.logger.error("Cannot get $MAIN_CONFIG file")
            return false
        }

        val configObject = JsonParser.parseString(configContents).asJsonObject

        // retrieve chance of lifesteal from config
        configObject.get(MainConfigFields.LIFESTEAL_CHANCE_FIELD)?.let {
            _lifestealChance = it.asInt
        }

        // retrieve weapons and register them
        val jsonWeapons = configObject.getAsJsonArray(MainConfigFields.WEAPONS_LIST_FIELD).asList()
        jsonWeapons.forEach {
            val weapon = gson.fromJson(it, LifestealWeapon::class.java)
            registerWeapon(weapon)
        }

        return true
    }

    private fun getConfigContents(configName: String): String {
        val configPath = pluginDataFolder.toPath().resolve(configName)
        return String(Files.readAllBytes(configPath))
    }

    private fun registerWeapon(weapon: LifestealWeapon) {
        if (weapon.id > 0) {
            val minecraftItem = Item.get(weapon.id)
            if (!isWeaponItemValid(minecraftItem)) {
                plugin.logger.warning("Cannot register a weapon with ID ${weapon.id}. It eithier doesn't exist or is not a weapon")
            } else {
                weapon.name = minecraftItem.name
                _weapons[minecraftItem.id] = weapon
                plugin.logger.info("Registered weapon: $weapon")
            }
        }
    }

    /**
     * Check if the imported from config weapon is a valid minecraft weapon item
     *
     * @param item item to check
     * @return true if valid, false if not
     */
    private fun isWeaponItemValid(item: Item): Boolean {
        return !item.isNull && !item.name.equals(UNKNOWN_ITEM, true) && (item.isAxe || item.isSword)
    }

    private fun isInitialized() {
        if (!configInitialized) {
            throw RuntimeException("Config wasn't initialized properly")
        }
    }
}

object MainConfigFields {
    const val LIFESTEAL_CHANCE_FIELD = "chance"
    const val WEAPONS_LIST_FIELD = "weapons"
}

data class LifestealWeapon(
    val id: Int,
    val lifesteal: Int,
    var name: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LifestealWeapon

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}