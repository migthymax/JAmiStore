package com.amisphere.market;

import java.text.MessageFormat;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final public class Category {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	static public class Builder {

		private int		id;
		private String	name;

		private int count = 0;

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

		public Builder setProductCount( final int count ) {
			this.count = count;

			return this;
		}

		public Builder setProductCount( final String count ) {
			return setProductCount( Integer.parseInt( count.trim() ) );
		}

		public Category build() {
			return new Category( this );
		}
	}

	private final int		id;
	private final String	name;
	private final int		count;

	private Category( final Builder builder ) {
		this.id = builder.id;
		this.name = builder.name;
		this.count = builder.count;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getProductCount() {
		return this.count;
	}

	@Override
	public int hashCode() {
		return Objects.hash( this.id,this.name );
	}

	@Override
	public boolean equals( final Object obj ) {
		if( this == obj )
			return true;
		if( !( obj instanceof Category ) )
			return false;
		final Category other = (Category)obj;
		return this.id == other.id && Objects.equals( this.name,other.name );
	}

	@Override
	public String toString() {
		return MessageFormat.format( "[Catgegory: id={0}, name={1}, productcount={2} ]",getId(),getName(),getProductCount() );
	}
}
