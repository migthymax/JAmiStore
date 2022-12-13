package com.amisphere;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Market;
import com.amisphere.market.Product;

public class MenuPanel extends JPanel {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = -7411643295748249058L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	transient final static private Logger logger = LogManager.getLogger();

	final CategoriesPanel<Product> products;

	MenuPanel( final Market market ) throws IOException {

		setLayout( new BorderLayout() );
		setMinimumSize( new Dimension( 900,800 ) );

		this.products = new CategoriesPanel<>( market,market.getProducts().values(),new ProductCellRenderer() );
		add( BorderLayout.CENTER,this.products );

		final JButton addToWishes = new JButton( "?" );
		addToWishes.setToolTipText( "Add to wish list" );
		addToWishes.setHorizontalAlignment( SwingConstants.CENTER );
		addToWishes.setEnabled( false );

		final JButton addToCart = new JButton( "+" );
		addToCart.setToolTipText( "Add to cart" );
		addToCart.setHorizontalAlignment( SwingConstants.CENTER );
		addToCart.setEnabled( false );

		final JPanel actions = new JPanel( new FlowLayout( SwingConstants.CENTER,5,0 ) );
		actions.setOpaque( false );
		actions.add( addToWishes );
		actions.add( addToCart );
		add( BorderLayout.SOUTH,actions );

		this.products.addListSelectionListener( event -> {
			addToWishes.setEnabled( this.products.getSelectedValue() != null );
			addToCart.setEnabled( this.products.getSelectedValue() != null );
		} );

		addToWishes.addActionListener( event -> {
			try {
				market.addToWhishList( getSelectedValue() );
			}
			catch( final IllegalStateException e ) {
				// TODO Auto-generated catch block
				MenuPanel.logger.fatal( e.getMessage(),e );
			}
			catch( final IOException e ) {
				// TODO Auto-generated catch block
				MenuPanel.logger.fatal( e.getMessage(),e );
			}
		} );

		addToCart.addActionListener( event -> {
			try {
				market.addToCart( getSelectedValue() );
			}
			catch( final IllegalStateException e ) {
				// TODO Auto-generated catch block
				MenuPanel.logger.fatal( e.getMessage(),e );
			}
			catch( final IOException e ) {
				// TODO Auto-generated catch block
				MenuPanel.logger.fatal( e.getMessage(),e );
			}
		} );
	}

	public void addListSelectionListener( final ListSelectionListener listener ) {
		this.products.addListSelectionListener( listener );
	}

	public Product getSelectedValue() {
		return this.products.getSelectedValue();
	}
}
