# Unsafe Deserialization Vulnerability in Many Minecraft Mods

A few weeks ago, a critical vulnerability allowing arbitrary remote code execution on clients and servers (and therefore all connected clients on a server) was discovered in many Minecraft mods.

Initially, we were trying to investigate the whole issue privately and responsibly so we can publish an extensive write-up and fix the vulnerability entirely, but since a group
named MMPA just published a [blog post](https://blog.mmpa.info/posts/bleeding-pipe/) about the issue, completely missing many important factors about the vulnerability, we were forced to release a statement and attempt to fix the issue immediately since at
the current time, they were putting millions of modded Minecraft users at risk.

## Information on the Vulnerability

The vulnerability is caused by an unsafe use of the Java serialization feature in network packets sent by servers to clients or clients to servers that allows to instantiate any Java class that is loaded in the Minecraft instance.

There was already a similar vulnerability in the past called "Mad Gadget". You can read more about that here:
- https://opensource.googleblog.com/2017/03/operation-rosehub.html
- https://foxglovesecurity.com/2015/11/06/what-do-weblogic-websphere-jboss-jenkins-opennms-and-your-application-have-in-common-this-vulnerability/

While there are just a relatively small amount of attacks targeting this vulnerability in the wild, because of the significance of the vulnerability, it is extremely dangerous to play with the unpatched mods currently.
Attackers have already attempted (and succeeded in some cases) in gaining access to Microsoft tokens and browser session data. Since they can execute any code they want on a target system, the possibilities are endless.

## How Can I Protect Myself Against the Vulnerability?

We developed a patcher that attempts to fix all mods that we know of (list of mods is below).

~~Should any more affected mods be discovered, a patch is as simple as updating the related config file. (We will publish a release that automates this for you)~~ Version 1.3 of the patch now automatically uses the the latest version of [the config file](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json) and otherwise falls back to the local config file. If there's no config present, there should be an error informing the user that there are currently no patches applied.

### Minecraft Forge 1.7.x - Latest

- Download the JAR file from the latest release on the [releases page](https://github.com/dogboy21/serializationisbad/releases)
  - The fix is now also available on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/serializationisbad) and [Modrinth](https://modrinth.com/mod/serializationisbad)
- Add the JAR file to your mods folder
- ~~Download the latest config file from [this Github repository](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json) and add it directly to your instances config directory~~  Version 1.3 of the patch now automatically uses the the latest version of [the config file](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json)

### Any Other Instances

- Download the JAR file from the latest release on the [releases page](https://github.com/dogboy21/serializationisbad/releases) (or alternatively from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/serializationisbad) or [Modrinth](https://modrinth.com/mod/serializationisbad)) and save it somewhere
- Add the following JVM argument to your client/server (refer to the documentation of the client/server launcher you are using on how to do this): `-javaagent:<PATH TO SAVED JAR FILE>`
- ~~Download the latest config file from [this Github repository](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json) and add it directly to your instances config directory~~ Version 1.3 of the patch now automatically uses the the latest version of [the config file](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json)

## Affected Mods

Unlike the blog post stated above, there are plenty of mods that are and could be affected by this issue. Although some of them are already fixed in their latest versions, these mods were exploitable in at least one older version. It is to be expected that several modpacks over the years just are not maintained anymore, but are still popular and loved within the community. Keeping this in-mind, we are trying to help those people who still love running those modpacks and strive to keep them safe as they play. 

**KEEP IN MIND THAT THIS LIST IS DEFINITELY NOT COMPLETE! THESE ARE JUST THE MODS WE ARE CURRENTLY AWARE OF!** CurseForge is already investigating the issue internally so we hope we can get a nearly complete list of vulnerable mods and versions in the future.

**Also please keep in mind that this is a VULNERABILITY (one that also affected many other software projects in the past), not something added to the affected mods with any malicious intents. So don't harass any mod authors because their mods are affected and also don't call affected mods viruses or something else. That's just completely wrong.**

We have moved our affected mods list to another location! See the link below:

**[Affected Mods List](docs/mods.md)**

This list will change frequently as we find more mods that could have vulnerabilities and as developers add patches to their own mods. If you want to help us in keeping this list up-to-date, please feel free to contribute! 

## Technical Approach

Our current approach to fixing the vulnerability is having a config file with all currently known mod classes that need to be patched.
SIB then checks these classes at runtime and replaces the exploitable calls to `ObjectInputStream` with our safe
[ClassFilteringObjectInputStream](https://github.com/dogboy21/serializationisbad/blob/master/core/src/main/java/io/dogboy/serializationisbad/core/ClassFilteringObjectInputStream.java)
that only allows the deserialization of classes that are on an allowlist in the config file.

This approach has the advantage that we only modify/block confirmed vulnerable uses of `ObjectInputStream`,
while leaving other secure and miscellaneous uses of this class completely unaffected.
As a result, the risk of potential gamebreaking issues caused by over-blocking is heavily minimized.
With this approach, we also don't have any incompatibilities with older Java versions.

This means that we need to add all vulnerable mods to our [config file](https://github.com/dogboy21/serializationisbad/blob/master/serializationisbad.json) so they get patched.
Possible new cases of mods that are vulnerable are not patched unless added to the above mentioned config file.
We're currently working on a good approach to also patch all other uses of `ObjectInputStream` in a safe way without breaking any mods in the process (see [#15](https://github.com/dogboy21/serializationisbad/pull/15) and [#18](https://github.com/dogboy21/serializationisbad/issues/18)).

## Credits

I'm not the only one that was working on the investigation of the whole situation.

Credits to anyone that was involved in this:

- Aidoneus (MineYourMind Server Network)
- Bennyboy1695 (Shadow Node Server Network)
- bziemons (Logistics Pipes Mod Developer)
- DarkStar (CraftDownUnder Server Network)
- Dogboy21 (MyFTB Server Network)
- Einhornyordle (MyFTB Server Network)
- emily (CraftDownUnder Server Network)
- Exa (Nomifactory Modpack Developer)
- HanoverFist (MineYourMind Server Network)
- Heather (MMCC Server Network)
- HellFirePvP (Astral Sorcery Mod Developer)
- Jacob (DirtCraft Server Network)
- Juakco_ (CraftDownUnder Server Network)
- Lìam (MineYourMind Server Network)
- MojangPlsFix (MyFTB Server Network)
- Niels Pilgaard (Enigmatica Modpack Developer)
- oliviajumba (CraftDownUnder Server Network)
- oly2o6 (All the Mods Modpack Developer / Akliz Server Hosting)
- PurpleIsEverything (Shadow Node Server Network)
- Pyker (Technic Launcher Developer)
- RyanTheAllmighty (ATLauncher Developer)
- Saereth (Modpack Developer)
- Sauramel (CraftDownUnder Server Network)
- ThePixelbrain (MMCC Server Network)
- Tridos (DirtCraft Server Network)
