package org.vivi.eps.libs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.vivi.eps.EPS;

public class FileUtils {

	/** Saves a resource from the EPS jar to the specified destination
	 * if it does not exist
	 * 
	 * @param resource The path to copy from
	 * @param dest The file you want to copy to
	 */
	public static void saveDefaultFile(String resource, File dest)
	{
	    if (!dest.exists())
	    {
	        try {
				dest.createNewFile();
		        FileUtils.copyResource(resource, dest);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}

	/** Copies a resource in the EPS jar from the specified file path
	 * to the specified file
	 * 
	 * @param str The file path to copy from
	 * @param dest The file to paste into
	 */
	public static void copyResource(String str, File dest) 
	{
		try {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = EPS.plugin.getClass().getResourceAsStream(str);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) != -1) {
	            os.write(buffer, 0, length);
	        }
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	    finally {
	        is.close();
	        os.close();
	    }
	}
	catch(IOException e) {}
	}

	public static boolean createNewFile(File file)
	{
		try {
			file.createNewFile();
			return true;
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/** Downloads a file from the specified URL.
	 * 
	 * @param localFileName The name of the file
	 * @param fromUrl The URL to download from
	 * @return The file
	 */
	public static File downloadFile(String localFileName, String fromUrl) { try {
	    File localFile = new File(localFileName);
	    if (!localFile.exists()) {
	    	localFile.createNewFile();
	    }
	    URL url = new URL(fromUrl);
	    OutputStream out = new BufferedOutputStream(new FileOutputStream(localFileName));
	    URLConnection conn = url.openConnection();
	    ((HttpURLConnection) conn).setRequestMethod("GET"); 
	    conn.setRequestProperty("User-Agent", "  Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
	    conn.connect();
	    InputStream in = conn.getInputStream();
	    byte[] buffer = new byte[16384];
	
	    int numRead;
	    while ((numRead = in.read(buffer)) != -1) {
	        out.write(buffer, 0, numRead);
	    }
	    if (in != null) {
	        in.close();
	    }
	    if (out != null) {
	        out.close();
	    }
	    out.flush();
	    
	    return localFile;   } catch (Exception e){return null;}
	}

	/** Gets the JAR file of a plugin
	 * 
	 * @param pl The plugin
	 * @return The file
	 */
	public static File getJarFile(Plugin pl)
	{ 
		try 
		{
			Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
			getFileMethod.setAccessible(true);
			File file = (File) getFileMethod.invoke((JavaPlugin) pl);
			return file; 
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace();  
			return null; 
		}
	}

	/** Gets files from a folder inside of the EPS jar
	 * 
	 * @param path The folder path
	 * @return The files inside the folder
	 */
	public static List<File> getFiles(String path)
	{
		try {
		final File jarFile = FileUtils.getJarFile(EPS.plugin);
        List<File> files = new ArrayList<File>(Arrays.asList());
	    if(jarFile.isFile()) {  // Run with JAR file
	        JarFile jar = new JarFile(jarFile);
	        final Enumeration<JarEntry> entries = jar.entries();
	        while(entries.hasMoreElements()) {
	        	JarEntry entry = entries.nextElement();
	            final String name = entry.getName();
	            File f = new File(name);
	            if (name.startsWith((path + "/").substring(1))) { 
	                files.add(getTempFile(jar, entry, f.getName()));
	            }
	        }
				jar.close();
	    }
	    return files;
		}
	    catch (IOException e)
	    {
	    	e.printStackTrace();
	    	return null;
	    }
	}
	
	/** Creates a new file from the specified ZipEntry
	 * 
	 * @param file The ZipFile to look into
	 * @param entry The ZipEntry to copy from
	 * @param name The name to give the file
	 * @return The file
	 */
	public static File getTempFile(ZipFile file, ZipEntry entry, String name)
	{
		File tempfolder = new File(EPS.dataFolder, "Temp");
		File temp = new File(tempfolder, name);
	    if (!tempfolder.exists()) 
	    	tempfolder.mkdirs();
	    if (!temp.exists()) 
	    	FileUtils.createNewFile(temp);
	    
		try 
		{
		    InputStream is = file.getInputStream(entry);
		    OutputStream os = null;
		    
		    try 
		    {
		        os = new FileOutputStream(tempfolder+"/"+name);
		        byte[] buffer = new byte[1024];
		        int length;
		        while ((length = is.read(buffer)) != -1)
		            os.write(buffer, 0, length);
		    }
		    catch (Exception e)
		    {
		    	e.printStackTrace();
		    }
		    finally 
		    {
		        is.close();
		        os.close();
		    }
		}
		catch (Exception e) 
		{ 
		    e.printStackTrace();
		}
		return (temp);
	}
	
	public static YamlConfiguration loadUTF8Configuration(File file)
	{
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return config;
	}
	
}
