package be.uhasselt.imob.feathers2.services.impl.entityManagerService;

import java.io.IOException;
import java.util.ArrayList;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.environmentService.IIncident;
import org.apache.log4j.Logger;

import com.csvreader.CsvWriter;

/**
 * This is a bean class which provide all fields for one episode from predicted schedule file from feathers.
   <ol>
      <li>Note that the private variables names reflect the terminology used in <code>Feathers0</code>.</li>
      <li>Definitions of <code>[f0_tod]</code> and <code>[minOfDay]</code> are in {@link be.uhasselt.imob.feathers2.services.reschedulingService} package info.</li>
      <li>Time values are kept and returned as [f0_tod]. Some methods allow times to be specified in <code>[minOfDay]</code>.</li>
      <li>As much variables as possible are made <code>static</code> in order to save memory. Millions of <em>episode</em> objects are instantiated simultaneously.</li>
      <li>Times used here all are <em>shiftedTime</em>s : see {@link be.uhasselt.imob.feathers2.services.commonsService.IConventions}</li>
   </ol>
 * @author MuhammadUsman
 */
public class Episode
{

	private int h_householdCounter;
	private short h_hhlocid;
	private short h_SEC;
	private short h_child;
	private short h_ncar;
	private short h_age;
	private short h_comp;
	
	private int P_personCounter;
	private short P_gend;
	private short P_age;
	private short P_twork;
	private short P_isDriv;
	
	/** Day-of-week : used only to be able to write new schedule using same format and contents as the original one. */
	private  short A_day;
	/** Feathers0 encoded activity type*/
	private short A_activityType;
	/** Feathers0 assigned activity identifier */
	private int A_activityCounter;
	/** Activity location identifier*/
	private short A_location;
	/** Activity start time [Feather0]*/
	private short A_beginningTime;
	/** Activity duration [min]*/
	private double A_duration;
	
