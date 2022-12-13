package com.amisphere;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Market;
import com.amisphere.market.Purchase;
import com.amisphere.market.User;
import com.amisphere.market.Market.UserUpdateListener;

public class PurchasesFrame extends JFrame implements UserUpdateListener {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = -1685483844442781409L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	transient final static private Logger logger = LogManager.getLogger();

	final private CategoriesPanel<Purchase> purchasesPanel;

	PurchasesFrame( final Market market ) throws IOException {
		market.addUserUpdateListener( this );

		this.purchasesPanel = new CategoriesPanel<>( market,new PurchaseCellRenderer() );

		final Container content = getContentPane();
		content.setLayout( new BorderLayout() );
		content.setMinimumSize( new Dimension( 900,800 ) );
		content.add( BorderLayout.CENTER,this.purchasesPanel );

		final JButton download = new JButton( "v" );
		download.setToolTipText( "Download purchase" );
		download.setHorizontalAlignment( SwingConstants.CENTER );

		final JButton orderHistory = new JButton( "Order History" );
		orderHistory.setToolTipText( "Download order history as PDF" );
		orderHistory.setHorizontalAlignment( SwingConstants.CENTER );

		final JPanel actions = new JPanel( new FlowLayout( SwingConstants.CENTER,5,0 ) );
		actions.setOpaque( false );
		actions.add( download );
		actions.add( orderHistory );
		add( BorderLayout.SOUTH,actions );

		this.purchasesPanel.addListSelectionListener( event -> {
			download.setEnabled( this.purchasesPanel.getSelectedValue() != null );
		} );

		download.addActionListener( event -> {
			try {
				Desktop.getDesktop().browseFileDirectory( market.downloadPurchase( market.getUser().get(),this.purchasesPanel.getSelectedValue() ) );
			}
			catch( final IllegalStateException e ) {
				// TODO Auto-generated catch block
				PurchasesFrame.logger.fatal( e.getMessage(),e );
			}
			catch( final IOException e ) {
				// TODO Auto-generated catch block
				PurchasesFrame.logger.fatal( e.getMessage(),e );
			}
		} );
		orderHistory.addActionListener( event -> {
			try {
				final Path pdf = Files.createTempFile( "JAS_",".pdf" );
				Files.copy( market.downloadPurchaseHistory( market.getUser().get() ),pdf,StandardCopyOption.REPLACE_EXISTING );
				Desktop.getDesktop().open( pdf.toFile() );
			}
			catch( final IllegalStateException e ) {
				// TODO Auto-generated catch block
				PurchasesFrame.logger.fatal( e.getMessage(),e );
			}
			catch( final IOException e ) {
				// TODO Auto-generated catch block
				PurchasesFrame.logger.fatal( e.getMessage(),e );
			}
		} );

		pack();

		userUpdate( market,market.getUser() );
	}

	@Override
	public void userUpdate( final Market market,final Optional<User> user ) {
		user.ifPresent( u -> {
			setTitle( MessageFormat.format( "Purchased made by {0}",u.getNickname() ) );

			this.purchasesPanel.updateProducts( u.getPurchases() );
		} );
	}

}
