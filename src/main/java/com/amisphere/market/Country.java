package com.amisphere.market;

import java.text.MessageFormat;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final public class Country {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	static public class Builder {

		private int		id;
		private String	name;
		private int		vatrate;

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

		public Builder setVatRate( final int vatRate ) {
			this.vatrate = vatRate;

			return this;
		}

		public Builder setVatRate( final String vatRate ) {
			return setVatRate( Integer.parseInt( vatRate.trim() ) );
		}

		public Country build() {
			return new Country( this );
		}
	}

	private final int		id;
	private final String	name;
	private final int		vattate;

	private Country( final Builder builder ) {
		this.id = builder.id;
		this.name = builder.name;
		this.vattate = builder.vatrate;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getVatRate() {
		return this.vattate;
	}

	@Override
	public int hashCode() {
		return Objects.hash( this.id,this.name );
	}

	@Override
	public boolean equals( final Object obj ) {
		if( this == obj )
			return true;
		if( !( obj instanceof Country ) )
			return false;
		final Country other = (Country)obj;
		return this.id == other.id && Objects.equals( this.name,other.name );
	}

	@Override
	public String toString() {
		return MessageFormat.format( "[Country: id={0}, name={1}, vatrate={2} ]",getId(),getName(),getVatRate() );
	}
}
