package ui;

import javax.swing.*;
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

public class AdminFrame extends JFrame {
    
    private Session session;
    private List<Cuenta> cuentas;
    private List<Tarjeta> tarjetas;
    private List<Usuario> usuarios;
    private FileManager fm;
    private JTextArea txtSalida;
    
    public AdminFrame(Session session, List<Cuenta> cuentas, List<Tarjeta> tarjetas, 
                      List<Usuario> usuarios, FileManager fm) {
        this.session = session;
        this.cuentas = cuentas;
        this.tarjetas = tarjetas;
        this.usuarios = usuarios;
        this.fm = fm;
        
        setTitle("Cajero Automático - Administración Bancaria");
        setSize(750, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        getContentPane().setBackground(new Color(240, 248, 255));
        
        initComponents();
        
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
        panelInfo.setBackground(new Color(128, 0, 0));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = new JLabel("ADMINISTRACIÓN BANCARIA");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelInfo.add(lblTitulo);
        add(panelInfo, BorderLayout.NORTH);
        
        // Panel de botones principal
        JPanel panelBotones = new JPanel(new GridLayout(0, 2, 15, 15));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panelBotones.setBackground(new Color(240, 248, 255));
        
        agregarBoton(panelBotones, "➕ CREAR USUARIO", new Color(0, 102, 204), e -> crearUsuario());
        agregarBoton(panelBotones, "💰 CREAR CUENTA", new Color(0, 102, 204), e -> crearCuenta());
        agregarBoton(panelBotones, "💳 CREAR TARJETA", new Color(0, 102, 204), e -> crearTarjeta());
        agregarBoton(panelBotones, "📋 LISTAR TODO", new Color(34, 139, 34), e -> listarTodo());
        agregarBoton(panelBotones, "✏️ MODIFICAR LÍMITE", new Color(255, 140, 0), e -> modificarLimite());
        agregarBoton(panelBotones, "🔓 ACTIVAR/DESACTIVAR", new Color(255, 140, 0), e -> activarTarjeta());
        agregarBoton(panelBotones, "🗑️ ELIMINAR USUARIO", new Color(220, 20, 60), e -> eliminarUsuario());
        agregarBoton(panelBotones, "🗑️ ELIMINAR CUENTA", new Color(220, 20, 60), e -> eliminarCuenta());
        agregarBoton(panelBotones, "🗑️ ELIMINAR TARJETA", new Color(220, 20, 60), e -> eliminarTarjeta());
        agregarBoton(panelBotones, "🚪 CERRAR SESIÓN", new Color(128, 128, 128), e -> cerrarSesion());
        
        add(panelBotones, BorderLayout.CENTER);
        
        // Área de texto para mostrar resultados
        txtSalida = new JTextArea();
        txtSalida.setEditable(false);
        txtSalida.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtSalida.setBackground(new Color(250, 250, 250));
        txtSalida.setBorder(BorderFactory.createTitledBorder("Resultados"));
        JScrollPane scrollPane = new JScrollPane(txtSalida);
        scrollPane.setPreferredSize(new Dimension(650, 200));
        add(scrollPane, BorderLayout.SOUTH);
    }
    
    private void agregarBoton(JPanel panel, String texto, Color color, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);  // Letras negras
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        btn.addActionListener(listener);
        panel.add(btn);
    }
    
