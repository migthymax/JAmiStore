package com.amisphere;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.CartEntry;
import com.amisphere.market.Market;
import com.amisphere.market.Market.UserUpdateListener;
import com.amisphere.market.User;

public class CartFrame extends JFrame implements UserUpdateListener {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = -1685483844442781409L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	transient final static private Logger logger = LogManager.getLogger();

	private final NumberFormat priceFormat = NumberFormat.getNumberInstance();

	private final CategoriesPanel<CartEntry> cartPanel;

	private final JButton checkoutCart;

	CartFrame( final Market market ) throws IOException {
		market.addUserUpdateListener( this );

		this.cartPanel = new CategoriesPanel<>( market,new CartEntryCellRenderer( market ) );

		final Container content = getContentPane();
		content.setLayout( new BorderLayout() );
		content.setMinimumSize( new Dimension( 1200,800 ) );
		content.add( BorderLayout.CENTER,this.cartPanel );

		this.checkoutCart = new JButton( "Checkout" );
		this.checkoutCart.setToolTipText( "Checkout the cart" );
		this.checkoutCart.setHorizontalAlignment( SwingConstants.CENTER );
		this.checkoutCart.setEnabled( false );

		final JButton removeFromCart = new JButton( "-" );
		removeFromCart.setToolTipText( "Remove wish from cart" );
		removeFromCart.setHorizontalAlignment( SwingConstants.CENTER );
		removeFromCart.setEnabled( false );

		final JButton removeAllfromCart = new JButton( "--" );
		removeAllfromCart.setToolTipText( "Clear cart" );
		removeAllfromCart.setHorizontalAlignment( SwingConstants.CENTER );

		final JPanel actions = new JPanel( new FlowLayout( SwingConstants.CENTER,5,0 ) );
		actions.setOpaque( false );
		actions.add( this.checkoutCart );
		actions.add( removeFromCart );
		actions.add( removeAllfromCart );
		add( BorderLayout.SOUTH,actions );

		this.cartPanel.addListSelectionListener( event -> {
			removeFromCart.setEnabled( this.cartPanel.getSelectedValue() != null );
			removeAllfromCart.setEnabled( this.cartPanel.getSelectedValue() != null );
		} );

		removeFromCart.addActionListener( event -> {
			try {
				market.removeFromCart( this.cartPanel.getSelectedValue() );
			}
			catch( final IllegalStateException e ) {
				// TODO Auto-generated catch block
				CartFrame.logger.fatal( e.getMessage(),e );

			}
			catch( final IOException e ) {
				// TODO Auto-generated catch block
				CartFrame.logger.fatal( e.getMessage(),e );

			}
		} );

		removeAllfromCart.addActionListener( event -> {
			try {
				for( final CartEntry entry : market.getUser().get().getCart() )
					try {
						market.removeFromCart( entry );
					}
					catch( final IllegalStateException e ) {
						// TODO Auto-generated catch block
						CartFrame.logger.fatal( e.getMessage(),e );

					}
					catch( final IOException e ) {
						// TODO Auto-generated catch block
						CartFrame.logger.fatal( e.getMessage(),e );

					}
			}
			catch( final NoSuchElementException e ) {
				// TODO Auto-generated catch block
				CartFrame.logger.fatal( e.getMessage(),e );
			}
		} );

		this.checkoutCart.addActionListener( event -> {
			try {
				boolean cancel = false;
				while( market.getUser().get().getCredits() < market.getUser().get().getCartCredits() && !cancel ) {
					final JPanel panel = new JPanel( new GridLayout( 0,1,2,2 ) );
					panel.add( new JLabel( "Your balance isn't sufficient for your cart",SwingConstants.LEFT ) );
					final JButton launch = new JButton( "Launch browser to continue with checkout..." );
					launch.addActionListener( e -> {
						try {
							market.checkoutCart( market.getUser().get() );
						}
						catch( IOException | URISyntaxException e1 ) {
							// TODO Auto-generated catch block
							CartFrame.logger.fatal( e1.getMessage(),e1 );

						}
					} );
					panel.add( launch );
					panel.add( new JLabel( "After finsihing the checkout in the browser, press OK to continue.",SwingConstants.LEFT ) );

					if( JOptionPane.showConfirmDialog( CartFrame.this,panel,"Checkout",JOptionPane.OK_CANCEL_OPTION ) == 0 )
						market.buyCart( market.getUser().get() );
					else
						cancel = true;
				}
				
				if( ! cancel )
					market.buyCart( market.getUser().get() );
			}
			catch( final IOException e ) {
				// TODO Auto-generated catch block
				CartFrame.logger.fatal( e.getMessage(),e );
			}
		} );

		pack();

		userUpdate( market,market.getUser() );
	}

	@Override
	public void userUpdate( final Market market,final Optional<User> user ) {
		user.ifPresent( u -> {
			setTitle( MessageFormat.format( "{0}''n cart",u.getNickname() ) );

			this.cartPanel.updateProducts( u.getCart() );

			this.checkoutCart.setEnabled( !u.getCart().isEmpty() );
			this.checkoutCart.setText( MessageFormat.format( "Checkout (Total: {0} {1})",u.getCurrency().getSymbol(),this.priceFormat.format( u.getCartSum() ) ) );
		} );
	}

}
