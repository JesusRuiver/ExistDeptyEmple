package ventanas;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import utils.ExtractXML;

import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Empleados extends JFrame {

	static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    static final String URI = "xmldb:exist://localhost:8081/exist/xmlrpc/db/empresa";
    static final String USER = "admin";
    static final String PASS = "admin";
    private Collection col;
    private XPathQueryService servicio;
	private JPanel contentPane;
	
	private JComboBox<String> depComboBox;
	private JTable table;
	private DefaultTableModel model;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Empleados frame = new Empleados();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Empleados() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				try {
					col.close();
				} catch (XMLDBException e) {
					e.printStackTrace();
				}
			}
		});
		
		col = conectar();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		iniciarTabla();
	}
	
	public Collection conectar() {

        try {
            Class cl = Class.forName(DRIVER);
            Database database = (Database) cl.newInstance();
            DatabaseManager.registerDatabase(database);
            col = DatabaseManager.getCollection(URI, USER, PASS);
            
            servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            return col;
        } catch (XMLDBException e) {
            System.out.println("Error al inicializar la BD eXist.");
        } catch (ClassNotFoundException e) {
            System.out.println("Error en el driver.");
        } catch (InstantiationException e) {
            System.out.println("Error al instanciar la BD.");
        } catch (IllegalAccessException e) {
            System.out.println("Error al instanciar la BD.");
        }
        return null;
    }
	
	private void iniciarTabla(){
		
		String[] columns = new String[]{"Número", "Apellido", "Oficio", "Director", "Fecha Alta", "Salario", "Departamento"};
        model = new DefaultTableModel(columns, 0);
        
        table.setModel(model);
		
		TableColumn columnaDepartamento = table.getColumnModel().getColumn(6);
		depComboBox = new JComboBox<String>();
		columnaDepartamento.setCellEditor(new DefaultCellEditor(depComboBox));;
		
		iniciarDatos();
	}
	
	private void iniciarDatos(){
		
		ResourceSet result;
		try {
			result = servicio.query("for $emp in /EMPLEADOS/EMP_ROW " +
					"let $dep := /departamentos/DEP_ROW[DEPT_NO = $emp/DEPT_NO] " +
					"return <res>{$emp, $dep}</res>");
		

        
	        ResourceIterator i;
	        i = result.getIterator();
	        if (!i.hasMoreResources()) {
	        	//labelMensajes.setText("El departamento con id " + id + " no existe.");
	        }
	        while (i.hasMoreResources()){
	            Resource r = i.nextResource();
	            ExtractXML xml = new ExtractXML((String)r.getContent());
	            Vector<String> rowData = new Vector<String>();
	            rowData.addElement(xml.getField("EMP_NO"));
	            rowData.addElement(xml.getField("APELLIDO"));
	            rowData.addElement(xml.getField("OFICIO"));
	            rowData.addElement(xml.getField("DIR"));
	            rowData.addElement(xml.getField("FECHA_ALT"));
	            rowData.addElement(xml.getField("SALARIO"));
	            rowData.addElement(xml.getField("DNOMBRE"));
	            model.addRow(rowData);
	        }
	        
	        result = servicio.query("for $dep in /departamentos/DEP_ROW return $dep");
	        i = result.getIterator();
	        while (i.hasMoreResources()){
	            Resource r = i.nextResource();
	            ExtractXML xml = new ExtractXML((String)r.getContent());
	            depComboBox.addItem(xml.getField("DNOMBRE"));
	        }
	        
	        
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
	}

}
