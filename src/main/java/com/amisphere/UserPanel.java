package com.amisphere;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Currency;
import com.amisphere.market.Market;
import com.amisphere.market.Market.UserUpdateListener;
import com.amisphere.market.User;

class UserPanel extends JPanel implements UserUpdateListener {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = -8955530128024897665L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	transient final static private Logger logger = LogManager.getLogger();

	final private PurchasesFrame	purchaseFrame;
	final private WishesFrame		whishesFrame;
	final private CartFrame			cartFrame;

	final JLabel	profile;
	final JLabel	purchases;
	final JLabel	whishes;
	final JLabel	cart;
	final JLabel	credit;

	UserPanel( final Market market ) throws IOException {
		market.addUserUpdateListener( this );

		this.purchaseFrame = new PurchasesFrame( market );
		this.whishesFrame = new WishesFrame( market );
		this.cartFrame = new CartFrame( market );

		setOpaque( false );
		setLayout( new GridBagLayout() );

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = new Insets( 5,5,5,5 );

		this.profile = new JLabel( new ImageIcon( market.getUser().isPresent() ? market.getUser().get().getAvatar() : market.getUnknownAvatar() ) );
		this.profile.setText( market.getUser().isPresent() ? market.getUser().get().getNickname() : "?" );
		this.profile.setToolTipText( market.getUser().isPresent() ? "Log out" : "Log in" );
		this.profile.setVerticalTextPosition( SwingConstants.BOTTOM );
		this.profile.setHorizontalTextPosition( SwingConstants.CENTER );
		this.profile.setForeground( Color.WHITE );
		this.profile.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked( final MouseEvent event ) {
				try {
					if( market.getUser().isPresent() ) {
						UserPanel.this.purchaseFrame.setVisible( false );
						UserPanel.this.whishesFrame.setVisible( false );
						UserPanel.this.cartFrame.setVisible( false );
						market.logout();
					}
					else {
						final JPanel panel = new JPanel( new GridLayout( 0,1,2,2 ) );
						panel.add( new JLabel( "Username (as..):",SwingConstants.LEFT ) );
						final JTextField username = new JTextField();
						panel.add( username );
						panel.add( new JLabel( "Password:",SwingConstants.LEFT ) );
						final JPasswordField password = new JPasswordField();
						panel.add( password );

						if( JOptionPane.showConfirmDialog( UserPanel.this,panel,"Login",JOptionPane.OK_CANCEL_OPTION ) == 0 )
							market.login( username.getText().trim(),new String( password.getPassword() ) );
					}
				}
				catch( final IOException e ) {
					logger.debug( "Unable to perfom login: {}",e.getMessage(),e );

					JOptionPane.showMessageDialog( UserPanel.this,new JLabel( "Unable to perfom login!" ),"Error",JOptionPane.OK_OPTION );
				}
			}
		} );

		add( this.profile,constraints );

		constraints.gridx++;
		constraints.weightx = 1;
		constraints.anchor = GridBagConstraints.LAST_LINE_END;
		constraints.fill = GridBagConstraints.NONE;

		final Map<TextAttribute,Integer> fontAttributes = new HashMap<>();
		fontAttributes.put( TextAttribute.UNDERLINE,TextAttribute.UNDERLINE_ON );
		final Font underline = getFont().deriveFont( fontAttributes );

		final JPanel status = new JPanel( new FlowLayout( SwingConstants.CENTER,5,0 ) );
		status.setOpaque( false );
		{
			this.purchases = new JLabel();
			this.purchases.setHorizontalAlignment( SwingConstants.RIGHT );
			this.purchases.setForeground( Color.WHITE );
			this.purchases.setVisible( false );
			market.getUser().ifPresent( user -> {
				this.purchases.setVisible( true );
				this.purchases.setText( "Purchases: " + user.getPurchases().size() );
			} );
			this.purchases.addMouseListener( new MouseAdapter() {
				@Override
				public void mouseEntered( final MouseEvent event ) {
					UserPanel.this.purchases.setFont( underline );
				}

				@Override
				public void mouseExited( final MouseEvent event ) {
					UserPanel.this.purchases.setFont( UserPanel.this.getFont() );
				}

				@Override
				public void mouseClicked( final MouseEvent event ) {
					UserPanel.this.purchaseFrame.setLocation( event.getXOnScreen(),event.getYOnScreen() );
					UserPanel.this.purchaseFrame.setVisible( !UserPanel.this.purchaseFrame.isVisible() );
				}
			} );
			status.add( this.purchases );

			this.whishes = new JLabel();
			this.whishes.setHorizontalAlignment( SwingConstants.RIGHT );
			this.whishes.setForeground( Color.WHITE );
			this.whishes.setVisible( false );
			market.getUser().ifPresent( user -> {
				this.whishes.setVisible( true );
				this.whishes.setText( "Whishes: " + user.getWishes().size() );
			} );
			this.whishes.addMouseListener( new MouseAdapter() {
				@Override
				public void mouseEntered( final MouseEvent event ) {
					UserPanel.this.whishes.setFont( underline );
				}

				@Override
				public void mouseExited( final MouseEvent event ) {
					UserPanel.this.whishes.setFont( UserPanel.this.getFont() );
				}

				@Override
				public void mouseClicked( final MouseEvent event ) {
					UserPanel.this.whishesFrame.setLocation( event.getXOnScreen(),event.getYOnScreen() );
					UserPanel.this.whishesFrame.setVisible( !UserPanel.this.whishesFrame.isVisible() );
				}
			} );
			status.add( this.whishes );

			this.cart = new JLabel();
			this.cart.setHorizontalAlignment( SwingConstants.RIGHT );
			this.cart.setForeground( Color.WHITE );
			this.cart.setVisible( false );
			market.getUser().ifPresent( user -> {
				this.cart.setVisible( true );
				this.cart.setText( "Cart: " + user.getCart().size() );
			} );
			this.cart.addMouseListener( new MouseAdapter() {
				@Override
				public void mouseEntered( final MouseEvent event ) {
					UserPanel.this.cart.setFont( underline );
				}

				@Override
				public void mouseExited( final MouseEvent event ) {
					UserPanel.this.cart.setFont( UserPanel.this.getFont() );
				}

				@Override
				public void mouseClicked( final MouseEvent event ) {
					UserPanel.this.cartFrame.setLocation( event.getXOnScreen(),event.getYOnScreen() );
					UserPanel.this.cartFrame.setVisible( !UserPanel.this.cartFrame.isVisible() );
				}
			} );
			status.add( this.cart );

			this.credit = new JLabel();
			this.credit.setHorizontalAlignment( SwingConstants.RIGHT );
			this.credit.setForeground( Color.WHITE );
			this.credit.setVisible( false );
			market.getUser().ifPresent( user -> {
				this.credit.setVisible( true );
				this.credit.setText( "Credit: " + user.getCurrency().getSymbol() + " " + NumberFormat.getNumberInstance().format( user.calcCurrency( user.getCredits() ) ) );
			} );
			this.credit.addMouseListener( new MouseAdapter() {
				@Override
				public void mouseEntered( final MouseEvent event ) {
					UserPanel.this.credit.setFont( underline );
				}

				@Override
				public void mouseExited( final MouseEvent event ) {
					UserPanel.this.credit.setFont( UserPanel.this.getFont() );
				}

				@Override
				public void mouseClicked( final MouseEvent event ) {
					try {
						final JComboBox<Currency> currencies = new JComboBox<>( market.getCurrencies().values().toArray( new Currency[ market.getCurrencies().values().size() ] ) );
						currencies.setRenderer( new CurrencyCellRenderer() );
						market.getUser().ifPresent( user -> currencies.setSelectedItem( user.getCurrency() ) );

						final String[] options = { "OK" };
						final int selection = JOptionPane.showOptionDialog( UserPanel.this,currencies,"Select desired curreny",JOptionPane.OK_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,options[ 0 ] );

						if( selection > 0 )
							System.out.println( "selection is: " + options[ selection ] );

						final Currency currency = (Currency)currencies.getSelectedItem();
						if( currency != null )
							market.updateCurrency( currency );
					}
					catch( final IOException e ) {
						// TODO Auto-generated catch block
						logger.fatal( e.getMessage(),e );
					}
				}
			} );
			status.add( this.credit );
		}

		add( status,constraints );
	}

	@Override
	public void userUpdate( final Market market,final Optional<User> user ) {
		try {
			this.profile.setIcon( new ImageIcon( user.isPresent() ? user.get().getAvatar() : market.getUnknownAvatar() ) );
		}
		catch( final IOException ioe ) {
			logger.warn( ioe );
		}
		this.profile.setText( user.isPresent() ? user.get().getNickname() : "?" );
		this.profile.setToolTipText( user.isPresent() ? "Log out" : "Log in" );

		this.purchases.setVisible( user.isPresent() );
		user.ifPresent( u -> this.purchases.setText( "Purchases: " + u.getPurchases().size() ) );

		this.whishes.setVisible( user.isPresent() );
		user.ifPresent( u -> this.whishes.setText( "Whishes: " + u.getWishes().size() ) );

		this.cart.setVisible( user.isPresent() );
		user.ifPresent( u -> this.cart.setText( "Cart: " + u.getCart().size() ) );

		this.credit.setVisible( user.isPresent() );
		user.ifPresent( u -> this.credit.setText( "Credit: " + u.getCurrency().getSymbol() + " " + NumberFormat.getNumberInstance().format( u.calcCurrency( u.getCredits() ) ) ) );
	}
}
