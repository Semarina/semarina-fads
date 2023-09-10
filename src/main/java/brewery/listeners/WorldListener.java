package main.java.brewery.listeners;

import main.java.brewery.BCauldron;
import main.java.brewery.Barrel;
import main.java.brewery.P;
import main.java.brewery.Wakeup;
import main.java.brewery.filedata.BConfig;
import main.java.brewery.filedata.BData;
import main.java.brewery.filedata.DataSave;
import main.java.brewery.utility.BUtil;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListener implements Listener {

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		final World world = event.getWorld();
		if (BConfig.loadDataAsync) {
			P.p.getServer().getScheduler().runTaskAsynchronously(P.p, () -> lwDataTask(world));
		} else {
			lwDataTask(world);
		}
	}

	private void lwDataTask(World world) {
		if (!BData.acquireDataLoadMutex()) return;  // Tries for 60 sec

		try {
			if (world.getName().startsWith("DXL_")) {
				BData.loadWorldData(BUtil.getDxlName(world.getName()), world);
			} else {
				BData.loadWorldData(world.getUID().toString(), world);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BData.releaseDataLoadMutex();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldUnload(WorldUnloadEvent event) {
		World world = event.getWorld();
		if (DataSave.running == null) {
			// No datasave running, save data if we have any in that world
			if (Barrel.hasDataInWorld(world) || BCauldron.hasDataInWorld(world)) {
				DataSave.unloadingWorlds.add(world);
				DataSave.save(false);
			}
		} else {
			// already running, tell it to unload world
			DataSave.unloadingWorlds.add(world);
		}
	}

}
