package com.amisphere;

import java.awt.Component;
import java.text.DateFormat;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Purchase;

final class PurchaseCellRenderer extends JLabel implements ListCellRenderer<Purchase> {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = 774849764227637644L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	private final DateFormat dateFormat = DateFormat.getDateInstance( DateFormat.SHORT );

	@Override
	public Component getListCellRendererComponent( final JList<? extends Purchase> list,final Purchase value,final int index,final boolean isSelected,final boolean cellHasFocus ) {
		setOpaque( true );

		final String version = value.getVersion().isPresent() ? "v" + value.getVersion().get() : "";
		setText( MessageFormat.format( "{2}: {0} {1} ",value.getName(),version,this.dateFormat.format( value.getPurchased() ) ) );
		setToolTipText( value.getProduct().getCode() );

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
