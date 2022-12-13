package com.amisphere.market;

import java.text.MessageFormat;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import com.amisphere.market.Market.Products;

final public class CartEntry implements Good {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	static public class Builder {

		private int					id;
		private Optional<Product>	product	= Optional.empty();

		private OptionalDouble		price			= OptionalDouble.empty();
		private Optional<String>	giftusername	= Optional.empty();
		private Optional<String>	giftnickname	= Optional.empty();
		private Optional<String>	giftmessage		= Optional.empty();

		public int getId() {
			return this.id;
		}

		public Builder setId( final int id ) {
			this.id = id;

			return this;
		}

		public Builder setId( final String id ) {
			return setId( Strings.isBlank( id ) ? null : Integer.parseInt( id.trim() ) );
		}

		public Optional<Product> getProduct() {
			return this.product;
		}

		public Builder setProduct( final Product product ) {
			this.product = Optional.ofNullable( product );

			return this;
		}

		public Builder setProduct( final Products products,final int purchase ) {
			return setProduct( products.values().stream().filter( product -> product.getId() == purchase ).findFirst().get() );
		}

		public Builder setProduct( final Products products,final String purchase ) {
			return setProduct( products,Integer.parseInt( purchase.trim() ) );
		}

		public OptionalDouble getPrice() {
			return this.price;
		}

		public Builder setPrice( final Double price ) {
			this.price = price != null ? OptionalDouble.of( price ) : OptionalDouble.empty();

			return this;
		}

		public Builder setPrice( final String price ) {
			return setPrice( Strings.isBlank( price ) ? null : Double.parseDouble( price.trim() ) );
		}

		public Optional<String> getGiftUsername() {
			return this.giftusername;
		}

		public Builder setGiftUsername( final String username ) {
			this.giftusername = Optional.ofNullable( Strings.isBlank( username ) ? null : username );

			return this;
		}

		public Optional<String> getGiftNickname() {
			return this.giftnickname;
		}

		public Builder setGiftNickname( final String nickname ) {
			this.giftnickname = Optional.ofNullable( Strings.isBlank( nickname ) ? null : nickname );

			return this;
		}

		public Optional<String> getGiftMessage() {
			return this.giftmessage;
		}

		public Builder setGiftMessage( final String message ) {
			this.giftmessage = Optional.ofNullable( Strings.isBlank( message ) ? null : message );

			return this;
		}

		public CartEntry build() {
			return new CartEntry( this );
		}
	}

	private final int				id;
	private final Optional<Product>	product;

	private OptionalDouble		price			= OptionalDouble.empty();
	private Optional<String>	giftusername	= Optional.empty();
	private Optional<String>	giftnickname	= Optional.empty();
	private Optional<String>	giftmessage		= Optional.empty();

	private CartEntry( final Builder builder ) {
		this.id = builder.id;
		this.product = builder.product;
		this.price = builder.price;
		this.giftusername = builder.giftusername;
		this.giftnickname = builder.giftnickname;
		this.giftmessage = builder.giftmessage;
	}

	@Override
	public String getName() {
		try {
			return getProduct().get().getName();
		}
		catch( final NoSuchElementException nsse ) {
			return String.valueOf( getId() );
		}
	}

	@Override
	public Category getCategory() {
		return getProduct().get().getCategory();
	}

	public int getId() {
		return this.id;
	}

	public Optional<Product> getProduct() {
		return this.product;
	}

	public OptionalDouble getPrice() {
		return this.price;
	}

	public Optional<String> getGiftUsername() {
		return this.giftusername;
	}

	public Optional<String> getGiftNickname() {
		return this.giftnickname;
	}

	public Optional<String> getGiftMessage() {
		return this.giftmessage;
	}

	@Override
	public int hashCode() {
		return Objects.hash( getId() );
	}

	@Override
	public boolean equals( final Object obj ) {
		if( this == obj )
			return true;
		if( !( obj instanceof CartEntry ) )
			return false;
		final CartEntry other = (CartEntry)obj;
		return getId() == other.getId();
	}

	@Override
	public String toString() {
		return MessageFormat.format( "[CartEntry: id= {0}, product={1}; price={2}]",getId(),getProduct().isEmpty() ? "N/A" : getProduct().get().getId(),getPrice().orElse( 0.0 ) );
	}
}
