

# Overview 
Enchantments+- is a custom enchantment plugin that is made for Prison and PVP servers. With enchants ranging from Token-giving blocks to explosive pickaxes, Enchantments+- is the solution to your custom enchanting needs.  

# Features  
Easily customizable custom enchants  
A special currency called Tokens that can be easily integrated with voting systems, crate systems, etc  
An easy-to-use API to make your own custom enchants  
PAPI Support, use %eps_tokens% to display your tokens!  

# Commands  
/enchants - Opens the enchant GUI  
/tokens [player] - Shows how many tokens [player] has. If [player] is blank, shows how many tokens you have  
/eps reload - Reloads everything.  
/eps changetokens [player] [amount] - Increases/decreases the amount of tokens [player] by [amount]  
/eps settokens [player] [amount] - Sets the amount of tokens [player] to [amount]  
/scrap - Destroys the current tool a player is holding in return for tokens. Can be disabled with permissions.  
/paytokens [player] [amount] - Pays [player] [amount] tokens. Player-only command.  

# Supported Versions:  
1.12.2+  
Anything below is broken.  


# Known bugs:  
NoClassDefFoundError (This can be fixed with a simple /reload or if you have Plugman, /plugman reload EnchantmentsPlusMinus)  

# Registered built-in enchants:  

#### Prison enchantments
Haste: Gives a random chance to apply Haste while mining  
Explosive: Has a chance to blow up blocks. Works with fortune!  
Excavate: Has a chance to blow a cube of blocks. Works with fortune!  
Diamond: Has a chance to blow up blocks in a diamond shape. Works with fortune!  
Autosmelt: Has a chance to automatically smelt items. Works with fortune!  
Telepathy: Automatically transfers items to your inventory.  
TokenBlocks: Has a chance to give tokens while mining.  
TokenCharity: Has a chance to give everyone tokens while mining.  
MoneyBlocks: Has a chance to give money while mining. (Vault needed)  
Charity: Has a chance to give everyone money while mining. (Vault needed)  

#### PVP enchantments
Jagged: Increases attack damage of a weapon if its durability is low  
Retaliate: Gives a very temporary strength boost if you get hit  
Lifesteal: Gives you health in return for attacking someone  
Momentum: Gives you a speed boost when attacking someone  
Poisonous: Poisons anyone who touches you!  
Volcanic: Hot! Sets anyone who touches you on fire.  
Saturated: Gives you saturation when hit!  
Insatiable: Deals more damage the less health you have.  
Beheading: Has a higher chance to give the head of a mob or player.  
Stiffen: Will give resistance if you have low health!  
Last Resort: Your attacks will deal 3x damage if you have very low health!  

#### Bow enchantments  
Enderbow: Allows you to teleport easily with bows! (Must Shift while shooting until teleported)  
Machinery: Send down a barrage of arrows on your opponents! (Only activates every few shots, can be configured)  
Energized: Shoot yourself to gain a temporary speed and regeneration buff!  
Shockwave: Damages all entities near the place you shot your arrow on!  

# Permissions  

#### Player commands  
eps.enchants - Gives access to /enchants.  
eps.tokens - Gives access to /tokens.  
eps.scrap - Gives access to /scrap.  
eps.paytokens - Gives access to /paytokens.  

#### Admin commands  
eps.admin.reload - Gives access to /eps reload.  
eps.admin.settokens - Gives access to /eps settokens.  
eps.admin.changetokens - Gives access to /eps changetokens.  
eps.admin.enchant - Gives access to /eps enchant.  
eps.admin.bypassmaxlevel - Allows bypassing of max level in the enchant GUI  
eps.admin.bypassincompatibilities - Allows bypassing of incompatibilities of enchants in the enchant GUI  

# Misc  
The plugin is in an early release stage.  
You can report bugs or crashes at https://github.com/dsdd/EnchantmentsPlusMinus/issues or in the Discord server listed below.  

# Discord  
It's getting harder to manage issues now, so there's a Discord server now! (please note this server is just a ticket system for me to personally respond asap)  
https://discord.gg/D8HujTT4bU  
