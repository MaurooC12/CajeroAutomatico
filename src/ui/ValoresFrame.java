package ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import model.*;
import service.*;
import java.util.*;

public class ValoresFrame extends JFrame {
    
    private Session session;
    private TransaccionService transService;
    private List<Movimiento> movimientos;
    private FileManager fm;
    private Cajero cajero;
    private JTextArea txtEstado;
    private JLabel lblTotalEfectivo;
    private JPanel panelEstado;
    
    public ValoresFrame(Session session, TransaccionService transService, 
                        List<Movimiento> movimientos, FileManager fm, Cajero cajero) {
        this.session = session;
        this.transService = transService;
        this.movimientos = movimientos;
        this.fm = fm;
        this.cajero = cajero;
        
        setTitle("Cajero Automático - Empresa de Valores");
        setSize(550, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        getContentPane().setBackground(new Color(240, 248, 255));
        
        initComponents();
        actualizarEstado();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                volverAlLogin();
            }
        });
    }
    
    private void initComponents() {
        // Panel superior
        JPanel panelInfo = new JPanel();
        panelInfo.setBackground(new Color(34, 139, 34));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = new JLabel("EMPRESA DE VALORES");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelInfo.add(lblTitulo);
        add(panelInfo, BorderLayout.NORTH);
        
        // Panel de estado del cajero
        JPanel panelEstadoContainer = new JPanel(new BorderLayout());
        panelEstadoContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        panelEstadoContainer.setBackground(new Color(240, 248, 255));
        
        panelEstado = new JPanel();
        panelEstado.setLayout(new BoxLayout(panelEstado, BoxLayout.Y_AXIS));
        panelEstado.setBackground(Color.WHITE);
        panelEstado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Total efectivo
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTotal.setBackground(Color.WHITE);
        JLabel lblTotalTexto = new JLabel("EFECTIVO DISPONIBLE");
        lblTotalTexto.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalTexto.setForeground(new Color(34, 139, 34));
        lblTotalEfectivo = new JLabel("$0");
        lblTotalEfectivo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalEfectivo.setForeground(new Color(0, 70, 140));
        panelTotal.add(lblTotalTexto);
        panelTotal.add(Box.createHorizontalStrut(10));
        panelTotal.add(lblTotalEfectivo);
        panelEstado.add(panelTotal);
        panelEstado.add(Box.createVerticalStrut(10));
        
        // Detalle de billetes - Tamaño reducido
        JPanel panelDetalle = new JPanel(new BorderLayout());
        panelDetalle.setBackground(Color.WHITE);
        panelDetalle.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "DETALLE DE BILLETES",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        
        txtEstado = new JTextArea(4, 20);  // Reducido de 6 a 4 filas
        txtEstado.setEditable(false);
        txtEstado.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtEstado.setBackground(Color.WHITE);
        txtEstado.setForeground(new Color(50, 50, 50));
        txtEstado.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        JScrollPane scrollEstado = new JScrollPane(txtEstado);
        scrollEstado.setBorder(null);
        scrollEstado.setPreferredSize(new Dimension(450, 100));  // Reducido de 500x150 a 450x100
        scrollEstado.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelDetalle.add(scrollEstado, BorderLayout.CENTER);
        
        panelEstado.add(panelDetalle);
        
        panelEstadoContainer.add(panelEstado, BorderLayout.CENTER);
        add(panelEstadoContainer, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 20, 20));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40));
        panelBotones.setBackground(new Color(240, 248, 255));
        
        JButton btnAbastecer = crearBoton("📦 ABASTECER CAJERO", new Color(240, 240, 240));
        JButton btnRetirarExcedentes = crearBoton("💰 RETIRAR EXCEDENTES", new Color(240, 240, 240));
        JButton btnHistorial = crearBoton("📋 VER HISTORIAL", new Color(240, 240, 240));
        JButton btnCerrar = crearBoton("🚪 CERRAR SESIÓN", new Color(240, 240, 240));
        
        btnAbastecer.addActionListener(e -> abastecer());
        btnRetirarExcedentes.addActionListener(e -> retirarExcedentes());
        btnHistorial.addActionListener(e -> verHistorial());
        btnCerrar.addActionListener(e -> cerrarSesion());
        
        panelBotones.add(btnAbastecer);
        panelBotones.add(btnRetirarExcedentes);
        panelBotones.add(btnHistorial);
        panelBotones.add(btnCerrar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 55));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        return btn;
    }
    
    private void actualizarEstado() {
        // Obtener las denominaciones directamente del cajero
        Map<Integer, Integer> denominaciones = cajero.getDenominaciones();
        long total = 0;
        StringBuilder sb = new StringBuilder();
        
        // Ordenar de mayor a menor para mejor presentación
        List<Integer> valores = new ArrayList<>(denominaciones.keySet());
        Collections.sort(valores, Collections.reverseOrder());
        
        for (int valor : valores) {
            int cantidad = denominaciones.get(valor);
            total += (long) valor * cantidad;
            // Formato más compacto para ahorrar espacio
            sb.append(String.format("  $%,d : %,d billetes\n", valor, cantidad));
        }
        
        txtEstado.setText(sb.toString());
        lblTotalEfectivo.setText("$" + String.format("%,d", total));
    }
    
    private void abastecer() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        int[] denoms = {100000, 50000, 20000, 10000};
        JTextField[] campos = new JTextField[4];
        
        for (int i = 0; i < denoms.length; i++) {
            panel.add(new JLabel("Billetes de $" + denoms[i] + ":"));
            campos[i] = new JTextField(10);
            panel.add(campos[i]);
        }
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Abastecer cajero", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            Map<Integer, Integer> billetes = new LinkedHashMap<>();
            int total = 0;
            
            for (int i = 0; i < denoms.length; i++) {
                try {
                    int cant = Integer.parseInt(campos[i].getText().trim());
                    if (cant < 0) cant = 0;
                    billetes.put(denoms[i], cant);
                    total += denoms[i] * cant;
                } catch (NumberFormatException e) {
                    billetes.put(denoms[i], 0);
                }
            }
            
            if (total > 0 && transService.abastecer(billetes)) {
                movimientos.add(new Movimiento("M" + (movimientos.size() + 1), 
                    "ABASTECIMIENTO", "", "", total, "Abastecimiento por Valores"));
                fm.guardarMovimientos(movimientos);
                actualizarEstado();
                JOptionPane.showMessageDialog(this, 
                    "✓ Abastecimiento de $" + String.format("%,d", total) + " registrado.\n\nOperación completada.",
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo abastecer. Verifique las denominaciones o capacidad.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void retirarExcedentes() {
        String input = JOptionPane.showInputDialog(this, 
            "Ingrese monto a retirar:\n(Múltiplo de 10,000)", 
            "Retiro de excedentes", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (input == null) return;
        
        try {
            int monto = Integer.parseInt(input);
            if (monto <= 0 || monto % 10000 != 0) {
                JOptionPane.showMessageDialog(this, 
                    "Monto inválido. Debe ser múltiplo de 10,000.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (transService.retirarExcedentes(monto)) {
                movimientos.add(new Movimiento("M" + (movimientos.size() + 1), 
                    "RETIRO_EXCEDENTES", "", "", monto, "Retiro de excedentes"));
                fm.guardarMovimientos(movimientos);
                actualizarEstado();
                JOptionPane.showMessageDialog(this, 
                    "✓ Retiro de excedentes por $" + String.format("%,d", monto) + " registrado.\n\nOperación completada.",
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo retirar. Verifique el monto o disponibilidad.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void verHistorial() {
        StringBuilder sb = new StringBuilder();
        boolean hay = false;
        
        for (Movimiento m : movimientos) {
            if (m.getTipo().equals("ABASTECIMIENTO") || m.getTipo().equals("RETIRO_EXCEDENTES")) {
                sb.append(m).append("\n");
                hay = true;
            }
        }
        
        if (!hay) {
            JOptionPane.showMessageDialog(this, 
                "No hay registros de abastecimientos.", 
                "Historial", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Historial de abastecimientos", 
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