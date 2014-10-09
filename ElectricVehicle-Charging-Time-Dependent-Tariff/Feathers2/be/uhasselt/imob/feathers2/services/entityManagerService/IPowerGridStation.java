package be.uhasselt.imob.feathers2.services.entityManagerService;

import java.util.ArrayList;

public interface IPowerGridStation
{

	//===============================================================
	/**
	 * @return the name
	 */
	public String getName();

	//===============================================================
	/**
	 * @param name the name to set
	 */
	public void setName(String name);

	//===============================================================
	/**
	 * @return the maxPowerCap
	 */
	public float getMaxPowerCap();

	//===============================================================
	/**
	 * @param maxPowerCap the maxPowerCap to set
	 */
	public void setMaxPowerCap(float maxPowerCap);

	//===============================================================
	/**
	 * @return the price
	 */
	public float[] getPrice();

	//===============================================================
	/**
	 * @param price the price to set
	 */
	public void setPrice(float[] price);

	//===============================================================
	/**
	 * @return the load
	 */
	public float[] getLoad();

	//===============================================================
	/**
	 * @param load the load to set
	 */
	public void setLoad(float[] load);

	//===============================================================
	/**
	 * @return the zones
	 */
	public ArrayList<Integer> getZones();

	//===============================================================
	/**
	 * @param zones the zones to set
	 */
	public void setZones(ArrayList<Integer> zones);
	//===============================================================

}