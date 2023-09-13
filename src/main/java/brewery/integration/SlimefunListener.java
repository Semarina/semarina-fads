package brewery.integration;

import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import brewery.Brew;
import brewery.P;
import brewery.filedata.BConfig;
import brewery.integration.item.SlimefunPluginItem;
import brewery.recipe.BCauldronRecipe;
import brewery.recipe.RecipeItem;
import brewery.utility.LegacyUtil;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class SlimefunListener implements Listener {

	/**
	 * Catch the Slimefun Right Click event, to cancel it if the right click was on a Cauldron.
	 * This prevents item consumption while adding to the cauldron
	 */
	@EventHandler
	public void onCauldronClickSlimefun(PlayerRightClickEvent event) {
		try {
			if (event.getClickedBlock().isPresent() && event.getHand() == EquipmentSlot.HAND) {
				if (LegacyUtil.isWaterCauldron(event.getClickedBlock().get().getType())) {
					Optional<SlimefunItem> slimefunItem = event.getSlimefunItem();
					if (slimefunItem.isPresent()) {
						for (RecipeItem rItem : BCauldronRecipe.acceptedCustom) {
							if (rItem instanceof SlimefunPluginItem) {
								if (slimefunItem.get().getId().equalsIgnoreCase(((SlimefunPluginItem) rItem).getItemId())) {
									event.cancel();
									P.p.playerListener.onPlayerInteract(event.getInteractEvent());
									return;
								}
							}
						}
					}
				}
			}
		} catch (Throwable e) {
			HandlerList.unregisterAll(this);
			P.p.errorLog("Slimefun check failed");
			e.printStackTrace();
		}
	}
}
