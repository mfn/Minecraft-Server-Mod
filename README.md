Minecraft Server Mod
====================
This is a server mod for the Minecraft Alpha server software. This is only the
source code, if you don't know how to use this you won't find it useful.

Compiling
---------
Add a new project from existing sources in whatever IDE you use. Add
minecraft_server.jar as a library, and mysql-connector-java-bin.jar if need be.
Your IDE should compile .class files that you can copy over into a clean
minecraft_server.jar, then you can test your changes.

Download
--------
You can download precompiled binaries from:
* http://hey0.net/get.php?dl=serverbeta - BETA version (latest updated)
* http://hey0.net/get.php?dl=serverstable - Stable version: OUTDATED!

Installing
----------
Simply extract the files somewhere, open the bin folder and run either
minecraft_mod.jar (if you want a GUI), or server_nogui.bat (if you don't want a
GUI). It'll automatically download the latest server from minecraft's servers.
Next you'll probably want to add yourself as an admin. Read over users.txt to
see what you have to do. If you're still confused here's an example:

    hey0:admins


That would give the person named 'hey0' admin. Just remember when editing any
of the configuration files, never add a space unless you're adding a kit (so
you can add a specific amount of an item).

Features
--------
* Colored, group-specific names that can be changed in server.properties
* Lots of commands (I'll go into specific below)
* Kits, and can be group-specific so you can have builder-specific kits.
* Player whitelist
* Adjustable player limit
* MOTD
* Usergroups (default, builder, mod and admin)
* Ability to change the position of /home
* Several administration commands like /ban, /banip, /unban, /kick, etc.
* Llama's mod integration (/lighter). Download llama's mod from here
* Configurable item spawn blacklist
* And much more...

Commands
--------
* /help [Page] - Shows a list of commands. 7 per page.
* /playerlist - Shows a list of players
* /reload - Reloads config
* /listbans <IP or bans> - Gives a list of bans
* /banip [Player] <Reason> - Bans the player's IP
* /unbanip [IP] - Unbans the IP
* /ban [Player] <Reason> - Bans the player
* /unban [Player] - Unbans the player
* /mute [Player] - Toggles mute on player.
* /tp [Player] - Teleports to player. Credits to Zet from SA
* /tphere [Player] - Teleports the player to you
* /kick [Player] <Reason> - Kicks player
* /item [ID] [Amount] <Player> - Gives items
* /kit [Kit] - Gives a kit. To get a list of kits type /kit
* /listwarps - Gives a list of available warps
* /home - Teleports you home
* /sethome - Sets your home
* /setspawn - Sets the spawn point to your position.
* /me [Message] -* hey0 says hi!
* /msg [Player] [Message] - Sends a message to player
* /spawn - Teleports you to spawn
* /warp [Warp] - Warps to the specified warp.
* /setwarp [Warp] - Sets the warp to your current position.
* /removewarp [Warp] - Removes the specified warp.
* /getpos - Displays your current position.
* /compass - Gives you a compass reading.
* /time [Time|day|night] - Changes time
* /lighter - Gives you a lighter for lighting furnaces
* /motd - Displays the MOTD
* /modify [player] [key] [value] - Type /modify for more info
* /whitelist [operation (add or remove)] [player]
* /reservelist [operation (add or remove)] [player]

Settings
--------
* data-source - Can be flatfile or mysql. If set to mysql, it will generate a mysql.properties for you to configure.
* alloweditems - A whitelist of allowed items for use in /item
* disalloweditems - A blacklist of disallowed items for use in /item
* itemspawnblacklist - A blacklist of items that can't be spawned (IE. Things like bedrock, lava, water, etc). Add IDs seperated by a comma.
* motd - The motd of the server. Seperate new lines with @
* max-players - The player limit. Defaults to 20.
* save-homes - If set to false it will no longer save homes.
* itemstxtlocation - The location of items.txt
* homelocation - The location of homes.txt
* kitstxtlocation - The location of kits.txt
* admintxtlocation - The location of users.txt (Was admins.txt before)
* homelocation - Location to save homes
* warplocation - Location to save warps
* group-txt-location - Location to read groups from
* whitelist-txt-location - Location to read/write the player whitelist
* reservelist-txt-location - Location to read/write the reserve list
* spawn-protection-size - Sets the spawn protection size
* reload-interval - The interval that the server reloads configuration files
* save-interval - The interval that the server save-alls (Default half-hour)
* logging - Enabling this will cause every command used to be logged to the logs.
