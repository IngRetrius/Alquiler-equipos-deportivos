package com.deportur.vista;

import com.deportur.controlador.UsuarioController;
import com.deportur.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    
    private UsuarioController usuarioController;
    private JTabbedPane tabbedPane;
    private PanelInventario panelInventario;
    private PanelReservas panelReservas;
    private PanelClientes panelClientes;
    private PanelDestinos panelDestinos;
    private PanelTiposEquipo panelTiposEquipo;
    private PanelUsuarios panelUsuarios;
    private JLabel lblUsuarioActual;
    
    public MainFrame(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
        initComponents();
        configureUI();
    }
    
    private void initComponents() {
        // Configurar la ventana principal
        setTitle("Sistema de Gestión para Alquiler de Equipos Deportivos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        
        // Crear el TabbedPane principal
        tabbedPane = new JTabbedPane();
        
        // Inicializar los paneles
        panelInventario = new PanelInventario();
        panelReservas = new PanelReservas();
        panelClientes = new PanelClientes();
        panelDestinos = new PanelDestinos();
        panelTiposEquipo = new PanelTiposEquipo();
        
        // Añadir los paneles al TabbedPane
        tabbedPane.addTab("Inventario", new ImageIcon(), panelInventario, "Gestionar inventario de equipos deportivos");
        tabbedPane.addTab("Reservas", new ImageIcon(), panelReservas, "Gestionar reservas de clientes");
        tabbedPane.addTab("Clientes", new ImageIcon(), panelClientes, "Gestionar información de clientes");
        tabbedPane.addTab("Destinos", new ImageIcon(), panelDestinos, "Gestionar destinos turísticos");
        tabbedPane.addTab("Tipos de Equipo", new ImageIcon(), panelTiposEquipo, "Gestionar tipos de equipos deportivos");
        
        // Solo si es administrador, añadir el panel de usuarios
        if (usuarioController.esAdministrador()) {
            panelUsuarios = new PanelUsuarios(usuarioController);
            tabbedPane.addTab("Usuarios", new ImageIcon(), panelUsuarios, "Gestionar usuarios del sistema");
        }
        
        // Panel de información de usuario
        JPanel panelUsuarioInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Usuario usuarioActual = usuarioController.getUsuarioActual();
        lblUsuarioActual = new JLabel("Usuario: " + usuarioActual.getNombre() + " " + 
                                    usuarioActual.getApellido() + " [" + usuarioActual.getRol() + "]");
        
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        
        panelUsuarioInfo.add(lblUsuarioActual);
        panelUsuarioInfo.add(btnCerrarSesion);
        
        // Añadir el TabbedPane y el panel de info al frame
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(panelUsuarioInfo, BorderLayout.SOUTH);
        
        // Crear la barra de menú
        createMenuBar();
    }
    
    private void configureUI() {
        // Configurar estilos y temas
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu fileMenu = new JMenu("Archivo");
        fileMenu.setMnemonic(KeyEvent.VK_A);
        
        JMenuItem cerrarSesionMenuItem = new JMenuItem("Cerrar Sesión", KeyEvent.VK_C);
        cerrarSesionMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        cerrarSesionMenuItem.addActionListener(e -> cerrarSesion());
        
        JMenuItem exitMenuItem = new JMenuItem("Salir", KeyEvent.VK_S);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        exitMenuItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(cerrarSesionMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
        // Menú Inventario
        JMenu inventoryMenu = new JMenu("Inventario");
        inventoryMenu.setMnemonic(KeyEvent.VK_I);
        
        JMenuItem addEquipMenuItem = new JMenuItem("Agregar Equipo", KeyEvent.VK_A);
        addEquipMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        addEquipMenuItem.addActionListener(e -> panelInventario.mostrarFormularioAgregar());
        
        inventoryMenu.add(addEquipMenuItem);
        
        // Menú Reservas
        JMenu reservasMenu = new JMenu("Reservas");
        reservasMenu.setMnemonic(KeyEvent.VK_R);
        
        JMenuItem newReservaMenuItem = new JMenuItem("Nueva Reserva", KeyEvent.VK_N);
        newReservaMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        newReservaMenuItem.addActionListener(e -> panelReservas.mostrarFormularioCrear());
        
        reservasMenu.add(newReservaMenuItem);
        
        // Menú Administración (solo para administradores)
        JMenu adminMenu = new JMenu("Administración");
        adminMenu.setMnemonic(KeyEvent.VK_D);
        
        if (usuarioController.esAdministrador()) {
            JMenuItem usuariosMenuItem = new JMenuItem("Gestionar Usuarios", KeyEvent.VK_U);
            usuariosMenuItem.addActionListener(e -> tabbedPane.setSelectedComponent(panelUsuarios));
            adminMenu.add(usuariosMenuItem);
        }
        
        // Menú Ayuda
        JMenu helpMenu = new JMenu("Ayuda");
        helpMenu.setMnemonic(KeyEvent.VK_Y);
        
        JMenuItem aboutMenuItem = new JMenuItem("Acerca de", KeyEvent.VK_A);
        aboutMenuItem.addActionListener(e -> mostrarAcercaDe());
        
        helpMenu.add(aboutMenuItem);
        
        // Añadir menús a la barra
        menuBar.add(fileMenu);
        menuBar.add(inventoryMenu);
        menuBar.add(reservasMenu);
        
        // Solo añadir el menú de administración si tiene elementos
        if (adminMenu.getItemCount() > 0) {
            menuBar.add(adminMenu);
        }
        
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void cerrarSesion() {
        usuarioController.cerrarSesion();
        this.dispose();
        
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
    }
    
    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
            "Sistema de Gestión para Alquiler de Equipos Deportivos\n" +
            "Versión 1.0\n\n" +
            "Desarrollado para destinos turísticos de Colombia\n" +
            "© 2025 - Todos los derechos reservados",
            "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }
}