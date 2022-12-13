package com.amisphere.market;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Market.Products;

final public class Purchase implements Good {

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	static public class Builder {

		private Product				purchase;
		private Date				purchased;
		private Optional<String>	serial					= Optional.empty();
		private Optional<String>	version					= Optional.empty();
		private Optional<String>	versionLastDownloaded	= Optional.empty();

		public Product getProduct() {
			return this.purchase;
		}

		public Builder setPurchase( final Product purchase ) {
			this.purchase = purchase;

			return this;
		}

		public Builder setPurchase( final Products products,final int purchase ) {
			return setPurchase( products.values().stream().filter( product -> product.getId() == purchase ).findFirst().get() );
		}

		public Builder setPurchase( final Products products,final String purchase ) {
			return setPurchase( products,Integer.parseInt( purchase.trim() ) );
		}

		public Builder setPurchased( final Date purchased ) {
			this.purchased = purchased;

			return this;
		}

		public Builder setPurchased( final String purchased ) throws ParseException {
			return setPurchased( new SimpleDateFormat( "yyyy-MM-DD HH:mm:ss" ).parse( purchased ) );
		}

		public Builder setSerialCode( final String serialcode ) {
			this.serial = Optional.ofNullable( serialcode == null || "".equals( serialcode.trim() ) ? null : serialcode.trim() );

			return this;
		}

		public Builder setVersion( final String version ) {
			this.version = Optional.ofNullable( version == null || "".equals( version.trim() ) ? null : version.trim() );

			return this;
		}

		public Builder setVersionLatestDownloaded( final String version ) {
			this.versionLastDownloaded = Optional.ofNullable( version == null || "".equals( version.trim() ) ? null : version.trim() );

			return this;
		}

		public Purchase build() {
			return new Purchase( this );
		}
	}

	private final Product	purchase;
	private final Date		purchased;

	private final Optional<String>	serial;
	private final Optional<String>	version;
	private final Optional<String>	versionLastDownloaded;

	private Purchase( final Builder builder ) {
		this.purchase = builder.purchase;
		this.purchased = builder.purchased;
		this.serial = builder.serial;
		this.version = builder.version;
		this.versionLastDownloaded = builder.versionLastDownloaded;
	}

	@Override
	public Category getCategory() {
		return getProduct().getCategory();
	}

	@Override
	public String getName() {
		return getProduct().getName();
	}

	public Product getProduct() {
		return this.purchase;
	}

	public Date getPurchased() {
		return this.purchased;
	}

	public Optional<String> getSerialCode() {
		return this.serial;
	}

	public Optional<String> getVersion() {
		return this.version;
	}

	public Optional<String> getVersionLatestDownloaded() {
		return this.versionLastDownloaded;
	}

	@Override
	public int hashCode() {
		return Objects.hash( this.purchase.getId() );
	}

	@Override
	public boolean equals( final Object obj ) {
		if( this == obj )
			return true;
		if( !( obj instanceof Purchase ) )
			return false;
		final Purchase other = (Purchase)obj;
		return this.purchase.getId() == other.getProduct().getId();
	}

	@Override
	public String toString() {
		return MessageFormat.format( "[Purchase: product={0}; date={1}; serial={2}; version={3}; latest_version_download={4} ]",getProduct().getId(),getPurchased(),getSerialCode().orElse( "N/A" ),getVersion().orElse( "N/A" ),getVersionLatestDownloaded().orElse( "N/A" ) );
	}
}
