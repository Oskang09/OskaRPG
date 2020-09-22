# Folder Structure

* `me.oska.minecraft` will be storing all minecraft related code, etc listeners, plugin main, gui.
* `me.oska.plugins` will be storing plugin related code, it can be expose an API for getting information or action.
* `me.oska.extension` will be storing extension code, etc Custom Skill or Setting.

# Using Database ( Hibernate & PostgreSQL )

Currently, all database action will be using `AbstractRepository<Model>`, it comes with basic crud and can be run asynchronously.

# Environment Variables

Variables | Usage
 --- | --- 
 OSKARPG_SERVER_ID | Define current server using which ORPGServer instance to setup