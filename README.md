# Nukkit Lifesteal plugin

Plugin for Nukkit that adds a Lifesteal feature. Written in Kotlin. Origin: [Lifesteal Plugin](https://github.com/uniloftsky/nukkit-lifesteal-plugin)

# Demo
![Demo GIF](./demo.gif)

# Configuration

This plugin can be configured via the `config.json` file. It contains the following:

```{
  "chance": 25,
  "weapons": [
    {
      "id": 268,
      "lifesteal": 10
    },
    {
      "id": 271,
      "lifesteal": 10
    }
  ]
}
```

Where `chance` defines the probability of lifesteal occurrence.<br>
The list of `weapons` includes the items for which the lifesteal feature is enabled. Each weapon object consists an
`id` (the corresponding item ID) and `lifesteal`, which specifies the lifesteal potential of the weapon.

# Permissions

You can define whether the lifesteal feature is enabled for a specific player by using the following permission:
`uniloftsky.nukkit.lifesteal`

# What is Nukkit?

Nukkit Server Software (https://cloudburstmc.org/articles/) is used to run a game servers for Minecraft:
Bedrock Edition.
