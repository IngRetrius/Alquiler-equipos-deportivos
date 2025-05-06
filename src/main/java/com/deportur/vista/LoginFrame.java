package com.deportur.vista;

import com.deportur.controlador.UsuarioController;
import com.deportur.vista.util.UIConstants;
import com.deportur.vista.util.UIUtils;
import com.deportur.vista.componentes.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Pantalla de inicio de sesión con diseño moderno
 */
public class LoginFrame extends JFrame {
    
    private UsuarioController usuarioController;
    
    // Componentes de la interfaz
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private RoundedButton btnIngresar;
    private RoundedButton btnSalir;
    private JLabel lblError;
    
    public LoginFrame() {
        usuarioController = new UsuarioController();
        initComponents();
        configureUI();
    }
    
    private void initComponents() {
        // Configurar la ventana
        setTitle("Iniciar Sesión - Sistema de Alquiler de Equipos Deportivos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true); // Sin bordes de ventana para un aspecto más moderno
        
        // Establecer forma redondeada para la ventana (solo funciona sin decoración)
        setShape(new RoundRectangle2D.Double(0, 0, 900, 600, 15, 15));
        
        // Panel principal con diseño de dos columnas
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder());
        
        // Panel de imagen (izquierda)
        JPanel imagePanel = UIUtils.createPanelWithBackgroundImage(UIConstants.LOGIN_BACKGROUND);
        imagePanel.setPreferredSize(new Dimension(450, 600));
        
        // Agregar logo sobre la imagen
        JLabel logoLabel = new JLabel(UIConstants.APP_ICON);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setBorder(new EmptyBorder(50, 0, 0, 0));
        imagePanel.add(logoLabel, BorderLayout.NORTH);
        
        // Panel de créditos en la parte inferior de la imagen
        JPanel creditsPanel = new JPanel();
        creditsPanel.setOpaque(false);
        JLabel creditsLabel = new JLabel("© 2025 DeporTur - Todos los derechos reservados");
        creditsLabel.setForeground(Color.WHITE);
        creditsLabel.setFont(UIConstants.SMALL_FONT);
        creditsPanel.add(creditsLabel);
        imagePanel.add(creditsPanel, BorderLayout.SOUTH);
        
        // Panel de formulario (derecha)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Título y subtítulo
        JLabel lblBienvenida = new JLabel("Bienvenido");
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblBienvenida.setForeground(UIConstants.PRIMARY_COLOR);
        
        JLabel lblSubtitulo = new JLabel("Inicia sesión para continuar");
        lblSubtitulo.setFont(UIConstants.SUBTITLE_FONT);
        lblSubtitulo.setForeground(UIConstants.DARK_COLOR);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(lblBienvenida, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 30, 30, 30);
        formPanel.add(lblSubtitulo, gbc);
        
        // Campo de usuario
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(UIConstants.NORMAL_FONT);
        lblUsuario.setForeground(UIConstants.DARK_COLOR);
        
        txtUsuario = new JTextField(20);
        txtUsuario.setFont(UIConstants.NORMAL_FONT);
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                UIConstants.ROUNDED_BORDER,
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 30, 5, 30);
        formPanel.add(lblUsuario, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 30, 15, 30);
        formPanel.add(txtUsuario, gbc);
        
        // Campo de contraseña
        JLabel lblContrasena = new JLabel("Contraseña");
        lblContrasena.setFont(UIConstants.NORMAL_FONT);
        lblContrasena.setForeground(UIConstants.DARK_COLOR);
        
        txtContrasena = new JPasswordField(20);
        txtContrasena.setFont(UIConstants.NORMAL_FONT);
        txtContrasena.setBorder(BorderFactory.createCompoundBorder(
                UIConstants.ROUNDED_BORDER,
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 30, 5, 30);
        formPanel.add(lblContrasena, gbc);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 30, 15, 30);
        formPanel.add(txtContrasena, gbc);
        
