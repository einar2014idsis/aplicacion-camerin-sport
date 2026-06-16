package com.example.camerinosports.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.camerinosports.model.Producto;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "camerino_sports.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLA_USUARIOS = "usuarios";
    public static final String TABLA_PRODUCTOS = "productos";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String crearTablaUsuarios = "CREATE TABLE " + TABLA_USUARIOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "apellidos TEXT NOT NULL, " +
                "correo TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "rol TEXT NOT NULL" +
                ")";

        String crearTablaProductos = "CREATE TABLE " + TABLA_PRODUCTOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "descripcion TEXT, " +
                "precio REAL NOT NULL, " +
                "stock INTEGER NOT NULL, " +
                "categoria TEXT NOT NULL, " +
                "imagen TEXT" +
                ")";

        db.execSQL(crearTablaUsuarios);
        db.execSQL(crearTablaProductos);

        insertarAdminInicial(db);
    }

    private void insertarAdminInicial(SQLiteDatabase db) {
        ContentValues valores = new ContentValues();
        valores.put("nombre", "Administrador");
        valores.put("apellidos", "Camerino Sports");
        valores.put("correo", "admin@camerinosports.com");
        valores.put("password", "123456");
        valores.put("rol", "admin");

        db.insert(TABLA_USUARIOS, null, valores);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PRODUCTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_USUARIOS);
        onCreate(db);
    }

    public boolean registrarUsuario(String nombre, String apellidos, String correo, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("apellidos", apellidos);
        valores.put("correo", correo);
        valores.put("password", password);
        valores.put("rol", "cliente");

        long resultado = db.insert(TABLA_USUARIOS, null, valores);
        db.close();

        return resultado != -1;
    }

    public boolean validarLogin(String correo, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLA_USUARIOS + " WHERE correo = ? AND password = ?",
                new String[]{correo, password}
        );

        boolean existe = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return existe;
    }

    public String obtenerRol(String correo) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT rol FROM " + TABLA_USUARIOS + " WHERE correo = ?",
                new String[]{correo}
        );

        String rol = "";

        if (cursor.moveToFirst()) {
            rol = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return rol;
    }

    public boolean existeCorreo(String correo) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLA_USUARIOS + " WHERE correo = ?",
                new String[]{correo}
        );

        boolean existe = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return existe;
    }

    public boolean registrarProducto(Producto producto) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nombre", producto.getNombre());
        valores.put("descripcion", producto.getDescripcion());
        valores.put("precio", producto.getPrecio());
        valores.put("stock", producto.getStock());
        valores.put("categoria", producto.getCategoria());
        valores.put("imagen", producto.getImagen());

        long resultado = db.insert(TABLA_PRODUCTOS, null, valores);
        db.close();

        return resultado != -1;
    }

    public ArrayList<Producto> listarProductos() {
        ArrayList<Producto> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, nombre, descripcion, precio, stock, categoria, imagen FROM " + TABLA_PRODUCTOS,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Producto producto = new Producto();
                producto.setId(cursor.getInt(0));
                producto.setNombre(cursor.getString(1));
                producto.setDescripcion(cursor.getString(2));
                producto.setPrecio(cursor.getDouble(3));
                producto.setStock(cursor.getInt(4));
                producto.setCategoria(cursor.getString(5));
                producto.setImagen(cursor.getString(6));

                lista.add(producto);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public boolean actualizarProducto(Producto producto) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nombre", producto.getNombre());
        valores.put("descripcion", producto.getDescripcion());
        valores.put("precio", producto.getPrecio());
        valores.put("stock", producto.getStock());
        valores.put("categoria", producto.getCategoria());
        valores.put("imagen", producto.getImagen());

        int filas = db.update(
                TABLA_PRODUCTOS,
                valores,
                "id = ?",
                new String[]{String.valueOf(producto.getId())}
        );

        db.close();

        return filas > 0;
    }

    public boolean eliminarProducto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int filas = db.delete(
                TABLA_PRODUCTOS,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return filas > 0;
    }
}