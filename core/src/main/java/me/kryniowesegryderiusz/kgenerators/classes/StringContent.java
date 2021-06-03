package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;

public class StringContent implements Cloneable {
	
	@Getter
	ArrayList<String> lines = new ArrayList<String>();
	
	public StringContent(String... contents)
	{
		lines = new ArrayList<>(Arrays.asList(contents));
	}
	
	public StringContent (ArrayList<String> lines)
	{
		this.lines.addAll(lines);
	}
	
	public void replace(String key, String value)
	{
		for (int i = 0; i < this.lines.size(); i++)
		{
			this.lines.set(i, this.lines.get(i).replace(key, value));
		}
	}
	
	@Override
	public StringContent clone()
	{
		return new StringContent(new ArrayList<String>(this.lines));
	}

}
