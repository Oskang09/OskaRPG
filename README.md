# Discontinued Project

Mainly plan to build up own game server but lack of time to maintain it, so will treat it as a learning journey.

# Thing were build 

1. Using [hibernate](https://hibernate.org/) as ORM connecting with PostgreSQL.
2. Customizing server entity, weapon, items to make it look like a RPG focused system.
3. Apply top level listener to listen all RPG related events like DamageEvent, ShootEvent.
4. Custom vault system which built in by Minecraft ( Economy, Chat, Permission )
5. Build skill engine and dynamically load from jar to achieve hot reload when in game w/o restarting the servers.
6. Applying attributes stats to all of the playable entity ( player, mobs )
7. Build logger to track error via Discord Webhook
8. Custom function with other plugin to achieve moving house time to time ( WeHouse ) 

# Folder Structure

* `me.oska.minecraft` will be storing all minecraft related code, etc listeners, plugin main, gui.
* `me.oska.plugins` will be storing plugin related code, it can be expose an API for getting information or action.
* `me.oska.extension` will be storing extension code, etc Custom Skill or Setting.
