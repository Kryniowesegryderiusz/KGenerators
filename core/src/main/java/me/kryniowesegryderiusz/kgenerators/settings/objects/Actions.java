package me.kryniowesegryderiusz.kgenerators.settings.objects;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.ActionType;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class Actions {

	private LinkedHashMap<ActionType, GeneratorAction> actions = new LinkedHashMap<ActionType, GeneratorAction>();

	public Actions() {
	}

	public GeneratorAction getGeneratorAction(ActionType action) {
		return this.actions.get(action);
	}

	public void addGeneratorAction(ActionType action, GeneratorAction gaction) {
		actions.put(action, gaction);
	}

	/**
	 * 
	 * @param config
	 * @param path,  .actions would be included
	 */
	public void load(Config config, String path) {
		if (path.isEmpty())
			path = path + "actions";
		else
			path = path + ".actions";

		this.addGeneratorAction(ActionType.PICKUP, new GeneratorAction(ActionType.PICKUP, config, path + ".pick-up"));
		this.addGeneratorAction(ActionType.OPENGUI, new GeneratorAction(ActionType.OPENGUI, config, path + ".open-gui"));
		this.addGeneratorAction(ActionType.TIMELEFT, new GeneratorAction(ActionType.TIMELEFT, config, path + ".time-left-check"));
		this.addGeneratorAction(ActionType.UPGRADE, new GeneratorAction(ActionType.UPGRADE, config, path + ".upgrade"));
	}

	public Set<Entry<ActionType, GeneratorAction>> getEntrySet() {
		return this.actions.entrySet();
	}

}
