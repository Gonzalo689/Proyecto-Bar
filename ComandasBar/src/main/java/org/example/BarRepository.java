package org.example;

import java.sql.*;
import java.util.*;

/**
 * Clase de Base de datos del bar el cual estara la conexión, las tablas y todas las querys usadas durante el uso de la
 * aplicación
 */
public class BarRepository {
    private Connection c ;
    private PreparedStatement s;

    public BarRepository(){
        this.c=null;
        this.s=null;
    }

    /**
     * Método para crear la conexion con la base de datos que en este caso será mysql
     */
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

    /**
     * Método para crear una conexión donde el JasperReport se pueda conectar
     * @return devuelve la conexion que se necesite
     */
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

    /**
     * Cierra la conexión de base de datos
     */
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

    /**
     * Crea todas las tablas que se necesitan en la base de datos si no existen
     */
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

    /**
     * Metodo que insertara un producto en la base de datos
     * @param id ID del droducto
     * @param nombre nombre del producto
     * @param precio Precio que tendra el prdducto
     */
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

    /**
     * Método que comprueba si existe un producto mediante su nombre
     * @param nombre nombre del producto
     * @return devolvera un boolean si existe o no el producto
     * @throws SQLException
     */
    private boolean existeProducto(String nombre) throws SQLException {
        String sql = "SELECT * FROM productos WHERE nombre = ?";
        s = c.prepareStatement(sql);
        s.setString(1, nombre);
        return s.executeQuery().next();
    }

    /**
     * Función que devuelve un producto mediante su id
     * @param id id del producto
     * @return Producto que deseamos
     */
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

    /**
     * Método que sirve para insertar las Mesas
     * @param mesa_id id de la mesa
     * @param mesa_nombre nombre de la mesesa
     */

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

    /**
     * Función que te dice si existe una mesa mediante su id
     * @param mesa_id ID de la mesa
     * @return devuelve el boolean según si existe la mesa
     * @throws SQLException
     */
    private boolean existeMesa(int mesa_id) throws SQLException {
        String sql = "SELECT * FROM mesas WHERE id = ?";
        s = c.prepareStatement(sql);
        s.setInt(1, mesa_id);
        return s.executeQuery().next();
    }

    /**
     * Función que devuelve una lista de todas las mesas de la base de datos
     * @return devuelve una lista de mesas
     */
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

    /**
     * Método que sirve para inicar una mesaComanda poniendo la fecha final a null
     * @param mesa mesa la cual tendra la mesaComanda
     */
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

    /**
     * Función que devuelve si la mesaComanda Esta activa
     * @param mesaId id de la mesa en la que esta la mesaComanda
     * @return devuelve si esta activa o no
     * @throws SQLException
     */
    private boolean existeMesaComandaActiva(int mesaId) throws SQLException {
        String sql = "SELECT * FROM mesas_comandas WHERE mesa_id = ? AND fecha_final IS NULL";
        s = c.prepareStatement(sql);
        s.setInt(1, mesaId);
        return s.executeQuery().next();
    }

    /**
     * Método que sirve para borrar una mesa si su fecha final es nula o si su precio total es 0
     */
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

    /**
     * Borra una mesaComanda en concreto según su id
     * @param mesa_id id a la mesa que pertenece
     */
    public void borrarMesaComandaMesa(int mesa_id){
        try {
            c.setAutoCommit(false);
            String sql = "DELETE FROM mesas_comandas WHERE mesa_id = ? and fecha_final IS NULL ";
            s = c.prepareStatement(sql);
            s.setInt(1, mesa_id);
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

    /**
     * Método que actualiza una mesa comanda cuando termina añadiendo la fecha en la que termino el precio total de
     * los producto que se pidio
     * @param mesa_id id de la mesa para saber la mesaComanda
     * @param preciototal precio total de los productos comsumidos
     */
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

    /**
     * Función que devueve el id de la mesa con comandas que aun no an acabado para hacer el Jasper de la mesa
     * @param mesa_id id de la mesa
     * @return id de la mesa con comandas
     */
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

    /**
     * Metodo que sirve para insertar comandas a una mesa si ya existe el precio se sumara al que tenga anteriormente y
     * Se sumara la cantidad de ese mismo producto
     * @param mesaComandaId id de la mesa con comandas
     * @param productoId producto que le queremos añadir
     * @param precio precio del producto
     */
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

    /**
     * Función que devuelve datos para el JasperHistorico
     * @return devuelve un hasmap dentro de otro hasmap el cual contendra el nombre de la mesas id de la mesas con comanda
     *      * el precio total de la mesa con comandas y la fecha que se inicio esa mesa con comandas
     */
    public HashMap<String, HashMap<Integer, Double>> obtenerHashMapMesasComandas() {
        HashMap<String, HashMap<Integer, Double>> hashMapMesasComandas = new HashMap<>();
        try {
            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT mesas.nombre, mesas_comandas.id, mesas_comandas.precioTotal,mesas_comandas.fecha_inic  " +
                    "FROM mesas " +
                    "JOIN mesas_comandas ON mesas.id = mesas_comandas.mesa_id");

            while (resultSet.next()) {
                String nombreMesa = resultSet.getString("nombre");
                int idMesasComandas = resultSet.getInt("id");
                double precioTotal = resultSet.getDouble("precioTotal");
                String fechaInc = resultSet.getString("fecha_inic");
                nombreMesa += " " + fechaInc;
                if (!hashMapMesasComandas.containsKey(nombreMesa)) {
                    hashMapMesasComandas.put(nombreMesa, new HashMap<>());
                }
                hashMapMesasComandas.get(nombreMesa).put(idMesasComandas, precioTotal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashMapMesasComandas;
    }

    /**
     * Función que devuelve un hasmap para las estadisticas de los productos y saber la cantidad de productos que se
     * Consumen
     * @return un hasmap el cual como clave tendre el nombre del producto y como valor la cantidad de veces que se consumio
     * ese mismo producto
     */
    public HashMap<String,Integer> obtenerCantidadProductos(){
        HashMap<String, Integer> hashMapProductosCant = new HashMap<>();
        try {
            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("select p.nombre, count(p.id) FROM productos p " +
                    "INNER JOIN comandas c ON c.producto_id = p.id " +
                    "group by p.nombre; ");

            while (resultSet.next()) {
                String nombreProducto = resultSet.getString(1);
                int cant = resultSet.getInt(2);

                hashMapProductosCant.put(nombreProducto, cant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashMapProductosCant;
    }
    /**
     * Función que devuelve un hasmap para las estadisticas de las mesas y saber la cantidad de mesas que se usan
     * @return un hasmap el cual como clave tendre el nombre de las mesas y como valor la cantidad de veces que se usan
     */
    public HashMap<String,Integer> obtenerMesasMasUsadas(){
        HashMap<String, Integer> hashMapMesasUsadas = new HashMap<>();
        try {
            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("Select m.nombre, count(mc.id) FROM mesas m "+
                    "left JOIN mesas_comandas mc on m.id = mc.mesa_id " +
                    "group by m.id;");

            while (resultSet.next()) {
                String nombreMesa = resultSet.getString(1);
                int cant = resultSet.getInt(2);

                hashMapMesasUsadas.put(nombreMesa, cant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashMapMesasUsadas;
    }





}
