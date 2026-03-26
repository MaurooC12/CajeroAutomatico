package ui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import model.*;
import service.*;
import java.util.*;

public class ClienteFrame extends JFrame {
    
    private Session session;
    private TransaccionService transService;
    private JLabel lblSaldo;
    
    public ClienteFrame(Session session, TransaccionService transService) {
        this.session = session;
        this.transService = transService;
        
        setTitle("Cajero Automático - Cliente");
        setSize(600, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        getContentPane().setBackground(new Color(240, 248, 255));
        
        initComponents();
        actualizarSaldo();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                volverAlLogin();
            }
        });
    }
    
    private void initComponents() {
        // Panel superior con información
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(new Color(0, 102, 204));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JPanel panelTexto = new JPanel(new GridLayout(2, 1));
        panelTexto.setBackground(new Color(0, 102, 204));
        
        JLabel lblBienvenido = new JLabel("Bienvenido, " + session.getCuenta().getIdUsuario());
        lblBienvenido.setFont(new Font("Arial", Font.BOLD, 20));
        lblBienvenido.setForeground(Color.WHITE);
        
        JLabel lblCuenta = new JLabel("Cuenta: " + session.getCuenta().getNumero());
        lblCuenta.setFont(new Font("Arial", Font.PLAIN, 14));
        lblCuenta.setForeground(Color.WHITE);
        
        panelTexto.add(lblBienvenido);
        panelTexto.add(lblCuenta);
        
        lblSaldo = new JLabel("Saldo: $0");
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 20));
        lblSaldo.setForeground(Color.WHITE);
        lblSaldo.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panelInfo.add(panelTexto, BorderLayout.WEST);
        panelInfo.add(lblSaldo, BorderLayout.EAST);
        
        add(panelInfo, BorderLayout.NORTH);
        
        // Panel central con botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 25, 25));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(50, 70, 50, 70));
        panelBotones.setBackground(new Color(240, 248, 255));
        
        JButton btnRetirar = crearBoton("RETIRAR EFECTIVO", "💵", new Color(240, 240, 240));
        JButton btnTransferir = crearBoton("TRANSFERIR", "🔄", new Color(240, 240, 240));
        JButton btnMovimientos = crearBoton("VER MOVIMIENTOS", "📋", new Color(240, 240, 240));
        
        btnRetirar.addActionListener(e -> retirar());
        btnTransferir.addActionListener(e -> transferir());
        btnMovimientos.addActionListener(e -> verMovimientos());
        
        panelBotones.add(btnRetirar);
        panelBotones.add(btnTransferir);
        panelBotones.add(btnMovimientos);
        
        add(panelBotones, BorderLayout.CENTER);
        
        // Panel inferior con botón cerrar sesión
        JPanel panelCerrar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelCerrar.setBackground(new Color(240, 248, 255));
        panelCerrar.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JButton btnCerrar = new JButton("🚪 CERRAR SESIÓN");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(240, 240, 240));
        btnCerrar.setForeground(Color.BLACK);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setPreferredSize(new Dimension(180, 45));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        btnCerrar.addActionListener(e -> cerrarSesion());
        
        panelCerrar.add(btnCerrar);
        add(panelCerrar, BorderLayout.SOUTH);
    }
    
    private JButton crearBoton(String texto, String icono, Color color) {
        JButton btn = new JButton(icono + " " + texto);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(220, 80));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setMargin(new Insets(8, 10, 8, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        return btn;
    }
    
    private void actualizarSaldo() {
        double saldo = transService.consultarSaldo(session.getCuenta());
        lblSaldo.setText("Saldo: $" + String.format("%,.0f", saldo));
    }
    
    private void retirar() {
        String input = JOptionPane.showInputDialog(this, 
            "Ingrese monto a retirar:\n(Múltiplo de 10,000)", 
            "Retiro de efectivo", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (input == null) return;
        
        try {
            int monto = Integer.parseInt(input);
            
            // Validaciones frontend
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (monto % 10000 != 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser múltiplo de 10,000.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (monto > session.getCuenta().getSaldo()) {
                JOptionPane.showMessageDialog(this, "Saldo insuficiente.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (monto > session.getCuenta().getLimiteDiario()) {
                JOptionPane.showMessageDialog(this, "Excede el límite diario de retiro.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Realizar retiro - El backend mostrará sus propios mensajes
            if (transService.retirar(session, monto)) {
                actualizarSaldo();
                JOptionPane.showMessageDialog(this, 
                    "✓ Retiro exitoso por $" + String.format("%,d", monto) + "\n\nGracias por utilizar nuestros servicios.",
                    "Operación exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
            // Si el retiro falla, el backend ya mostró el mensaje de error
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Monto inválido. Ingrese solo números.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void transferir() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        JTextField txtDestino = new JTextField(15);
        JTextField txtMonto = new JTextField(10);
        
        panel.add(new JLabel("Cuenta destino:"));
        panel.add(txtDestino);
        panel.add(new JLabel("Monto: $"));
        panel.add(txtMonto);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Transferencia entre cuentas", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String destino = txtDestino.getText().trim();

                if (destino.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ingrese cuenta destino.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!transService.existeCuenta(destino)) {
                    JOptionPane.showMessageDialog(this, "La cuenta destino no existe.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double monto = Double.parseDouble(txtMonto.getText().trim());
                
                if (monto <= 0) {
                    JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (monto > session.getCuenta().getSaldo()) {
                    JOptionPane.showMessageDialog(this, "Saldo insuficiente.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Realizar transferencia - El backend validará existencia de cuenta y mostrará mensaje
                if (transService.transferir(session, destino, monto)) {
                    actualizarSaldo();
                    JOptionPane.showMessageDialog(this, 
                        "✓ Transferencia exitosa por $" + String.format("%,.0f", monto) + 
                        " a la cuenta " + destino + "\n\nGracias por utilizar nuestros servicios.",
                        "Operación exitosa", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
                // Si la transferencia falla, el backend ya mostró el mensaje de error
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void verMovimientos() {
        List<Movimiento> movs = transService.consultarMovimientos(session.getCuenta());
        
        if (movs.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay movimientos registrados.", 
                "Historial de movimientos", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== HISTORIAL DE MOVIMIENTOS ===\n\n");
        for (Movimiento m : movs) {
            sb.append(m).append("\n");
            sb.append("-".repeat(50)).append("\n");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 450));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Historial de movimientos", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cerrarSesion() {
        session.cerrarSesion();
        dispose();
    }
    
    private void volverAlLogin() {
        for (Window window : Window.getWindows()) {
            if (window instanceof LoginFrame) {
                window.setVisible(true);
                break;
            }
        }
    }
}