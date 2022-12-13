package com.amisphere.market;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Market.Products;
import com.amisphere.market.Market.Publishers;

final public class Product implements Good {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	static public class Builder {

		private int		id;
		private String	code;

		private Category			category;
		private Optional<Publisher>	publisher	= Optional.empty();
		private String				name;
		private String				version;
		private String				status;

		private double			price;
		private Optional<Date>	available	= Optional.empty();

		private Optional<Product>	upgradeFrom		= Optional.empty();
		private OptionalDouble		upgradePrice	= OptionalDouble.empty();

		private String	overview;
		private String	features;
		private String	requirements;

		private Optional<String> brochure = Optional.empty();

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

		public String getCode() {
			return this.code;
		}

		public Builder setCode( final String code ) {
			this.code = code.trim();

			return this;
		}

		public Builder setCategory( final Category category ) {
			this.category = category;

			return this;
		}

		public Builder setPublisher( final Publisher publisher ) {
			this.publisher = Optional.ofNullable( publisher );

			return this;
		}

		public Builder setPublisher( final Publishers publishers,final String name ) {
			return setPublisher( publishers.values().stream().filter( publisher -> publisher.getName().equals( name ) ).findFirst().orElse( null ) );
		}

		public String getName() {
			return this.name;
		}

		public Builder setName( final String name ) {
			this.name = name.trim();

			return this;
		}

		public String getVersion() {
			return this.version;
		}

		public Builder setVersion( final String version ) {
			this.version = version.trim();

			return this;
		}

		public String getStatus() {
			return this.status;
		}

		public Builder setStatus( final String status ) {
			this.status = status.trim();

			return this;
		}

		public double getPrice() {
			return this.price;
		}

		public Builder setPrice( final double price ) {
			this.price = price;

			return this;
		}

		public Builder setPrice( final String price ) {
			return setPrice( Double.parseDouble( price.trim() ) );
		}

		public Optional<Date> getAvailable() {
			return this.available;
		}

		public Builder setAvailable( final Date available ) {
			this.available = Optional.ofNullable( available );

			return this;
		}

		public Builder setAvailable( final String available ) throws ParseException {
			return setAvailable( available == null || "".equals( available.trim() ) ? null : new SimpleDateFormat( "yyyy-MM-DD" ).parse( available.trim() ) );
		}

		public Optional<Product> getUpgradeFrom() {
			return this.upgradeFrom;
		}

		public Builder setUpgradeFrom( final Product upgradeFrom ) {
			this.upgradeFrom = Optional.ofNullable( upgradeFrom );

			return this;
		}

		public Builder setUpgradeFrom( final Products products,final String upgradeFrom ) throws NoSuchElementException {
			try {
				return setUpgradeFrom( upgradeFrom == null || "".equals( upgradeFrom.trim() ) ? null : products.get( Integer.parseInt( upgradeFrom ) ) );
			}
			catch( final NumberFormatException nfe ) {
				return setUpgradeFrom( products.values().stream().filter( product -> product.getName().equals( upgradeFrom ) ).findFirst().get() );
			}
		}

		public OptionalDouble getUpgradePrice() {
			return this.upgradePrice;
		}

		public Builder setUpgradePrice( final double upgradePrice ) {
			this.upgradePrice = upgradePrice == 0.0 ? OptionalDouble.empty() : OptionalDouble.of( upgradePrice );

			return this;
		}

		public Builder setUpgradePrice( final String upgradePrice ) {
			return setUpgradePrice( Double.parseDouble( upgradePrice.trim() ) );
		}

		public String getOverview() {
			return this.overview;
		}

		public Builder setOverview( final String overview ) {
			this.overview = overview.trim();

			return this;
		}

		public String getFeatures() {
			return this.features;
		}

		public Builder setFeatures( final String features ) {
			this.features = features.trim();

			return this;
		}

		public String getRequirements() {
			return this.requirements;
		}

		public Builder setRequirements( final String requirements ) {
			this.requirements = requirements.trim();

			return this;
		}

		public Optional<String> getBrochure() {
			return this.brochure;
		}

		public Builder setBrochure( final String brochure ) {
			this.brochure = Optional.ofNullable( "".equals( brochure.trim() ) ? null : brochure.trim() );

			return this;
		}

		public Product build() {
			return new Product( this );
		}
	}

	private final int		id;
	private final String	code;

	private final Category				category;
	private final Optional<Publisher>	publisher;
	private final String				name;
	private final String				version;
	private final String				status;

	private final double			price;
	private final Optional<Date>	available;

	private final Optional<Product>	upgradeFrom;
	private final OptionalDouble	upgradePrice;

	private final String	overview;
	private final String	features;
	private final String	requirements;

	private final Optional<String> brochure = Optional.empty();

	private Product( final Builder builder ) {
		this.id = builder.id;
		this.code = builder.code;
		this.category = builder.category;
		this.publisher = builder.publisher;
		this.name = builder.name;
		this.version = builder.version;
		this.status = builder.status;
		this.price = builder.price;
		this.available = builder.available;
		this.upgradeFrom = builder.upgradeFrom;
		this.upgradePrice = builder.upgradePrice;
		this.overview = builder.overview.replace( "\r","<br/>" ).replaceAll( "[\t ]{2,}","" );
		this.features = builder.features.replace( "\r","<br/>" ).replaceAll( "[\t ]{2,}","" );
		this.requirements = builder.requirements.replace( "\r","<br/>" ).replaceAll( "[\t ]{2,}","" );
	}

	public int getId() {
		return this.id;
	}

	public String getCode() {
		return this.code;
	}

	@Override
	public Category getCategory() {
		return this.category;
	}

	public Optional<Publisher> getPublisher() {
		return this.publisher;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getVersion() {
		return this.version;
	}

	public String getStatus() {
		return this.status;
	}

	public double getPrice() {
		return this.price;
	}

	public Optional<Date> getAvailable() {
		return this.available;
	}

	public Optional<Product> getUpgradeFrom() {
		return this.upgradeFrom;
	}

	public OptionalDouble getUpgradePrice() {
		return this.upgradePrice;
	}

	public String getOverview() {
		return this.overview;
	}

	public String getFeatures() {
		return this.features;
	}

	public String getRequirements() {
		return this.requirements;
	}

	public Optional<String> getBrochure() {
		return this.brochure;
	}

	@Override
	public int hashCode() {
		return Objects.hash( this.id,this.name );
	}

	@Override
	public boolean equals( final Object obj ) {
		if( this == obj )
			return true;
		if( !( obj instanceof Product ) )
			return false;
		final Product other = (Product)obj;
		return this.id == other.id && Objects.equals( this.name,other.name );
	}

	@Override
	public String toString() {
		return MessageFormat.format( "[Produuct: id={0}; category={1}; name={2} ]",getId(),getCategory().getId(),getName() );
	}
}
