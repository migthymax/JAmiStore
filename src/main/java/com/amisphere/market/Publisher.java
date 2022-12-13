package com.amisphere.market;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final public class Publisher {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	static public class Builder {

		private int						id;
		private String					name;
		private Optional<BufferedImage>	image	= Optional.empty();

		public int getId() {
			return this.id;
		}

		public Builder setId( final int id ) {
			this.id = id;

			return this;
		}

		public Builder setId( final String id ) {
			return setId( Integer.parseInt( id.trim() ) );
		}

		public String getName() {
			return this.name;
		}

		public Builder setName( final String name ) {
			this.name = name.trim();

			return this;
		}

		public Builder setImage( final BufferedImage image ) {
			this.image = Optional.of( image );

			return this;
		}

		public Publisher build() {
			return new Publisher( this );
		}
	}

	private final int						id;
	private final String					name;
	private final Optional<BufferedImage>	image;

	private Publisher( final Builder builder ) {
		this.id = builder.id;
		this.name = builder.name;
		this.image = builder.image;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public Optional<BufferedImage> getImage() {
		return this.image;
	}

	@Override
	public int hashCode() {
		return Objects.hash( this.id,this.name );
	}

	@Override
	public boolean equals( final Object obj ) {
		if( this == obj )
			return true;
		if( !( obj instanceof Publisher ) )
			return false;
		final Publisher other = (Publisher)obj;
		return this.id == other.id && Objects.equals( this.name,other.name );
	}

	@Override
	public String toString() {
		return MessageFormat.format( "[Publisher: id={0}, name={1}, image={2} ]",getId(),getName(),getImage().isPresent() );
	}
}
