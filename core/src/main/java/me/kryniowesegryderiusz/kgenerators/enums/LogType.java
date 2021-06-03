package me.kryniowesegryderiusz.kgenerators.enums;

public enum LogType
{
	INFO(""),
	ERROR("!!! ERROR !!! "),
	DEBUG("[Debug] "),
	WARNINGS("!!! WARNING !!! "),
;
	String header;
	
	LogType(String header) {
		this.header = header;
	}
	
	public String getHeader()
	{
		return this.header;
	}

}