    private void mostrarResultado(String titulo, String contenido) {
        txtSalida.setText(contenido);
        txtSalida.setCaretPosition(0);
        JTextArea textArea = new JTextArea(contenido, 15, 50);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(this, scrollPane, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void listarTodo() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== USUARIOS ===\n");
        if (usuarios.isEmpty()) sb.append("(No hay usuarios)\n");
        for (Usuario u : usuarios) sb.append(u).append("\n");
        
        sb.append("\n=== CUENTAS ===\n");
        if (cuentas.isEmpty()) sb.append("(No hay cuentas)\n");
        for (Cuenta c : cuentas) sb.append(c).append("\n");
        
        sb.append("\n=== TARJETAS ===\n");
        if (tarjetas.isEmpty()) sb.append("(No hay tarjetas)\n");
        for (Tarjeta t : tarjetas) sb.append(t).append("\n");
        
        mostrarResultado("Listado General", sb.toString());
    }
    
    private void crearUsuario() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 12, 12));
        JTextField txtNombre = new JTextField(15);
        JTextField txtDoc = new JTextField(15);
        JTextField txtTel = new JTextField(15);
        
        panel.add(new JLabel("Nombre completo:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Documento:"));
        panel.add(txtDoc);
        panel.add(new JLabel("Teléfono:"));
        panel.add(txtTel);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Crear usuario", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            int maxIdCliente = 0;
            for (Usuario usuario : usuarios) {
                String idUsuario = usuario.getId();
                if (idUsuario != null && idUsuario.matches("C\\d+")) {
                    int numero = Integer.parseInt(idUsuario.substring(1));
                    if (numero > maxIdCliente) {
                        maxIdCliente = numero;
                    }
                }
            }

            String id = "C" + String.format("%03d", maxIdCliente + 1);
            usuarios.add(new Usuario(id, txtNombre.getText(), txtDoc.getText(), txtTel.getText(), ""));
            fm.guardarUsuarios(usuarios);
            JOptionPane.showMessageDialog(this, "✓ Usuario creado con ID: " + id);
        }
    }
    
    private void crearCuenta() {
        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero cree un usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 12, 12));
        JComboBox<String> cbUsuarios = new JComboBox<>();
        for (Usuario u : usuarios) {
            cbUsuarios.addItem(u.getId() + " - " + u.getNombres());
        }
        JTextField txtSaldo = new JTextField(12);
        JTextField txtLimite = new JTextField(12);
        
        panel.add(new JLabel("Usuario:"));
        panel.add(cbUsuarios);
        panel.add(new JLabel("Saldo inicial: $"));
        panel.add(txtSaldo);
        panel.add(new JLabel("Límite diario: $"));
        panel.add(txtLimite);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Crear cuenta", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String idUsuario = ((String) cbUsuarios.getSelectedItem()).split(" - ")[0];
                String numCuenta = String.valueOf(1001 + cuentas.size());
                double saldo = Double.parseDouble(txtSaldo.getText().trim());
                double limite = Double.parseDouble(txtLimite.getText().trim());
                cuentas.add(new Cuenta(numCuenta, idUsuario, saldo, limite));
                fm.guardarCuentas(cuentas);
                JOptionPane.showMessageDialog(this, "✓ Cuenta creada con número: " + numCuenta);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Saldo o límite inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void crearTarjeta() {
        if (cuentas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero cree una cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        JComboBox<String> cbCuentas = new JComboBox<>();
        for (Cuenta c : cuentas) {
            cbCuentas.addItem(c.getNumero() + " - Usuario: " + c.getIdUsuario());
        }
        JTextField txtPin = new JTextField(10);
        
        panel.add(new JLabel("Cuenta:"));
        panel.add(cbCuentas);
        panel.add(new JLabel("PIN (4 dígitos):"));
        panel.add(txtPin);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Crear tarjeta", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String numCuenta = ((String) cbCuentas.getSelectedItem()).split(" - ")[0];
            String numTarjeta = String.format("%08d", tarjetas.size() + 1);
            String pin = txtPin.getText().trim();
            
            if (pin.length() != 4 || !pin.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "PIN inválido. Debe ser 4 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            tarjetas.add(new Tarjeta(numTarjeta, numCuenta, pin, "CLIENTE"));
            fm.guardarTarjetas(tarjetas);
            JOptionPane.showMessageDialog(this, "✓ Tarjeta creada con número: " + numTarjeta);
        }
    }
    
    private void modificarLimite() {
        if (cuentas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay cuentas.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JComboBox<String> cbCuentas = new JComboBox<>();
        for (Cuenta c : cuentas) {
            cbCuentas.addItem(c.getNumero() + " - Saldo: $" + String.format("%,.0f", c.getSaldo()));
        }
        
        int result = JOptionPane.showConfirmDialog(this, cbCuentas, "Seleccione cuenta:", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String seleccion = (String) cbCuentas.getSelectedItem();
            String numCuenta = seleccion.split(" - ")[0];
            Cuenta cuenta = buscarCuenta(numCuenta);
            
            String nuevoLimite = JOptionPane.showInputDialog(this, 
                "Límite actual: $" + String.format("%,.0f", cuenta.getLimiteDiario()) + "\nNuevo límite diario: $", 
                "Modificar límite", 
                JOptionPane.QUESTION_MESSAGE);
            
            if (nuevoLimite != null) {
                try {
                    double limite = Double.parseDouble(nuevoLimite);
                    cuenta.setLimiteDiario(limite);
                    fm.guardarCuentas(cuentas);
                    JOptionPane.showMessageDialog(this, "✓ Límite actualizado.");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void activarTarjeta() {
        if (tarjetas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay tarjetas.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JComboBox<String> cbTarjetas = new JComboBox<>();
        for (Tarjeta t : tarjetas) {
            cbTarjetas.addItem(t.getNumero() + " - " + (t.isActiva() ? "Activa" : "Bloqueada"));
        }
        
        int result = JOptionPane.showConfirmDialog(this, cbTarjetas, "Seleccione tarjeta:", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String seleccion = (String) cbTarjetas.getSelectedItem();
            String numTarjeta = seleccion.split(" - ")[0];
            Tarjeta tarjeta = buscarTarjeta(numTarjeta);
            
            tarjeta.setActiva(!tarjeta.isActiva());
            fm.guardarTarjetas(tarjetas);
            JOptionPane.showMessageDialog(this, "✓ Tarjeta " + (tarjeta.isActiva() ? "activada" : "desactivada"));
        }
    }
    
    private void eliminarUsuario() {
        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay usuarios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JComboBox<String> cbUsuarios = new JComboBox<>();
        for (Usuario u : usuarios) {
            if (!u.getId().equals("ADMIN") && !u.getId().equals("VALORES")) {
                cbUsuarios.addItem(u.getId() + " - " + u.getNombres());
            }
        }
        
        if (cbUsuarios.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay usuarios eliminables.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, cbUsuarios, "Seleccione usuario:", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String seleccion = (String) cbUsuarios.getSelectedItem();
            String idEliminar = seleccion.split(" - ")[0];
            
            List<Cuenta> cuentasUsuario = new ArrayList<>();
            for (Cuenta c : cuentas) {
                if (c.getIdUsuario().equals(idEliminar)) cuentasUsuario.add(c);
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Eliminar usuario y sus " + cuentasUsuario.size() + " cuenta(s)?", 
                "Confirmar", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                for (Cuenta c : cuentasUsuario) {
                    tarjetas.removeIf(t -> t.getNumCuenta().equals(c.getNumero()));
                }
                cuentas.removeAll(cuentasUsuario);
                usuarios.removeIf(u -> u.getId().equals(idEliminar));
                fm.guardarUsuarios(usuarios);
                fm.guardarCuentas(cuentas);
                fm.guardarTarjetas(tarjetas);
                JOptionPane.showMessageDialog(this, "✓ Usuario eliminado.");
            }
        }
    }
    
    private void eliminarCuenta() {
        if (cuentas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay cuentas.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JComboBox<String> cbCuentas = new JComboBox<>();
        for (Cuenta c : cuentas) {
            cbCuentas.addItem(c.getNumero() + " - Usuario: " + c.getIdUsuario());
        }
        
        int result = JOptionPane.showConfirmDialog(this, cbCuentas, "Seleccione cuenta:", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String seleccion = (String) cbCuentas.getSelectedItem();
            String numEliminar = seleccion.split(" - ")[0];
            
            boolean tieneTarjeta = tarjetas.stream().anyMatch(t -> t.getNumCuenta().equals(numEliminar));
            if (tieneTarjeta) {
                JOptionPane.showMessageDialog(this, "La cuenta tiene tarjeta asociada. Elimine la tarjeta primero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            cuentas.removeIf(c -> c.getNumero().equals(numEliminar));
            fm.guardarCuentas(cuentas);
            JOptionPane.showMessageDialog(this, "✓ Cuenta eliminada.");
        }
    }
    
    private void eliminarTarjeta() {
        if (tarjetas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay tarjetas.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JComboBox<String> cbTarjetas = new JComboBox<>();
        for (Tarjeta t : tarjetas) {
            if (!t.getNumero().equals("12340001") && !t.getNumero().equals("99990001")) {
                cbTarjetas.addItem(t.getNumero() + " - Cuenta: " + t.getNumCuenta());
            }
        }
        
        if (cbTarjetas.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay tarjetas eliminables.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, cbTarjetas, "Seleccione tarjeta:", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String seleccion = (String) cbTarjetas.getSelectedItem();
            String numEliminar = seleccion.split(" - ")[0];
            tarjetas.removeIf(t -> t.getNumero().equals(numEliminar));
            fm.guardarTarjetas(tarjetas);
            JOptionPane.showMessageDialog(this, "✓ Tarjeta eliminada.");
        }
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
    
    private Cuenta buscarCuenta(String num) {
        for (Cuenta c : cuentas) {
            if (c.getNumero().equals(num)) return c;
        }
        return null;
    }
    
    private Tarjeta buscarTarjeta(String num) {
        for (Tarjeta t : tarjetas) {
            if (t.getNumero().equals(num)) return t;
        }
        return null;
    }
}