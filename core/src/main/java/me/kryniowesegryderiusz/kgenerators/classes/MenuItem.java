package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuItem;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.xseries.XUtils;

public class MenuItem implements Cloneable {
	private boolean enabled = true;
	@Getter
	private String itemType;
	@Setter
	private ItemStack itemStack;
	@Getter
	private String name = "";
	private ArrayList<String> lore = new ArrayList<String>();
	private String slots = "";
	@Setter
	private boolean glow = false;
	
	public MenuItem(String itemType)
	{
		this.itemType = itemType;
	}
	
	public MenuItem(String itemType, String name, boolean glow, String slots)
	{
		this.itemType = itemType;
		setName(name);
		this.glow = glow;
		this.slots = slots.replaceAll(" ", "");
	}
	
	public MenuItem(boolean enabled, String itemType, String name, boolean glow, String slots, ArrayList<String> lore)
	{
		this.enabled = enabled;
		this.itemType = itemType;
		setName(name);
		this.glow = glow;
		this.slots = slots;
		this.lore.clear();
		addLore(lore);
	}
	
	public MenuItem(String itemType, String name, boolean glow, String... loreAndSlot)
	{
		this.itemType = itemType;
		setName(name);
		this.glow = glow;
		boolean first = true;
		for (String s : Arrays.asList(loreAndSlot))
		{
			if (first)
			{
				this.slots = s.replaceAll(" ", "");
				first = false;
			}
			else
			{
				addLore(s);
			}
		}
	}
	
	public void addLore(String loreLine)
	{
		this.lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
	}
	
	public void addLore(String... lore)
	{
		for (String s : Arrays.asList(lore))
		{
			this.lore.add(ChatColor.translateAlternateColorCodes('&', s));
		}
	}
	
	public void addLore(ArrayList<String> lore)
	{
		for (String s : lore)
		{
			this.lore.add(ChatColor.translateAlternateColorCodes('&', s));
		}
	}
	
	public void setName(String name)
	{
		this.name = ChatColor.translateAlternateColorCodes('&', name);
	}
	
	public void replace(String key, String value)
	{
		this.name = this.name.replace(key, value);
		for (int i = 0; i < this.lore.size(); i++)
		{
			this.lore.set(i, this.lore.get(i).replace(key, value));
		}
	}
	
	public ItemStack build()
	{
		ItemStack item;
		if (itemStack != null)
			item = itemStack;
		else
			item = XUtils.parseItemStack(this.itemType, "MenuItem", false);
		
		ItemMeta meta = (ItemMeta) item.getItemMeta();
		
		meta.setDisplayName(this.name);
		
		if (!lore.isEmpty())
			meta.setLore(this.lore);
		
		if (glow)
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		item.setItemMeta(meta);
		
		if (glow)
			item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
		
		return item;
		
	}

	public void load(EnumMenuItem menu, Config config) {
		String path = menu.getKey();
		if (config.contains(path))
		{
			this.itemType = config.getString(path+".item");
			if (config.contains(path+".name")) setName(config.getString(path+".name"));
			this.lore.clear();
			if (config.contains(path+".lore")) addLore((ArrayList) config.getList(path+".lore"));
			if (config.contains(path+".glow")) this.glow = config.getBoolean(path+".glow");
			if (config.contains(path+".slots")) this.slots = config.getString(path+".slots");
			if (config.contains(path+".enabled")) this.enabled = config.getBoolean(path+".enabled");
		}
		else
		{
			config.set(path+".enabled", true);
			config.set(path+".item", this.itemType);
			config.set(path+".name", this.name);
			config.set(path+".lore", this.lore);
			config.set(path+".slots", this.slots);
			config.set(path+".glow", this.glow);
		}
	}
	
	public ArrayList<Integer> getSlots()
	{
		ArrayList<Integer> slots = new ArrayList<Integer>();
		String[] s = this.slots.split(",");
		for (int i = 0; i<s.length; i++)
			slots.add(Integer.valueOf(s[i]));
		return slots;
	}
	
	public MenuItem clone()
    {
        return new MenuItem(this.enabled, this.itemType, this.name, this.glow, this.slots, new ArrayList<String>(this.lore));
    }
	
}
