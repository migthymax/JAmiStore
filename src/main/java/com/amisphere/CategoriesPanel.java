package com.amisphere;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amisphere.market.Category;
import com.amisphere.market.Good;
import com.amisphere.market.Market;
import com.amisphere.market.Market.Categories;

class CategoriesPanel<G extends Good> extends JPanel implements ActionListener {

	/**
	 * Auto generated serialUID
	 */
	private static final long serialVersionUID = 7143115291183098638L;

	/**
	 * {@link Logger} for logging proposes.
	 */
	@SuppressWarnings( "unused" )
	transient final static private Logger logger = LogManager.getLogger();

	final private DefaultComboBoxModel<Category>	categories	= new DefaultComboBoxModel<>();
	final private JComboBox<Category>				category;
	final private Collection<G>						productsAll;
	final private DefaultListModel<G>				productsModel;
	final private JLabel							productsHeader;

	final private JList<? extends G> products;

	CategoriesPanel( final Market market,final ListCellRenderer<G> renderer ) throws IOException {
		this( market,new ArrayList<>(),false,renderer );
	}

	CategoriesPanel( final Market market,final Collection<G> products,final ListCellRenderer<G> renderer ) throws IOException {
		this( market,products,true,renderer );
	}

	CategoriesPanel( final Market market,final Collection<G> products,final boolean showProductCount,final ListCellRenderer<G> renderer ) throws IOException {
		this.productsAll = products;

		this.productsModel = new DefaultListModel<>();
		this.productsModel.addAll( this.productsAll );
		this.categories.addAll( Categories.fromGoods( this.productsModel ).values() );

		setLayout( new GridBagLayout() );

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets( 5,5,5,5 );

		add( new JLabel( "Category:" ),constraints );

		this.category = new JComboBox<>( this.categories );
		this.category.setRenderer( new CategoryCellRenderer( showProductCount ) );
		this.category.addActionListener( this );
		constraints.gridy++;
		add( this.category,constraints );

		constraints.gridy++;
		this.productsHeader = new JLabel( MessageFormat.format( "Products ({0}):",this.productsModel.size() ) );
		add( this.productsHeader,constraints );

		this.products = new JList<>( this.productsModel );
		this.products.setCellRenderer( renderer );

		final JScrollPane productsScrollPane = new JScrollPane( this.products );
		productsScrollPane.setAlignmentX( Component.LEFT_ALIGNMENT );
		productsScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		productsScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );

		constraints.gridy++;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weighty = 1;

		add( productsScrollPane,constraints );
	}

	@Override
	public void actionPerformed( final ActionEvent event ) {
		this.productsModel.clear();

		final Category selected = (Category)this.category.getSelectedItem();
		if( selected == null )
			this.productsModel.addAll( this.productsAll );
		else
			this.productsModel.addAll( this.productsAll.stream().filter( product -> product.getCategory().equals( selected ) ).collect( Collectors.toList() ) );

		this.productsHeader.setText( MessageFormat.format( "Products ({0}):",this.productsModel.size() ) );
	}

	public void updateProducts( final Collection<G> wishes ) {
		this.productsAll.clear();
		this.productsAll.addAll( wishes );

		updateCategories();
	}

	public void updateCategories() {
		this.categories.removeAllElements();
		this.categories.addAll( Categories.fromGoods( this.productsAll ).values() );
	}

	public void addListSelectionListener( final ListSelectionListener listener ) {
		this.products.addListSelectionListener( listener );
	}

	public G getSelectedValue() {
		return this.products.getSelectedValue();
	}
}
