package com.sixtyfour.cbmnative;

/**
 * @author EgonOlsen
 * 
 */
public interface PlatformProvider {

	/**
	 * @return
	 */
	int getStackSize();

	/**
	 * @return
	 */
	int getForStackSize();

	/**
	 * @return
	 */
	Optimizer getOptimizer();

	/**
	 * @return
	 */
	Transformer getTransformer();

	/**
	 * @return
	 */
	Unlinker getUnlinker();

	/**
	 * @return
	 */
	boolean useLooseTypes();
}
