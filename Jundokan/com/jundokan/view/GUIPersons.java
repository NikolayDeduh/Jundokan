package jundokan.view;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import jundokan.connection.*;
import jundokan.model.*;

import com.itextpdf.text.log.SysoLogger;
import com.toedter.calendar.JDateChooser;

public class GUIPersons extends JDialog{

	private JPanel contentPane;
	private ResourceBundle bundle;
	private JTextField txtFirstName;
	private JTextField txtLastName;
	private JTextField txtMiddleName;
	private JTextField txtWeight;
	private JDateChooser dateChooser;
	private JButton btnAccept;
	private JComboBox<String> jcbDegree;
	private JComboBox<String> jcbOrganization;
	private JRadioButton rbtnFemale;
	private JRadioButton rbtnMale;
	private ButtonGroup btnGroup;
	private TextPrompt tpFirstName;
	private TextPrompt tpLastName;
	private TextPrompt tpWeight;
	private TextPrompt tpDateChooser;
	private TextPrompt tpDegree;
	private TextPrompt tpOrganization;
	private JButton btnMakeFoto;
	private JTextArea textAreaDocParents;
	private ArrayList<Integer> idDegrees;
	private ArrayList<Integer> idOrganization;
	private JButton btnSelectImage;
	private int PersID;
	private String photoURL = "";
	private File file;
	private JLabel lblFoto;
	ArrayList<ITransferData> listeners;
	//private ITransferData transferData;
	
