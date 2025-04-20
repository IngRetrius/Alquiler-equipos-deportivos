package com.deportur.vista;

import com.deportur.controlador.UsuarioController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    
    private UsuarioController usuarioController;
    
    // Componentes de la interfaz
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JButton btnSalir;
    
    public LoginFrame() {
        usuarioController = new UsuarioController();
        initComponents();
        configureUI();
    }
    
    private void initComponents() {
        // Configurar la ventana
        setTitle("Iniciar Sesión - Sistema de Alquiler de Equipos Deportivos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel para el formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Etiqueta de bienvenida
        JLabel lblBienvenida = new JLabel("Bienvenido al Sistema");
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 16));
        lblBienvenida.setHorizontalAlignment(JLabel.CENTER);
        
        // Campo de usuario
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Usuario:"), gbc);
        
        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        formPanel.add(txtUsuario, gbc);
        
        // Campo de contraseña
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Contraseña:"), gbc);
        
        gbc.gridx = 1;
        txtContrasena = new JPasswordField(15);
        formPanel.add(txtContrasena, gbc);
        
        // Panel para botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        btnIngresar = new JButton("Ingresar");
        btnIngresar.addActionListener(e -> iniciarSesion());
        
        btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(btnIngresar);
        buttonPanel.add(btnSalir);
        
        // Agregar todo al panel principal
        mainPanel.add(lblBienvenida, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Agregar el panel principal a la ventana
        setContentPane(mainPanel);
        
        // Configurar acción al presionar Enter
        getRootPane().setDefaultButton(btnIngresar);
        
        // Configurar acciones de teclado para ESCAPE
        getRootPane().registerKeyboardAction(
            e -> System.exit(0),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void configureUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void iniciarSesion() {
        String nombreUsuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());
        
        if (nombreUsuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese usuario y contraseña",
                "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (usuarioController.autenticar(nombreUsuario, contrasena)) {
            // Autenticación exitosa
            this.dispose();
            
            MainFrame mainFrame = new MainFrame(usuarioController);
            mainFrame.setVisible(true);
        } else {
            // Limpiar el campo de contraseña
            txtContrasena.setText("");
            txtContrasena.requestFocus();
        }
    }
    
    // Método principal modificado para probar la pantalla de login
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}