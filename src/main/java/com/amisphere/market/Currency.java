package com.amisphere.market;

import java.text.MessageFormat;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final public class Currency {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	static public class Builder {

		private int		id;
		private String	name;
		private String	symbol;
		private double	rate	= 1;

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

		public String getSymbol() {
			return this.symbol;
		}

		public Builder setSymbol( final String symbol ) {
			this.symbol = symbol.trim();

			return this;
		}

		public Builder setRate( final double rate ) {
			this.rate = rate;

			return this;
		}

		public Builder setRate( final String rate ) {
			return setRate( Double.parseDouble( rate.trim() ) );
		}

		public Currency build() {
			return new Currency( this );
		}
	}

	private final int		id;
	private final String	name;
	private final String	symbol;
	private final double	rate;

	private Currency( final Builder builder ) {
		this.id = builder.id;
		this.name = builder.name;
		this.symbol = builder.symbol;
		this.rate = builder.rate;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public double getRate() {
		return this.rate;
	}

	@Override
	public int hashCode() {
		return Objects.hash( this.id,this.name );
	}

	@Override
	public boolean equals( final Object obj ) {
		if( this == obj )
			return true;
		if( !( obj instanceof Currency ) )
			return false;
		final Currency other = (Currency)obj;
		return this.id == other.id && Objects.equals( this.name,other.name );
	}

	@Override
	public String toString() {
		return MessageFormat.format( "[Curremcy: id={0}, name={1}, symbol={2}, rate={3} ]",getId(),getName(),getSymbol(),getRate() );
	}
}
