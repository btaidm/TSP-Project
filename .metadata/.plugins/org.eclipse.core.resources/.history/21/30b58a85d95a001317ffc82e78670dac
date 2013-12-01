package com.tsp.util;

import java.util.concurrent.atomic.AtomicInteger;

public class KDTuple {

	private AtomicInteger kills;
	private AtomicInteger deaths;
	
	public KDTuple() {
		kills = new AtomicInteger(0);
		deaths = new AtomicInteger(0);
	}
	
	public KDTuple(int kills, int deaths) {
		this.kills = new AtomicInteger(kills);
		this.deaths = new AtomicInteger(deaths);
	}
	
	public void incrementKills() {
		this.kills.getAndIncrement();
	}
	
	public int kills() {
		return this.kills.get();
	}
	
	public void incrementDeaths() {
		this.deaths.getAndIncrement();
	}
	
	public int deaths() {
		return this.deaths.get();
	}
	
	@Override
	public String toString() {
		return this.kills.get() + "/" + this.deaths.get();
	}
}
