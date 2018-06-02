package ventanas;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import utils.BuildXML;
import utils.ExtractXML;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Ejercicio1 extends JFrame {

	static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    static final String URI = "xmldb:exist://localhost:8081/exist/xmlrpc/db/empresa";
    static final String USER = "admin";
    static final String PASS = "admin";
    private Collection col;
    private XPathQueryService servicio;
	private JPanel contentPane;
	private JSpinner spinnerNumero;
	private JTextField textNombre;
	private JTextField textLocalidad;
	private JButton btnConsultar;
	private JButton btnAlta;
	private JButton btnBaja;
	private JButton btnModificacin;
	private JButton btnLimpiar;
	private JLabel labelMensajes;
	private JButton btnPrev;
	private JButton btnNext;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Ejercicio1 frame = new Ejercicio1();
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
	public Ejercicio1() {
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
		setBounds(100, 100, 521, 221);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblGestinDeDepartamentos = new JLabel("GESTI\u00D3N DE DEPARTAMENTOS");
		lblGestinDeDepartamentos.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblGestinDeDepartamentos.setBounds(59, 11, 239, 14);
		contentPane.add(lblGestinDeDepartamentos);
		
		JLabel lblNDeDepartamento = new JLabel("N\u00BA de departamento:");
		lblNDeDepartamento.setBounds(69, 39, 121, 14);
		contentPane.add(lblNDeDepartamento);
		
		btnConsultar = new JButton("Consultar");
		btnConsultar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				consutarDepartamento((Integer)spinnerNumero.getValue());
			}
		});
		btnConsultar.setBounds(351, 35, 89, 23);
		contentPane.add(btnConsultar);
		
		JLabel lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(69, 64, 69, 14);
		contentPane.add(lblNombre);
		
		textNombre = new JTextField();
		textNombre.setBounds(148, 61, 292, 20);
		contentPane.add(textNombre);
		textNombre.setColumns(10);
		
		JLabel lblLocalidad = new JLabel("Localidad:");
		lblLocalidad.setBounds(69, 90, 69, 14);
		contentPane.add(lblLocalidad);
		
		textLocalidad = new JTextField();
		textLocalidad.setColumns(10);
		textLocalidad.setBounds(148, 87, 292, 20);
		contentPane.add(textLocalidad);
		
		btnAlta = new JButton("Alta");
		btnAlta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				crearDepartamento();
			}
		});
		btnAlta.setBounds(69, 148, 80, 23);
		contentPane.add(btnAlta);
		
		btnBaja = new JButton("Baja");
		btnBaja.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				borrarDepartamento((Integer)spinnerNumero.getValue());
			}
		});
		btnBaja.setBounds(148, 148, 80, 23);
		contentPane.add(btnBaja);
		
		JButton btnModificacin = new JButton("Modificaci\u00F3n");
		btnModificacin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modificarDepartamento();
			}
		});
		btnModificacin.setBounds(228, 148, 126, 23);
		contentPane.add(btnModificacin);
		
		JButton btnLimpiar = new JButton("Limpiar");
		btnLimpiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				limpiarCampos();
			}
		});
		btnLimpiar.setBounds(354, 148, 86, 23);
		contentPane.add(btnLimpiar);
		
		labelMensajes = new JLabel("");
		labelMensajes.setHorizontalAlignment(SwingConstants.CENTER);
		labelMensajes.setBounds(67, 123, 373, 14);
		contentPane.add(labelMensajes);
		
		spinnerNumero = new JSpinner();
		spinnerNumero.setModel(new SpinnerNumberModel(new Integer(0), null, null, new Integer(1)));
		spinnerNumero.setBounds(219, 36, 89, 20);
		contentPane.add(spinnerNumero);
		
		btnPrev = new JButton("<<");
		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prevDepartamento();
			}
		});
		btnPrev.setBounds(10, 60, 49, 23);
		contentPane.add(btnPrev);
		
		btnNext = new JButton(">>");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextDepartamento();
			}
		});
		btnNext.setBounds(450, 60, 49, 23);
		contentPane.add(btnNext);
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
	
	private void consutarDepartamento(int id){
		try {
			
			textNombre.setText("");
			textLocalidad.setText("");
			labelMensajes.setText("");
			
            //XPathQueryService servicio;
            //servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            ResourceSet result = servicio.query("for $dep in /departamentos/DEP_ROW[DEPT_NO = "+id+"] return $dep");

            
            ResourceIterator i;
            i = result.getIterator();
            if (!i.hasMoreResources()) {
            	labelMensajes.setText("El departamento con id " + id + " no existe.");
            }else{
                Resource r = i.nextResource();
                mostrarDepartamento((String) r.getContent());
            }
           
            //col.close();
        } catch (XMLDBException e) {
            System.out.println(" ERROR AL CONSULTAR DOCUMENTO.");
            e.printStackTrace();
        }
	}
	
	private void mostrarDepartamento(String DEP_ROWxml){
		ExtractXML xml = new ExtractXML(DEP_ROWxml);
		
		spinnerNumero.setValue(Integer.parseInt(xml.getField("DEPT_NO")));
        textNombre.setText(xml.getField("DNOMBRE"));
        textLocalidad.setText(xml.getField("LOC"));
        
        checkFirstLastDepartamento();
        
        labelMensajes.setText("");
	}
	
	private boolean existsDepartamento(int id){
		try {
            //XPathQueryService servicio;
            //servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            ResourceSet result = servicio.query("for $dep in /departamentos/DEP_ROW[DEPT_NO = "+id+"] return $dep");
            ResourceIterator i = result.getIterator();
            if (!i.hasMoreResources()) {
            	return false;
            }else{
            	return true;
            }
        } catch (XMLDBException e) {
            System.out.println(" ERROR AL CONSULTAR DOCUMENTO.");
            e.printStackTrace();
        }
		return false;
	}
	
	private void borrarDepartamento(int id){
        try {
        	
        	if(existsDepartamento(id)){
	        	//XPathQueryService servicio;
				//servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
				servicio.query("update delete /departamentos/DEP_ROW[DEPT_NO = "+id+"]");
				
				labelMensajes.setText("Departamento " + id + " eliminado.");
				
				limpiarCampos();
        	}else{
        		labelMensajes.setText("El departamento " + id + " no se puede borrar, no existe.");
        	}
        } catch (XMLDBException e) {
			e.printStackTrace();
		}
	}
	
	private void crearDepartamento(){
		try {
			int id = (Integer)spinnerNumero.getValue();
        	if(!existsDepartamento(id)){
	        	
        		String depart = BuildXML.createDepartamento(id, textNombre.getText(), textLocalidad.getText());
				servicio.query("update insert "+depart+" into /departamentos");
				
				labelMensajes.setText("Departamento " + id + " guardado.");
        	}else{
        		labelMensajes.setText("El departamento " + id + " ya existe. No se puede volver a insertar");
        	}
        } catch (XMLDBException e) {
			e.printStackTrace();
		}
	}
	
	private void modificarDepartamento(){
		try {
			int id = (Integer)spinnerNumero.getValue();
        	if(existsDepartamento(id)){
	        	
        		String replacement = BuildXML.createDepartamento(id, textNombre.getText(), textLocalidad.getText());
				servicio.query("update replace /departamentos/DEP_ROW[DEPT_NO = "+id+"] with " + replacement);
				
				labelMensajes.setText("Departamento " + id + " modificado.");
        	}else{
        		labelMensajes.setText("El departamento " + id + " no existe. No se puede modificar");
        	}
        } catch (XMLDBException e) {
			e.printStackTrace();
		}
	}
	
	private void nextDepartamento(){
		try {
			ResourceSet result = servicio.query("(for $dep in /departamentos/DEP_ROW"
					+" where number($dep/DEPT_NO) > " + (Integer)spinnerNumero.getValue()
					+" order by number($dep/DEPT_NO) "
					+" return $dep)[1]");
	        ResourceIterator i;
	        i = result.getIterator();
	        if(i.hasMoreResources()){
		        Resource r = i.nextResource();
		        mostrarDepartamento((String) r.getContent());
	        }
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
	}
	
	private void prevDepartamento(){
		try {
			ResourceSet result = servicio.query("(for $dep in /departamentos/DEP_ROW"
					+" where number($dep/DEPT_NO) < " + (Integer)spinnerNumero.getValue()
					+" order by number($dep/DEPT_NO) descending"
					+" return $dep)[1]");
	        ResourceIterator i;
	        i = result.getIterator();
	        if(i.hasMoreResources()){
		        Resource r = i.nextResource();
		        mostrarDepartamento((String) r.getContent());
	        }
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
	}
	
	private void checkFirstLastDepartamento(){
		
		btnPrev.setEnabled(true);
		btnNext.setEnabled(true);
		try {
			//PREV
			ResourceSet prev = servicio.query("/departamentos/DEP_ROW[number(DEPT_NO) < "+(Integer)spinnerNumero.getValue()+"]");
			if(prev.getSize() == 0){
				btnPrev.setEnabled(false);
			}
			
			//NEXT
			ResourceSet next = servicio.query("/departamentos/DEP_ROW[number(DEPT_NO) > "+(Integer)spinnerNumero.getValue()+"]");
			if(next.getSize() == 0){
				btnNext.setEnabled(false);
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
	}
	
	private void limpiarCampos(){
		spinnerNumero.setValue(new Integer(0));
		textNombre.setText("");
		textLocalidad.setText("");
		labelMensajes.setText("");
	}
}
