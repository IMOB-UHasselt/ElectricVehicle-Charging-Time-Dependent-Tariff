/**
 * 
 */
package be.uhasselt.imob.feathers2.services.impl.entityManagerService;

import java.io.IOException;
import java.util.ArrayList;

import com.csvreader.CsvWriter;

import be.uhasselt.imob.feathers2.services.entityManagerService.IHouseHold;
import be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.Car;

/**
 * This is a bean class for the Household Entity. A house is distinguished by a unique number. A house may contain a number of persons and the cars. 
 * 
 * @author MuhammadUsman
 *
 */
public class HouseHold implements IHouseHold
{
	/**a unique id which a house hold obtains*/
	private int H_householdCounter;

	/**List of persons living in the particular house*/
	private ArrayList<Person> persons = null;

	/**List of cars possessed by the particular house*/
	private ArrayList<Car> cars = null;

	/** Household home location identifier */
	private short H_hhlocid;

	private short H_SEC;
	private short H_child;
	private short H_ncar;
	private short H_age;
	private short H_comp;

	
	
	

	// ====================================================================================
	/**
	 * @param H_householdCounter
	 * @param persons
	 * @param cars
	 * @param H_hhlocid
	 * @param H_SEC
	 * @param H_child
	 * @param H_ncar
	 * @param H_age
	 * @param H_comp
	 */
	public HouseHold(short H_householdCounter, ArrayList<Person> persons, ArrayList<Car> cars, short H_hhlocid, short H_SEC, short H_child, short H_ncar, short H_age, short H_comp)
	{
		this.H_householdCounter = H_householdCounter;
		this.persons = persons;
		this.cars = cars;
		this.H_hhlocid = H_hhlocid;
		this.H_SEC = H_SEC;
		this.H_child = H_child;
		this.H_ncar = H_ncar;
		this.H_age = H_age;
		this.H_comp = H_comp;
	}

	public HouseHold() 
	{
	}

	public HouseHold(int h_householdCounter, short h_hhlocid, short h_SEC,
			short h_child, short h_ncar, short h_age, short h_comp) 
	{
		this.H_householdCounter = h_householdCounter;
		this.H_hhlocid = h_hhlocid;
		this.H_SEC = h_SEC;
		this.H_child = h_child;
		this.H_ncar = h_ncar;
		this.H_age = h_age;
		this.H_comp = h_comp;
	}

	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ii#getH_hhlocid()
	 */
	@Override
	public short getH_hhlocid()
	{
		return H_hhlocid;
	}

	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ii#setH_hhlocid(short)
	 */
	@Override
	public void setH_hhlocid(short H_hhlocid) {
		this.H_hhlocid = H_hhlocid;
	}

	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ii#getH_comp()
	 */
	@Override
	public short getH_comp() {
		return H_comp;
	}

	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ii#setH_comp(short)
	 */
	@Override
	public void setH_comp(short H_comp) {
		this.H_comp = H_comp;
	}


	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ii#getH_SEC()
	 */
	@Override
	public short getH_SEC() {
		return H_SEC;
	}


	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ii#setH_SEC(short)
	 */
	@Override
	public void setH_SEC(short H_SEC) {
		this.H_SEC = H_SEC;
	}



	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ii#getH_age()
	 */
	@Override
	public short getH_age() {
		return H_age;
	}


	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ii#setH_age(short)
	 */
	@Override
	public void setH_age(short H_age) {
		this.H_age = H_age;
	}


	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ii#getH_child()
	 */
	@Override
	public short getH_child() {
		return H_child;
	}


	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ii#setH_child(short)
	 */
	@Override
	public void setH_child(short H_child) {
		this.H_child = H_child;
	}


	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ii#getH_ncar()
	 */
	@Override
	public short getH_ncar() {
		return H_ncar;
	}


	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ii#setH_ncar(short)
	 */
	@Override
	public void setH_ncar(short H_ncar) {
		this.H_ncar = H_ncar;
	}



	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.be#getH_householdCounter()
	 */
	@Override
	public int getH_householdCounter() 
	{
		return H_householdCounter;
	}

	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.be#setH_householdCounter(short)
	 */
	@Override
	public void setH_householdCounter(int H_householdCounter) 
	{
		this.H_householdCounter = H_householdCounter;
	}

	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.be#getPerson()
	 */
	@Override
	public ArrayList<Person> getPerson() 
	{
		return persons;
	}

	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.be#setPerson(java.util.ArrayList)
	 */
	@Override
	public void setPerson(ArrayList<Person> persons) 
	{
		this.persons = persons;
	}

	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.be#getCars()
	 */	@Override
	public ArrayList<Car> getCars() 
	{
		return cars;
	}
	// ====================================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.be#setCars(java.util.ArrayList)
	 */
	@Override
	public void setCars(ArrayList<Car> cars) 
	{
		this.cars = cars;
	}

	public void writeValues(CsvWriter writer) throws IOException 
	{
		writer.write(String.valueOf(H_householdCounter));
		writer.write(String.valueOf(H_hhlocid));
		writer.write(String.valueOf(H_comp));
		writer.write(String.valueOf(H_SEC));
		writer.write(String.valueOf(H_age));
		writer.write(String.valueOf(H_child));
		writer.write(String.valueOf(H_ncar));		
	}


}
