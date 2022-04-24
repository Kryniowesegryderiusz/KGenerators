package me.kryniowesegryderiusz.kgenerators.lang.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.lang.interfaces.IMenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class StringContent implements Cloneable {
	
	@Getter
	ArrayList<String> lines = new ArrayList<String>();
	
	public <T extends Enum<T> & IMenuItemAdditionalLines> StringContent(String path, Config config, StringContent defaultContent)
	{
		this.load(path, config, defaultContent);
	}
	
	public StringContent(String... contents)
	{
		this.lines = new ArrayList<>(Arrays.asList(contents));
	}
	
	public StringContent (ArrayList<String> lines)
	{
		this.lines.addAll(lines);
	}
	
	public StringContent replace(String key, String value)
	{
		for (int i = 0; i < this.lines.size(); i++)
		{
			this.lines.set(i, this.lines.get(i).replace(key, value));
		}
		return this;
	}
	
	public StringContent replace(String key, StringContent stringContent)
	{
		ArrayList<String> newDesc = new ArrayList<String>();
		for (String s : this.lines)
		{
			if (s.contains(key))
			{
				if (stringContent != null)
				{
					for (String as : stringContent.getLines())
					{
						newDesc.add(Main.getMultiVersion().getChatUtils().colorize(as));
					}
				}
			}
			else
			{
				newDesc.add(s);
			}
		}
		this.lines = newDesc;
		return this;
	}
	
	public StringContent addLines(ArrayList<String> lines)
	{
		this.lines.addAll(lines);
		return this;
	}
	
	public StringContent addLine(String line)
	{
		this.lines.add(line);
		return this;
	}
	
	public String getLineWith(String key)
	{
		String line = "";
		for (String s : this.lines)
		{
			if (s.contains(key))
				line = s;
		}
		
		return line;
	}
	
	public void load(String path, Config config, StringContent defaultContent)
	{
		if (config.contains(path)) 
		{
			for (String s : (ArrayList<String>) config.getStringList(path))
			{
				lines.add(Main.getMultiVersion().getChatUtils().colorize(s));
			}
		}
		else if (defaultContent != null)
		{
			config.set(path, defaultContent.getLines());
			try {
				config.saveConfig();
			} catch (IOException ex) {
				Logger.error("Lang: Cant save lang file!");
				Logger.error(ex);
			}
		}
		else
			Logger.error("Cannot load " + path + " from " + config.getName() + " because it doesnt exist!");
	}
	
	@Override
	public StringContent clone()
	{
		return new StringContent(new ArrayList<String>(this.lines));
	}

}
