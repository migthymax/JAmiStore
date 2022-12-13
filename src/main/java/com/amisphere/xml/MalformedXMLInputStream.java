package com.amisphere.xml;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MalformedXMLInputStream extends InputStream {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	final private InputStream delegate;

	public MalformedXMLInputStream( final InputStream in ) {
		this.delegate = new ConvertBRInputStream( new FixBElementsInputStream( new EncodedXMLEntitiesInputStream( new FilterNoneUTF8CharatcersInputStream( in ) ) ) );
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
			if( value == '<' ) {
				this.buffer = new StringBuilder();

				boolean attribute = false;
				int red = this.delegate.read();
				for( ;red != -1 && red != '>';red = this.delegate.read() )
					if( attribute ) {
						if( red == '"' )
							attribute = false;
						else if( red == ' ' || red == '>' || red == '/' ) {
							this.buffer.append( '"' );
							attribute = false;
						}

						this.buffer.append( (char)red );
					}
					else {
						this.buffer.append( (char)red );

						if( red == '=' ) {
							attribute = true;

							red = this.delegate.read();

							if( red == '>' ) {
								this.buffer.append( "\"" );
								break;
							}
							else if( red != '"' )
								this.buffer.append( '"' );

							this.buffer.append( (char)red );
						}
					}

				if( red == '>' && attribute ) {
					attribute = false;
					this.buffer.append( '"' );
				}

				if( red != -1 )
					this.buffer.append( (char)red );
			}

			return value;
		}
	}
}
