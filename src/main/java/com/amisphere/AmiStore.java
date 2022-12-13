package com.amisphere;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Market;
import com.amisphere.market.Product;

public class AmiStore extends JFrame {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = 5616917779342111130L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	final private Market market = new Market();

	final ProductPanel details;

	AmiStore() throws IOException {
		setTitle( MessageFormat.format( "J''AmiStore (server v{0})",this.market.getStoreVersion() ) );
		setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		setPreferredSize( new Dimension( 1000,700 ) );

		final Container content = getContentPane();
		content.setBackground( new Color( 32,32,32 ) );
		content.setLayout( new GridBagLayout() );

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets( 5,5,5,5 );

		final UserPanel user = new UserPanel( this.market );
		content.add( user,constraints );

		final MenuPanel menu = new MenuPanel( this.market );

		this.details = new ProductPanel( this.market );
		final JScrollPane detailsScrollPane = new JScrollPane( this.details );
		detailsScrollPane.setAlignmentX( Component.LEFT_ALIGNMENT );
		detailsScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		detailsScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );

		final JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,menu,detailsScrollPane );
		splitPane.setOneTouchExpandable( true );
		splitPane.setDividerLocation( 250 );

		menu.setMinimumSize( new Dimension( 300,800 ) );
		detailsScrollPane.setMinimumSize( new Dimension( 800,800 ) );

		constraints.gridy++;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.fill = GridBagConstraints.BOTH;

		content.add( splitPane,constraints );

		menu.addListSelectionListener( event -> {
			showProduct( menu.getSelectedValue() );
		} );
	}

	public void showProduct( final Product product ) {
		this.details.setProduct( product );
	}

	public static void main( final String[] args ) throws Throwable {
		SwingUtilities.invokeLater( () -> {
			try {
				final AmiStore amiStore = new AmiStore();
				amiStore.pack();
				amiStore.setLocation( MouseInfo.getPointerInfo().getLocation() );
				amiStore.setVisible( true );
			}
			catch( final IOException ioe ) {
				throw new RuntimeException( ioe.getMessage(),ioe );
			}
		} );
	}
}
