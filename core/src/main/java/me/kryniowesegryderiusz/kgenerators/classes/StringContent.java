package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;

public class StringContent {
	
	@Getter
	ArrayList<String> lines = new ArrayList<String>();
	
	public StringContent(String... contents)
	{
		lines = new ArrayList<>(Arrays.asList(contents));
	}

}
