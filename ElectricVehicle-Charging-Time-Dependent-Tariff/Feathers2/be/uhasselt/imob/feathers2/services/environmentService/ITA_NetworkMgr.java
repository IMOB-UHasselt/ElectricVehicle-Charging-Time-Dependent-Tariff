package be.uhasselt.imob.feathers2.services.environmentService;

/**
 * Traffic Assignment Network Manager Interface.
 * @author IMOB/Luk Knapen
 * @version $Id: ITA_NetworkMgr.java 526 2012-12-01 19:19:54Z sboshort $
 */
public interface ITA_NetworkMgr
{
	/** Initialize Network Manager.
       Because this can require external processes to be activated, it was
       decided tot defer the initialisation until the complete framework has
       been set up, hence the need for this method.
	 */
	public void initialize();

	/** Returns the name of the network to be used for traffic assignment in the specified period.
	 */
	public String networkName(int period);

}
