package com.cvo.web.common.data;

import java.util.Map;

@SuppressWarnings("serial")
public class ModelBox extends BaseBox {

	/**
	 * Instantiates a new model box.
	 */
	public ModelBox() {
		super();
	}

	/**
	 * Instantiates a new model box.
	 *
	 * @param map the map
	 */
	@SuppressWarnings({ "rawtypes" })
	public ModelBox(Map map) {
		super(map);
	}

	/**
	 * Instantiates a new model box.
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor the load factor
	 */
	public ModelBox(int initialCapacity, float loadFactor) {
    	super(initialCapacity, loadFactor);
    }

    /**
     * Instantiates a new model box.
     *
     * @param initialCapacity the initial capacity
     */
    public ModelBox(int initialCapacity) {
    	super(initialCapacity);
    }

}
