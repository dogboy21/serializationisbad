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
  - The fix is now also available on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/serializationisbad) (the Modrinth release is currently under review)
- Add the JAR file to your mods folder
- ~~Download the latest config file from [this Github repository](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json) and add it directly to your instances config directory~~  Version 1.3 of the patch now automatically uses the the latest version of [the config file](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json)

### Any other instances

- Download the JAR file from the latest release on the [releases page](https://github.com/dogboy21/serializationisbad/releases) and save it somewhere
- Add the following JVM argument to your client/server (refer to the documentation of the client/server launcher you are using on how to do this): `-javaagent:<PATH TO SAVED JAR FILE>`
- ~~Download the latest config file from [this Github repository](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json) and add it directly to your instances config directory~~ Version 1.3 of the patch now automatically uses the the latest version of [the config file](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json)

## Affected mods

Unlike the blog post stated above, there are plenty of mods that are and could be affected by this issue. Although some of them are already fixed in their latest versions, these mods were exploitable in at least one older verison. It is to be expected that several modpacks over the years just are not maintained anymore, but are still popular and loved within the community. Keeping this in-mind, we are trying to help those people who still love running those modpacks and strive to keep them safe as they play. 

**KEEP IN MIND THAT THIS LIST IS DEFINITELY NOT COMPLETE! THESE ARE JUST THE MODS WE ARE CURRENTLY AWARE OF!** Curseforge is already investigating the issue internally so we can maybe get a nearly complete list of vulnerable mods and versions in the future.

We have moved our affected mods list to another location! See the link below:

**[Affected Mods List](docs/mods.md)**

This list will change frequently as we find more mods that could have vulnerabilities and developers add patches to their own mods. If you want to help us in keeping this list up-to-date, please feel free to contribute! 


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
