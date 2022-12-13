package com.amisphere.xml;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConvertBRInputStream extends InputStream {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	final private InputStream delegate;

	public ConvertBRInputStream( final InputStream in ) {
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
			int value = this.delegate.read();
			if( value == '<' ) {
				final StringBuilder reference = new StringBuilder();
				int ch;
				for( ch = this.delegate.read();Character.isLetterOrDigit( ch );ch = this.delegate.read() )
					reference.append( (char)ch );

				if( ch == '>' && "br".equalsIgnoreCase( reference.toString() ) )
					value = '\r';
				else {
					this.buffer = new StringBuilder();
					this.buffer.append( reference ).append( (char)ch );
				}
			}

			return value;
		}
	}
}
