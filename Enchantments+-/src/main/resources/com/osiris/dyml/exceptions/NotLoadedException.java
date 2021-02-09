/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.exceptions;

public class NotLoadedException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6529686340569534705L;

	@Override
    public String getMessage() {
        return "Make sure to call the load() method once before setting/adding any values!";
    }
}
