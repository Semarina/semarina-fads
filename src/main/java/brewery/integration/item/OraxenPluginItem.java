package brewery.integration.item;

import brewery.P;
import brewery.filedata.BConfig;
import brewery.recipe.PluginItem;
import io.lumine.mythic.lib.api.item.NBTItem;

import java.util.Set;

import org.bukkit.inventory.ItemStack;

public class OraxenPluginItem extends PluginItem {

	@Override
	public boolean matches(ItemStack item) {
		try {
			NBTItem nbtItem = NBTItem.get(item);
			Set<String> tags = nbtItem.getTags();
			
			if (tags.contains("PublicBukkitValues")) {
				var pbv = nbtItem.getNBTCompound("PublicBukkitValues");
				if (pbv.hasTag("oraxen:id")) {
					 return pbv.getString("oraxen:id").toLowerCase().equals(getItemId().toLowerCase());
				}
			}
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			P.p.errorLog("Could not check MMOItems for Item ID");
			BConfig.hasMMOItems = false;
			return false;
		}
	}
}
