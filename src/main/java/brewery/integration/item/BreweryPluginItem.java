package main.java.brewery.integration.item;

import main.java.brewery.Brew;
import main.java.brewery.recipe.BRecipe;
import main.java.brewery.recipe.PluginItem;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * For recipes that use Brewery Items as input
 */
public class BreweryPluginItem extends PluginItem {

// When implementing this, put Brewery as softdepend in your plugin.yml!
// We're calling this as server start:
// PluginItem.registerForConfig("brewery", BreweryPluginItem::new);

	@Override
	public boolean matches(ItemStack item) {
		Brew brew = Brew.get(item);
		if (brew != null) {
			BRecipe recipe = brew.getCurrentRecipe();
			if (recipe != null) {
				return recipe.getRecipeName().equalsIgnoreCase(getItemId()) || recipe.getName(10).equalsIgnoreCase(getItemId());
			}
			return ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase(getItemId());
		}
		return false;
	}
}
