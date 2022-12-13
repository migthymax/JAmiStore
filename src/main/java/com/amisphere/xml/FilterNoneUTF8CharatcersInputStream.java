package com.amisphere.xml;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilterNoneUTF8CharatcersInputStream extends InputStream {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	final private InputStream delegate;

	public FilterNoneUTF8CharatcersInputStream( final InputStream in ) {
		this.delegate = in;
	}

	private StringBuilder buffer = null;

	@Override
	public int read() throws IOException {
		if( this.buffer != null ) {
			final int value = this.buffer.charAt( 0 );

			this.buffer.deleteCharAt( 0 );
			if( this.buffer.length() == 0 )
				this.buffer = null;

			return value;
		}
		else {
			final int value = this.delegate.read();
			if( value == 183 )
				return '*';

			if( value == 174 ) {
				this.buffer = new StringBuilder();
				this.buffer.append( "R)" );

				return '(';
			}

			return value;
		}
	}
}
