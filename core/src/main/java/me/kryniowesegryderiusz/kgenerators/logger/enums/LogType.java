package me.kryniowesegryderiusz.kgenerators.logger.enums;

public enum LogType
{
	INFO(""),
	ERROR("!!! ERROR !!! "),
	DEBUG("[Debug] "),
	WARNING("!!! WARNING !!! "),
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
