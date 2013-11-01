package com.tsp.game.actors.ai;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Tim
 */
public class AIFactory
{
	private static ArrayList<Class<?>> classes = new ArrayList<Class<?>>();


	public static void addAI(Class<?> aiClass)
	{
		if (aiClass.getSuperclass() == AI.class)
			classes.add(aiClass);
	}

	public static AI getAI()
	{
		Random r = new Random();
		int i = r.nextInt(classes.size());
		try
		{
			Constructor<?> constructor = classes.get(i).getConstructor();
			return (AI) constructor.newInstance();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		}
	}

}
