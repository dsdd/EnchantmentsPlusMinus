package me.backstabber.entityai.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

public class YamlCommentor
{
    public static void addComments(File file,Map<Integer, String> comments)
    {
        //load all data from file
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e)
        {
            return;
        }
        Reader reader=new InputStreamReader(stream);
        BufferedReader input = (reader instanceof BufferedReader) ? (BufferedReader)reader : new BufferedReader(reader);
        List<String> toSave=new ArrayList<String>();
        try
        {
            String temp;
            try
            {
                while ((temp = input.readLine()) != null)
                {
                    toSave.add(temp);
                }
            }
            catch (IOException e)
            {
                return;
            }
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (IOException e)
            {
                return;
            }
        }
        //add comments to the data
        int maxIndex=Integer.MIN_VALUE;
        for(int i:comments.keySet())
            if(i>maxIndex)
                maxIndex=i;
        while(maxIndex>toSave.size())
            toSave.add("");
        for(int i=0;i<=maxIndex;i++)
        {
            if(comments.containsKey(i))
            {
                String comment=comments.get(i);
                if(!comment.startsWith("#"))
                    comment="#"+comment;
                toSave.add(i, comment);
            }
        }
        //make string
        StringBuilder builder = new StringBuilder();
        for(String s:toSave)
        {
            builder.append(s);
            builder.append("\n");
        }
        //save it
        try
        {
            Files.write(file.toPath(), builder.toString().getBytes(), StandardOpenOption.WRITE);
        }
        catch (IOException e)
        {
            return;
        }
    }
    public static void addComment(File file,String comment,int line)
    {
        if(!comment.startsWith("#"))
            comment="#"+comment;
        //load all data from file
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e)
        {
            return;
        }
        Reader reader=new InputStreamReader(stream);
        BufferedReader input = (reader instanceof BufferedReader) ? (BufferedReader)reader : new BufferedReader(reader);
        List<String> toSave=new ArrayList<String>();
        try
        {
            String temp;
            try
            {
                while ((temp = input.readLine()) != null)
                {
                    toSave.add(temp);
                }
            }
            catch (IOException e)
            {
                return;
            }
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (IOException e)
            {
                return;
            }
        }
        //add comments to the data
        while(line>toSave.size())
            toSave.add("");
        toSave.add(line, comment);
        //make string
        StringBuilder builder = new StringBuilder();
        for(String s:toSave)
        {
            builder.append(s);
            builder.append("\n");
        }
        //save it
        try
        {
            Files.write(file.toPath(), builder.toString().getBytes(), StandardOpenOption.WRITE);
        }
        catch (IOException e)
        {
            return;
        }
    }
    public static void addComment(File file,String comment)
    {
        if(!comment.startsWith("#"))
            comment="#"+comment;
        //load all data from file
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e)
        {
            return;
        }
        Reader reader=new InputStreamReader(stream);
        BufferedReader input = (reader instanceof BufferedReader) ? (BufferedReader)reader : new BufferedReader(reader);
        List<String> toSave=new ArrayList<String>();
        try
        {
            String temp;
            try
            {
                while ((temp = input.readLine()) != null)
                {
                    toSave.add(temp);
                }
            }
            catch (IOException e)
            {
                return;
            }
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (IOException e)
            {
                return;
            }
        }
        //add comments to the data
        toSave.add(comment);
        //make string
        StringBuilder builder = new StringBuilder();
        for(String s:toSave)
        {
            builder.append(s);
            builder.append("\n");
        }
        //save it
        try
        {
            Files.write(file.toPath(), builder.toString().getBytes(), StandardOpenOption.WRITE);
        }
        catch (IOException e)
        {
            return;
        }
    }
    public static void saveCommented(FileConfiguration fileConfiguration,File file)
    {
        //load all comments
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e)
        {
            return;
        }
        Reader reader=new InputStreamReader(stream);
        BufferedReader input = (reader instanceof BufferedReader) ? (BufferedReader)reader : new BufferedReader(reader);
        Map<Integer, String> comments=new HashMap<Integer, String>();
        try
        {
            String line;
            int index=0;
            try
            {
                while ((line = input.readLine()) != null)
                {
                    if(line.contains("#"))
                        comments.put(index, line.substring(line.indexOf("#")));
                    index++;
                }
            }
            catch (IOException e)
            {
                return;
            }
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (IOException e)
            {
                return;
            }
        }
        //load all data
        List<String> toSave=new ArrayList<String>();
        String dataStream=fileConfiguration.saveToString();
        for(String s:dataStream.split("\n"))
        {
            toSave.add(s);
        }
        //add comments to the data
        int maxIndex=Integer.MIN_VALUE;
        for(int i:comments.keySet())
            if(i>maxIndex)
                maxIndex=i;
        while(maxIndex>toSave.size())
            toSave.add("");
        for(int i=0;i<=maxIndex;i++)
        {
            if(comments.containsKey(i))
                toSave.add(i, comments.get(i));
        }
        //make string
        StringBuilder builder = new StringBuilder();
        for(String s:toSave)
        {
            builder.append(s);
            builder.append("\n");
        }
        //save it
        try
        {
            Files.write(file.toPath(), builder.toString().getBytes(), StandardOpenOption.WRITE);
        }
        catch (IOException e)
        {
            return;
        }
    }
}