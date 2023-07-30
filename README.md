# Unsafe Deserialization Vulnerability in many Minecraft mods

A few weeks ago, a very critical vulnerability allowing arbitrary remote code execution on clients and servers (and therefor even all connected clients on a server) was discovered in many Minecraft mods.

Initially we were trying to investigate the whole issue privately and responsible so we can publish an extensive writeup and fix about the whole situation but since a group
named MMPA just published a [blog post](https://blog.mmpa.info/posts/bleeding-pipe/) about the issue, completely missing many important factors about the issue, we were forced to release a statement and attempt to fix the issue immediately since at
the current time they're literally putting millions of modded Minecraft users at risk.

## Information on the vulnerability

The vulnerability is caused by an unsafe use of the Java serialization feature in network packets sent by servers to clients or clients to servers that allows to instantiate any Java class that is loaded in the Minecraft instance.

There was already a similar vulnerability in the past called "Mad Gadget". You can read more about that here:
- https://opensource.googleblog.com/2017/03/operation-rosehub.html
- https://foxglovesecurity.com/2015/11/06/what-do-weblogic-websphere-jboss-jenkins-opennms-and-your-application-have-in-common-this-vulnerability/

While there are just a relatively small amount of attacks targetting this vulnerability in the wild, because of the significance of the vulnerability, it is completely dangerous to play with unpatched mods currently.
Attackers already attempted (and succeeded in some cases) Microsoft access token and browser session steals. But since they can literally execute any code they want on a target system, the possibilities are endless.

## How to protect against the vulnerability?

We developed a patcher that attempts to fix all currently known affected mods (listed below).

~~Should any more affected mods be discovered, a patch is as simple as updating the related config file. (We will publish a relesae that automates this for you)~~ Version 1.3 of the patch now automatically uses the the latest version of [the config file](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json) and otherwise falls back to the local config file. If there's no config present, there should be an error informing the user that there are currently no patches applied.

### Minecraft Forge 1.7.x - latest

- Download the JAR file from the latest release on the [releases page](https://github.com/dogboy21/serializationisbad/releases)
- Add the JAR file to your mods folder
- ~~Download the latest config file from [this Github repository](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json) and add it directly to your instances config directory~~  Version 1.3 of the patch now automatically uses the the latest version of [the config file](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json)

### Any other instances

- Download the JAR file from the latest release on the [releases page](https://github.com/dogboy21/serializationisbad/releases) and save it somewhere
- Add the following JVM argument to your client/server (refer to the documentation of the client/server launcher you are using on how to do this): `-javaagent:<PATH TO SAVED JAR FILE>`
- ~~Download the latest config file from [this Github repository](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json) and add it directly to your instances config directory~~ Version 1.3 of the patch now automatically uses the the latest version of [the config file](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json)

## Affected mods

Unlike stated in the above blog post, there are plenty more mods that are affected by this issue.
Although some of them already are fixed in the latest versions, these mods were exploitable in at least one older version:

**KEEP IN MIND THAT THIS LIST IS DEFINITELY NOT COMPLETE. THESE ARE JUST THE MODS WE ARE CURRENTLY AWARE OF. At least Curseforge is already investigating the issue internally so we can maybe get a nearly complete list of vulnerable mods and versions in the future.**

Because of the rushed announcement, we are currently unable to give exact version ranges of affected mods. If you want to help out with that, feel free to contribute to this list.

- [AetherCraft](https://www.curseforge.com/minecraft/mc-mods/aec)
- [Advent of Ascension (Nevermine)](https://www.curseforge.com/minecraft/mc-mods/advent-of-ascension-nevermine) (Only affects versions for Minecraft 1.12.2)
- [Arrows Plus](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1290719-1-6-2-ssp-smp-arrows-plus-v1-0-0-minecraft)
- [AsieLib](https://wiki.vexatos.com/wiki:asielib) (Fixed in version 0.5.4 by [unofficial fork](https://github.com/GTNewHorizons/AsieLib))
- [Astral Sorcery](https://www.curseforge.com/minecraft/mc-mods/astral-sorcery) (affected versions: <=1.9.1)
- [BdLib](https://www.curseforge.com/minecraft/mc-mods/bdlib) (Only affects versions for Minecraft 1.7.10-1.16.5. Fixed in version 1.16.0.6 for Minecraft 1.16.5. [relevant commit](https://github.com/bdew-minecraft/bdlib/commit/447210530ceec72fb3374efecb0930ed359d2297) Fixed in version 1.9.8-GTNH for Minecraft 1.7.10 by [unofficial fork](https://github.com/GTNewHorizons/bdlib))
- [Carbonization](https://www.curseforge.com/minecraft/mc-mods/carbonization)
- [CreativeCore](https://www.curseforge.com/minecraft/mc-mods/creativecore) (Only affects versions for Minecraft 1.7.10. Fixed in version 1.3.31-GTNH by [unofficial fork](https://github.com/GTNewHorizons/CreativeCore))
- [Custom Friends Capes](https://www.curseforge.com/minecraft/mc-mods/custom-friends-capes)
- [CustomOreGen](https://www.curseforge.com/minecraft/mc-mods/customoregen)
- [DankNull](https://www.curseforge.com/minecraft/mc-mods/dank-null)
- [Energy Manipulation](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1290125-1-6-4-1-6-2-1-5-2-1-4-7-energy-manipulation-1-1)
- [EnderCore](https://www.curseforge.com/minecraft/mc-mods/endercore) (Only affects versions for Minecraft 1.7.10-1.13. Fixed in the following versions: 0.5.77 for MC 1.12.2, 0.4.1.67-beta for MC 1.10.2, 0.2.0.40_beta for 1.7.10)
- [EndermanEvolution](https://www.curseforge.com/minecraft/mc-mods/enderman-evolution)
- Extrafirma
- [Gadomancy](https://www.curseforge.com/minecraft/mc-mods/gadomancy) (Fixed in version 1.1.2 by [unofficial fork](https://github.com/GTNewHorizons/Gadomancy))
- [Giacomo's Bookshelf](https://www.curseforge.com/minecraft/mc-mods/giacomos-bookshelf)
- [Immersive Armors](https://www.curseforge.com/minecraft/mc-mods/immersive-armors) (Fixed in version 1.5.6 for Minecraft 1.18.2, 1.19.2-1.19.4, 1.20, versions for 1.16.5, 1.17.1, 1.18.1, 1.19.0, 1.19.1 remain affected, [relevant commit](https://github.com/Luke100000/ImmersiveArmors/issues/68))
- [Immersive Aircraft](https://www.curseforge.com/minecraft/mc-mods/immersive-aircraft)
- [Immersive Paintings](https://www.curseforge.com/minecraft/mc-mods/immersive-paintings)
- [JourneyMap](https://www.curseforge.com/minecraft/mc-mods/journeymap) (Fixed introduced in 1.16.5-5.7.1 and fixed in 1.16.5-5.7.2 No other versions were effected)
- [LanteaCraft / SGCraft](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1292427-lanteacraft)
- [LogisticsPipes](https://www.curseforge.com/minecraft/mc-mods/logistics-pipes) (Only affects versions for Minecraft 1.4.7-1.7.10. Fixed in version 0.10.0.71 for MC 1.7.10, [relevant security advisory](https://github.com/RS485/LogisticsPipes/security/advisories/GHSA-mcp7-xf3v-25x3))
- [Minecraft Comes Alive (MCA)](https://www.curseforge.com/minecraft/mc-mods/minecraft-comes-alive-mca) (Only affects versions for Minecraft 1.5.2-1.6.4)
- [MattDahEpic Core (MDECore)](https://www.curseforge.com/minecraft/mc-mods/mattdahepic-core) (Only affects versions for Minecraft 1.8.8-1.12.2)
- [mxTune](https://www.curseforge.com/minecraft/mc-mods/mxtune) (Only affects versions for Minecraft 1.12-1.16.5)
- [p455w0rd's Things](https://www.curseforge.com/minecraft/mc-mods/p455w0rds-things)
- [Project Blue](https://www.csse.canterbury.ac.nz/greg.ewing/minecraft/mods/ProjectBlue/) (Fixed in version 1.1.12-GTNH by [unofficial fork](https://github.com/GTNewHorizons/ProjectBlue))
- [RadixCore](https://www.curseforge.com/minecraft/mc-mods/radixcore)
- [RebornCore](https://www.curseforge.com/minecraft/mc-mods/reborncore) (affected versions: >= 3.13.8, <4.7.3, [relevant security advisory](https://github.com/TechReborn/RebornCore/security/advisories/GHSA-r7pg-4xrf-7mrm))
- [SimpleAchievements](https://www.curseforge.com/minecraft/mc-mods/simple-achievements)
- [SmartMoving](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1274224-smart-moving)
- [Strange](https://www.curseforge.com/minecraft/mc-mods/strange)
- [SuperMartijn642's Config Lib](https://www.curseforge.com/minecraft/mc-mods/supermartijn642s-config-lib) (Fixed in version 1.0.9, [relevant security advisory](https://github.com/SuperMartijn642/SuperMartijn642sConfigLib/security/advisories/GHSA-f4r5-w453-2jx6))
- [TecTech](https://github.com/Technus/TecTech) (Fixed in version 5.2.38 by [unofficial fork](https://github.com/GTNewHorizons/TecTech))
- [Thaumic Tinkerer](https://www.curseforge.com/minecraft/mc-mods/thaumic-tinkerer) (Fixed in version 2.3-138 for Minecraft 1.7.2, versions for 1.6-1.6.4 remain affected, [relevant commit](https://github.com/Thaumic-Tinkerer/ThaumicTinkerer/commit))
- [Tough Expansion](https://www.curseforge.com/minecraft/mc-mods/tough-expansion)
- [ttCore](https://www.curseforge.com/minecraft/mc-mods/ttcore) (Only affects versions for Minecraft 1.7.10)

## Credits

I'm not the only one that was working on the investigation of the whole situation.

Credits to anyone that was involved in this:

- Aidoneus (MineYourMind Server Network)
- bziemons (Logistics Pipes Mod Developer)
- Bennyboy1695 (Shadow Node Server Network)
- Dogboy21 (MyFTB Server Network)
- Einhornyordle (MyFTB Server Network)
- emily (CraftDownUnder Server Network)
- Exa (Nomifactory Modpack Developer)
- HanoverFist (MineYourMind Server Network)
- HellFirePvP (Astral Sorcery Mod Developer)
- Jacob (DirtCraft Server Network)
- Juakco_ (CraftDownUnder Server Network)
- Lìam (MineYourMind Server Network)
- MojangPlsFix (MyFTB Server Network)
- Heather (MMCC Server Network)
- Niels Pilgaard (Enigmatica Modpack Developer)
- oliviajumba (CraftDownUnder Server Network)
- oly2o6 (All the Mods Modpack Developer / Akliz Server Hoster)
- PurpleIsEverything (Shadow Node Server Network)
- Pyker (Technic Launcher Developer)
- RyanTheAllmighty (ATLauncher Developer)
- Saereth (Modpack Developer)
- Sauramel (CraftDownUnder Server Network)
- ThePixelbrain (MMCC Server Network)
- Tridos (DirtCraft Server Network)
- DarkStar (CraftDownUnder Server Network)
