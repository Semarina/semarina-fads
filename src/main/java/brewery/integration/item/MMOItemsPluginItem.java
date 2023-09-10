package main.java.brewery.integration.item;

import main.java.brewery.P;
import main.java.brewery.filedata.BConfig;
import main.java.brewery.recipe.PluginItem;
import io.lumine.mythic.lib.api.item.NBTItem;
import org.bukkit.inventory.ItemStack;

public class MMOItemsPluginItem extends PluginItem {

// When implementing this, put Brewery as softdepend in your plugin.yml!
// We're calling this as server start:
// PluginItem.registerForConfig("mmoitems", MMOItemsPluginItem::new);

	@Override
	public boolean matches(ItemStack item) {
		if (BConfig.hasMMOItems == null) {
			BConfig.hasMMOItems = P.p.getServer().getPluginManager().isPluginEnabled("MMOItems")
				&& P.p.getServer().getPluginManager().isPluginEnabled("MythicLib");
		}
		if (!BConfig.hasMMOItems) return false;

		try {
			NBTItem nbtItem = NBTItem.get(item);
			return nbtItem.hasType() && nbtItem.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(getItemId());
		} catch (Throwable e) {
			e.printStackTrace();
			P.p.errorLog("Could not check MMOItems for Item ID");
			BConfig.hasMMOItems = false;
			return false;
		}
	}
}
