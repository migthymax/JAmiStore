package com.amisphere;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Market;
import com.amisphere.market.Product;
import com.amisphere.market.User;
import com.amisphere.market.Market.UserUpdateListener;

public class WishesFrame extends JFrame implements UserUpdateListener {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = -1685483844442781409L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	transient final static private Logger logger = LogManager.getLogger();

	final private CategoriesPanel<Product> whishesPanel;

	WishesFrame( final Market market ) throws IOException {
		market.addUserUpdateListener( this );

		this.whishesPanel = new CategoriesPanel<>( market,new ProductCellRenderer() );

		final Container content = getContentPane();
		content.setLayout( new BorderLayout() );
		content.setMinimumSize( new Dimension( 900,800 ) );
		content.add( BorderLayout.CENTER,this.whishesPanel );

		final JButton removeFromWishList = new JButton( "-" );
		removeFromWishList.setToolTipText( "Remove wish from list" );
		removeFromWishList.setHorizontalAlignment( SwingConstants.CENTER );
		removeFromWishList.setEnabled( false );

		final JButton addToCart = new JButton( "+" );
		addToCart.setToolTipText( "Move wish from list to cart" );
		addToCart.setHorizontalAlignment( SwingConstants.CENTER );
		addToCart.setEnabled( false );

		final JButton addAllToCart = new JButton( "++" );
		addAllToCart.setToolTipText( "Move all wishes from list to cart" );
		addAllToCart.setHorizontalAlignment( SwingConstants.CENTER );

		final JPanel actions = new JPanel( new FlowLayout( SwingConstants.CENTER,5,0 ) );
		actions.setOpaque( false );
		actions.add( removeFromWishList );
		actions.add( addToCart );
		actions.add( addAllToCart );
		add( BorderLayout.SOUTH,actions );

		this.whishesPanel.addListSelectionListener( event -> {
			removeFromWishList.setEnabled( this.whishesPanel.getSelectedValue() != null );
			addToCart.setEnabled( this.whishesPanel.getSelectedValue() != null );
		} );

		removeFromWishList.addActionListener( event -> {
			try {
				market.removeFromWhishList( this.whishesPanel.getSelectedValue() );
			}
			catch( final IllegalStateException e ) {
				// TODO Auto-generated catch block
				WishesFrame.logger.fatal( e.getMessage(),e );

			}
			catch( final IOException e ) {
				// TODO Auto-generated catch block
				WishesFrame.logger.fatal( e.getMessage(),e );

			}
		} );

		addToCart.addActionListener( event -> {
			try {
				final Product product = this.whishesPanel.getSelectedValue();
				market.addToCart( product );
				market.removeFromWhishList( product );
			}
			catch( final IllegalStateException e ) {
				// TODO Auto-generated catch block
				WishesFrame.logger.fatal( e.getMessage(),e );

			}
			catch( final IOException e ) {
				// TODO Auto-generated catch block
				WishesFrame.logger.fatal( e.getMessage(),e );

			}
		} );

		addAllToCart.addActionListener( event -> {
			try {
				for( final Product product : market.getUser().get().getWishes() )
					try {
						market.addToCart( product );
						market.removeFromWhishList( product );
					}
					catch( final IllegalStateException e ) {
						// TODO Auto-generated catch block
						WishesFrame.logger.fatal( e.getMessage(),e );

					}
					catch( final IOException e ) {
						// TODO Auto-generated catch block
						WishesFrame.logger.fatal( e.getMessage(),e );

					}
			}
			catch( final NoSuchElementException e ) {
				// TODO Auto-generated catch block
				WishesFrame.logger.fatal( e.getMessage(),e );
			}
		} );

		pack();

		userUpdate( market,market.getUser() );
	}

	@Override
	public void userUpdate( final Market market,final Optional<User> user ) {
		user.ifPresent( u -> {
			setTitle( MessageFormat.format( "{0}''n wish list",u.getNickname() ) );

			this.whishesPanel.updateProducts( u.getWishes() );
		} );
	}
}
