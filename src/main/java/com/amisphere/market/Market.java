package com.amisphere.market;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.Base64;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amisphere.market.Product.Builder;
import com.amisphere.xml.MalformedXMLInputStream;

public final class Market {

	/**
	 * {@link Logger} for logging proposes.
	 */
	transient final static private Logger logger = LogManager.getLogger();

	public static class Countries extends HashMap<Integer,Country> {

		/**
		 * Auto generated serialUID
		 */
		private static final long serialVersionUID = -5517373805803403446L;

		private Countries() {
		}
	}

	public static class Currencies extends HashMap<Integer,Currency> {

		/**
		 * Auto generated serialUID
		 */
		private static final long serialVersionUID = -5517373805803403446L;

		private Currencies() {
		}
	}

	public static class Categories extends HashMap<Integer,Category> {

		/**
		 * Auto generated serialUID
		 */
		private static final long serialVersionUID = -3317898194716507400L;

		private Categories() {
			put( -1,null );
		}

		static public Categories fromGoods( final Collection<? extends Good> goods ) {
			final Categories result = new Categories();

			goods.forEach( good -> {
				try {
					result.putIfAbsent( good.getCategory().getId(),good.getCategory() );
				}
				catch( final NoSuchElementException nsse ) {}
			} );

			return result;
		}

		static public Categories fromGoods( final ListModel<? extends Good> goods ) {
			final Categories result = new Categories();

			for( int index = 0;index < goods.getSize();index++ ) {
				final Good good = goods.getElementAt( index );
				result.putIfAbsent( good.getCategory().getId(),good.getCategory() );
			}

			return result;
		}
	}

	public static class Publishers extends HashMap<Integer,Publisher> {

		/**
		 * Auto generated serialUID
		 */
		private static final long serialVersionUID = 7137155948058571921L;

		private Publishers() {
		}
	}

	public static class Products extends HashMap<Integer,Product> {

		/**
		 * Auto generated serialUID
		 */
		private static final long serialVersionUID = 2153148719943817899L;

		private Products() {
		}
	}

	public class Purchases extends HashMap<Integer,Purchase> {

		/**
		 * Auto generated serialUID
		 */
		private static final long serialVersionUID = -8720683544620592692L;

		private Purchases() {
		}
	}

	public interface UserUpdateListener extends EventListener {

		public void userUpdate( Market market,Optional<User> user );
	}

	private final URL server;

	private final URL store_version;

	private final URL	function_countries;
	private final URL	function_currencies;
	private final URL	function_publishers;
	private final URL	function_categories;
	private final URL	function_product_categories;
	private final URL	function_product_info;

	private final URL	download_function_user;
	private final URL	download_function_profile;
	private final URL	download_function_mypurchases;
	private final URL	download_function_purchases;
	private final URL	download_function_whishlist;
	private final URL	download_function_getcart;
	private final URL	download_function_addtocart;
	private final URL	download_function_removefromcart;
	private final URL	download_function_checkout;
	private final URL	download_function_buy;
	private final URL	download_function_filemanager;
	private final URL	download_function_pdforderhistory;

	private final URL images_avatars_showavatar;

	private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	private Optional<Countries>		countries	= Optional.empty();
	private Optional<Currencies>	currencies	= Optional.empty();

	private Optional<Categories>	categories	= Optional.empty();
	private Optional<Publishers>	publishers	= Optional.empty();
	private Optional<Products>		products	= Optional.empty();

	private Optional<BufferedImage> avatarUnknown = Optional.empty();

	private Optional<User>		user		= Optional.empty();
	private Optional<Currency>	currency	= Optional.empty();

	private final EventListenerList listeners = new EventListenerList();

