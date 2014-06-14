package com.codeon.gogreen;

import android.location.Location;

public class Park {
	private String address;
	private String name;
	private double dist;
	public double getDist() {
		return dist;
	}
	public void setDist(double dist) {
		this.dist = dist;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Location getLoc() {
		return loc;
	}
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	private Location loc;
	public Park(String a, String n, double distance){
		name = n;
		address = a;
		dist = distance;
	}
}
