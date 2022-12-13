package com.amisphere;

import java.awt.Component;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Currency;

final class CurrencyCellRenderer extends JLabel implements ListCellRenderer<Currency> {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = 4805178046581683736L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	@Override
	public Component getListCellRendererComponent( final JList<? extends Currency> list,final Currency value,final int index,final boolean isSelected,final boolean cellHasFocus ) {
		if( value == null )
			setText( "?" );
		else
			setText( MessageFormat.format( "{0} ({1})",value.getName(),value.getSymbol() ) );

		return this;
	}
}
