package me.kryniowesegryderiusz.kgenerators.multiversion;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.SkullUtils;

public class SkullUtils_1_21 implements SkullUtils {
	
	private boolean isPaper = false;
	
	public SkullUtils_1_21() {
		try {
			Main.getInstance().getServer().createProfile(UUID.randomUUID());
			isPaper = true;
		} catch (NoClassDefFoundError e) {
			Main.getInstance().getLogger().warning("This server is not running PaperMC. Base64 skulls wont work.");
		}
	}

	@Override
	public ItemStack itemFromName(String name) {
		ItemStack is = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) is.getItemMeta();
		meta.setOwner(name);
		is.setItemMeta(meta);
		return is;
	}

	@Override
	public ItemStack itemFromBase64(String base64) {
				
		if (!isPaper) {
			return new ItemStack(Material.PLAYER_HEAD);
		}
		
		ItemStack is = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) is.getItemMeta();
		
		meta.setPlayerProfile(getProfile(base64));

		is.setItemMeta(meta);
		
		return is;
	}

	@Override
	public void blockWithBase64(Block block, String skullBase64) {
		
		if (!isPaper) {
			return;
		}

		if (block.getType() != Material.PLAYER_HEAD) {
			block.setType(Material.PLAYER_HEAD);
		}

        Skull skull = (Skull) block.getState();
        skull.setPlayerProfile(getProfile(skullBase64));
        skull.update(true);
		
	}
	
	private PlayerProfile getProfile(String base64) {
		PlayerProfile profile = Main.getInstance().getServer().createProfile(
				UUID.nameUUIDFromBytes("custom-textures".getBytes(StandardCharsets.UTF_8)), "custom-textures");
		profile.getProperties().add(new ProfileProperty("textures", base64));
		return profile;
	}

}
