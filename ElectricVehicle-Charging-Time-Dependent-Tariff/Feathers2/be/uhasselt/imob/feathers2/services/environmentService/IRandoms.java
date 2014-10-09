package be.uhasselt.imob.feathers2.services.environmentService;

import java.util.BitSet;

/**
   Utility methods to draw random values according to specified densities.
   <ol>
      <li>Code found on the internet and integrated/adapted by Muhammad Usman.</li>
      <li>Supported probability weight functions (discrete)
         <ol>
            <li>Poisson</li>
            <li>CDF specified in an array (called Multinomial)</li>
         </ol>
      </li>
      <li>Supported probability density functions (continuous)
         <ol>
            <li>Uniform (Are we sure about the quality of this one ? Why hasn't the method from the JDK been used ? This is critical (since every other density depends on this one IMOB/LK).</li>
            <li>Normal</li>
            <li>Gamma</li>
            <li>Chi Square</li>
            <li>Exponential</li>
            <li>Beta</li>
         </ol>
      </li>
      <li>Notes on Gamma density</li>
         <ol>
            <li>Be careful: the formula does not use the conventional parameter naming. The case caleld (shape/scale) on wikipedia is used but the parameters are call alpha an beta (like in the shape/rate case).</li>
            <li>pdf(x;alpha,beta) = 1/(Gamma(alpha)*beta^alpha)*x^(alpha-1)*exp(-1/beta)</li>
            <li><em>beta </em>is a scale factor.</li>
         </ol>
   </ol>
   @author IMOB/Luk Knapen
   @version $Id: IRandoms.java 505 2012-11-28 21:53:52Z sboshort $
*/
public interface IRandoms
{
  /** Return random integer from Poission with parameter lambda.  
   * The mean of this distribution is lambda.  The variance is lambda. */
  public int nextPoisson(double lambda);

  /** Return nextPoisson(1). */
  public int nextPoisson();

  /** Return a random boolean, equally likely to be true or false. */
  public boolean nextBoolean();

  /** Return a random boolean, with probability p of being true. */
  public boolean nextBoolean(double p);

  /** Return a random BitSet with "size" bits, each having probability p of being true. */
  public BitSet nextBitSet (int size, double p);

  /** Return a random double in the range 0 to 1, inclusive, uniformly sampled from that range. 
   * The mean of this distribution is 0.5.  The variance is 1/12. */
  public double nextUniform();

  /** Return a random double in the range a to b, inclusive, uniformly sampled from that range.
   * The mean of this distribution is (b-a)/2.  The variance is (b-a)^2/12 */
  public double nextUniform(double a,double b);

  /** Draw a single sample from multinomial "a". */
  public int nextDiscrete (double[] a);

  /** draw a single sample from (unnormalized) multinomial "a", with normalizing factor "sum". */
  public int nextDiscrete (double[] a, double sum);

  /** Return a random double drawn from a Gaussian distribution with mean 0 and variance 1. */
  public double nextGaussian();

  /** Return a random double drawn from a Gaussian distribution with mean m and variance s2. */
  public double nextGaussian(double m,double s2);

  /** Return a random double drawn from a Gamma distribution with mean 1.0 and variance 1.0. */
  public double nextGamma();

  /** Return a random double drawn from a Gamma distribution with mean alpha and variance 1.0. */
  public double nextGamma(double alpha);

  /** Return a random double drawn from a Gamma distribution with mean alpha*beta and variance alpha*beta^2. */
  public double nextGamma(double alpha, double beta);

  /** Return a random double drawn from a Gamma distribution with mean alpha*beta+lamba and variance alpha*beta^2. */
  public double nextGamma(double alpha,double beta,double lambda);

  /** Return a random double drawn from an Exponential distribution with mean 1 and variance 1. */
  public double nextExp();

  /** Return a random double drawn from an Exponential distribution with mean beta and variance beta^2. */
  public double nextExp(double beta);

  /** Return a random double drawn from an Exponential distribution with mean beta+lambda and variance beta^2. */
  public double nextExp(double beta,double lambda);

  /** Return a random double drawn from an Chi-squarted distribution with mean 1 and variance 2. 
   * Equivalent to nextChiSq(1) */
  public double nextChiSq();

  /** Return a random double drawn from an Chi-squared distribution with mean df and variance 2*df.  */
  public double nextChiSq(int df);

  /** Return a random double drawn from an Chi-squared distribution with mean df+lambda and variance 2*df.  */
  public double nextChiSq(int df,double lambda);

  /** Return a random double drawn from a Beta distribution with mean a/(a+b) and variance ab/((a+b+1)(a+b)^2).  */
  public double nextBeta(double alpha,double beta);

}

