package com.amisphere.xml;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FixBElementsInputStream extends InputStream {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	final private InputStream delegate;

	public FixBElementsInputStream( final InputStream in ) {
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
				this.buffer = new StringBuilder();

				int ch;
				for( ch = this.delegate.read();Character.isLetterOrDigit( ch );ch = this.delegate.read() )
					this.buffer.append( (char)ch );
				this.buffer.append( (char)ch );

				if( ch == ']' ) {
					final int red = this.delegate.read();
					if( red == '*' )
						this.buffer.replace( this.buffer.length() - 1,this.buffer.length(),">" );
					this.buffer.append( (char)red );
				}
				else if( ch == ' ' ) {
					final int red = this.delegate.read();
					if( red == ']' )
						this.buffer.append( ">" );
					else
						this.buffer.append( (char)red );
				}
			}
			else if( value == '[' ) {
				int red = this.delegate.read();
				if( red == 'b' ) {
					this.buffer = new StringBuilder();
					this.buffer.append( (char)red );

					red = this.delegate.read();
					if( red == '>' ) {
						this.buffer.append( (char)red );
						value = '<';
					}
				}
			}

			return value;
		}
	}
}
