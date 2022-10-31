package me.kryniowesegryderiusz.kgenerators.addons;

import java.util.ArrayList;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.addons.objects.Addon;

public class Addons {

	@Getter
	static ArrayList<Addon> addons = new ArrayList<Addon>();

	public static void register(Addon addon) {
		if (!addons.contains(addon))
			addons.add(addon);
	}

}
