package com.amisphere;

import java.awt.Component;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Category;

final class CategoryCellRenderer extends JLabel implements ListCellRenderer<Category> {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = 1422485910155296992L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	final private boolean showProductCount;

	public CategoryCellRenderer() {
		this( true );
	}

	public CategoryCellRenderer( final boolean showProductCount ) {
		this.showProductCount = showProductCount;
	}

	@Override
	public Component getListCellRendererComponent( final JList<? extends Category> list,final Category value,final int index,final boolean isSelected,final boolean cellHasFocus ) {
		if( value == null )
			setText( "All" );
		else
			setText( this.showProductCount ? // CRLF
					MessageFormat.format( "{0} ({1})",value.getName(),value.getProductCount() ) :  // CRLF
					MessageFormat.format( "{0}",value.getName() ) );

		return this;
	}
}
