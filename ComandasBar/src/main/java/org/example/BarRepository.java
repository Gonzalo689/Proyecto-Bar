package org.example;

import java.sql.*;
import java.util.ArrayList;


public class BarRepository {
    private Connection c ;
    private PreparedStatement s;

    public BarRepository(){
        this.c=null;
        this.s=null;
    }
    public void crearConexion()  {
        String db = "comandas_bar";
        String host = "localhost";
        String port = "3306";
        String urlConnection = "jdbc:mysql://"+host+":"+port+"/"+db;
        String user = "root";
        String pwd = "infobbdd";
        try{
            c = DriverManager.getConnection(urlConnection, user, pwd);
        }catch (Exception e) {
           e.printStackTrace();
        }
    }
    public Connection crearConexionJasper()  {
        String db = "comandas_bar";
        String host = "localhost";
        String port = "3306";
        String urlConnection = "jdbc:mysql://"+host+":"+port+"/"+db;
        String user = "root";
        String pwd = "infobbdd";
        try{
            return DriverManager.getConnection(urlConnection, user, pwd);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void cerrarConexion(){
        try {
            if (s!=null)
                s.close();
            if (c!=null)
                c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void crearTablas(){
        try {
            c.setAutoCommit(false);
            s = c.prepareStatement("CREATE TABLE IF NOT EXISTS mesas (" +
                    "id INT PRIMARY KEY," +
                    "nombre VARCHAR(50) NOT NULL)");
            s.execute();
            s = c.prepareStatement("CREATE TABLE IF NOT EXISTS mesas_comandas (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "mesa_id INT NOT NULL," +
                    "fecha_inic DATETIME DEFAULT NOW(),"+
                    "fecha_final DATETIME," +
                    "precioTotal DECIMAL(10,2) DEFAULT 0," +
                    "FOREIGN KEY (mesa_id) REFERENCES mesas(id))");
            s.execute();
            s = c.prepareStatement("CREATE TABLE IF NOT EXISTS productos (" +
                    "id INT PRIMARY KEY," +
                    "nombre VARCHAR(50) NOT NULL," +
                    "precio DECIMAL(10,2) NOT NULL)");
            s.execute();
            s = c.prepareStatement("CREATE TABLE IF NOT EXISTS comandas (" +
                    "mesa_comanda_id INT NOT NULL," +
                    "producto_id INT NOT NULL," +
                    "cant INT," +
                    "precio Double," +
                    "PRIMARY KEY (mesa_comanda_id, producto_id)," +
                    "FOREIGN KEY (mesa_comanda_id) REFERENCES mesas_comandas(id) ON DELETE CASCADE ," +
                    "FOREIGN KEY (producto_id) REFERENCES productos(id))");
            s.execute();
            c.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                c.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void insertarProducto(int id, String nombre, Double precio) {
        try {
            if (!existeProducto(nombre)) {
                c.setAutoCommit(false);
                String sql = "INSERT INTO productos (id, nombre, precio) VALUES (?, ?, ?)";
                s = c.prepareStatement(sql);
                s.setInt(1, id);
                s.setString(2, nombre);
                s.setDouble(3, precio);
                s.executeUpdate();
                c.commit();
            }
        } catch (SQLException e) {
            try {
                c.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }

    private boolean existeProducto(String nombre) throws SQLException {
        String sql = "SELECT * FROM productos WHERE nombre = ?";
        s = c.prepareStatement(sql);
        s.setString(1, nombre);
        return s.executeQuery().next();
    }
    public Producto recibirProducto(int id)  {
        String sql = "SELECT * FROM productos WHERE id = ?";
        try {
            s = c.prepareStatement(sql);
            s.setInt(1, id);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                int productId = rs.getInt("id");
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");

                return new Producto(productId, nombre, precio);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void insertarMesas(int mesa_id, String mesa_nombre) {
        try {
            if(!existeMesa(mesa_id)) {
                c.setAutoCommit(false);
                String sql = "INSERT INTO mesas (id,nombre) VALUES (?, ?)";
                s = c.prepareStatement(sql);
                s.setInt(1, mesa_id);
                s.setString(2, mesa_nombre);
                s.executeUpdate();
                c.commit();
            }
        } catch (SQLException e) {
            try {
                c.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }
    private boolean existeMesa(int mesa_id) throws SQLException {
        String sql = "SELECT * FROM mesas WHERE id = ?";
        s = c.prepareStatement(sql);
        s.setInt(1, mesa_id);
        return s.executeQuery().next();
    }
    public ArrayList<Mesa> recibirListaMesa(){
        ArrayList<Mesa> mesas = new ArrayList<>();
        String sql = "SELECT * FROM mesas";
        try {
            s = c.prepareStatement(sql);
            ResultSet resultSet = s.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                Mesa mesa = new Mesa(id, nombre);
                mesas.add(mesa);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return mesas;
    }

    public void iniciarMesa_Comanda(Mesa mesa){
        try {
            if (!existeMesaComandaActiva(mesa.getId())) {
                c.setAutoCommit(false);
                String sql = "INSERT INTO mesas_comandas (mesa_id, fecha_final) VALUES (?, ?)";
                s = c.prepareStatement(sql);
                s.setInt(1, mesa.getId());
                s.setNull(2, Types.TIMESTAMP);
                s.executeUpdate();
                c.commit();
            }
        } catch (SQLException e) {
            try {
                c.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }

    private boolean existeMesaComandaActiva(int mesaId) throws SQLException {
        String sql = "SELECT * FROM mesas_comandas WHERE mesa_id = ? AND fecha_final IS NULL";
        s = c.prepareStatement(sql);
        s.setInt(1, mesaId);
        return s.executeQuery().next();
    }
    public void borrarMesaComandaSinDatos(){
        try {
            c.setAutoCommit(false);
            String sql = "DELETE FROM mesas_comandas WHERE fecha_final IS NULL OR precioTotal = 0";
            s = c.prepareStatement(sql);
            s.executeUpdate();
            c.commit();
        }catch (Exception e){
            try {
                c.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
    public void actualizarMesaComanda(int mesa_id, double preciototal) {
        try {
            c.setAutoCommit(false);
            String sql = "UPDATE mesas_comandas SET preciototal = ?, fecha_final = NOW() WHERE mesa_id = ? and fecha_final is null";

            s = c.prepareStatement(sql);

            s.setDouble(1, preciototal);
            s.setInt(2, mesa_id);

            s.executeUpdate();
            c.commit();
        } catch (SQLException e) {
            try {
                c.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
    public int id_mesa_comanda(int mesa_id) {
        int idMesaComanda = -1;
        try {
            String sql = "SELECT id FROM mesas_comandas WHERE mesa_id = ? AND fecha_final IS NULL";
            s = c.prepareStatement(sql);

            s.setInt(1, mesa_id);

            ResultSet rs = s.executeQuery();

            if (rs.next()) {
                idMesaComanda = rs.getInt("id");
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idMesaComanda;
    }

    public void insertarComanda(int mesaComandaId, int productoId, double precio) {
        try {
            c.setAutoCommit(false);
            String sqlInsert = "INSERT INTO comandas (mesa_comanda_id, producto_id, cant, precio) VALUES (?, ?, 1, ?)";
            String sqlUpdate = "UPDATE comandas SET cant = cant + 1, precio = precio + ? WHERE mesa_comanda_id = ? AND producto_id = ?";

            // Verificar si la comanda ya existe
            PreparedStatement selectStatement = c.prepareStatement("SELECT * FROM comandas WHERE mesa_comanda_id = ? AND producto_id = ?");
            selectStatement.setInt(1, mesaComandaId);
            selectStatement.setInt(2, productoId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Si la comanda ya existe, actualizar la cantidad y el precio total
                PreparedStatement updateStatement = c.prepareStatement(sqlUpdate);
                updateStatement.setDouble(1, precio);
                updateStatement.setInt(2, mesaComandaId);
                updateStatement.setInt(3, productoId);
                updateStatement.executeUpdate();
            } else {
                // Si la comanda no existe, insertar una nueva
                PreparedStatement insertStatement = c.prepareStatement(sqlInsert);
                insertStatement.setInt(1, mesaComandaId);
                insertStatement.setInt(2, productoId);
                insertStatement.setDouble(3, precio);
                insertStatement.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            try {
                c.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }

}
