package com.deportur.vista;

import com.deportur.controlador.UsuarioController;
import com.deportur.modelo.Usuario;
import com.deportur.vista.componentes.RoundedButton;
import com.deportur.vista.componentes.SideMenu;
import com.deportur.vista.paneles.*;
import com.deportur.vista.util.UIConstants;
import com.deportur.vista.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Ventana principal de la aplicación con diseño moderno
 */
public class MainFrame extends JFrame {
    
    private static UsuarioController usuarioController;
    private JPanel contentPanel;
    private SideMenu sideMenu;
    private JLabel lblUsuarioActual;
    private JLabel lblRolUsuario;
    private JLabel lblTituloPantalla;
    
    // Paneles de contenido
    private PanelInventario panelInventario;
    private PanelReservas panelReservas;
    private PanelClientes panelClientes;
    private PanelDestinos panelDestinos;
    private PanelTiposEquipo panelTiposEquipo;
    private PanelUsuarios panelUsuarios;
    
    // Índices de menú
    private static final int MENU_INVENTORY = 0;
    private static final int MENU_RESERVATIONS = 1;
    private static final int MENU_CLIENTS = 2;
    private static final int MENU_DESTINATIONS = 3;
    private static final int MENU_EQUIPMENT_TYPES = 4;
    private static final int MENU_USERS = 5;
    
    public MainFrame(UsuarioController usuarioController) {
        MainFrame.usuarioController = usuarioController;
        initComponents();
        configureUI();
    }
    
    // Método estático para acceder al controlador desde cualquier panel
    public static UsuarioController getUsuarioController() {
        return usuarioController;
    }
    
