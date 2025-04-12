package net.uniloftsky.nukkit.lifesteal

enum class Permissions(
    val permission: String
) {

    LIFESTEAL_ABILITY_PERMISSION("uniloftsky.nukkit.lifesteal");

    override fun toString(): String {
        return "$name:$permission"
    }
}