        // Etiqueta de error (inicialmente oculta)
        lblError = new JLabel("");
        lblError.setFont(UIConstants.SMALL_FONT);
        lblError.setForeground(UIConstants.WARNING_COLOR);
        lblError.setVisible(false);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 30, 15, 30);
        formPanel.add(lblError, gbc);
        
        // Panel para botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        
        btnIngresar = new RoundedButton("Ingresar", UIConstants.PRIMARY_COLOR);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.addActionListener(e -> iniciarSesion());
        
        btnSalir = new RoundedButton("Salir", Color.LIGHT_GRAY);
        btnSalir.setForeground(Color.DARK_GRAY);
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalir.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(btnSalir);
        buttonPanel.add(btnIngresar);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(15, 30, 30, 30);
        formPanel.add(buttonPanel, gbc);
        
        // Añadir panel para mover la ventana sin decoración
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Botón de cierre personalizado
        JLabel closeButton = new JLabel("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 20));
        closeButton.setForeground(Color.GRAY);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(UIConstants.WARNING_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(Color.GRAY);
            }
        });
        
        titleBar.add(closeButton, BorderLayout.EAST);
        
        // Funcionalidad para mover la ventana
        MouseAdapter moveWindowAdapter = new MouseAdapter() {
            private int lastX, lastY;
            
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getXOnScreen();
                lastY = e.getYOnScreen();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                int dx = x - lastX;
                int dy = y - lastY;
                
                setLocation(getLocation().x + dx, getLocation().y + dy);
                
                lastX = x;
                lastY = y;
            }
        };
        
        titleBar.addMouseListener(moveWindowAdapter);
        titleBar.addMouseMotionListener(moveWindowAdapter);
        
        // Agregar titleBar al panel principal
        mainPanel.add(imagePanel);
        mainPanel.add(formPanel);
        
        // Crear panel con capa para poder superponer titleBar
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(900, 600));
        
        mainPanel.setBounds(0, 0, 900, 600);
        titleBar.setBounds(0, 0, 900, 30);
        
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(titleBar, JLayeredPane.PALETTE_LAYER);
        
        // Agregar el panel a la ventana
        setContentPane(layeredPane);
        
        // Configurar acción al presionar Enter en el campo de contraseña
        txtContrasena.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    iniciarSesion();
                }
            }
        });
        
        // Configurar acciones de teclado para ESCAPE
        getRootPane().registerKeyboardAction(
            e -> System.exit(0),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Establecer botón por defecto
        getRootPane().setDefaultButton(btnIngresar);
    }
    
    private void configureUI() {
        // Configurar look and feel
        UIUtils.configureGlobalLookAndFeel();
        SwingUtilities.updateComponentTreeUI(this);
        
        // Añadir efecto de sombra (solo visible en algunos sistemas)
        JFrame.setDefaultLookAndFeelDecorated(true);
    }
    
    private void iniciarSesion() {
        String nombreUsuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());
        
        if (nombreUsuario.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor ingrese usuario y contraseña");
            return;
        }
        
        // Intentar autenticar
        if (usuarioController.autenticar(nombreUsuario, contrasena)) {
            // Autenticación exitosa - Mostrar animación de carga
            mostrarAnimacionCarga();
            
            // Usar Timer para simular carga y dar sensación de proceso
            Timer timer = new Timer(800, e -> {
                dispose();
                MainFrame mainFrame = new MainFrame(usuarioController);
                mainFrame.setVisible(true);
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            // Autenticación fallida
            mostrarError("Usuario o contraseña incorrectos");
            txtContrasena.setText("");
            txtContrasena.requestFocus();
        }
    }
    
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        
        // Efecto de desvanecimiento después de 5 segundos
        Timer timer = new Timer(5000, e -> {
            lblError.setVisible(false);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void mostrarAnimacionCarga() {
        // Deshabilitar controles de entrada
        txtUsuario.setEnabled(false);
        txtContrasena.setEnabled(false);
        btnIngresar.setEnabled(false);
        btnSalir.setEnabled(false);
        
        // Cambiar el texto del botón de ingreso para mostrar estado
        btnIngresar.setText("Ingresando...");
    }
    
    // Método principal modificado para probar la pantalla de login
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}