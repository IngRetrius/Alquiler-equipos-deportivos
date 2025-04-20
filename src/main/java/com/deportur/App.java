package com.deportur;

import com.deportur.vista.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    
    public static void main(String[] args) {
        // Configurar Look and Feel de la aplicación
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Iniciar la aplicación en el EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}