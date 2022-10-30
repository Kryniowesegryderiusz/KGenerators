package me.kryniowesegryderiusz.kgenerators.data.objects;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class SQLConfig {
	@Getter
	private String dbHost;

	@Getter
	private int dbPort;

	@Getter
	private String dbName;

	@Getter
	private String dbUser;

	@Getter
	private String dbPass;

	@Getter
	private boolean ssl = false;

	@Getter
	private int poolSize = 10;

	public SQLConfig(String dbHost, int dbPort, String dbName, String dbUser, String dbPass) {
		this.dbHost = dbHost;
		this.dbPort = dbPort;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPass = dbPass;
	}

	public SQLConfig(Config config) {
		if (config.contains("database.host"))
			this.dbHost = config.getString("database.host");
		if (config.contains("database.port"))
			this.dbPort = config.getInt("database.port");
		if (config.contains("database.database"))
			this.dbName = config.getString("database.database");
		if (config.contains("database.username"))
			this.dbUser = config.getString("database.username");
		if (config.contains("database.password"))
			this.dbPass = config.getString("database.password");
		if (config.contains("database.use-ssl"))
			this.ssl = config.getBoolean("database.use-ssl");
		if (config.contains("database.pool-size"))
			this.poolSize = config.getInt("database.pool-size");
	}
}
