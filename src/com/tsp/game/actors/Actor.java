package com.tsp.game.actors;

import java.awt.Point;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.tsp.game.map.Point3D;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/18/13
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class Actor implements JSONAware
{

	public ActorType getType()
	{
		return type;
	}

	public String getSymbol()
	{
		return symbol;
	}

	public int getZ()
	{
		return pos.getZ();
	}

	public int getX()
	{
		return (int) pos.getX();
	}

	public int getY()
	{
		return (int) pos.getY();
	}

	public int getColor()
	{
		return color;
	}

	public void setX(Integer x)
	{
		pos.x = x;
	}

	public void setY(Integer y)
	{
		pos.y = y;
	}

	public void setZ(Integer z)
	{
		pos.setZ(z);
	}

	public void setHealth(Integer health)
	{
		this.health = health;
	}

	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}

	public enum ActorType
	{
		ACTOR_PLAYER,
		ACTOR_AI
	}
	Point3D pos;
	String name = "RandomActor";
	int id;
	int health;
	int color = 255;
	ActorType type;
	String symbol;
	boolean attacking;
	String attackAnimation;
	
	protected static int count = 0;

	public Actor()
	{
		id = count;
		count++;
	}

	public Actor(int id, int health, Point3D pos, String name, ActorType type, String symbol, int color)
	{
		this.id = id;
		this.name = name;
		this.pos = pos;
		this.health = health;
		this.type = type;
		this.symbol = symbol;
		this.color = color;
	}

	boolean checkHit(int x, int y, int z)
	{

		return checkHit(new Point3D(x, y, z));
	}

	boolean checkHit(Point3D check)
	{
		return pos.equals(check);
	}

	int getDamage()
	{
		return 1;
	}

	public Point3D getPos()
	{
		return pos;
	}

	public void move(int x, int y)
	{
		move(new Point3D(x, y, 0));
	}

	public void move(int x, int y, int z)
	{
		move(new Point3D(x, y, z));
	}

	public void move(Point3D movement)
	{
		pos.add(movement);
	}

	public void setPos(Point3D pos)
	{
		this.pos = pos;
	}

	public String getName()
	{
		return name;
	}

	public int getHealth()
	{
		return health;
	}


	public int getId()
	{
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void hit(Actor attacking)
	{
		this.health -= attacking.getDamage();
	}

	public void newPosition(int COLS, int ROWS, int LVLS)
	{
		java.util.Random r = new java.util.Random();
		pos = new Point3D(r.nextInt(COLS), r.nextInt(ROWS), r.nextInt(LVLS));
	}

	public boolean isAttacking() {
		return this.attacking;
	}
	
	public void startAttacking(Point point) {
		this.attacking = true;
	}
	
	@Override
	public String toJSONString()
	{
		JSONObject jb = new JSONObject();
		jb.put("name",this.name);
		jb.put("id",id);
		jb.put("X",((int)pos.getX()));
		jb.put("Y",((int)pos.getY()));
		jb.put("Z",pos.getZ());
		jb.put("health",health);
		jb.put("type", type.toString());
		jb.put("symbol", symbol);
		jb.put("color", color);
		return jb.toString();
	}

	@Override
	public String toString()
	{
		return "Actor{" +
		       "pos=" + pos +
		       ", name='" + name + '\'' +
		       ", id=" + id +
		       ", health=" + health +
		       ", color=" + color +
		       ", type=" + type +
		       ", symbol='" + symbol + '\'' +
		       '}';
	}
}
