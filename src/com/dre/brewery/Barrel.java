package com.dre.brewery;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Barrel {

public static CopyOnWriteArrayList<Barrel> barrels = new CopyOnWriteArrayList<Barrel>();

//private CopyOnWriteArrayList<Brew> brews = new CopyOnWriteArrayList<Brew>();
private Block spigot;
private Inventory inventory;
private float time;

	public Barrel(Block spigot){
		this.spigot = spigot;
	}

	public static void onUpdate(){
		Block broken;
		for(Barrel barrel:barrels){
			broken = getBrokenBlock(barrel.spigot);
			//remove the barrel if it was destroyed
			if(broken != null){
				barrel.remove(broken);
			} else {
				//Minecraft day is 20 min, so add 1/20 to the time every minute
				barrel.time += 1.0/20.0;
			}
		}
	}

	//player opens the barrel
	public void open(Player player){
		if(inventory == null){
			inventory = org.bukkit.Bukkit.createInventory(null, 54, "Fass");
		} else {
			//if nobody has the inventory opened
			if(inventory.getViewers().isEmpty()){
				//if inventory contains potions
				if(inventory.contains(373)){
					for(ItemStack item:inventory.getContents()){
						if(item != null){
							if(item.getTypeId() == 373){
								if(item.hasItemMeta()){
									Brew.age(item,time,getWood());
								}
							}
						}
					}
				}
			}
		}
		//reset barreltime, potions have new age
		time = 0;
		player.openInventory(inventory);
	}

	public static Barrel get(Block spigot){
		for(Barrel barrel:barrels){
			if(barrel.spigot.equals(spigot)){
				return barrel;
			}
		}
		return null;
	}

	//creates a new Barrel out of a sign
	public static boolean create(Block block){
		Block spigot = getSpigotOfSign(block);
		if(getBrokenBlock(spigot) == null){
			if(get(spigot) == null){
				barrels.add(new Barrel(spigot));
				return true;
			}
		}
		return false;
	}

	//removes a barrel, throwing included potions to the ground
	public void remove(Block broken){
		if(inventory != null){
			ItemStack[] items = inventory.getContents();
			for(ItemStack item:items){
				if(item != null){
					if(item.getTypeId() == 373){
						//Brew before throwing
						Brew.age(item,time,getWood());
					}
					//broken is the block that was broken, throw them there!
					if(broken != null){
						broken.getLocation().getWorld().dropItem(broken.getLocation(), item);
					} else {
						spigot.getLocation().getWorld().dropItem(spigot.getLocation(), item);
					}
				}
			}
		}
		barrels.remove(this);
	}

	//direction of the barrel from the spigot
	public static int getDirection(Block spigot){
		int direction = 0;//1=x+  2=x-  3=z+  4=z-
		if(spigot.getRelative(0,0,1).getTypeId() == 5){
			direction = 3;
		}
		if(spigot.getRelative(0,0,-1).getTypeId() == 5){
			if(direction == 0){
				direction = 4;
			} else {
				return 0;
			}
		}
		if(spigot.getRelative(1,0,0).getTypeId() == 5){
			if(direction == 0){
				direction = 1;
			} else {
				return 0;
			}
		}
		if(spigot.getRelative(-1,0,0).getTypeId() == 5){
			if(direction == 0){
				direction = 2;
			} else {
				return 0;
			}
		}
		return direction;
	}

	//woodtype of the block the spigot is attached to
	public byte getWood(){
		int direction = getDirection(this.spigot);//1=x+  2=x-  3=z+  4=z-
		Block wood = null;
		if(direction == 0){
			return 0;
		} else if (direction == 1){
			wood = this.spigot.getRelative(1,0,0);
		} else if (direction == 2){
			wood = this.spigot.getRelative(-1,0,0);
		} else if (direction == 3){
			wood = this.spigot.getRelative(0,0,1);
		} else {
			wood = this.spigot.getRelative(0,0,-1);
		}
		if(wood.getTypeId() == 5){
			return wood.getData();
		}
		return 0;
	}

	//returns null if Barrel is correctly placed, block that is missing when not
	//the barrel needs to be formed correctly
	public static Block getBrokenBlock(Block spigot){
		if(spigot == null){
			return spigot;
		}
		int direction = getDirection(spigot);//1=x+  2=x-  3=z+  4=z-
		if(direction == 0){
			return spigot;
		}
		int startX = 0;
		int startZ = 0;
		int endX;
		int endZ;

		if (direction == 1){
			startX = 1;
			endX = startX + 3;
			startZ = -1;
			endZ = 1;
		} else if (direction == 2){
			startX = -4;
			endX = startX + 3;
			startZ = -1;
			endZ = 1;
		} else if (direction == 3){
			startX = -1;
			endX = 1;
			startZ = 1;
			endZ = startZ + 3;
		} else {
			startX = -1;
			endX = 1;
			startZ = -4;
			endZ = startZ + 3;
		}

		int typeId;
		int x = startX;
		int y = 0;
		int z = startZ;
		//P.p.log("startX="+startX+" startZ="+startZ+" endX="+endX+" endZ="+endZ+" direction="+direction);
		while(y <= 2){
			while(x <= endX){
				while(z <= endZ){
					typeId = spigot.getRelative(x,y,z).getTypeId();
					//spigot.getRelative(x,y,z).setTypeId(1);
					if(direction == 1 || direction == 2){
						if(y == 1 && z == 0){
							if(x == -2 || x == -3 || x == 2 || x == 3){
								z++;
								continue;
							} else if (x == -1 || x == -4 || x == 1 || x == 4){
								if(typeId != 0){
									z++;
									continue;
								}
							}
						}
					} else {
						if(y == 1 && x == 0){
							if(z == -2 || z == -3 || z == 2 || z == 3){
								z++;
								continue;
							} else if (z == -1 || z == -4 || z == 1 || z == 4){
								if(typeId != 0){
									z++;
									continue;
								}
							}
						}
					}
					if(typeId == 5 || typeId == 53 || typeId == 134 || typeId == 135 || typeId == 136){
						z++;
						continue;
					} else {
						return spigot.getRelative(x,y,z);
					}
				}
				z = startZ;
				x++;
			}
			z = startZ;
			x = startX;
			y++;
		}
		return null;

	}

	//returns the fence above/below a block
	public static Block getSpigotOfSign(Block block){

		int y = -2;
		while(y <= 1){
			//Fence and Netherfence
			if(block.getRelative(0,y,0).getTypeId() == 85 ||
			block.getRelative(0,y,0).getTypeId() == 113){
				return (block.getRelative(0,y,0));
			}
			y++;
		}
		return null;
	}

}