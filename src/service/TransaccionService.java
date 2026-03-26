package service;

import model.Cuenta;
import model.Movimiento;
import model.Cajero;
import model.Session;
import javax.swing.JOptionPane;
import java.util.*;

public class TransaccionService {
    
    private Cajero cajero;
    private List<Movimiento> movimientos;
    private List<Cuenta> cuentas;
    private int contadorMovimientos;
    
    public TransaccionService(Cajero cajero, List<Movimiento> movimientos, List<Cuenta> cuentas) {
        this.cajero = cajero;
        this.movimientos = movimientos;
        this.cuentas = cuentas;
        this.contadorMovimientos = movimientos.size() + 1;
    }
    
    public double consultarSaldo(Cuenta cuenta) {
        return cuenta.getSaldo();
    }
    
    public boolean retirar(Session session, int monto) {
        if (session == null || !session.isActiva()) {
            System.out.println("Sesion no activa.");
            JOptionPane.showMessageDialog(null, "Sesión no activa.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        Cuenta cuenta = session.getCuenta();
        
        if (monto <= 0) {
            System.out.println("Monto invalido.");
            JOptionPane.showMessageDialog(null, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (monto % 10000 != 0) {
            System.out.println("Monto invalido. Debe ser multiplo de 10000.");
            JOptionPane.showMessageDialog(null, "El monto debe ser múltiplo de 10,000.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (monto > cuenta.getSaldo()) {
            System.out.println("Saldo insuficiente.");
            JOptionPane.showMessageDialog(null, "Saldo insuficiente.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (monto > cuenta.getLimiteDiario()) {
            System.out.println("Excede limite diario de retiro.");
            JOptionPane.showMessageDialog(null, "Excede el límite diario de retiro.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (monto > cajero.getTotalEfectivo()) {
            System.out.println("Cajero no tiene suficiente efectivo disponible.");
            JOptionPane.showMessageDialog(null, "Cajero no tiene suficiente efectivo disponible.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (cajero.calcularRetiro(monto) == null) {
            System.out.println("No es posible dispensar ese monto con las denominaciones disponibles.");
            JOptionPane.showMessageDialog(null, "No es posible dispensar ese monto con las denominaciones disponibles.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!cajero.retirar(monto)) {
            System.out.println("Error en el retiro del cajero.");
            JOptionPane.showMessageDialog(null, "Error en el retiro del cajero.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        cuenta.retirar(monto);
        movimientos.add(new Movimiento("M" + contadorMovimientos++, "RETIRO", 
                        cuenta.getNumero(), "", monto, "Retiro en cajero"));
        System.out.println("Retiro exitoso. Nuevo saldo: $" + cuenta.getSaldo());
        return true;
    }
    
    public boolean consignar(String numCuenta, Map<Integer, Integer> billetes) {
        Cuenta cuenta = buscarCuenta(numCuenta);
        
        if (cuenta == null) {
            System.out.println("Error: Cuenta no existe.");
            JOptionPane.showMessageDialog(null, "Cuenta no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        int total = billetes.entrySet().stream().mapToInt(e -> e.getKey() * e.getValue()).sum();
        if (total <= 0) {
            System.out.println("Error: No ingreso ningun billete valido.");
            JOptionPane.showMessageDialog(null, "No ingresó ningún billete válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!cajero.consignar(billetes)) {
            System.out.println("Error: El cajero se encuentra a su capacidad maxima.");
            JOptionPane.showMessageDialog(null, "El cajero se encuentra a su capacidad máxima. Vuelva después.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        cuenta.depositar(total);
        movimientos.add(new Movimiento("M" + contadorMovimientos++, "CONSIGNACION", 
                        "", numCuenta, total, "Consignacion en cajero"));
        
        System.out.println("\n=== CONSIGNACION EXITOSA ===");
        System.out.println("Cuenta destino: " + numCuenta);
        System.out.println("Monto consignado: $" + total);
        return true;
    }
    
    public boolean transferir(Session session, String cuentaDestino, double monto) {
        if (session == null || !session.isActiva()) {
            System.out.println("Sesion no activa.");
            JOptionPane.showMessageDialog(null, "Sesión no activa.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        Cuenta cuentaOrigen = session.getCuenta();
        Cuenta destino = buscarCuenta(cuentaDestino);
        
        if (destino == null) {
            System.out.println("Cuenta destino no existe.");
            JOptionPane.showMessageDialog(null, "La cuenta destino no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (cuentaOrigen.getNumero().equals(cuentaDestino)) {
            System.out.println("No se puede transferir a la misma cuenta.");
            JOptionPane.showMessageDialog(null, "No se puede transferir a la misma cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (monto <= 0) {
            System.out.println("Monto invalido.");
            JOptionPane.showMessageDialog(null, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (monto > cuentaOrigen.getSaldo()) {
            System.out.println("Saldo insuficiente.");
            JOptionPane.showMessageDialog(null, "Saldo insuficiente.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Realizar la transferencia
        cuentaOrigen.retirar(monto);
        destino.depositar(monto);
        
        // Registrar movimiento
        movimientos.add(new Movimiento("M" + contadorMovimientos++, "TRANSFERENCIA", 
                        cuentaOrigen.getNumero(), cuentaDestino, monto, 
                        "Transferencia a " + cuentaDestino));
        
        System.out.println("Transferencia exitosa. Nuevo saldo: $" + cuentaOrigen.getSaldo());
        return true;
    }
    
    public List<Movimiento> consultarMovimientos(Cuenta cuenta) {
        List<Movimiento> resultado = new ArrayList<>();
        for (Movimiento m : movimientos) {
            if (m.getCuentaOrigen().equals(cuenta.getNumero()) || m.getCuentaDestino().equals(cuenta.getNumero())) {
                resultado.add(m);
            }
        }
        return resultado;
    }

    public boolean existeCuenta(String numCuenta) {
        return buscarCuenta(numCuenta) != null;
    }
    
    private Cuenta buscarCuenta(String num) {
        for (Cuenta c : cuentas) {
            if (c.getNumero().equals(num)) return c;
        }
        return null;
    }
    
    public String mostrarEstadoCajero() {
        return cajero.mostrarEstado();
    }
    
    public boolean abastecer(Map<Integer, Integer> billetes) {
        return cajero.consignar(billetes);
    }
    
    public boolean retirarExcedentes(int monto) {
        return cajero.retirar(monto);
    }
}