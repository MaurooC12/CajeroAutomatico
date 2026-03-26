package ui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import model.*;
import service.*;
import java.util.*;

public class TarjetaFrame extends JFrame {
    
    private JTextField txtTarjeta;
    private JTextField txtPin;
    private JButton btnAceptar, btnCancelar;
    
    private AutenticacionService authService;
    private TransaccionService transService;
    private List<Tarjeta> tarjetas;
    private List<Cuenta> cuentas;
    private List<Usuario> usuarios;
    private Cajero cajero;
    private List<Movimiento> movimientos;
    private FileManager fm;
    private LoginFrame loginFrame;
    
    public TarjetaFrame(AutenticacionService authService, TransaccionService transService,
                        List<Tarjeta> tarjetas, List<Cuenta> cuentas, Cajero cajero,
                        List<Movimiento> movimientos, FileManager fm, List<Usuario> usuarios,
                        LoginFrame loginFrame) {
        this.authService = authService;
        this.transService = transService;
        this.tarjetas = tarjetas;
        this.cuentas = cuentas;
        this.cajero = cajero;
        this.movimientos = movimientos;
        this.fm = fm;
        this.usuarios = usuarios;
        this.loginFrame = loginFrame;
        
        setTitle("Insertar Tarjeta");
        setSize(450, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);
        
        getContentPane().setBackground(Color.WHITE);
        
        initComponents();
        
        SwingUtilities.invokeLater(() -> txtTarjeta.requestFocusInWindow());
    }
    
    private void initComponents() {
        // Panel superior
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(0, 70, 140));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        
        JLabel lblTitulo = new JLabel("INSERTAR TARJETA");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con formulario
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo tarjeta
        JLabel lblTarjeta = new JLabel("Número de tarjeta:");
        lblTarjeta.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panelCentral.add(lblTarjeta, gbc);
        
        txtTarjeta = new JTextField(18);
        txtTarjeta.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTarjeta.setOpaque(true);
        txtTarjeta.setBackground(Color.WHITE);
        txtTarjeta.setForeground(Color.BLACK);
        txtTarjeta.setDisabledTextColor(Color.BLACK);
        txtTarjeta.setCaretColor(Color.BLACK);
        txtTarjeta.setSelectionColor(new Color(0, 102, 204));
        txtTarjeta.setSelectedTextColor(Color.WHITE);
        txtTarjeta.setHorizontalAlignment(SwingConstants.LEFT);
        txtTarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panelCentral.add(txtTarjeta, gbc);
        
        // Campo PIN
        JLabel lblPin = new JLabel("PIN:");
        lblPin.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panelCentral.add(lblPin, gbc);
        
        txtPin = new JTextField(18);
        txtPin.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPin.setOpaque(true);
        txtPin.setBackground(Color.WHITE);
        txtPin.setForeground(Color.BLACK);
        txtPin.setDisabledTextColor(Color.BLACK);
        txtPin.setCaretColor(Color.BLACK);
        txtPin.setSelectionColor(new Color(0, 102, 204));
        txtPin.setSelectedTextColor(Color.WHITE);
        txtPin.setHorizontalAlignment(SwingConstants.LEFT);
        txtPin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panelCentral.add(txtPin, gbc);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 10, 30, 10));
        
        btnAceptar = new JButton("Aceptar");
        btnAceptar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAceptar.setBackground(new Color(240, 240, 240));
        btnAceptar.setForeground(Color.BLACK);
        btnAceptar.setFocusPainted(false);
        btnAceptar.setPreferredSize(new Dimension(120, 45));
        btnAceptar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAceptar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancelar.setBackground(new Color(240, 240, 240));
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setPreferredSize(new Dimension(120, 45));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        
        btnAceptar.addActionListener(this::autenticar);
        btnCancelar.addActionListener(e -> {
            dispose();
            loginFrame.setVisible(true);
        });
        
        getRootPane().setDefaultButton(btnAceptar);
        
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void autenticar(ActionEvent e) {
        String numTarjeta = txtTarjeta.getText().trim();
        String pin = txtPin.getText().trim();
        
        if (numTarjeta.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese número de tarjeta y PIN.", 
                "Campos incompletos", 
                JOptionPane.WARNING_MESSAGE);
            txtTarjeta.requestFocusInWindow();
            return;
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        Session session = authService.autenticar(numTarjeta, pin, tarjetas, cuentas);
        
        setCursor(Cursor.getDefaultCursor());
        
        if (session != null) {
            this.dispose();
            
            if (session.getRol().equals("ADMIN")) {
                new AdminFrame(session, cuentas, tarjetas, usuarios, fm).setVisible(true);
            } else if (session.getRol().equals("VALORES")) {
                new ValoresFrame(session, transService, movimientos, fm, cajero).setVisible(true);
            } else {
                new ClienteFrame(session, transService).setVisible(true);
            }
        } else {
            // Limpiar campos y mantener en el mismo frame
            txtTarjeta.setText("");
            txtPin.setText("");
            txtTarjeta.requestFocusInWindow();
        }
    }
}