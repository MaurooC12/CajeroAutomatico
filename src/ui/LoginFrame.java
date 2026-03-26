package ui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Component;  // ← NUEVO
import java.awt.event.ActionEvent;
import model.*;
import service.*;
import java.util.*;

public class LoginFrame extends JFrame {
    
    private JButton btnConsignar, btnInsertarTarjeta, btnSalir;
    
    private AutenticacionService authService;
    private TransaccionService transService;
    private List<Tarjeta> tarjetas;
    private List<Cuenta> cuentas;
    private List<Usuario> usuarios;
    private Cajero cajero;
    private List<Movimiento> movimientos;
    private FileManager fm;
    
    public LoginFrame(AutenticacionService authService, TransaccionService transService,
                      List<Tarjeta> tarjetas, List<Cuenta> cuentas, Cajero cajero,
                      List<Movimiento> movimientos, FileManager fm, List<Usuario> usuarios) {
        this.authService = authService;
        this.transService = transService;
        this.tarjetas = tarjetas;
        this.cuentas = cuentas;
        this.cajero = cajero;
        this.movimientos = movimientos;
        this.fm = fm;
        this.usuarios = usuarios;
        
        setTitle("Cajero Automático");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);
        
        getContentPane().setBackground(new Color(240, 248, 255));
        
        initComponents();
    }
    
    private void initComponents() {
        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(0, 70, 140));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(35, 20, 35, 20));
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        
        JLabel lblIcono = new JLabel("🏧");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 55));
        lblIcono.setForeground(Color.WHITE);
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTitulo = new JLabel("CAJERO AUTOMÁTICO");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel("Banco Digital");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(200, 220, 240));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelTitulo.add(lblIcono);
        panelTitulo.add(Box.createVerticalStrut(8));
        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(4));
        panelTitulo.add(lblSubtitulo);
        
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con botones
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panelBotones.setLayout(new GridLayout(3, 1, 0, 20));
        
        btnConsignar = crearBoton("CONSIGNAR DINERO", new Color(34, 139, 34));
        btnInsertarTarjeta = crearBoton("INSERTAR TARJETA", new Color(0, 102, 204));
        btnSalir = crearBoton("TERMINAR OPERACIÓN", new Color(128, 128, 128));
        
        btnConsignar.addActionListener(e -> consignarDinero());
        btnInsertarTarjeta.addActionListener(e -> insertarTarjeta());
        btnSalir.addActionListener(e -> System.exit(0));
        
        panelBotones.add(btnConsignar);
        panelBotones.add(btnInsertarTarjeta);
        panelBotones.add(btnSalir);
        
        add(panelBotones, BorderLayout.CENTER);
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);  // Letras negras
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        return btn;
    }
    
    private void consignarDinero() {
        new ConsignacionFrame(transService, cuentas, usuarios).setVisible(true);
    }
    
    private void insertarTarjeta() {
        this.setVisible(false);
        new TarjetaFrame(authService, transService, tarjetas, cuentas, cajero, movimientos, fm, usuarios, this).setVisible(true);
    }
    
    public void mostrar() {
        this.setVisible(true);
    }
}