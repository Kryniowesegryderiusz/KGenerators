package me.kryniowesegryderiusz.kgenerators;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import me.kryniowesegryderiusz.kgenerators.api.Addon;
import me.kryniowesegryderiusz.kgenerators.enums.LogType;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.files.FilesFunctions;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.managers.Addons;

public class Logger {
	
	static String logFile = "logs/latest.log";
	static String configFile = "config.yml";
	static String generatorsFile = "generators.yml";
	static String recipesFile = "recipes.yml";
	static String upgradesFile = "upgrades.yml";
	static String limitsFile = "limits.yml";
	
	private static void log(Object object, LogType logType)
	{
		String message = logType.getHeader() + "";
		if (object instanceof String)
		{
			message += (String) object;
			if(logType.equals(LogType.ERROR)) Main.getInstance().getLogger().warning(message);
			else Main.getInstance().getLogger().info(message);
		}
		else if (object instanceof Exception)
		{
			message += exceptionStacktraceToString((Exception) object);
			((Exception) object).printStackTrace();
		}
		else
		{
			message += object.toString();
			if(logType.equals(LogType.ERROR)) Main.getInstance().getLogger().warning(message);
			else Main.getInstance().getLogger().info(message);
		}
		logToFile(message, logFile);
	}
	
	public static void error(Object object)
	{
		log(object, LogType.ERROR);	
	}
	
	public static void warn(Object object)
	{
		log(object, LogType.WARNINGS);	
	}
	
	public static void info(Object object)
	{
		log(object, LogType.INFO);	
	}
	
	public static void debug(Object object)
	{
		log(object, LogType.DEBUG);	
	}
	
	public static void textToConsole(String message)
	{
		Main.getInstance().getLogger().info(message);
	}
	
	private static String exceptionStacktraceToString(Exception e)
	{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(baos);
	    e.printStackTrace(ps);
	    ps.close();
	    return baos.toString();
	}
	
	public static void logToFile(String message, String file)
    {
        try
        {
            File dataFolder = Main.getInstance().getDataFolder();
            if(!dataFolder.exists())
            {
                dataFolder.mkdir();
            }
 
            File saveTo = new File(Main.getInstance().getDataFolder(), file);
            if (!saveTo.exists())
            {
                saveTo.createNewFile();
            }
 
 
            FileWriter fw = new FileWriter(saveTo, true);
 
            PrintWriter pw = new PrintWriter(fw);
            
    		Date now = new Date();
    		SimpleDateFormat format = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss] ");
 
            pw.println(format.format(now) + message);
 
            pw.flush();
 
            pw.close();
 
        } catch (IOException e)
        {
            e.printStackTrace();
        }
 
    }
	
	public static void setup()
	{
		FilesFunctions.mkdir("logs");
		File saveTo = new File(Main.getInstance().getDataFolder(), logFile);
        if (saveTo.exists())
        {
        	try {
        		Date now = new Date();
        		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
				Files.move(saveTo.toPath(), new File(Main.getInstance().getDataFolder(), "logs/"+format.format(now).toString()+".log").toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				Logger.error("Cannot create new logs file");
				Logger.error(e);
			}
        }
	}
	
	public static void debugPaste(CommandSender sender)
	{
		Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable()
				{
					@Override
					public void run() {
						String fileString = "";
						
					    Iterator<Map.Entry<Object, Object>> iterator = System.getProperties().entrySet().iterator();
					    while (iterator.hasNext()) {
					      Map.Entry<Object, Object> e = iterator.next();
					      if (e.getKey().toString().contains("java.version"))
					    	  fileString += "Java version: " + e.getValue().toString() + "\n";
					      else if (e.getKey().toString().contains("user.country"))
					    	  fileString += "User country: " + e.getValue().toString() + "\n";
					      else if (e.getKey().toString().contains("os.name"))
					    	  fileString += "System: " + e.getValue().toString() + "\n";
					    } 
					    
					    fileString += "\n";
					    
						fileString += "Server version: " + Main.getInstance().getServer().getVersion() + "\n";
						fileString += "Plugin version: " + Main.getInstance().getDescription().getVersion() + "\n";
						fileString += "Enabled dependencies: " + Main.dependencies.toString() + "\n";
						fileString += "Registered addons: " + Addons.getAddons().toString() + "\n";
						
						fileString += "Enabled plugins: ";
						for (Plugin plugin : Main.getInstance().getServer().getPluginManager().getPlugins())
						{
							fileString += plugin.getName() + " " + plugin.getDescription().getVersion() + ", ";
						}
							
						fileString += "\n\n";
							
						try {
							fileString += getLinesFromFile(logFile) + "\n";
							fileString += getLinesFromFile(configFile) + "\n";
							fileString += getLinesFromFile(generatorsFile) + "\n";
							fileString += getLinesFromFile(upgradesFile) + "\n";
							fileString += getLinesFromFile(limitsFile) + "\n";
							fileString += getLinesFromFile(recipesFile) + "\n";
							
							for (Addon a : Addons.getAddons())
							{
								if (a.getConfigs() != null)
								{
									for (String f : a.getConfigs())
									{
										fileString += getLinesFromFile(f) + "\n";
									}
								}
							}
							
							String url = postHaste(sender, fileString, false);
							if (url != null)
							{
								Lang.getMessageStorage().send(sender, Message.COMMANDS_DEBUG_DONE,
										"<url>", url);
							}
							
						} catch (IOException e) {
							Lang.getMessageStorage().send(sender, Message.COMMANDS_DEBUG_ERROR);
							Logger.error(e);
						}
					}
				});
	}
	
	private static String getLinesFromFile(String file) throws IOException
	{
		String fileStrings = "########################################\n########## " + file + " ##########\n########################################\n\n";
		List<String> lines = Files.readAllLines(Paths.get(Main.getInstance().getDataFolder().getPath(), file), Charset.defaultCharset());
		for (String l : lines)
		{
			fileStrings += l + "\n";
		}
		return fileStrings;
		
	}
	
	private static String postHaste(CommandSender sender, String text, boolean raw) throws IOException 
	{
		byte[] postData = text.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;

		String requestURL = "http://paste.skyup.pl/documents";
		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("User-Agent", "Hastebin Java Api");
		conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
		conn.setUseCaches(false);

		String response = null;
		DataOutputStream wr;
		try {
			wr = new DataOutputStream(conn.getOutputStream());
			wr.write(postData);
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			response = reader.readLine();
		} catch (IOException e) {
			Lang.getMessageStorage().send(sender, Message.COMMANDS_DEBUG_ERROR);
			Logger.error(e);
		}
		
		if (response != null && response.contains("\"key\"")) {
			response = response.substring(response.indexOf(":") + 2, response.length() - 2);
		
			String postURL = raw ? "http://paste.skyup.pl/raw/" : "http://paste.skyup.pl/";
			response = postURL + response;
		}
		
		return response;
	}



}
