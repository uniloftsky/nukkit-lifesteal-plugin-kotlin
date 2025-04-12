package net.uniloftsky.nukkit.lifesteal

import cn.nukkit.plugin.PluginBase
import net.uniloftsky.nukkit.lifesteal.config.LifestealConfig
import net.uniloftsky.nukkit.lifesteal.listener.EventListener

class LifestealPlugin : PluginBase() {

    private lateinit var config: LifestealConfig
    private lateinit var lifestealCore: LifestealCore

    override fun onEnable() {
        this.config = LifestealConfig(this)
        val initialized = config.init()

        if (!initialized) {
            this.logger.error("Configuration cannot be initialized. Plugin will be disabled")
            this.server.pluginManager.disablePlugin(this)
            return
        }

        this.lifestealCore = LifestealCore(config)
        this.server.pluginManager.registerEvents(EventListener(this.logger, lifestealCore), this)
        this.logger.info("Lifesteal plugin enabled!")
    }

    override fun onDisable() {
        this.logger.info("Lifesteal plugin disabled!")
    }
}