	/** Feathers0 encoded transportation mode */
	private short J_transportMode;
	/** Trip distance [km]*/
	private double J_distance;
	/** Episode duration [min]*/
	private double J_duration = -1;
	

	
	
	
	/**
	 * @param h_householdCounter
	 * @param h_hhlocid
	 * @param h_SEC
	 * @param h_child
	 * @param h_ncar
	 * @param h_age
	 * @param h_comp
	 * @param p_personCounter
	 * @param p_gend
	 * @param p_age
	 * @param p_twork
	 * @param p_isDriv
	 * @param a_day
	 * @param a_activityType
	 * @param a_activityCounter
	 * @param a_location
	 * @param a_beginningTime
	 * @param a_duration2
	 * @param j_transportMode
	 * @param j_distance
	 * @param j_duration2
	 */
	
	                 
	public Episode(int h_householdCounter, short h_hhlocid, short h_SEC,
			short h_child, short h_ncar, short h_age, short h_comp,
			int p_personCounter, short p_gend, short p_age, short p_twork,
			short p_isDriv, short a_day, short a_activityType,
			int a_activityCounter, short a_location, short a_beginningTime,
			double a_duration2, short j_transportMode, double j_distance,
			double j_duration2) {
		this.h_householdCounter = h_householdCounter;
		this.h_hhlocid = h_hhlocid;
		this.h_SEC = h_SEC;
		this.h_child = h_child;
		this.h_ncar = h_ncar;
		this.h_age = h_age;
		this.h_comp = h_comp;
		P_personCounter = p_personCounter;
		P_gend = p_gend;
		P_age = p_age;
		P_twork = p_twork;
		P_isDriv = p_isDriv;
		A_day = a_day;
		A_activityType = a_activityType;
		A_activityCounter = a_activityCounter;
		A_location = a_location;
		A_beginningTime = a_beginningTime;
		A_duration = a_duration2;
		J_transportMode = j_transportMode;
		J_distance = j_distance;
		J_duration = j_duration2;
	}
	/**
	 * @return the p_personCounter
	 */
	public int getP_personCounter() {
		return P_personCounter;
	}
	/**
	 * @param p_personCounter the p_personCounter to set
	 */
	public void setP_personCounter(int p_personCounter) {
		P_personCounter = p_personCounter;
	}
	/**
	 * @return the p_gend
	 */
	public short getP_gend() {
		return P_gend;
	}
	/**
	 * @param p_gend the p_gend to set
	 */
	public void setP_gend(short p_gend) {
		P_gend = p_gend;
	}
	/**
	 * @return the p_age
	 */
	public short getP_age() {
		return P_age;
	}
	/**
	 * @param p_age the p_age to set
	 */
	public void setP_age(short p_age) {
		P_age = p_age;
	}
	/**
	 * @return the p_twork
	 */
	public short getP_twork() {
		return P_twork;
	}
	/**
	 * @param p_twork the p_twork to set
	 */
	public void setP_twork(short p_twork) {
		P_twork = p_twork;
	}
	/**
	 * @return the p_isDriv
	 */
	public short getP_isDriv() {
		return P_isDriv;
	}
	/**
	 * @param p_isDriv the p_isDriv to set
	 */
	public void setP_isDriv(short p_isDriv) {
		P_isDriv = p_isDriv;
	}
	/**
	 * @return the h_householdCounter
	 */
	public int getH_householdCounter() {
		return h_householdCounter;
	}
	/**
	 * @param h_householdCounter the h_householdCounter to set
	 */
	public void setH_householdCounter(int h_householdCounter) {
		this.h_householdCounter = h_householdCounter;
	}
	/**
	 * @return the h_hhlocid
	 */
	public short getH_hhlocid() {
		return h_hhlocid;
	}
	/**
	 * @param h_hhlocid the h_hhlocid to set
	 */
	public void setH_hhlocid(short h_hhlocid) {
		this.h_hhlocid = h_hhlocid;
	}
	/**
	 * @return the h_SEC
	 */
	public short getH_SEC() {
		return h_SEC;
	}
	/**
	 * @param h_SEC the h_SEC to set
	 */
	public void setH_SEC(short h_SEC) {
		this.h_SEC = h_SEC;
	}
	/**
	 * @return the h_child
	 */
	public short getH_child() {
		return h_child;
	}
	/**
	 * @param h_child the h_child to set
	 */
	public void setH_child(short h_child) {
		this.h_child = h_child;
	}
	/**
	 * @return the h_ncar
	 */
	public short getH_ncar() {
		return h_ncar;
	}
	/**
	 * @param h_ncar the h_ncar to set
	 */
	public void setH_ncar(short h_ncar) {
		this.h_ncar = h_ncar;
	}
	/**
	 * @return the h_age
	 */
	public short getH_age() {
		return h_age;
	}
	/**
	 * @param h_age the h_age to set
	 */
	public void setH_age(short h_age) {
		this.h_age = h_age;
	}
	/**
	 * @return the h_comp
	 */
	public short getH_comp() {
		return h_comp;
	}
	/**
	 * @param h_comp the h_comp to set
	 */
	public void setH_comp(short h_comp) {
		this.h_comp = h_comp;
	}
	/**
	 * @return the a_day
	 */
	public short getA_day() {
		return A_day;
	}
	/**
	 * @param a_day the a_day to set
	 */
	public void setA_day(short a_day) {
		A_day = a_day;
	}
	/**
	 * @return the a_activityType
	 */
	public short getA_activityType() {
		return A_activityType;
	}
	/**
	 * @param a_activityType the a_activityType to set
	 */
	public void setA_activityType(short a_activityType) {
		A_activityType = a_activityType;
	}
	/**
	 * @return the a_activityCounter
	 */
	public int getA_activityCounter() {
		return A_activityCounter;
	}
	/**
	 * @param a_activityCounter the a_activityCounter to set
	 */
	public void setA_activityCounter(int a_activityCounter) {
		A_activityCounter = a_activityCounter;
	}
	/**
	 * @return the a_location
	 */
	public short getA_location() {
		return A_location;
	}
	/**
	 * @param a_location the a_location to set
	 */
	public void setA_location(short a_location) {
		A_location = a_location;
	}
	/**
	 * @return the j_transportMode
	 */
	public short getJ_transportMode() {
		return J_transportMode;
	}
	/**
	 * @param j_transportMode the j_transportMode to set
	 */
	public void setJ_transportMode(short j_transportMode) {
		J_transportMode = j_transportMode;
	}
	/**
	 * @return the j_distance
	 */
	public double getJ_distance() {
		return J_distance;
	}
	/**
	 * @param j_distance the j_distance to set
	 */
	public void setJ_distance(double j_distance) {
		J_distance = j_distance;
	}
	/**
	 * @return the a_beginningTime
	 */
	public short getA_beginningTime() {
		return A_beginningTime;
	}
	/**
	 * @param a_beginningTime the a_beginningTime to set
	 */
	public void setA_beginningTime(short a_beginningTime) {
		A_beginningTime = a_beginningTime;
	}
	/**
	 * @return the a_duration
	 */
	public double getA_duration() {
		return A_duration;
	}
	/**
	 * @param a_duration the a_duration to set
	 */
	public void setA_duration(double a_duration) {
		A_duration = a_duration;
	}
	/**
	 * @return the j_duration
	 */
	public double getJ_duration() {
		return J_duration;
	}
	/**
	 * @param j_duration the j_duration to set
	 */
	public void setJ_duration(double j_duration) {
		J_duration = j_duration;
	}
	
	public void writeValues(CsvWriter wrtr) throws IOException
	{
		wrtr.write(String.valueOf(h_householdCounter));
		wrtr.write(String.valueOf(h_hhlocid));
		wrtr.write(String.valueOf(h_comp));
		wrtr.write(String.valueOf(h_SEC));
		wrtr.write(String.valueOf(h_age));
		wrtr.write(String.valueOf(h_child));
		wrtr.write(String.valueOf(h_ncar));
		wrtr.write(String.valueOf(P_personCounter));
		wrtr.write(String.valueOf(P_age));
		wrtr.write(String.valueOf(P_twork));
		wrtr.write(String.valueOf(P_gend));
		wrtr.write(String.valueOf(P_isDriv));
		wrtr.write(String.valueOf(A_activityCounter));
		wrtr.write(String.valueOf(A_day));
		wrtr.write(String.valueOf(A_activityType));
		wrtr.write(String.valueOf(A_beginningTime));
		wrtr.write(String.valueOf(A_duration));
		wrtr.write(String.valueOf(A_location));
		wrtr.write(String.valueOf(J_duration));
		wrtr.write(String.valueOf(J_transportMode));
		wrtr.write(String.valueOf(J_distance));
	}
	
	
	
	
	
}

