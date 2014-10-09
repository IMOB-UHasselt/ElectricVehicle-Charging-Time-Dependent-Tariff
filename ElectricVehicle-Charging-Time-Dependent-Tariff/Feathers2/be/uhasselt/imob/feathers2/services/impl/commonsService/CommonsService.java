package be.uhasselt.imob.feathers2.services.impl.commonsService;

import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.commonsService.IConventions;
import be.uhasselt.imob.feathers2.services.commonsService.IEVC_Conventions;
import be.uhasselt.imob.library.config.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.apache.log4j.Logger;

import com.csvreader.CsvWriter;

/**
 * Commons service.
 * @author IMOB/Luk Knapen
 * @version $Id: CommonsService.java 1141 2014-04-07 08:46:33Z MuhammadUsman $
 * 
 <ol>
    <li>Provides constants, conventions and basic support stuff.</li>
 </ol>
 */
public class CommonsService implements ICommonsService
{
	private Logger logger = null;
	private Config config = null;
	private Conventions conventions = null;
	private EVC_Conventions evcConventions = null;
	private boolean configError = false;

	// =========================================================================
	public CommonsService(Config config) throws F2Exception
	{
		assert (config != null) : be.uhasselt.imob.feathers2.core.Constants.PRECOND_VIOLATION + "config == null";
		this.config = config;
		if (logger == null) 
			this.logger = Logger.getLogger(getClass().getName());

		configError = false;
		loadConfig(config);
	}

	// =========================================================================
	/** Provide conventions management implemenetation. */
	public Conventions conventions()
	{
		if (conventions == null) conventions = new Conventions();
		return conventions;
	}


	@Override
    public IEVC_Conventions evcConventions()
    {
		if (evcConventions == null) evcConventions = new EVC_Conventions();
		return evcConventions;
    }

	// =========================================================================
	/** Load configuration. */
	private void loadConfig(Config config)
	{
		// TODO
	}

	// =========================================================================
	/**
      If named directory exists, non-recursively remove all regular files from it otherwise create empty directory.
	 */
	public boolean cleanupDir(String dirName)
	{
		boolean ok = true;
		String msg;
		File directory = new File(dirName);
		try
		{
			if (directory.exists())
			{
				if (directory.isDirectory())
				{
					File[] filesOnDir = directory.listFiles();
					for (int i = 0; i < filesOnDir.length; i++)
					{
						try
						{
							if (filesOnDir[i].isFile())
							{
								// only delete regular files
								filesOnDir[i].delete();
							}
						}
						catch (SecurityException se)
						{
							msg = se.toString()+" : failed to delete ["+filesOnDir[i].toString()+"]";
							logger.error(msg);
							System.err.println(msg);
							ok = false;
						}
					}
				}
				else
				{
					// specified name does designate object different from directory
					msg = "["+dirName+"] exists but is not a directory";
					logger.error(msg);
					System.err.println(msg);
					ok = false;
				}
			}
			else
			{
				// create directory
				directory.mkdirs();
			}
		}
		catch (SecurityException se)
		{
			logger.error(se.toString());
			System.err.println(se.toString());
			ok = false;
		}
		return ok;
	}

	// =========================================================================
	/* inherit from interface */
	public void cleanup()
	{
	}

	@Override
	public String checksumValue(String file) 
	{
		long checksum = 0;
		try 
		{
			FileInputStream fileInputStream = new FileInputStream(file);
			CRC32 crc32 = new CRC32();
			CheckedInputStream checkedInputStream = new CheckedInputStream(fileInputStream, crc32);


			byte[] buffer = new byte[100];
			try 
			{
				logger = Logger.getLogger(getClass().getName());
				while (checkedInputStream.read(buffer) >= 0) 
				{
					checksum = checkedInputStream.getChecksum().getValue();
				}
			} catch (IOException e) 
			{

				logger.error(e.getMessage());
			}
		} catch (FileNotFoundException e) 
		{
			logger.error(e.getMessage());
		}

		return String.valueOf(checksum);
	}

	// =========================================================================
	/* calculate two norm */
	@Override
	public double twoNorm(double[][] matrix) {
		double norm2 = 0;
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix.length; j++)
			{
				double entry = matrix[i][j];
				norm2 += entry * entry;
			}
		}
		return Math.sqrt(norm2);
	}

	@Override
	public double twoNorm(int[][] matrix) {
		double norm2 = 0;
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix.length; j++)
			{
				int entry = matrix[i][j];
				norm2 += entry * entry;
			}
		}
		return Math.sqrt(norm2);
	}

	/*Delete the directory recursively, All folders and files inside it*/
	@Override
	public boolean deleteDirRecusively(String dirName)
	{
		boolean ok = true;
		File dir = new File(dirName);
		try
		{
			if (dir.isDirectory())
			{
				for (File subDir : dir.listFiles())
				{
					ok &= deleteDirRecusively(subDir.getAbsolutePath());
				}
			}
			ok &= dir.delete();
		}
		catch (SecurityException se)
		{
			logger.error(se.getStackTrace());
			System.err.println(se.getStackTrace());
			ok = false;
		}
		return ok;
	}

	@Override
	public double standardDeviation(float[] array)
	{
		int n = array.length;
		if(n == 0) return 0f;

		double sum = 0;
		double sq_sum = 0;
		double val ;
		for(int i = 0; i < n; ++i) 
		{
			val = Double.parseDouble(String.valueOf(array[i]));
			sum += val;
			sq_sum += val * val;
		}
		double mean = sum / n;
		double variance = sq_sum / n - mean * mean;
		return Math.sqrt(variance);
	}

	@Override
	public void writeDataForPopulationDensity(String fileName, float[] array)
    {
		
		Arrays.sort(array); 
		try
		{
			File file = new File(fileName);//.createNewFile();
			if(file.exists())
			{
				file.delete();
			}
			file.createNewFile();
			CsvWriter writer = new CsvWriter(new FileWriter(fileName, false), ';' );
			int i = 1;
			for(float f : array)
			{
				writer.write(String.valueOf(f));
				writer.write(String.valueOf(i));
				writer.endRecord();
				i++;
			}
			writer.close();
		} catch(IOException e){
			logger.error("writeS File ("+fileName+") failed, IOException occurred.\n"+e.getStackTrace());
			System.out.println("writeS File ("+fileName+") failed, IOException occurred.\n"+e.getStackTrace());
		}
    }

	@Override
    public boolean createParentDir(File f)
    {
		boolean success = false;
		File pDir = f.getParentFile();
		if(!pDir.exists())
			success = pDir.mkdirs();
		return success;
    }
}