    private void initComponents() {
        // Configurar la ventana principal
        setTitle("Sistema de Gestión para Alquiler de Equipos Deportivos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1024, 768));
        
        // Inicializar paneles
        panelInventario = new PanelInventario();
        panelReservas = new PanelReservas();
        panelClientes = new PanelClientes();
        panelDestinos = new PanelDestinos();
        panelTiposEquipo = new PanelTiposEquipo();
        
        if (usuarioController.esAdministrador()) {
            panelUsuarios = new PanelUsuarios(usuarioController);
        }
        
        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Barra superior
        JPanel topBar = createTopBar();
        
        // Menú lateral
        sideMenu = createSideMenu();
        
        // Panel de contenido (inicialmente vacío)
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Agregar componentes al panel principal
        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(sideMenu, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Establecer panel principal como contenido de la ventana
        setContentPane(mainPanel);
        
        // Mostrar el panel inicial (Inventario)
        sideMenu.selectOption(MENU_INVENTORY);
        showPanel(panelInventario);
        lblTituloPantalla.setText("Inventario");
        
        // Crear el menú superior
        createMainMenu();
    }
    
    private void configureUI() {
        // Configurar look and feel
        UIUtils.configureGlobalLookAndFeel();
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    /**
     * Crea la barra superior con información de usuario y controles
     */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIConstants.PRIMARY_COLOR);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        topBar.setPreferredSize(new Dimension(0, 60));
        
        // Panel izquierdo con título
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        
        // Ícono de la aplicación
        JLabel iconLabel = new JLabel(UIConstants.APP_ICON);
        
        // Título de la aplicación
        JLabel titleLabel = new JLabel("DeporTur");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        // Título de la pantalla actual
        lblTituloPantalla = new JLabel("Inventario");
        lblTituloPantalla.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTituloPantalla.setForeground(Color.WHITE);
        
        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);
        leftPanel.add(new JSeparator(JSeparator.VERTICAL) {
            {
                setPreferredSize(new Dimension(1, 30));
                setForeground(new Color(255, 255, 255, 100));
            }
        });
        leftPanel.add(lblTituloPantalla);
        
        // Panel derecho con información de usuario
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        // Información del usuario
        Usuario usuarioActual = usuarioController.getUsuarioActual();
        
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
        userInfoPanel.setOpaque(false);
        
        lblUsuarioActual = new JLabel(usuarioActual.getNombre() + " " + usuarioActual.getApellido());
        lblUsuarioActual.setForeground(Color.WHITE);
        lblUsuarioActual.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuarioActual.setHorizontalAlignment(JLabel.RIGHT);
        
        lblRolUsuario = new JLabel(usuarioActual.getRol());
        lblRolUsuario.setForeground(new Color(220, 220, 220));
        lblRolUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRolUsuario.setHorizontalAlignment(JLabel.RIGHT);
        
        userInfoPanel.add(lblUsuarioActual);
        userInfoPanel.add(lblRolUsuario);
        
        // Botón de cerrar sesión
        RoundedButton btnCerrarSesion = new RoundedButton("Cerrar Sesión", UIConstants.LOGOUT_ICON, new Color(200, 80, 80));
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        
        rightPanel.add(userInfoPanel);
        rightPanel.add(new JSeparator(JSeparator.VERTICAL) {
            {
                setPreferredSize(new Dimension(1, 30));
                setForeground(new Color(255, 255, 255, 100));
            }
        });
        rightPanel.add(btnCerrarSesion);
        
        // Agregar paneles a la barra superior
        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    /**
     * Crea el menú lateral con las opciones de navegación
     */
    private SideMenu createSideMenu() {
        SideMenu menu = new SideMenu();
        
        // Agregar opciones al menú
        menu.addOption("Inventario", UIConstants.INVENTORY_ICON);
        menu.addOption("Reservas", UIConstants.RESERVATION_ICON);
        menu.addOption("Clientes", UIConstants.CLIENT_ICON);
        menu.addOption("Destinos", UIConstants.DESTINATION_ICON);
        menu.addOption("Tipos de Equipo", UIConstants.TYPE_ICON);
        
        // Agregar opción de usuarios solo para administradores
        if (usuarioController.esAdministrador()) {
            menu.addOption("Usuarios", UIConstants.USER_ICON);
        }
        
        // Configurar listener para cambios de selección
        menu.setMenuSelectionListener(this::handleMenuSelection);
        
        return menu;
    }
    
    /**
     * Maneja la selección de opciones en el menú
     */
    private void handleMenuSelection(int index) {
        switch (index) {
            case MENU_INVENTORY:
                showPanel(panelInventario);
                lblTituloPantalla.setText("Inventario");
                break;
            case MENU_RESERVATIONS:
                showPanel(panelReservas);
                lblTituloPantalla.setText("Reservas");
                break;
            case MENU_CLIENTS:
                showPanel(panelClientes);
                lblTituloPantalla.setText("Clientes");
                break;
            case MENU_DESTINATIONS:
                showPanel(panelDestinos);
                lblTituloPantalla.setText("Destinos Turísticos");
                break;
            case MENU_EQUIPMENT_TYPES:
                showPanel(panelTiposEquipo);
                lblTituloPantalla.setText("Tipos de Equipo");
                break;
            case MENU_USERS:
                if (panelUsuarios != null) {
                    showPanel(panelUsuarios);
                    lblTituloPantalla.setText("Usuarios del Sistema");
                }
                break;
        }
    }
    
    /**
     * Muestra un panel en el área de contenido
     */
    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Crea el menú principal de la aplicación
     */
    private void createMainMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(UIConstants.PANEL_COLOR);
        
        // Menú Archivo
        JMenu fileMenu = new JMenu("Archivo");
        fileMenu.setMnemonic(KeyEvent.VK_A);
        
        JMenuItem cerrarSesionMenuItem = new JMenuItem("Cerrar Sesión", UIConstants.LOGOUT_ICON);
        cerrarSesionMenuItem.setMnemonic(KeyEvent.VK_C);
        cerrarSesionMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        cerrarSesionMenuItem.addActionListener(e -> cerrarSesion());
        
        JMenuItem exitMenuItem = new JMenuItem("Salir", null);
        exitMenuItem.setMnemonic(KeyEvent.VK_S);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        exitMenuItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(cerrarSesionMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
        // Menú Navegación
        JMenu navegacionMenu = new JMenu("Navegación");
        navegacionMenu.setMnemonic(KeyEvent.VK_N);
        
        JMenuItem inventarioMenuItem = new JMenuItem("Inventario", UIConstants.INVENTORY_ICON);
        inventarioMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK));
        inventarioMenuItem.addActionListener(e -> sideMenu.selectOption(MENU_INVENTORY));
        
