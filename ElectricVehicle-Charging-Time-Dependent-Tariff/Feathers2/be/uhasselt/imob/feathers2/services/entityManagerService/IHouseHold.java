/**
 * 
 */
package be.uhasselt.imob.feathers2.services.entityManagerService;

import java.io.IOException;
import java.util.ArrayList;

import com.csvreader.CsvWriter;

import be.uhasselt.imob.feathers2.services.impl.entityManagerService.Person;
import be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.Car;

/**
 * @author MuhammadUsman
 *
 */
public interface IHouseHold 
{
	/** Returns household home location identifier */
	public short getH_hhlocid();

	/** Register household home location identifier */
	public void setH_hhlocid(short H_hhlocid);

	/**
	 * @return the h_comp
	 */
	public short getH_comp();

	/**
	 * @param h_comp the h_comp to set
	 */
	public void setH_comp(short h_comp);

	/**
	 * @return the h_SEC
	 */
	public short getH_SEC();

	/**
	 * @param h_SEC the h_SEC to set
	 */
	public void setH_SEC(short h_SEC);

	/**
	 * @return the h_age
	 */
	public short getH_age();

	/**
	 * @param h_age the h_age to set
	 */
	public void setH_age(short h_age);

	/**
	 * @return the h_child
	 */
	public short getH_child();

	/**
	 * @param h_child the h_child to set
	 */
	public void setH_child(short h_child);

	/**
	 * @return the h_ncar
	 */
	public short getH_ncar();

	/**
	 * @param h_ncar the h_ncar to set
	 */
	public void setH_ncar(short h_ncar);

	/**
	 * @return the h_householdCounter
	 */
	public  int getH_householdCounter();

	/**
	 * @param h_householdCounter the h_householdCounter to set
	 */
	public  void setH_householdCounter(int h_householdCounter);

	/**
	 * @return the persons List
	 */
	public  ArrayList<Person> getPerson();

	/**
	 * @param persons the persons List to set
	 */
	public  void setPerson(ArrayList<Person> persons);

	/**
	 * @return the cars List
	 */
	public  ArrayList<Car> getCars();

	/**
	 * @param cars the cars List to set
	 */
	public  void setCars(ArrayList<Car> cars);

	public void writeValues(CsvWriter writer) throws IOException;

}