	/**
	 * Create the frame.
	 */
	public GUIPersons() {
		Property prop = new Property(Paths.get(System.getProperty("user.dir"),"jundokan.ini").toString());
		Locale.setDefault(new Locale(prop.getKey("locale")));
		try {
			Path path = Paths.get(System.getProperty("user.dir"),"/lang/",Locale.getDefault().toString());
			bundle = ResourceBundle.getBundle("Persons", Locale.getDefault(), new URLClassLoader(new URL [] {path.toUri().toURL()}));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		setTitle(bundle.getString("titleAdd"));
		init(bundle.getString("bntAccept"));
		initDegrees();
		initOrganization();
		btnAccept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isEmpty()) JOptionPane.showMessageDialog(null, bundle.getString("notEmpty"));
				else {
					changePhoto(file);
					insertPerson();
					setVisible(false);
					dispose();
					};
			}
		});
		setTitle(bundle.getString("titleAdd"));		
		setAlwaysOnTop(true);
	}
	
	public GUIPersons(DBPersons person) {
		setAlwaysOnTop(true);
		Property prop = new Property(Paths.get(System.getProperty("user.dir"),"jundokan.ini").toString());
		Locale.setDefault(new Locale(prop.getKey("locale")));
		try {
			Path path = Paths.get(System.getProperty("user.dir"),"/lang/",Locale.getDefault().toString());
			bundle = ResourceBundle.getBundle("Persons", Locale.getDefault(), new URLClassLoader(new URL [] {path.toUri().toURL()}));
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		setTitle(bundle.getString("titleEdit"));		
		init(bundle.getString("btnEdit"));
        initDegrees();
        initOrganization();
        PersID = person.PersID;
        photoURL = person.PersPhoto;
        btnAccept.setBounds(310, 274, 102, 23);
        btnAccept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isEmpty()) JOptionPane.showMessageDialog(null, bundle.getString("notEmpty"));
				else {
					changePhoto(file);
					updatePerson();
					setVisible(false);
					dispose();
					};
			}
		});
        try {
        	if(!person.PersPhoto.equals(""))
			lblFoto.setIcon(ScalingImage.getScalingImage(ImageIO.read(new File(person.PersPhoto)), new Dimension(164, 165)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        txtFirstName.setText(person.PersFN);
		txtLastName.setText(person.PersLN);
		txtMiddleName.setText(person.PersMN);
		txtWeight.setText(String.valueOf(person.PersWeight));
		dateChooser.setDate(person.PersDate);
		btnGroup.setSelected(person.PersSex? rbtnMale.getModel(): rbtnFemale.getModel(), true);
		for (int i = 0; i < idDegrees.size(); i++) {
			if(idDegrees.get(i) == person.DegreeID) {
				jcbDegree.setSelectedIndex(i);
				break;
			}
		}
		for (int i = 0; i < idOrganization.size(); i++) {
			if(idOrganization.get(i) == person.OrgID) {
				jcbOrganization.setSelectedIndex(i);
				break;
			}
		}
		
	}
	private void init(String buttonName) {
		this.listeners = new ArrayList<ITransferData>();
		DimensionFrame dim = new DimensionFrame(getClass().getSimpleName(), 427, 345);
        addWindowListener(dim.getWindowAdapter(this));
        setBounds(dim.getBounds());
        this.setResizable(false);
        setModal(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lblFoto = new JLabel();
		lblFoto.setVerticalAlignment(SwingConstants.CENTER);
		lblFoto.setBorder(UIManager.getBorder("DesktopIcon.border"));
		lblFoto.setBounds(10, 10, 170, 171);
		contentPane.add(lblFoto);

		txtFirstName = new JTextField();
		txtFirstName.setBounds(190, 10, 221, 20);
	    tpFirstName = new TextPrompt(bundle.getString("txtFirstName"), txtFirstName);
	    tpFirstName.changeAlpha(0.5f);
		contentPane.add(txtFirstName);
		txtFirstName.setColumns(10);

		txtLastName = new JTextField();
		txtLastName.setBounds(190, 35, 221, 20);
		tpLastName = new TextPrompt(bundle.getString("txtLastName"), txtLastName);
		tpLastName.changeAlpha(0.5f);
		contentPane.add(txtLastName);
		txtLastName.setColumns(10);

		txtMiddleName = new JTextField();
		txtMiddleName.setBounds(190, 61, 221, 20);
		TextPrompt txt = new TextPrompt(bundle.getString("txtMiddleName"), txtMiddleName);
		txt.changeAlpha(0.5f);
		contentPane.add(txtMiddleName);
		txtMiddleName.setColumns(10);

		txtWeight = new JTextField();
		txtWeight.setBounds(190, 136, 221, 20);
		txtWeight.addKeyListener(new KeyListener() {			
			@Override
			public void keyPressed(KeyEvent e) {
				 if(txtWeight.getText().length()>=3)
				    {
					 txtWeight.setText(txtWeight.getText().substring(0, 2));
				    }
			}
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() < 48 || e.getKeyChar() > 57 && e.getKeyChar() != 8) e.setKeyChar('\0');
			}
			@Override
			public void keyReleased(KeyEvent e) {}
		});
		tpWeight = new TextPrompt(bundle.getString("txtWeight"), txtWeight);
		tpWeight.changeAlpha(0.5f);
		contentPane.add(txtWeight);
		txtWeight.setColumns(10);
		
		btnGroup = new ButtonGroup();
		rbtnMale = new JRadioButton(bundle.getString("rbtnMale"),true);
		rbtnMale.setBounds(240, 86, 88, 23);
		btnGroup.add(rbtnMale);
		contentPane.add(rbtnMale);
		
		rbtnFemale = new JRadioButton(bundle.getString("rbtnFemale"));
		rbtnFemale.setBounds(330, 86, 81, 23);
		btnGroup.add(rbtnFemale);
		contentPane.add(rbtnFemale);
		
		JLabel lblSex = new JLabel(bundle.getString("lblSex"));
		lblSex.setBounds(190, 90, 44, 14);
		contentPane.add(lblSex);

		jcbOrganization = new JComboBox<String>();
		jcbOrganization.setEditable(true);
		jcbOrganization.setBounds(190, 186, 221, 20);
		tpOrganization = new TextPrompt(bundle.getString("jcbOrganization"), (JTextField)jcbOrganization.getEditor().getEditorComponent());
		tpOrganization.changeAlpha(0.5f);
		contentPane.add(jcbOrganization);

		jcbDegree = new JComboBox<String>();
		jcbDegree.setEditable(true);
		jcbDegree.setBounds(190, 161, 221, 20);
		tpDegree = new TextPrompt(bundle.getString("jcbDegree"), (JTextField)jcbDegree.getEditor().getEditorComponent());
		tpDegree.changeAlpha(0.5f);
		contentPane.add(jcbDegree);

		textAreaDocParents = new JTextArea();
		txt = new TextPrompt(bundle.getString("textAreaDocParents"), textAreaDocParents);
		txt.changeAlpha(0.5f);
		textAreaDocParents.setBounds(10, 211, 401, 58);
		contentPane.add(textAreaDocParents);
		btnAccept = new JButton(buttonName);
		btnAccept.setBounds(310, 274, 102, 23);
		contentPane.add(btnAccept);

		JButton btnCancel = new JButton(bundle.getString("btnCancel"));
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();			
			}
		});
		btnCancel.setBounds(200, 274, 102, 23);
		contentPane.add(btnCancel);
		
		dateChooser = new JDateChooser();
		dateChooser.setBounds(190, 111, 221, 20);
		tpDateChooser = new TextPrompt(bundle.getString("dateChooser"), (JTextField)dateChooser.getDateEditor());
		tpDateChooser.changeAlpha(0.5f);
		contentPane.add(dateChooser);
		
		btnSelectImage = new JButton(bundle.getString("btnSelectImage"));
		btnSelectImage.setFocusable(false);
		btnSelectImage.setHorizontalAlignment(SwingConstants.LEFT);
		btnSelectImage.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
		btnSelectImage.setIconTextGap(3);
		btnSelectImage.setIcon(new ImageIcon(System.getProperty("user.dir") +"/resource/gui/folder.png"));
		btnSelectImage.setBounds(95, 181, 86, 26);
		btnSelectImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*try {
					UIManager.setLookAndFeel(UIManager 
							.getSystemLookAndFeelClassName()); // Set "System" - style for openDialog.
				} catch (Exception e1) {
					e1.printStackTrace();
				}*/
				JFileChooser fileOpen = new JFileChooser(); // Creation JFileChooser for button.
				fileOpen.setFileFilter(new FileNameExtensionFilter ("Image files", "png", "jpg", "gif")); // Set filter for button.
				fileOpen.setAcceptAllFileFilterUsed(false);
				int ret = fileOpen.showDialog(contentPane, bundle.getString("openFile"));
				if (ret == JFileChooser.APPROVE_OPTION) { // Check whether the selected file.
					file = fileOpen.getSelectedFile(); // Get selected file in variable "file".
					try {
						lblFoto.setIcon(ScalingImage.getScalingImage(ImageIO.read(file), new Dimension(164, 165)));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		contentPane.add(btnSelectImage);
		
		btnMakeFoto = new JButton(bundle.getString("btnMakeFoto"));
		btnMakeFoto.setHorizontalAlignment(SwingConstants.LEFT);
		btnMakeFoto.setFocusable(false);
		btnMakeFoto.setBorder(null);
		btnMakeFoto.setIconTextGap(1);
		btnMakeFoto.setIcon(new ImageIcon(System.getProperty("user.dir") +"/resource/gui/webcam2.png"));
		btnMakeFoto.setBounds(10, 181, 86, 26);
		contentPane.add(btnMakeFoto);
	}
	private void changePhoto(File file) {
		BufferedImage image = null;
		try {
			if(file != null)//�� ������ ���� ����������
			if(!photoURL.equals("")){ //������� �� � ���� �� ����������
				if (file.getPath() != photoURL) {//����� � �����?
					image = ImageIO.read(file);
					if(photoURL != null) {
						photoURL = photoURL.replace(System.getProperty("user.dir") +"\\resource\\users\\", "");
					}
				    File f = new File(System.getProperty("user.dir") +"/resource/users/" + photoURL);
				    ImageIO.write(image, "png", f);
				    photoURL = f.getPath();
				}
			} else {
				    image = ImageIO.read(file);
				    File f = new File(System.getProperty("user.dir") +"/resource/users/" + ScalingImage.getNameFoto() + ".png");
				    ImageIO.write(image, "png", f);
				    photoURL = f.getPath();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	private boolean isEmpty() {
		boolean flag = true;
		if(!ValidationComponents.isEmpty(txtFirstName)) flag = false;
		if(!ValidationComponents.isEmpty(txtLastName)) flag = false;
		if(!ValidationComponents.isEmpty(dateChooser)) flag = false;
		if(!ValidationComponents.isEmpty(txtWeight)) flag = false;
		if(!ValidationComponents.isEmpty(jcbDegree)) flag = false;
		if(!ValidationComponents.isEmpty(jcbOrganization)) flag = false;
		return flag;
	}
	private ArrayList<DBDegrees> getDegrees(){
		return new Degrees().select();
	}
	private void initDegrees() {
		this.idDegrees = new ArrayList<Integer>();
		for (DBDegrees degree : getDegrees()) {
			this.jcbDegree.addItem(degree.Degree);
			this.idDegrees.add(degree.DegreeID);
		}
		jcbDegree.setSelectedIndex(-1);
	}
	private int insertDegree() {
		return new Degrees().insert(jcbDegree.getSelectedItem().toString());
	}	
	private ArrayList<DBOrganization> getOrganization(){
		return new Organization().select();
	}
	private void initOrganization() {
		this.idOrganization = new ArrayList<Integer>();
		for (DBOrganization org : getOrganization()) {
			this.jcbOrganization.addItem(org.OrgName);
			this.idOrganization.add(org.OrgID);
		}
		jcbOrganization.setSelectedIndex(-1);
	}
	private int insertOrganization() {
		return new Organization().insert(jcbOrganization.getSelectedItem().toString());
	}
	private void insertPerson() {
		DBPersonExtend person = new DBPersonExtend();
		person.PersFN = txtFirstName.getText();
		person.PersLN = txtLastName.getText();
		person.PersMN = txtMiddleName.getText();
		person.PersWeight = Integer.parseInt(txtWeight.getText());
		person.PersDate = Date.getDate(dateChooser.getDate());
		person.PersAge = Date.getAge(dateChooser.getDate());
		person.PersSex = btnGroup.isSelected(rbtnMale.getModel())? true: false;
		person.DegreeID = jcbDegree.getSelectedIndex() == -1? insertDegree(): idDegrees.get(jcbDegree.getSelectedIndex());
		person.OrgID = jcbOrganization.getSelectedIndex() == -1? insertOrganization(): idOrganization.get(jcbOrganization.getSelectedIndex());
		person.PersPhoto = photoURL;
		int id = new Persons().insert((DBPersons)person);
		person.Deegree = (String) jcbDegree.getSelectedItem();
		person.Organization = (String) jcbOrganization.getSelectedItem();
		for (ITransferData listener : listeners) {
			listener.transferAdd(person);
		}
		/*
		if(!textAreaDocParents.getText().equals("")){
			DBDocumentParents doc = new DBDocumentParents();
			doc.DParent = textAreaDocParents.getText();
			doc.PersID = id;
			new DocumentParents().insert(doc);
		}*/
	}	
	private void updatePerson() {
		DBPersonExtend person = new DBPersonExtend();
		person.PersID = PersID;
		person.PersFN = txtFirstName.getText();
		person.PersLN = txtLastName.getText();
		person.PersMN = txtMiddleName.getText();
		person.PersWeight = Integer.parseInt(txtWeight.getText());
		person.PersDate = Date.getDate(dateChooser.getDate());
		person.PersAge = Date.getAge(dateChooser.getDate());
		person.PersSex = btnGroup.isSelected(rbtnMale.getModel())? true: false;
		person.DegreeID = jcbDegree.getSelectedIndex() == -1? insertDegree(): idDegrees.get(jcbDegree.getSelectedIndex());
		person.OrgID = jcbOrganization.getSelectedIndex() == -1? insertOrganization(): idOrganization.get(jcbOrganization.getSelectedIndex());	
		person.PersPhoto = photoURL;
		new Persons().update((DBPersons)person);
		person.Deegree = (String) jcbDegree.getSelectedItem();
		person.Organization = (String) jcbOrganization.getSelectedItem();
		for (ITransferData listener : listeners) {
			listener.transferUpdate(person);
		}
		/*if(!textAreaDocParents.getText().equals("")){
			DBDocumentParents doc = new DBDocumentParents();
			doc.DParent = textAreaDocParents.getText();
			doc.PersID = id;
			new DocumentParents().insert(doc);
		}*/
	}
	public void addTransferDataListener(ITransferData transferData) {
		this.listeners.add(transferData);
	}
}