        JMenuItem reservasMenuItem = new JMenuItem("Reservas", UIConstants.RESERVATION_ICON);
        reservasMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_DOWN_MASK));
        reservasMenuItem.addActionListener(e -> sideMenu.selectOption(MENU_RESERVATIONS));
        
        JMenuItem clientesMenuItem = new JMenuItem("Clientes", UIConstants.CLIENT_ICON);
        clientesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_DOWN_MASK));
        clientesMenuItem.addActionListener(e -> sideMenu.selectOption(MENU_CLIENTS));
        
        JMenuItem destinosMenuItem = new JMenuItem("Destinos", UIConstants.DESTINATION_ICON);
        destinosMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.ALT_DOWN_MASK));
        destinosMenuItem.addActionListener(e -> sideMenu.selectOption(MENU_DESTINATIONS));
        
        JMenuItem tiposMenuItem = new JMenuItem("Tipos de Equipo", UIConstants.TYPE_ICON);
        tiposMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.ALT_DOWN_MASK));
        tiposMenuItem.addActionListener(e -> sideMenu.selectOption(MENU_EQUIPMENT_TYPES));
        
        navegacionMenu.add(inventarioMenuItem);
        navegacionMenu.add(reservasMenuItem);
        navegacionMenu.add(clientesMenuItem);
        navegacionMenu.add(destinosMenuItem);
        navegacionMenu.add(tiposMenuItem);
        
        // Solo agregar opción de usuarios para administradores
        if (usuarioController.esAdministrador()) {
            JMenuItem usuariosMenuItem = new JMenuItem("Usuarios", UIConstants.USER_ICON);
            usuariosMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.ALT_DOWN_MASK));
            usuariosMenuItem.addActionListener(e -> sideMenu.selectOption(MENU_USERS));
            navegacionMenu.add(usuariosMenuItem);
        }
        
        // Menú Ayuda
        JMenu helpMenu = new JMenu("Ayuda");
        helpMenu.setMnemonic(KeyEvent.VK_Y);
        
        JMenuItem aboutMenuItem = new JMenuItem("Acerca de", UIConstants.INFO_ICON);
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.addActionListener(e -> mostrarAcercaDe());
        
        helpMenu.add(aboutMenuItem);
        
        // Añadir menús a la barra
        menuBar.add(fileMenu);
        menuBar.add(navegacionMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Muestra el diálogo "Acerca de"
     */
    private void mostrarAcercaDe() {
        // Crear un panel personalizado para el diálogo "Acerca de"
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Logo de la aplicación
        JLabel logoLabel = new JLabel(UIConstants.APP_ICON);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Título de la aplicación
        JLabel titleLabel = new JLabel("DeporTur");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Versión
        JLabel versionLabel = new JLabel("Versión 1.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        versionLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Panel superior con logo y título
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);
        topPanel.add(logoLabel, BorderLayout.NORTH);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(versionLabel, BorderLayout.SOUTH);
        
        // Descripción
        JTextArea descriptionTextArea = new JTextArea(
            "Sistema de Gestión para Alquiler de Equipos Deportivos\n" +
            "Desarrollado para destinos turísticos de Colombia\n" +
            "© 2025 - Todos los derechos reservados"
        );
        descriptionTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setOpaque(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        
        // Autores
        JPanel authorsPanel = new JPanel(new BorderLayout());
        authorsPanel.setOpaque(false);
        
        JLabel authorsLabel = new JLabel("Autores:");
        authorsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JTextArea authorsTextArea = new JTextArea(
            "Juan Perea\n" +
            "Kevin Beltran\n" +
            "Carlos Rincon"
        );
        authorsTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        authorsTextArea.setEditable(false);
        authorsTextArea.setOpaque(false);
        
        authorsPanel.add(authorsLabel, BorderLayout.NORTH);
        authorsPanel.add(authorsTextArea, BorderLayout.CENTER);
        
        // Panel central con descripción y autores
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(descriptionTextArea, BorderLayout.NORTH);
        centerPanel.add(authorsPanel, BorderLayout.CENTER);
        
        // Agregar componentes al panel principal
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Mostrar diálogo
        JOptionPane.showMessageDialog(
            this,
            panel,
            "Acerca de",
            JOptionPane.PLAIN_MESSAGE
        );
    }
    
    /**
     * Cierra la sesión actual y muestra la pantalla de login
     */
    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea cerrar sesión?",
            "Confirmar Cierre de Sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            usuarioController.cerrarSesion();
            dispose();
            
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }
}