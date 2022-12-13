package com.amisphere;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Product;

final class ProductCellRenderer extends JLabel implements ListCellRenderer<Product> {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = 8472188653420909831L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	@Override
	public Component getListCellRendererComponent( final JList<? extends Product> list,final Product value,final int index,final boolean isSelected,final boolean cellHasFocus ) {
		setOpaque( true );

		setText( value.getName() );
		setToolTipText( value.getCode() );

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