	public Market() {
		try {
			this.dbf.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING,true );

			this.server = new URL( "https://www.amisphere.com" );

			this.store_version = new URL( this.server,"storeversion.txt" );

			this.function_countries = new URL( this.server,"functions/countries.php" );
			this.function_currencies = new URL( this.server,"functions/currencies.php" );
			this.function_publishers = new URL( this.server,"functions/publishers.php" );
			this.function_categories = new URL( this.server,"functions/categories.php" );
			this.function_product_categories = new URL( this.server,"functions/product_categories.php" );
			this.function_product_info = new URL( this.server,"functions/product_info.php" );

			this.download_function_user = new URL( this.server,"download/functions/user.php" );
			this.download_function_profile = new URL( this.server,"download/functions/getprofile.php" );
			this.download_function_mypurchases = new URL( this.server,"download/functions/mypurchases.php" );
			this.download_function_purchases = new URL( this.server,"download/functions/purchases.php" );
			this.download_function_whishlist = new URL( this.server,"download/functions/wishlist.php" );
			this.download_function_getcart = new URL( this.server,"download/functions/getcart.php" );
			this.download_function_addtocart = new URL( this.server,"download/functions/addtocart.php" );
			this.download_function_removefromcart = new URL( this.server,"download/functions/removefromcart.php" );
			this.download_function_checkout = new URL( this.server,"checkout.php" );
			this.download_function_buy = new URL( this.server,"buy.php" );
			this.download_function_filemanager = new URL( this.server,"download/functions/file_manager.php" );
			this.download_function_pdforderhistory = new URL( this.server,"download/functions/pdforderhistory.php" );

			this.images_avatars_showavatar = new URL( this.server,"images/avatars/showavatar.php" );
		}
		catch( final MalformedURLException | ParserConfigurationException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

	}

	public String getStoreVersion() throws IOException {
		final URLConnection connection = new URL( this.store_version.toExternalForm() ).openConnection();
		try( BufferedReader response = new BufferedReader( new InputStreamReader( connection.getInputStream(),StandardCharsets.UTF_8 ) ) ) {
			return response.readLine();
		}
	}

	public BufferedImage getUnknownAvatar() throws IOException {
		try {
			return this.avatarUnknown.get();
		}
		catch( final NoSuchElementException nsse ) {
			this.avatarUnknown = Optional.of( retrieveUnknownAvatar() );
			return this.avatarUnknown.get();
		}
	}

	public BufferedImage retrieveUnknownAvatar() throws IOException {
		return ImageIO.read( new URL( this.images_avatars_showavatar.toExternalForm() ) );
	}

	BufferedImage getAvatar( final User user ) throws IOException {
		return ImageIO.read( new URL( this.images_avatars_showavatar.toExternalForm() + "?username=" + user.getId() + ".jpg" ) );
	}

	public Currencies getCurrencies() throws IOException {
		try {
			return this.currencies.get();
		}
		catch( final NoSuchElementException nsse ) {
			this.currencies = Optional.of( retrieveCurrencies() );
			return this.currencies.get();
		}
	}

	public Optional<User> updateCurrency( final Currency currency ) throws IllegalStateException,IOException {
		this.currency = Optional.of( currency );

		try {
			final User user = getUser().get();
			logout();
			return Optional.of( login( user.getAuthToken() ) );
		}
		catch( final NoSuchElementException nsse ) {}

		return Optional.empty();
	}

