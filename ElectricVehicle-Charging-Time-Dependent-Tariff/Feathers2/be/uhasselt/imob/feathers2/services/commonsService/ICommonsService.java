package be.uhasselt.imob.feathers2.services.commonsService;

import java.beans.beancontext.BeanContext;
import java.io.File;

/**
   The CommonsService provides basic facilities. The conventions object hides all conventions used in the software.
   @author IMOB/Luk Knapen
   @version $Id: ICommonsService.java 1141 2014-04-07 08:46:33Z MuhammadUsman $
 *
 */
public interface ICommonsService
{
	// ----------------------------------------------------------------------
	/** Get conventions embedding object.
	 */
	public IConventions conventions();

	// ----------------------------------------------------------------------
	/** Get conventions embedding object.
	 */
	public IEVC_Conventions evcConventions();
	// ----------------------------------------------------------------------
	/**
      If named directory exists, non-recursively remove all regular files from it otherwise create empty directory.
      @param dirName Name for directory to cleanup or create (can be absolute or relative).
      @return true iff the reqired directory is available (but if it existed before, there is no guarantee that it can be modified).
	 */
	public boolean cleanupDir(String dirName);

	// ----------------------------------------------------------------------
	/** Cleanup. */
	public void cleanup();



	// ----------------------------------------------------------------------
	/**
	 * Calculate checksum for the supplied file using CRC-32.. 
	 * @param file File name including path 
	 * @return calculated checksum
	 */
	public String checksumValue(String file);



	// ----------------------------------------------------------------------

	/**
	 * calculate two dimensional norm of the supplied 2 dimensional matrix of doubles
	 * @param matrix 2-D array of double
	 * @return norm of matrix
	 */
	public double twoNorm(double matrix[][]);
	// ----------------------------------------------------------------------
	/**
	 * 
	 * calculate two dimensional norm of the supplied 2 dimensional matrix of integers
	 * @param matrix 2-D array of integers
	 * @return norm of matrix
	 */
	public double twoNorm(int matrix[][]);
	// ----------------------------------------------------------------------
	/**
	 * Deletes the directory and everything what is inside it (All folders and files)
	 * @param dirName Name for directory  to be deleted
	 */
	public boolean deleteDirRecusively(String dirName);
	/**
	 * calculates the standard deviation of generic type of array. and returns a double type standad deviation.
	 * @param array
	 * @return standard deviation
	 */
	public double standardDeviation(float[] array);
	/**
	 * sorts the array first. then creates a CSV file(complete file path) and dump the data in the file which is served for  PDF-CDF plotter. This is ';' separated file.  
	 * @param fileName
	 * @param array an unsorted array
	 */

	public void writeDataForPopulationDensity(String fileName, float[] array);

	/**
	 * if parent directory of the givn file does not exists, it creates the directory structure for the parent directory.
	 * @param f child File name with path
	 * @return success
	 */
	public boolean createParentDir(File f);

}
