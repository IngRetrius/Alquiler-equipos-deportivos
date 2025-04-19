package com.deportur.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    private PanelInventario panelInventario;
    private PanelReservas panelReservas;
    private PanelClientes panelClientes;
    private PanelDestinos panelDestinos;
    private PanelTiposEquipo panelTiposEquipo;
    
    public MainFrame() {
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
        
        // Añadir el TabbedPane al frame
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
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
        
        JMenuItem exitMenuItem = new JMenuItem("Salir", KeyEvent.VK_S);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        exitMenuItem.addActionListener(e -> System.exit(0));
        
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
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
            "Sistema de Gestión para Alquiler de Equipos Deportivos\n" +
            "Versión 1.0\n\n" +
            "Desarrollado para destinos turísticos de Colombia\n" +
            "© 2025 - Todos los derechos reservados",
            "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Método principal para pruebas
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}