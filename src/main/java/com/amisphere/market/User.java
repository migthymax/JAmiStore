package com.amisphere.market;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final public class User {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	static public class Builder {

		private final Market	market;
		private final String	authToken;

		private String	id;
		private double	balance;
		private String	status;

		private String	nickname;
		private String	firstname;
		private String	lastname;
		private String	email;

		private Currency	currency;
		private Country		country;

		private final Set<Purchase>		purchases	= new HashSet<>();
		private final Set<Product>		wishes		= new HashSet<>();
		private final Set<CartEntry>	cart		= new HashSet<>();

		public Builder( final Market market,final String authToken ) {
			this.market = market;
			this.authToken = authToken;
		}

		public String getId() {
			return this.id;
		}

		public Builder setId( final String id ) {
			this.id = id;

			return this;
		}

		public double getBalance() {
			return this.balance;
		}

		public Builder setBalance( final double balance ) {
			this.balance = balance;

			return this;
		}

		public Builder setBalance( final String balance ) {
			return setBalance( Double.parseDouble( balance.trim() ) );
		}

		public String getStatus() {
			return this.status;
		}

		public Builder setStatus( final String status ) {
			this.status = status;

			return this;
		}

		public String getNickname() {
			return this.nickname;
		}

		public Builder setNickname( final String nickname ) {
			this.nickname = nickname;

			return this;
		}

		public String getFirstname() {
			return this.firstname;
		}

		public Builder setFirstname( final String firstname ) {
			this.firstname = firstname;

			return this;
		}

		public String getLastname() {
			return this.lastname;
		}

		public Builder setLastname( final String lastname ) {
			this.lastname = lastname;

			return this;
		}

		public String getEMail() {
			return this.email;
		}

		public Builder setEMail( final String email ) {
			this.email = email;

			return this;
		}

		public Country getCountry() {
			return this.country;
		}

		public Builder setCountry( final Country country ) {
			this.country = country;

			return this;
		}

		public Currency getCurrency() {
			return this.currency;
		}

		public Builder setCurrency( final Currency currency ) {
			this.currency = currency;

			return this;
		}

		public Builder addPurchase( final Purchase purchase ) {
			this.purchases.add( purchase );

			return this;
		}

		public Builder addWish( final Product wish ) {
			this.wishes.add( wish );

			return this;
		}

		public Builder addCartEntry( final CartEntry entry ) {
			this.cart.add( entry );

			return this;
		}

		public User build() {
			try {
				return new User( this );
			}
			finally {
				this.purchases.clear();
				this.wishes.clear();
				this.cart.clear();
			}
		}
	}

	private final Market market;

	private final String	authToken;
	private final String	id;
	private final double	credits;
	private final String	status;
	private final String	nickname;
	private final String	firstname;
	private final String	lastname;
	private final String	email;

	private final Country	country;
	private final Currency	currency;

	private final Set<Purchase>		purchases;
	private final Set<Product>		wishes;
	private final Set<CartEntry>	cart;

	private User( final Builder builder ) {
		this.market = builder.market;
		this.authToken = builder.authToken;
		this.id = builder.id;
		this.credits = builder.balance;
		this.status = builder.status;
		this.nickname = builder.nickname;
		this.firstname = builder.firstname;
		this.lastname = builder.lastname;
		this.email = builder.email;
		this.country = builder.country;
		this.currency = builder.currency;
		this.purchases = Collections.unmodifiableSet( new HashSet<>( builder.purchases ) );
		this.wishes = Collections.unmodifiableSet( new HashSet<>( builder.wishes ) );
		this.cart = Collections.unmodifiableSet( new HashSet<>( builder.cart ) );
	}

	public String getAuthToken() {
		return this.authToken;
	}

	public String getId() {
		return this.id;
	}

	public double getCredits() {
		return this.credits;
	}

	public String getStatus() {
		return this.status;
	}

	public String getNickname() {
		return this.nickname;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public String getLastname() {
		return this.lastname;
	}

	public String getEmail() {
		return this.email;
	}

	public Country getCountry() {
		return this.country;
	}

	public Currency getCurrency() {
		return this.currency;
	}

	public Set<Purchase> getPurchases() {
		return this.purchases;
	}

	public Set<Product> getWishes() {
		return this.wishes;
	}

	public Set<CartEntry> getCart() {
		return this.cart;
	}

	public double getCartCredits() {
		return getCart().stream().collect( Collectors.summingDouble( e -> e.getPrice().orElse( 0 ) ) );
	}

	public double getCartSum() {
		return getCart().stream().collect( Collectors.summingDouble( e -> calcCurrency( e.getPrice().orElse( 0 ) ) ) );
	}

	public BufferedImage getAvatar() throws IOException {
		return this.market.getAvatar( this );
	}

	public double calcCurrency( double balance ) {
		balance = Math.round( 100 * ( balance * getCurrency().getRate() - 0.00005 ) ) / 100d;
		balance = Math.round( balance * ( 100 + getCountry().getVatRate() ) - 0.00005 ) / 100d;

		return balance;
	}

	@Override
	public int hashCode() {
		return Objects.hash( this.id );
	}

	@Override
	public boolean equals( final Object obj ) {
		if( this == obj )
			return true;
		if( !( obj instanceof User ) )
			return false;
		final User other = (User)obj;
		return this.id == other.id;
	}

	@Override
	public String toString() {
		return MessageFormat.format( "[User: id={0}; balance={1}; status={2}; purchases={3}; wishes={4} ]",getId(),getCredits(),getStatus(),getPurchases().size(),getWishes().size() );
	}

}
