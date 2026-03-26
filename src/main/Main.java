package main;

import model.*;
import service.*;
import ui.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class Main {
    
    private static FileManager fm = new FileManager("data");
    
    // Datos en memoria
    private static List<Usuario> usuarios = new ArrayList<>();
    private static List<Cuenta> cuentas = new ArrayList<>();
    private static List<Tarjeta> tarjetas = new ArrayList<>();
    private static List<Movimiento> movimientos = new ArrayList<>();
    
    // Servicios
    private static Cajero cajero;
    private static AutenticacionService authService = new AutenticacionService();
    private static TransaccionService transService;
    
    // Contadores
    private static int contadorUsuarios = 1;
    private static int contadorCuentas = 1001;
    private static int contadorTarjetasCliente = 1;
    
    public static void main(String[] args) {
        // Configurar look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("=== CAJERO AUTOMATICO ===\n");
        
        new File("data").mkdirs();
        cargarDatos();
        transService = new TransaccionService(cajero, movimientos, cuentas);
        
        // Crear datos por defecto
        boolean necesitaGuardar = false;
        
        if (tarjetas.stream().noneMatch(t -> t.getNumero().equals("12340001"))) {
            tarjetas.add(new Tarjeta("12340001", "ADMIN", "1234", "ADMIN"));
            necesitaGuardar = true;
        }
        if (tarjetas.stream().noneMatch(t -> t.getNumero().equals("99990001"))) {
            tarjetas.add(new Tarjeta("99990001", "VALORES", "9999", "VALORES"));
            necesitaGuardar = true;
        }
        if (usuarios.stream().noneMatch(u -> u.getId().equals("ADMIN"))) {
            usuarios.add(new Usuario("ADMIN", "Administrador", "000", "000", ""));
            necesitaGuardar = true;
        }
        if (usuarios.stream().noneMatch(u -> u.getId().equals("VALORES"))) {
            usuarios.add(new Usuario("VALORES", "Empresa Valores", "000", "000", ""));
            necesitaGuardar = true;
        }

        for (Cuenta cuenta : cuentas) {
            String idUsuarioCuenta = cuenta.getIdUsuario();
            if (idUsuarioCuenta != null && idUsuarioCuenta.matches("C\\d+")
                && usuarios.stream().noneMatch(u -> u.getId().equalsIgnoreCase(idUsuarioCuenta))) {
                usuarios.add(new Usuario(idUsuarioCuenta, "Cliente " + idUsuarioCuenta, "", "", ""));
                necesitaGuardar = true;
            }
        }
        
        if (necesitaGuardar) {
            guardarDatos();
        }
        
        // Iniciar interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(authService, transService, tarjetas, cuentas, 
                                                    cajero, movimientos, fm, usuarios);
            loginFrame.setVisible(true);
        });
    }
    
    private static void cargarDatos() {
        usuarios = fm.cargarUsuarios();
        cuentas = fm.cargarCuentas();
        tarjetas = fm.cargarTarjetas();
        movimientos = fm.cargarMovimientos();
        
        Map<Integer, Integer> estadoCajero = fm.cargarCajero();
        cajero = new Cajero(estadoCajero);
        
        // Actualizar contadores
        for (Usuario u : usuarios) {
            if (u.getId().matches("C\\d+")) {
                int num = Integer.parseInt(u.getId().substring(1));
                if (num >= contadorUsuarios) contadorUsuarios = num + 1;
            }
        }
        for (Cuenta c : cuentas) {
            int num = Integer.parseInt(c.getNumero());
            if (num >= contadorCuentas) contadorCuentas = num + 1;
        }
        for (Tarjeta t : tarjetas) {
            if (t.getNumero().matches("0000\\d+") && !t.getRol().equals("ADMIN") && !t.getRol().equals("VALORES")) {
                int num = Integer.parseInt(t.getNumero());
                if (num >= contadorTarjetasCliente) contadorTarjetasCliente = num + 1;
            }
        }
    }
    
    private static void guardarDatos() {
        fm.guardarUsuarios(usuarios);
        fm.guardarCuentas(cuentas);
        fm.guardarTarjetas(tarjetas);
        fm.guardarMovimientos(movimientos);
        fm.guardarCajero(cajero.getDenominaciones());
    }
}