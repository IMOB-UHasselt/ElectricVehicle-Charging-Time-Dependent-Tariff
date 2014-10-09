package be.uhasselt.imob.feathers2.core;

/**
   IAgentStaticData contains the agent permanent signature data.
   <ol>
      <li>Purpose</li>
      <ol>
         <li>This interface specifies the minimal amount of static data required for an agent.</li>
         <li>Research topic specific extensions are best introduced by implementing specific interface or class extensions. Examples : <em>fun-shopping</em> related attributes, ...</li>
      </ol>
      <li>Age expressed in years is kept because that provides most info : age category can easily be derived.</li>
      <li>Multiple people can belong to a specific household.</li>
      <li>Transport means :
      <ol>
         <li>cars are owned by the household</li>
         <li>bikes are assigned to specific persons</li>
      </ol>
      </li>
   </ol>
   
   @author IMOB/lk
   @version $Id: IAgentStaticData.java 51 2011-02-25 23:34:29Z LukKnapen $

*/
public interface IAgentStaticData
{
   /** Most of the definitions used in <code>enum workStatus</code> have been gathered by <code>`dict ...`</code> */

   enum workStatus {
      /** Child, not yet attending school */
      nonSchoolChild,
      /** Young person attending school (up through senior high school) [syn: {school-age child}, {pupil}] */
      schoolChild,
      /** A person engaged in study (after attending senior high school) */
      student,
      /** A worker who is hired to perform a job. */
      employee,
      /** Working for itself [syn: {freelance}] [ant: {salaried}] */
      selfEmployedWorker,
      retired,
      unemployed,
      housekeeper
      }
   /** Age [years]. 
      @postcond. <code>age in [0 .. 120]</code> */
   int age();

   /** Gender */
   boolean male();

   /** Has driver license */
   boolean hasDriverLicense();

   /** Home location. */
   ILocation homeAddress();

   /** Socio-economic classifier used to categorize actors/agents. */
   INumericCategories socioEconomicCategories();

   /** Socio-economic class (income category).
      @postcond. <code>socioEconClass() in [0,{@link #socioEconomicCategories()}]</code>
   */
   int socioEconClass();

   /** Household agent belongs too. */
   IHousehold household();

   /** Number of bikes available to person. */
   int nBikes();

   /** Average yearly total kilometers as a car driver.*/
   int yearlyAverageKmAsDriver();

}

