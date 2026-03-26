package ui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Cursor;
import model.*;
import service.*;
import java.util.*;

public class ConsignacionFrame extends JFrame {
    
    private TransaccionService transService;
    private List<Cuenta> cuentas;
    private List<Usuario> usuarios;
    private JTextField txtCuenta;
    private JTextField[] txtBilletes;
    private int[] denoms = {100000, 50000, 20000, 10000};
    private JButton btnBuscar, btnConfirmar;
    private JLabel lblNombreUsuario;
    private Cuenta cuentaEncontrada = null;
    
    public ConsignacionFrame(TransaccionService transService, List<Cuenta> cuentas, List<Usuario> usuarios) {
        this.transService = transService;
        this.cuentas = cuentas;
        this.usuarios = usuarios;
        
        setTitle("Consignación sin tarjeta");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        
        getContentPane().setBackground(Color.WHITE);
        
        initComponents();
    }
    
    private void initComponents() {
        // Panel superior
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(34, 139, 34));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = new JLabel("CONSIGNACIÓN SIN TARJETA");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Buscar cuenta
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblCuenta = new JLabel("Número de cuenta:");
        lblCuenta.setFont(new Font("Arial", Font.BOLD, 13));
        panelCentral.add(lblCuenta, gbc);
        
        txtCuenta = new JTextField(15);
        txtCuenta.setFont(new Font("Arial", Font.PLAIN, 14));
        txtCuenta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        gbc.gridx = 1;
        panelCentral.add(txtCuenta, gbc);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.setBackground(new Color(240, 240, 240));
        btnBuscar.setForeground(Color.BLACK);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        btnBuscar.addActionListener(e -> buscarCuenta());
        gbc.gridx = 2;
        panelCentral.add(btnBuscar, gbc);
        
        // Mostrar nombre del titular
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        lblNombreUsuario = new JLabel("");
        lblNombreUsuario.setFont(new Font("Arial", Font.ITALIC, 12));
        lblNombreUsuario.setForeground(new Color(34, 139, 34));
        panelCentral.add(lblNombreUsuario, gbc);
        
        // Billetes
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        JLabel lblBilletes = new JLabel("Ingrese los billetes a consignar:");
        lblBilletes.setFont(new Font("Arial", Font.BOLD, 13));
        panelCentral.add(lblBilletes, gbc);
        
        JPanel panelBilletes = new JPanel(new GridLayout(4, 2, 10, 10));
        panelBilletes.setBackground(Color.WHITE);
        txtBilletes = new JTextField[4];
        
        for (int i = 0; i < denoms.length; i++) {
            JLabel lblDenom = new JLabel("$" + String.format("%,d", denoms[i]) + ":");
            lblDenom.setFont(new Font("Arial", Font.PLAIN, 12));
            panelBilletes.add(lblDenom);
            txtBilletes[i] = new JTextField(10);
            txtBilletes[i].setHorizontalAlignment(JTextField.RIGHT);
            txtBilletes[i].setEnabled(false);
            txtBilletes[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            panelBilletes.add(txtBilletes[i]);
        }
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        panelCentral.add(panelBilletes, gbc);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 10, 25, 10));
        
        btnConfirmar = new JButton("Confirmar consignación");
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirmar.setBackground(new Color(240, 240, 240));
        btnConfirmar.setForeground(Color.BLACK);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setEnabled(false);
        btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        btnConfirmar.addActionListener(e -> consignar());
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancelar.setBackground(new Color(240, 240, 240));
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void buscarCuenta() {
        String numCuenta = txtCuenta.getText().trim();
        if (numCuenta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese número de cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Buscar cuenta real en la lista de cuentas
        cuentaEncontrada = null;
        Usuario titular = null;
        for (Cuenta c : cuentas) {
            if (c.getNumero().equals(numCuenta)) {
                cuentaEncontrada = c;
                titular = buscarTitular(c);
                break;
            }
        }
        
        if (cuentaEncontrada == null) {
            JOptionPane.showMessageDialog(this, "Cuenta no encontrada. Verifique el número.", "Error", JOptionPane.ERROR_MESSAGE);
            txtCuenta.setText("");
            txtCuenta.requestFocusInWindow();
            return;
        }
        
        // Mostrar información de la cuenta (nombre real del titular)
        String nombreTitular;
        if (titular != null && titular.getNombres() != null && !titular.getNombres().trim().isEmpty()) {
            nombreTitular = titular.getNombres();
        } else {
            nombreTitular = cuentaEncontrada.getIdUsuario();
        }
        lblNombreUsuario.setText("Titular: " + nombreTitular);
        
        for (JTextField tf : txtBilletes) {
            tf.setEnabled(true);
            tf.setText("");
        }
        btnConfirmar.setEnabled(true);
        txtCuenta.setEnabled(false);
        btnBuscar.setEnabled(false);
    }

    private Usuario buscarTitular(Cuenta cuenta) {
        String idCuenta = cuenta.getIdUsuario() == null ? "" : cuenta.getIdUsuario().trim();

        for (Usuario usuario : usuarios) {
            String idUsuario = usuario.getId() == null ? "" : usuario.getId().trim();
            if (idUsuario.equalsIgnoreCase(idCuenta)) {
                return usuario;
            }
        }

        Integer idNumericoCuenta = extraerNumeroIdCliente(idCuenta);
        if (idNumericoCuenta != null) {
            for (Usuario usuario : usuarios) {
                String idUsuario = usuario.getId() == null ? "" : usuario.getId().trim();
                Integer idNumericoUsuario = extraerNumeroIdCliente(idUsuario);
                if (idNumericoUsuario != null && idNumericoUsuario.equals(idNumericoCuenta)) {
                    return usuario;
                }
            }
        }

        return null;
    }

    private Integer extraerNumeroIdCliente(String id) {
        if (id == null) {
            return null;
        }
        String limpio = id.trim().toUpperCase();
        if (!limpio.startsWith("C")) {
            return null;
        }

        String parteNumerica = limpio.substring(1).replaceAll("^0+", "");
        if (parteNumerica.isEmpty()) {
            parteNumerica = "0";
        }

        try {
            return Integer.parseInt(parteNumerica);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private void consignar() {
        String numCuenta = txtCuenta.getText().trim();
        
        Map<Integer, Integer> billetes = new LinkedHashMap<>();
        int total = 0;
        
        for (int i = 0; i < denoms.length; i++) {
            try {
                int cant = Integer.parseInt(txtBilletes[i].getText().trim());
                if (cant < 0) cant = 0;
                billetes.put(denoms[i], cant);
                total += denoms[i] * cant;
            } catch (NumberFormatException e) {
                billetes.put(denoms[i], 0);
            }
        }
        
        if (total == 0) {
            JOptionPane.showMessageDialog(this, "Ingrese al menos un billete válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Monto total a consignar: $" + String.format("%,d", total) + "\n¿Confirmar?", 
            "Confirmar consignación", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (transService.consignar(numCuenta, billetes)) {
                JOptionPane.showMessageDialog(this, 
                    "✓ Consignación exitosa\n\nCuenta: " + numCuenta + "\nMonto: $" + String.format("%,d", total),
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        }
    }
}