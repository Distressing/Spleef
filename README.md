Spleef Plugin,

This is a simple spleef pluging. The plugin stores all player data on a MongoDB and saves arena data to flat file.

How it works is you setup arenas using the commands and you can create multiple games that use the same arena build.Keep in mind when the game is created the arenas origin point is set to the players current position.As I have already said we store all player data in a Mongo Database this data includes the player UUID, their wins and their match losses. This plugin is still very much in a Beta state however is stable from my testing. If you encounter any issues ensure to create a pull request.
