package org.whyisthisnecessary.ids;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IDS {

	private Map<String, Object> mappings = new HashMap<String, Object>();
	private Map<String, Object> oldMappings = new HashMap<String, Object>();
	private List<String> lines = new ArrayList<String>();
	private File f = null;
	private String sp = System.getProperty("line.separator");
	
	public IDS(File file)
	{
		try {
			loadFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reload() throws IOException
	{
		loadFile(f);
	}
	
	public String getID(Object o)
	{
		for (Map.Entry<String, Object> entry : mappings.entrySet())
			if (entry.getValue().equals(o))
				return entry.getKey();
		throw new NullPointerException("No ID found with value");
	}
	
	public boolean idExists(String s)
	{
		return mappings.containsKey(s);
	}
	
	public void set(String id, Object value)
	{
		mappings.put(id, value);
	}
	
	public void save() throws IOException
	{
		List<String> newLines = new ArrayList<String>();
		Map<String, Object> c = oldMappings;
		for (Map.Entry<String, Object> entry : mappings.entrySet())
			c.put(entry.getKey(), entry.getValue());
		for (Map.Entry<String, Object> entry : c.entrySet())
			if (entry.getValue() != null)
				newLines.add(entry.getKey()+":"+entry.getValue().toString());
		
		BufferedWriter output = new BufferedWriter(new FileWriter(f));
		for (String s : newLines)
			output.append(s+sp);
		output.close();
	}
	
	private void loadFile(File file) throws IOException
	{
		f = file;
		lines = Files.readAllLines(file.toPath());
		for (String line : lines)
		{
			String[] split = line.split(":");
			mappings.put(split[0], split[1]);
			oldMappings.put(split[0], split[1]);
		}
	}
	
	/*
	 * Get methods
	 * 
	 */
	
	public Map<String, Object> getAll()
	{
		return mappings;
	}
	
	public File getFile()
	{
		return f;
	}
	
	public String getString(String s)
	{
		 return mappings.get(s).toString();
	}
	
	public byte getByte(String s)
	{
		return Byte.parseByte(mappings.get(s).toString());
	}
	
	public short getShort(String s)
	{
		return Short.parseShort(mappings.get(s).toString());
	}

	public int getInt(String s)
	{
		return Integer.parseInt(mappings.get(s).toString());
	}
	
	public long getLong(String s)
	{
		return Long.parseLong(mappings.get(s).toString());
	}
	
	public float getFloat(String s)
	{
		return Float.parseFloat(mappings.get(s).toString());
	}
	
	public double getDouble(String s)
	{
		return Double.parseDouble(mappings.get(s).toString());
	}
	
	public boolean getBoolean(String s)
	{
		return Boolean.parseBoolean(mappings.get(s).toString());
	}
	
	public char getChar(String s)
	{
		return mappings.get(s).toString().charAt(0);
	}
	
	public List<Object> getList(String s)
	{
		String str = getString(s);
		String[] array = str.substring(1, str.length()-1).split("\\s*,\\s*");
		Object[] fin = new Object[array.length];
		for (int i=0;i<array.length;i++)
			fin[i]=array[i];
		return Arrays.asList(fin);
	}
	
	public List<String> getStringList(String s)
	{
		String str = getString(s);
		return Arrays.asList(str.substring(1, str.length()-1).split("\\s*,\\s*"));
	}
	
	public List<Byte> getByteList(String s)
	{
		List<Byte> l = new ArrayList<Byte>();
		for (String str : getStringList(s))
			l.add(Byte.parseByte(str));
		return l;
	}
	
	public List<Short> getShortList(String s)
	{
		List<Short> l = new ArrayList<Short>();
		for (String str : getStringList(s))
			l.add(Short.parseShort(str));
		return l;
	}
	
	public List<Integer> getIntegerList(String s)
	{
		List<Integer> l = new ArrayList<Integer>();
		for (String str : getStringList(s))
			l.add(Integer.parseInt(str));
		return l;
	}
	
	public List<Long> getLongList(String s)
	{
		List<Long> l = new ArrayList<Long>();
		for (String str : getStringList(s))
			l.add(Long.parseLong(str));
		return l;
	}
	
	public List<Float> getFloatList(String s)
	{
		List<Float> l = new ArrayList<Float>();
		for (String str : getStringList(s))
			l.add(Float.parseFloat(str));
		return l;
	}
	
	public List<Double> getDoubleList(String s)
	{
		List<Double> l = new ArrayList<Double>();
		for (String str : getStringList(s))
			l.add(Double.parseDouble(str));
		return l;
	}
	
	public List<Boolean> getBooleanList(String s)
	{
		List<Boolean> l = new ArrayList<Boolean>();
		for (String str : getStringList(s))
			l.add(Boolean.parseBoolean(str));
		return l;
	}
	
	public Map<Object, Object> getMap(String s)
	{
		String str = getString(s);
		String[] entries = str.substring(1, str.length()-1).split("\\s*,\\s*");
		Map<Object, Object> map = new HashMap<Object, Object>();
		for (String v : entries)
		{
			String[] split = v.split("=");
			map.put(split[0], split[1]);
		}
		return map;
	}
	
	public Object get(String s)
	{
		Object obj = mappings.get(s);
		return obj;
	}
	
	public Object get(String s, Object def)
	{
		Object obj = mappings.get(s);
		return obj == null ? def : obj;
	}
}
