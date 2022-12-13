package com.amisphere;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Market;
import com.amisphere.market.Product;
import com.amisphere.market.User;
import com.amisphere.market.Market.UserUpdateListener;

class ProductPanel extends JPanel implements UserUpdateListener {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = -6459770403090185467L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	final private Market market;

	private Product product = null;

	ProductPanel( final Market market ) {
		this.market = market;
		this.market.addUserUpdateListener( this );

		setOpaque( false );
		setLayout( new GridBagLayout() );

		setMaximumSize( new Dimension( 300,-1 ) );
	}

	public void setProduct( final Product product ) {
		removeAll();

		if( product != null ) {
			this.product = product;
			setBorder( BorderFactory.createTitledBorder( MessageFormat.format( "{0} v{1} ({2} bucks)",product.getName(),product.getVersion(),product.getPrice() ) ) );

			final String price = this.market.getUser().isPresent() ? "(" + this.market.getUser().get().getCurrency().getSymbol() + " " + NumberFormat.getNumberInstance().format( this.market.getUser().get().calcCurrency( product.getPrice() ) ) + ")" : "";
			product.getPublisher().ifPresent( publisher -> setBorder( BorderFactory.createTitledBorder( MessageFormat.format( "{0} v{1} by {2} {3}",product.getName(),product.getVersion(),publisher.getName(),price ) ) ) );

			final GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.anchor = GridBagConstraints.LINE_START;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.insets = new Insets( 5,5,5,5 );

			final JTextArea overview = new JTextArea( product.getOverview() );
			overview.setBorder( BorderFactory.createTitledBorder( "Overview" ) );
			add( overview,constraints );

			constraints.gridy++;

			final JTextArea features = new JTextArea( product.getFeatures() );
			features.setBorder( BorderFactory.createTitledBorder( "Features" ) );
			add( features,constraints );

			constraints.gridy++;

			final JTextArea requirements = new JTextArea( product.getRequirements() );
			requirements.setBorder( BorderFactory.createTitledBorder( "Requirements" ) );
			add( requirements,constraints );
		}

		revalidate();
		repaint();
	}

	@Override
	public void userUpdate( final Market market,final Optional<User> user ) {
		if( this.product != null && user.isPresent() )
			setProduct( this.product );
	}
}
