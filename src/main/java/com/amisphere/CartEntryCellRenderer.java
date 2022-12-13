package com.amisphere;

import java.awt.Component;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.CartEntry;
import com.amisphere.market.Market;

final class CartEntryCellRenderer extends JLabel implements ListCellRenderer<CartEntry> {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = 5839641451389842314L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	private final NumberFormat priceFormat = NumberFormat.getNumberInstance();

	private final Market market;

	CartEntryCellRenderer( final Market market ) {
		this.market = market;
	}

	@Override
	public Component getListCellRendererComponent( final JList<? extends CartEntry> list,final CartEntry value,final int index,final boolean isSelected,final boolean cellHasFocus ) {
		setOpaque( true );

		final String price = this.market.getUser().get().getCurrency().getSymbol() + " " + ( value.getPrice().isEmpty() ? "N/A" : this.priceFormat.format( this.market.getUser().get().calcCurrency( value.getPrice().getAsDouble() ) ) );
		setText( MessageFormat.format( "{1}: {0} ",value.getName(),price ) );
		value.getProduct().ifPresent( product -> setToolTipText( product.getCode() ) );

		if( isSelected ) {
			setForeground( list.getBackground() );
			setBackground( list.getForeground() );
		}
		else {
			setForeground( list.getForeground() );
			setBackground( list.getBackground() );
		}

		return this;
	}
}
