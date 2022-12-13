package com.amisphere.xml;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EncodedXMLEntitiesInputStream extends InputStream {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	final private InputStream delegate;

	public EncodedXMLEntitiesInputStream( final InputStream in ) {
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
			if( value == '&' ) {
				this.buffer = new StringBuilder();

				final StringBuilder reference = new StringBuilder();
				int ch;
				for( ch = this.delegate.read();Character.isLetterOrDigit( ch );ch = this.delegate.read() )
					reference.append( (char)ch );
				// Did we tidily finish?
				if( ch == ';' )
					this.buffer.append( reference ).append( (char)ch );
				else
					// Did not terminate properly!
					// Perhaps an & on its own or a malformed reference.
					// Either way, escape the &
					this.buffer.append( "amp;" ).append( reference ).append( (char)ch );
			}
			else if( value == 163 ) {
				this.buffer = new StringBuilder( "#163; " );

				return '&';
			}
			else if( value == 128 ) {
				this.buffer = new StringBuilder( "#128; " );

				return '&';
			}

			return value;
		}
	}
}
