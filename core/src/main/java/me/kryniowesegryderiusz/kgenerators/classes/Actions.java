package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import me.kryniowesegryderiusz.kgenerators.enums.Action;
import me.kryniowesegryderiusz.kgenerators.utils.Config;

public class Actions {

	private LinkedHashMap<Action, GeneratorAction> actions = new LinkedHashMap<Action, GeneratorAction>();
	
	public Actions() {}
	
	public GeneratorAction getGeneratorAction(Action action)
	{
		return this.actions.get(action);
	}
	
	public void addGeneratorAction(Action action, GeneratorAction gaction)
	{
		actions.put(action, gaction);
	}
	
	/**
	 * 
	 * @param config
	 * @param path, .actions would be included
	 */
	public void load(Config config, String path)
	{
		if (path.isEmpty())
			path = path + "actions";
		else
			path = path + ".actions";
		
		this.addGeneratorAction(Action.PICKUP, new GeneratorAction(Action.PICKUP, config, path+".pick-up"));
		this.addGeneratorAction(Action.OPENGUI, new GeneratorAction(Action.OPENGUI, config, path+".open-gui"));
		this.addGeneratorAction(Action.TIMELEFT, new GeneratorAction(Action.TIMELEFT, config, path+".time-left-check"));
	}

	public Set<Entry<Action, GeneratorAction>> getEntrySet() {
		return this.actions.entrySet();
	}
	
}