	private Currencies retrieveCurrencies() throws IOException {
		final Currencies result = new Currencies();

		final URLConnection connection = new URL( this.function_currencies.toExternalForm() ).openConnection();
		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			final NodeList currencies = doc.getElementsByTagName( "currency" );
			for( int index = 0;index < currencies.getLength();index++ ) {
				final Node node = currencies.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element currency = (Element)node;

					final com.amisphere.market.Currency.Builder builder = new com.amisphere.market.Currency.Builder();
					builder.setId( currency.getAttribute( "id" ) );
					builder.setName( currency.getElementsByTagName( "name" ).item( 0 ).getTextContent().trim() );
					builder.setSymbol( currency.getElementsByTagName( "symbol" ).item( 0 ).getTextContent().trim() );
					builder.setRate( currency.getElementsByTagName( "rate" ).item( 0 ).getTextContent().trim() );

					result.put( builder.getId(),builder.build() );
				}
			}
		}
		catch( ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

		return result;
	}

	public Countries getCountries() throws IOException {
		try {
			return this.countries.get();
		}
		catch( final NoSuchElementException nsse ) {
			this.countries = Optional.of( retrieveCountries() );
			return this.countries.get();
		}
	}

	private Countries retrieveCountries() throws IOException {
		final Countries result = new Countries();

		final URLConnection connection = new URL( this.function_countries.toExternalForm() ).openConnection();
		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			final NodeList countries = doc.getElementsByTagName( "country" );
			for( int index = 0;index < countries.getLength();index++ ) {
				final Node node = countries.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element country = (Element)node;

					final Country.Builder builder = new Country.Builder();
					builder.setId( country.getAttribute( "id" ) );
					builder.setName( country.getElementsByTagName( "name" ).item( 0 ).getTextContent().trim() );
					builder.setVatRate( country.getElementsByTagName( "vatrate" ).item( 0 ).getTextContent().trim() );

					result.put( builder.getId(),builder.build() );
				}
			}
		}
		catch( ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

		return result;
	}

	public Categories getCategories() throws IOException {
		try {
			return this.categories.get();
		}
		catch( final NoSuchElementException nsse ) {
			this.categories = Optional.of( retrieveCategories() );
			return this.categories.get();
		}
	}

	private Categories retrieveCategories() throws IOException {
		final Categories result = new Categories();

		final HashMap<Integer,Category.Builder> builders = new HashMap<>();

		URLConnection connection = new URL( this.function_categories.toExternalForm() ).openConnection();
		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			final NodeList categories = doc.getElementsByTagName( "category" );
			for( int index = 0;index < categories.getLength();index++ ) {
				final Node node = categories.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element category = (Element)node;

					final Category.Builder builder = new Category.Builder();
					builder.setId( category.getAttribute( "id" ) );
					builder.setName( category.getTextContent() );

					builders.put( builder.getId(),builder );
				}
			}
		}
		catch( ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

		connection = new URL( this.function_product_categories.toExternalForm() ).openConnection();
		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			final NodeList categories = doc.getElementsByTagName( "category" );
			for( int index = 0;index < categories.getLength();index++ ) {
				final Node node = categories.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element category = (Element)node;

					final String name = category.getElementsByTagName( "name" ).item( 0 ).getTextContent().trim();

					final Category.Builder builder = builders.get( Integer.parseInt( category.getAttribute( "id" ).trim() ) );

					if( !name.equals( builder.getName() ) )
						logger.warn( "Category name '{}' differs compared to '{}'",builder.getName(),name );

					builder.setProductCount( category.getElementsByTagName( "productcount" ).item( 0 ).getTextContent().trim() );

					result.put( builder.getId(),builder.build() );
				}
			}
		}
		catch( ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

		return result;
	}

	public Publishers getPublishers() throws IOException {
		try {
			return this.publishers.get();
		}
		catch( final NoSuchElementException nsse ) {
			this.publishers = Optional.of( retrievePublishers() );
			return this.publishers.get();
		}
	}

	private Publishers retrievePublishers() throws IOException {
		final Publishers result = new Publishers();

		final URLConnection connection = new URL( this.function_publishers.toExternalForm() ).openConnection();
		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			final NodeList publishers = doc.getElementsByTagName( "publisher" );
			for( int index = 0;index < publishers.getLength();index++ ) {
				final Node node = publishers.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element publisher = (Element)node;

					final Publisher.Builder builder = new Publisher.Builder();
					builder.setId( publisher.getAttribute( "id" ) );
					builder.setName( publisher.getElementsByTagName( "name" ).item( 0 ).getTextContent().trim() );

					try {
						final Element image = (Element)publisher.getElementsByTagName( "image" ).item( 0 );

						if( Integer.parseInt( image.getAttribute( "size" ) ) > 0 )
							logger.fatal( "implement image retrival for publishers!" );
					}
					catch( final Throwable thr ) {
						logger.debug( "Publisher has no image element",thr );
					}

					result.put( builder.getId(),builder.build() );
				}
			}
		}
		catch( ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

		return result;
	}

	public Products getProducts() throws IOException {
		try {
			return this.products.get();
		}
		catch( final NoSuchElementException nsse ) {
			this.products = Optional.of( retrieveProducts( getCategories(),getPublishers() ) );
			return this.products.get();
		}
	}

	private Products retrieveProducts( final Categories categories,final Publishers publishers ) throws IOException {
		final Products result = new Products();

		final URLConnection connection = new URL( this.function_product_info.toExternalForm() + "?id=all&version=1.1" ).openConnection();
		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( new InputSource( new InputStreamReader( response,StandardCharsets.UTF_8 ) ) );
			doc.getDocumentElement().normalize();

			final Map<Element,Product.Builder> backlog = new HashMap<>();

			final NodeList products = doc.getElementsByTagName( "product" );
			for( int index = 0;index < products.getLength();index++ ) {
				final Node node = products.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element product = (Element)node;

					final Product.Builder builder = new Product.Builder();
					try {
						builder.setId( product.getAttribute( "id" ) );
						builder.setCode( product.getElementsByTagName( "code" ).item( 0 ).getTextContent().trim() );
						builder.setCategory( categories.get( Integer.parseInt( product.getElementsByTagName( "category" ).item( 0 ).getTextContent().trim() ) ) );
						builder.setPublisher( publishers,product.getElementsByTagName( "publisher" ).item( 0 ).getTextContent().trim() );
						builder.setName( product.getElementsByTagName( "name" ).item( 0 ).getTextContent().trim() );
						builder.setVersion( product.getElementsByTagName( "version" ).item( 0 ).getTextContent().trim() );
						builder.setStatus( product.getElementsByTagName( "status" ).item( 0 ).getTextContent().trim() );
						builder.setPrice( product.getElementsByTagName( "price" ).item( 0 ).getTextContent().trim() );
						try {
							builder.setAvailable( product.getElementsByTagName( "available" ).item( 0 ).getTextContent().trim() );
						}
						catch( final ParseException e ) {}

						builder.setUpgradeFrom( result,product.getElementsByTagName( "upgradefromproduct" ).item( 0 ).getTextContent().trim() );
						builder.setUpgradePrice( product.getElementsByTagName( "upgradeprice" ).item( 0 ).getTextContent().trim() );
						builder.setOverview( product.getElementsByTagName( "overview" ).item( 0 ).getTextContent().trim() );
						builder.setFeatures( product.getElementsByTagName( "features" ).item( 0 ).getTextContent().trim() );
						builder.setRequirements( product.getElementsByTagName( "requirements" ).item( 0 ).getTextContent().trim() );
						builder.setBrochure( product.getElementsByTagName( "brochure" ).item( 0 ).getTextContent().trim() );

						result.put( builder.getId(),builder.build() );
					}
					catch( final NoSuchElementException nsse ) {
						backlog.put( product,builder );
					}

				}
			}

			for( final Map.Entry<Element,Builder> entry : backlog.entrySet() ) {
				final Element product = entry.getKey();

				final Product.Builder builder = entry.getValue();
				builder.setId( product.getAttribute( "id" ) );
				builder.setCode( product.getElementsByTagName( "code" ).item( 0 ).getTextContent().trim() );
				builder.setCategory( categories.get( Integer.parseInt( product.getElementsByTagName( "category" ).item( 0 ).getTextContent().trim() ) ) );
				builder.setPublisher( publishers,product.getElementsByTagName( "publisher" ).item( 0 ).getTextContent().trim() );
				builder.setName( product.getElementsByTagName( "name" ).item( 0 ).getTextContent().trim() );
				builder.setVersion( product.getElementsByTagName( "version" ).item( 0 ).getTextContent().trim() );
				builder.setStatus( product.getElementsByTagName( "status" ).item( 0 ).getTextContent().trim() );
				builder.setPrice( product.getElementsByTagName( "price" ).item( 0 ).getTextContent().trim() );
				try {
					builder.setAvailable( product.getElementsByTagName( "available" ).item( 0 ).getTextContent().trim() );
				}
				catch( final ParseException e ) {}
				try {
					builder.setUpgradeFrom( result,product.getElementsByTagName( "upgradefromproduct" ).item( 0 ).getTextContent().trim() );
				}
				catch( final NoSuchElementException nsse ) {
					logger.warn( "Upgrade product '{}' not in catalog!",product.getElementsByTagName( "upgradefromproduct" ).item( 0 ).getTextContent().trim() );
				}
				builder.setUpgradePrice( product.getElementsByTagName( "upgradeprice" ).item( 0 ).getTextContent().trim() );
				builder.setOverview( product.getElementsByTagName( "overview" ).item( 0 ).getTextContent().trim() );
				builder.setFeatures( product.getElementsByTagName( "features" ).item( 0 ).getTextContent().trim() );
				builder.setRequirements( product.getElementsByTagName( "requirements" ).item( 0 ).getTextContent().trim() );
				builder.setBrochure( product.getElementsByTagName( "brochure" ).item( 0 ).getTextContent().trim() );

				result.put( builder.getId(),builder.build() );
			}
		}
		catch( ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

		return result;
	}

	private Set<Product> getWishlist( final Products products,final String authToken ) throws IOException {
		final Set<Product> wishlist = new HashSet<>();

		final URLConnection connection = new URL( this.download_function_whishlist.toExternalForm() + "?product=all" ).openConnection();
		connection.setRequestProperty( "Authorization","Basic " + authToken );
		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			for( final String pid : doc.getDocumentElement().getTextContent().trim().split( "\\s" ) )
				if( !"".equals( pid.trim() ) )
					wishlist.add( products.get( Integer.parseInt( pid.trim() ) ) );
		}
		catch( ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

		return wishlist;
	}

	public User addToWhishList( final Product product ) throws IllegalStateException,IOException {
		final User user = getUser().get();
		if( addWish( user,product ) ) {
			logout();
			return login( user.getAuthToken() );
		}
		else
			throw new IllegalStateException( "Could not add wish from whishlist!" );
	}

	private boolean addWish( final User user,final Product product ) throws IOException {
		final URLConnection connection = new URL( this.download_function_whishlist.toExternalForm() + "?add=true&product=" + product.getId() ).openConnection();
		connection.setRequestProperty( "Authorization","Basic " + user.getAuthToken() );
		try( BufferedReader response = new BufferedReader( new InputStreamReader( connection.getInputStream(),StandardCharsets.UTF_8 ) ) ) {
			return "added".equalsIgnoreCase( response.readLine().trim() );
		}
	}

	public User removeFromWhishList( final Product product ) throws IllegalStateException,IOException {
		final User user = getUser().get();
		if( removeWish( user,product ) ) {
			logout();
			return login( user.getAuthToken() );
		}
		else
			throw new IllegalStateException( "Could not remove wish from whishlist!" );
	}

	private boolean removeWish( final User user,final Product product ) throws IOException {
		final URLConnection connection = new URL( this.download_function_whishlist.toExternalForm() + "?remove=true&product=" + product.getId() ).openConnection();
		connection.setRequestProperty( "Authorization","Basic " + user.getAuthToken() );
		try( BufferedReader response = new BufferedReader( new InputStreamReader( connection.getInputStream(),StandardCharsets.UTF_8 ) ) ) {
			return "removed".equalsIgnoreCase( response.readLine().trim() );
		}
	}

	private Set<CartEntry> getCart( final Products products,final String authToken ) throws IOException {
		final Set<CartEntry> cart = new HashSet<>();

		final URLConnection connection = new URL( this.download_function_getcart.toExternalForm() ).openConnection();
		connection.setRequestProperty( "Authorization","Basic " + authToken );
		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			final NodeList entries = doc.getElementsByTagName( "product" );
			for( int index = 0;index < entries.getLength();index++ ) {
				final Node node = entries.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element entry = (Element)node;

					final CartEntry.Builder builder = new CartEntry.Builder();
					builder.setId( entry.getElementsByTagName( "cartid" ).item( 0 ).getTextContent().trim() );
					try {
						builder.setProduct( products.get( Integer.parseInt( entry.getAttribute( "id" ).trim() ) ) );
					}
					catch( NumberFormatException | NullPointerException ex ) {}
					builder.setPrice( entry.getElementsByTagName( "price" ).item( 0 ).getTextContent().trim() );
					builder.setGiftUsername( entry.getElementsByTagName( "giftusername" ).item( 0 ).getTextContent().trim() );
					builder.setGiftNickname( entry.getElementsByTagName( "giftnickname" ).item( 0 ).getTextContent().trim() );
					builder.setGiftMessage( entry.getElementsByTagName( "giftmessage" ).item( 0 ).getTextContent().trim() );

					cart.add( builder.build() );
				}
			}
		}
		catch( ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

		return cart;
	}

	public User addToCart( final Product product ) throws IllegalStateException,IOException {
		final User user = getUser().get();
		if( addToCart( user,product ) ) {
			logout();
			return login( user.getAuthToken() );
		}
		else
			throw new IllegalStateException( "Could not add entry to cart!" );
	}

	private boolean addToCart( final User user,final Product product ) throws IOException {
		final URLConnection connection = new URL( this.download_function_addtocart.toExternalForm() + "?product_id=" + product.getId() ).openConnection();
		connection.setRequestProperty( "Authorization","Basic " + user.getAuthToken() );

		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			final String username = doc.getDocumentElement().getElementsByTagName( "username" ).item( 0 ).getTextContent().trim();
			final String productid = doc.getElementsByTagName( "product" ).item( 0 ).getTextContent().trim();

			return user.getId().equals( username ) // CRLF
					&& product.getId() == Integer.parseInt( productid );
		}
		catch( NumberFormatException | ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}
	}

	public User removeFromCart( final CartEntry entry ) throws IllegalStateException,IOException {
		final User user = getUser().get();
		if( removeFromCart( user,entry ) ) {
			logout();
			return login( user.getAuthToken() );
		}
		else
			throw new IllegalStateException( "Could not remove entry from cart!" );
	}

	private boolean removeFromCart( final User user,final CartEntry entry ) throws IOException {
		final URLConnection connection = new URL( this.download_function_removefromcart.toExternalForm() + "?cartid=" + entry.getId() ).openConnection();
		connection.setRequestProperty( "Authorization","Basic " + user.getAuthToken() );

		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			final String username = doc.getDocumentElement().getElementsByTagName( "username" ).item( 0 ).getTextContent().trim();
			final String productid = doc.getElementsByTagName( "product" ).item( 0 ).getTextContent().trim();

			return user.getId().equals( username ) // CRLF
					&& entry.getProduct().isEmpty() || Strings.isBlank( productid ) || entry.getProduct().get().getId() == Integer.parseInt( productid );
		}
		catch( NumberFormatException | ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}
	}

	public void checkoutCart( final User user ) throws IOException,URISyntaxException {
		final URI checkout = new URL( this.download_function_checkout.toExternalForm() + "?uid=" + user.getId() + "&currency=" + user.getCurrency().getName() + "&vat=" + user.getCountry().getVatRate() ).toURI();
		Desktop.getDesktop().browse( checkout );
	}

	public void buyCart( final User user ) throws IOException {
		for( final CartEntry entry : user.getCart() ) {
			buy( user,entry );
			removeFromCart( user,entry );
		}
	}

	private void buy( final User user,final CartEntry entry ) throws IOException {
		final URLConnection connection = new URL( this.download_function_buy.toExternalForm() + "?product=" + entry.getProduct().get().getCode() ).openConnection();
		connection.setRequestProperty( "Authorization","Basic " + user.getAuthToken() );

		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {}
	}

	private Purchases getPurchases( final Products products,final String authToken ) throws IOException {
		final Purchases myPurchases = new Purchases();

		final Map<Integer,Purchase.Builder> builders = new HashMap<>();

		final URLConnection connection = this.download_function_mypurchases.openConnection();
		connection.setRequestProperty( "Authorization","Basic " + authToken );
		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			final NodeList purchases = doc.getElementsByTagName( "purchase" );
			for( int index = 0;index < purchases.getLength();index++ ) {
				final Node node = purchases.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element purchase = (Element)node;

					final Purchase.Builder builder = new Purchase.Builder();
					builder.setPurchase( products.get( Integer.parseInt( purchase.getTextContent().trim() ) ) );
					builders.put( builder.getProduct().getId(),builder );
				}
			}

			final NodeList purchaseds = doc.getElementsByTagName( "purchased" );
			for( int index = 0;index < purchaseds.getLength();index++ ) {
				final Node node = purchaseds.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element purchased = (Element)node;

					final Purchase.Builder builder = builders.get( Integer.parseInt( purchased.getAttribute( "pid" ) ) );
					builder.setPurchased( purchased.getTextContent().trim() );
				}
			}

			final NodeList versions = doc.getElementsByTagName( "version" );
			for( int index = 0;index < versions.getLength();index++ ) {
				final Node node = versions.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element version = (Element)node;

					final Purchase.Builder builder = builders.get( Integer.parseInt( version.getAttribute( "pid" ) ) );
					builder.setVersion( version.getTextContent().trim() );
				}
			}

			final NodeList keycodes = doc.getElementsByTagName( "keycode" );
			for( int index = 0;index < keycodes.getLength();index++ ) {
				final Node node = keycodes.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element keycode = (Element)node;

					final Purchase.Builder builder = builders.get( Integer.parseInt( keycode.getAttribute( "pid" ) ) );
					builder.setSerialCode( keycode.getTextContent().trim() );
				}
			}

		}
		catch( ParseException | ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

		// connection = this.download_function_purchases.openConnection();
		// connection.setRequestProperty( "Authorization","Basic " + Base64.getEncoder().encodeToString( ( name + ":" + password ).getBytes() ) );
		// try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
		// final DocumentBuilder db = this.dbf.newDocumentBuilder();
		//
		// final Document doc = db.parse( response );
		// doc.getDocumentElement().normalize();
		// }
		// catch( ParseException | ParserConfigurationException | SAXException ex ) {
		// throw new RuntimeException( ex.getMessage(),ex );
		// }

		for( final Map.Entry<Integer,Purchase.Builder> entry : builders.entrySet() )
			myPurchases.put( entry.getKey(),entry.getValue().build() );

		return myPurchases;
	}

	public File downloadPurchase( final User user,final Purchase purchase ) throws IOException {
		final URLConnection connection = new URL( this.download_function_filemanager.toExternalForm() + "?product=" + purchase.getProduct().getCode() ).openConnection();
		connection.setRequestProperty( "Authorization","Basic " + user.getAuthToken() );

		System.out.println( "Reqest download at: " + connection.getURL().toExternalForm() );

		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			final NodeList files = doc.getElementsByTagName( "file" );
			for( int index = 0;index < files.getLength();index++ ) {
				final Node node = files.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element file = (Element)node;

					final String filename = file.getElementsByTagName( "name" ).item( 0 ).getTextContent().trim();
					final File downloadfile = new File( filename );
					try {
						final String url = file.getElementsByTagName( "url" ).item( 0 ).getTextContent().trim();

						System.out.println( "Decode url pro with key: " + url );
					}
					catch( final NullPointerException npe ) {
						final URLConnection download = new URL( this.download_function_filemanager.toExternalForm() + "?product=" + purchase.getProduct().getCode() + "&downloadfile=" + filename ).openConnection();
						download.setRequestProperty( "Authorization","Basic " + user.getAuthToken() );

						Files.copy( download.getInputStream(),downloadfile.toPath(),StandardCopyOption.REPLACE_EXISTING );
						return downloadfile.getAbsoluteFile();
					}
				}
			}
		}
		catch( ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

		throw new IOException( "Cannot determine download link" );
	}

	public InputStream downloadPurchaseHistory( final User user ) throws IOException {
		final URLConnection connection = new URL( this.download_function_pdforderhistory.toExternalForm() ).openConnection();
		connection.setRequestProperty( "Authorization","Basic " + user.getAuthToken() );
		return connection.getInputStream();
	}

	public Optional<User> getUser() {
		return this.user;
	}

	public User login( final String name,final String password ) throws IOException {
		return login( Base64.getEncoder().encodeToString( ( name + ":" + password ).getBytes() ) );
	}

	private User login( final String authToken ) throws IOException {
		final Countries countries = getCountries();
		final Purchases myPurchases = getPurchases( getProducts(),authToken );

		final User.Builder builder = new User.Builder( this,authToken );
		builder.setCurrency( this.currency.orElse( getCurrencies().values().iterator().next() ) );

		URLConnection connection = this.download_function_user.openConnection();
		connection.setRequestProperty( "Authorization","Basic " + authToken );
		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			builder.setId( doc.getDocumentElement().getAttribute( "id" ) );
			builder.setBalance( doc.getElementsByTagName( "balance" ).item( 0 ).getTextContent().trim() );
			builder.setStatus( doc.getElementsByTagName( "status" ).item( 0 ).getTextContent().trim() );

			final NodeList purchases = doc.getElementsByTagName( "purchase" );
			for( int index = 0;index < purchases.getLength();index++ ) {
				final Node node = purchases.item( index );
				if( node.getNodeType() == Node.ELEMENT_NODE ) {
					final Element purchase = (Element)node;

					builder.addPurchase( myPurchases.get( Integer.parseInt( purchase.getTextContent().trim() ) ) );
				}
			}

			for( final Product wish : getWishlist( getProducts(),authToken ) )
				builder.addWish( wish );

			for( final CartEntry entry : getCart( getProducts(),authToken ) )
				builder.addCartEntry( entry );
		}
		catch( ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

		connection = this.download_function_profile.openConnection();
		connection.setRequestProperty( "Authorization","Basic " + authToken );
		try( InputStream response = new MalformedXMLInputStream( connection.getInputStream() ) ) {
			final DocumentBuilder db = this.dbf.newDocumentBuilder();

			final Document doc = db.parse( response );
			doc.getDocumentElement().normalize();

			builder.setNickname( doc.getElementsByTagName( "nickname" ).item( 0 ).getTextContent().trim() );
			builder.setFirstname( doc.getElementsByTagName( "firstname" ).item( 0 ).getTextContent().trim() );
			builder.setLastname( doc.getElementsByTagName( "lastname" ).item( 0 ).getTextContent().trim() );
			builder.setEMail( doc.getElementsByTagName( "email" ).item( 0 ).getTextContent().trim() );
			builder.setCountry( countries.values().stream().filter( country -> country.getName().equals( doc.getElementsByTagName( "country" ).item( 0 ).getTextContent().trim() ) ).findFirst().get() );
		}
		catch( ParserConfigurationException | SAXException ex ) {
			throw new RuntimeException( ex.getMessage(),ex );
		}

		this.user = Optional.of( builder.build() );

		fireUserUpdate( getUser() );

		return getUser().get();
	}

	public void logout() {
		this.user = Optional.empty();

		fireUserUpdate( getUser() );
	}

	public void addUserUpdateListener( final UserUpdateListener listener ) {
		this.listeners.add( UserUpdateListener.class,listener );
	}

	public void removeUserUpdateListener( final UserUpdateListener listener ) {
		this.listeners.remove( UserUpdateListener.class,listener );
	}

	protected void fireUserUpdate( final Optional<User> user ) {
		for( final UserUpdateListener listener : this.listeners.getListeners( UserUpdateListener.class ) )
			listener.userUpdate( this,user );
	}
}
