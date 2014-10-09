/**
 * 
 */
package be.uhasselt.imob.feathers2.services.entityManagerService;

import java.io.IOException;
import java.util.ArrayList;

import com.csvreader.CsvWriter;

import be.uhasselt.imob.feathers2.services.impl.entityManagerService.Schedule;

/**
 * @author MuhammadUsman
 *
 */
public interface IPerson  extends  Comparable<IPerson>
{
	/**
	 * @return the schedule
	 */
	public ISchedule getSchedule();

	public IHouseHold getHouse();
	public int getP_personCounter();
	public void writeValues(CsvWriter writer) throws IOException;
	public short getP_age();
	public short getP_gend();
	public short getP_twork();

	/**
	 * checks whether all activities location exist in list of zones
	 * @param zones list of subzones ids
	 * @return if schedule locations exist in list
	 */
	public boolean hasAllActInZoneList(ArrayList<Short> zones);

	/**
	 * creates the clone of the object
	 * It also clones all of the contained objects as well
	 * @return IPerson
	 */
	public IPerson clone();

}
