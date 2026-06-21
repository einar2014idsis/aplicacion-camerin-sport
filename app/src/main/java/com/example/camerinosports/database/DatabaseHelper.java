/*
implementacion antigua de sqlite desactivada; la aplicacion actual usa el servidor.
package com.example.camerinosports.database;

import android.content.contentvalues;
import android.content.context;
import android.database.cursor;
import android.database.sqlite.sqlitedatabase;
import android.database.sqlite.sqliteopenhelper;

import com.example.camerinosports.model.producto;
import com.example.camerinosports.model.pedido;
import com.example.camerinosports.model.pedidodetalle;
import com.example.camerinosports.model.promocion;
import com.example.camerinosports.model.consejo;
import com.example.camerinosports.model.usuario;

import java.text.simpledateformat;
import java.util.arraylist;
import java.util.date;
import java.util.locale;

/ **
 * implementacion historica desactivada.
 *
 * esta clase fue creada durante la primera etapa academica del proyecto para
 * simular usuarios, productos, pedidos, promociones y consejos sin servidor.
 *
 * desde la migracion definitiva:
 * android -> retrofit -> api c# -> sql server
 *
 * ningun repository ni viewmodel instancia esta clase. se conserva unicamente
 * como documentacion del prototipo local y no debe utilizarse en codigo nuevo.
 * toda lectura y escritura vigente debe realizarse mediante las interfaces del
 * paquete network y los endpoints de camerinosportsservicios.
 * /
@deprecated
public class databasehelper extends sqliteopenhelper {

  / **  private static final string database_name = "camerino_sports.db";
    private static final int database_version = 6;

    public static final string tabla_usuarios = "usuarios";
    public static final string tabla_productos = "productos";
    public static final string tabla_pedidos = "pedidos";
    public static final string tabla_pedido_detalle = "pedido_detalle";
    public static final string tabla_promociones = "promociones";
    public static final string tabla_consejos = "consejos";

    public databasehelper(context context) {
        super(context, database_name, null, database_version);
    }
* /
    @override
    public void onconfigure(sqlitedatabase db) {
        super.onconfigure(db);
        db.setforeignkeyconstraintsenabled(true);
    }

    @override
    public void oncreate(sqlitedatabase db) {
        creartablausuarios(db);
        creartablaproductos(db);
        creartablaspreparadas(db);
        insertaradmininicial(db);
    }

    @override
    public void onupgrade(sqlitedatabase db, int oldversion, int newversion) {
        // no se eliminan tablas ni datos existentes
        creartablausuarios(db);
        creartablaproductos(db);

        if (oldversion < 3) {
            agregarcolumnasinoexiste(db, tabla_productos, "talla", "text");
            agregarcolumnasinoexiste(db, tabla_productos, "color", "text");
            agregarcolumnasinoexiste(db, tabla_productos, "sostenible", "integer default 0");
        }

        creartablaspreparadas(db);

        if (oldversion < 4) {
            agregarcolumnasinoexiste(db, tabla_pedidos, "latitud", "real");
            agregarcolumnasinoexiste(db, tabla_pedidos, "longitud", "real");
        }

        if (oldversion < 5) {
            agregarcolumnasinoexiste(db, tabla_pedidos, "metodopago", "text");
            agregarcolumnasinoexiste(db, tabla_pedidos, "estadopago", "text default 'pendiente'");
            agregarcolumnasinoexiste(db, tabla_pedidos, "codigooperacionpago", "text");
        }

        if (oldversion < 6) {
            agregarcolumnasinoexiste(db, tabla_consejos, "imagen", "text");
        }

        insertaradmininicial(db);
    }

    private void creartablausuarios(sqlitedatabase db) {
        // se conservan las columnas actuales para no romper login ni registro
        string sql = "create table if not exists " + tabla_usuarios + " (" +
                "id integer primary key autoincrement, " +
                "nombre text not null, " +
                "apellidos text not null, " +
                "correo text not null unique, " +
                "password text not null, " +
                "rol text not null" +
                ")";

        db.execsql(sql);
    }

    private void creartablaproductos(sqlitedatabase db) {
        string sql = "create table if not exists " + tabla_productos + " (" +
                "id integer primary key autoincrement, " +
                "nombre text not null, " +
                "descripcion text, " +
                "precio real not null, " +
                "stock integer not null, " +
                "categoria text not null, " +
                "imagen text, " +
                "talla text, " +
                "color text, " +
                "sostenible integer default 0" +
                ")";

        db.execsql(sql);
    }

    private void creartablaspreparadas(sqlitedatabase db) {
        // estas tablas quedan listas para modulos posteriores
        db.execsql("create table if not exists " + tabla_pedidos + " (" +
                "id integer primary key autoincrement, " +
                "usuarioid integer, " +
                "cliente text not null, " +
                "telefono text not null, " +
                "correo text, " +
                "direccion text not null, " +
                "referencia text, " +
                "fecha text not null, " +
                "total real not null, " +
                "estado text not null, " +
                "comentario text, " +
                "latitud real, " +
                "longitud real, " +
                "metodopago text, " +
                "estadopago text default 'pendiente', " +
                "codigooperacionpago text, " +
                "foreign key(usuarioid) references " + tabla_usuarios + "(id)" +
                ")");

        db.execsql("create table if not exists " + tabla_pedido_detalle + " (" +
                "id integer primary key autoincrement, " +
                "pedidoid integer not null, " +
                "productoid integer not null, " +
                "nombreproducto text not null, " +
                "cantidad integer not null, " +
                "precio real not null, " +
                "subtotal real not null, " +
                "foreign key(pedidoid) references " + tabla_pedidos + "(id), " +
                "foreign key(productoid) references " + tabla_productos + "(id)" +
                ")");

        db.execsql("create table if not exists " + tabla_promociones + " (" +
                "id integer primary key autoincrement, " +
                "productoid integer not null, " +
                "preciooriginal real not null, " +
                "preciooferta real not null, " +
                "fechainicio text not null, " +
                "fechafin text not null, " +
                "activa integer default 1, " +
                "foreign key(productoid) references " + tabla_productos + "(id)" +
                ")");

        db.execsql("create table if not exists " + tabla_consejos + " (" +
                "id integer primary key autoincrement, " +
                "titulo text not null, " +
                "descripcion text not null, " +
                "categoria text not null, " +
                "imagen text, " +
                "activo integer default 1" +
                ")");
    }

    private void agregarcolumnasinoexiste(sqlitedatabase db, string tabla, string columna, string definicion) {
        cursor cursor = db.rawquery("pragma table_info(" + tabla + ")", null);
        boolean existe = false;

        while (cursor.movetonext()) {
            string nombrecolumna = cursor.getstring(1);
            if (columna.equals(nombrecolumna)) {
                existe = true;
            }
        }

        cursor.close();

        if (!existe) {
            db.execsql("alter table " + tabla + " add column " + columna + " " + definicion);
        }
    }

    private void insertaradmininicial(sqlitedatabase db) {
        contentvalues valores = new contentvalues();
        valores.put("nombre", "administrador");
        valores.put("apellidos", "camerino sports");
        valores.put("correo", "admin@camerinosports.com");
        valores.put("password", "123456");
        valores.put("rol", "admin");

        db.insertwithonconflict(tabla_usuarios, null, valores, sqlitedatabase.conflict_ignore);
    }

    public void inicializarbasedatos() {
        // fuerza la creacion inicial de tablas
        this.getwritabledatabase();
    }

    public boolean registrarusuario(string nombre, string apellidos, string correo, string password) {
        sqlitedatabase db = this.getwritabledatabase();

        contentvalues valores = new contentvalues();
        valores.put("nombre", nombre);
        valores.put("apellidos", apellidos);
        valores.put("correo", correo);
        valores.put("password", password);
        valores.put("rol", "cliente");

        long resultado = db.insert(tabla_usuarios, null, valores);
        return resultado != -1;
    }

    public boolean validarlogin(string correo, string password) {
        sqlitedatabase db = this.getreadabledatabase();

        cursor cursor = db.rawquery(
                "select id from " + tabla_usuarios + " where correo = ? and password = ?",
                new string[]{correo, password}
        );

        boolean existe = cursor.movetofirst();

        cursor.close();

        return existe;
    }

    public string obtenerrol(string correo) {
        sqlitedatabase db = this.getreadabledatabase();

        cursor cursor = db.rawquery(
                "select rol from " + tabla_usuarios + " where correo = ?",
                new string[]{correo}
        );

        string rol = "";

        if (cursor.movetofirst()) {
            rol = cursor.getstring(0);
        }

        cursor.close();

        return rol;
    }

    public boolean existecorreo(string correo) {
        sqlitedatabase db = this.getreadabledatabase();

        cursor cursor = db.rawquery(
                "select id from " + tabla_usuarios + " where correo = ?",
                new string[]{correo}
        );

        boolean existe = cursor.movetofirst();

        cursor.close();

        return existe;
    }

    public usuario obtenerusuarioporcorreo(string correo) {
        sqlitedatabase db = this.getreadabledatabase();

        cursor cursor = db.rawquery(
                "select id, nombre, apellidos, correo, password, rol from " + tabla_usuarios + " where correo = ?",
                new string[]{correo}
        );

        usuario usuario = null;

        if (cursor.movetofirst()) {
            usuario = new usuario(
                    cursor.getint(0),
                    cursor.getstring(1),
                    cursor.getstring(2),
                    cursor.getstring(3),
                    cursor.getstring(4),
                    cursor.getstring(5)
            );
        }

        cursor.close();

        return usuario;
    }

    public boolean actualizarusuario(usuario usuario) {
        sqlitedatabase db = this.getwritabledatabase();
        contentvalues valores = new contentvalues();
        valores.put("nombre", usuario.getnombre());
        valores.put("apellidos", usuario.getapellidos());
        valores.put("password", usuario.getpassword());

        int filas = db.update(
                tabla_usuarios,
                valores,
                "id = ?",
                new string[]{string.valueof(usuario.getid())}
        );

        return filas > 0;
    }

    public int contarclientes() {
        sqlitedatabase db = this.getreadabledatabase();
        cursor cursor = db.rawquery(
                "select count(*) from " + tabla_usuarios + " where rol = ?",
                new string[]{"cliente"}
        );

        int total = 0;

        if (cursor.movetofirst()) {
            total = cursor.getint(0);
        }

        cursor.close();
        return total;
    }

    public boolean registrarproducto(producto producto) {
        sqlitedatabase db = this.getwritabledatabase();

        long resultado = db.insert(tabla_productos, null, obtenervaloresproducto(producto));
        if (resultado != -1) {
            producto.setid((int) resultado);
        }
        return resultado != -1;
    }

    public boolean guardarproductosincronizado(producto producto) {
        sqlitedatabase db = this.getwritabledatabase();
        contentvalues valores = obtenervaloresproducto(producto);

        int filas = db.update(
                tabla_productos,
                valores,
                "id = ?",
                new string[]{string.valueof(producto.getid())}
        );

        if (filas > 0) {
            return true;
        }

        valores.put("id", producto.getid());
        return db.insert(tabla_productos, null, valores) != -1;
    }

    public void sincronizarproductosdesdeservicio(arraylist<producto> productos) {
        sqlitedatabase db = this.getwritabledatabase();
        db.begintransaction();

        try {
            arraylist<string> idsservidor = new arraylist<>();

            for (producto producto : productos) {
                contentvalues valores = obtenervaloresproducto(producto);
                int filas = db.update(
                        tabla_productos,
                        valores,
                        "id = ?",
                        new string[]{string.valueof(producto.getid())}
                );

                if (filas == 0) {
                    valores.put("id", producto.getid());
                    db.insertorthrow(tabla_productos, null, valores);
                }

                idsservidor.add(string.valueof(producto.getid()));
            }

            if (idsservidor.isempty()) {
                db.delete(tabla_productos, null, null);
            } else {
                stringbuilder marcadores = new stringbuilder();
                for (int i = 0; i < idsservidor.size(); i++) {
                    if (i > 0) {
                        marcadores.append(",");
                    }
                    marcadores.append("?");
                }

                db.delete(
                        tabla_productos,
                        "id not in (" + marcadores + ")",
                        idsservidor.toarray(new string[0])
                );
            }

            db.settransactionsuccessful();
        } finally {
            db.endtransaction();
        }
    }

    public arraylist<producto> listarproductos() {
        return consultarproductos(
                "select id, nombre, descripcion, precio, stock, categoria, imagen, talla, color, sostenible from " +
                        tabla_productos + " order by nombre",
                null
        );
    }

    public producto obtenerproductoporid(int id) {
        arraylist<producto> lista = consultarproductos(
                "select id, nombre, descripcion, precio, stock, categoria, imagen, talla, color, sostenible from " +
                        tabla_productos + " where id = ?",
                new string[]{string.valueof(id)}
        );

        if (lista.isempty()) {
            return null;
        }

        return lista.get(0);
    }

    public boolean actualizarproducto(producto producto) {
        sqlitedatabase db = this.getwritabledatabase();

        int filas = db.update(
                tabla_productos,
                obtenervaloresproducto(producto),
                "id = ?",
                new string[]{string.valueof(producto.getid())}
        );

        return filas > 0;
    }

    public boolean eliminarproducto(int id) {
        sqlitedatabase db = this.getwritabledatabase();

        int filas = db.delete(
                tabla_productos,
                "id = ?",
                new string[]{string.valueof(id)}
        );

        return filas > 0;
    }

    public arraylist<producto> buscarproductospornombre(string nombre) {
        return consultarproductos(
                "select id, nombre, descripcion, precio, stock, categoria, imagen, talla, color, sostenible from " +
                        tabla_productos + " where nombre like ? order by nombre",
                new string[]{"%" + nombre + "%"}
        );
    }

    public arraylist<producto> filtrarproductosporcategoria(string categoria) {
        return consultarproductos(
                "select id, nombre, descripcion, precio, stock, categoria, imagen, talla, color, sostenible from " +
                        tabla_productos + " where categoria like ? order by nombre",
                new string[]{"%" + categoria + "%"}
        );
    }

    public arraylist<producto> listarproductosconstockbajo() {
        return consultarproductos(
                "select id, nombre, descripcion, precio, stock, categoria, imagen, talla, color, sostenible from " +
                        tabla_productos + " where stock <= 3 order by stock",
                null
        );
    }

    public boolean actualizarstock(int productoid, int stock) {
        sqlitedatabase db = this.getwritabledatabase();

        contentvalues valores = new contentvalues();
        valores.put("stock", stock);

        int filas = db.update(
                tabla_productos,
                valores,
                "id = ?",
                new string[]{string.valueof(productoid)}
        );

        return filas > 0;
    }

    public boolean reducirstock(int productoid, int cantidad) {
        producto producto = obtenerproductoporid(productoid);

        if (producto == null || producto.getstock() < cantidad) {
            return false;
        }

        return actualizarstock(productoid, producto.getstock() - cantidad);
    }

    public boolean restaurarstock(int productoid, int cantidad) {
        producto producto = obtenerproductoporid(productoid);

        if (producto == null) {
            return false;
        }

        return actualizarstock(productoid, producto.getstock() + cantidad);
    }

    private contentvalues obtenervaloresproducto(producto producto) {
        contentvalues valores = new contentvalues();
        valores.put("nombre", producto.getnombre());
        valores.put("descripcion", producto.getdescripcion());
        valores.put("precio", producto.getprecio());
        valores.put("stock", producto.getstock());
        valores.put("categoria", producto.getcategoria());
        valores.put("imagen", producto.getimagen());
        valores.put("talla", producto.gettalla());
        valores.put("color", producto.getcolor());
        valores.put("sostenible", producto.issostenible() ? 1 : 0);

        return valores;
    }

    private arraylist<producto> consultarproductos(string sql, string[] argumentos) {
        arraylist<producto> lista = new arraylist<>();
        sqlitedatabase db = this.getreadabledatabase();

        cursor cursor = db.rawquery(sql, argumentos);

        if (cursor.movetofirst()) {
            do {
                producto producto = new producto();
                producto.setid(cursor.getint(0));
                producto.setnombre(cursor.getstring(1));
                producto.setdescripcion(cursor.getstring(2));
                producto.setprecio(cursor.getdouble(3));
                producto.setstock(cursor.getint(4));
                producto.setcategoria(cursor.getstring(5));
                producto.setimagen(cursor.getstring(6));
                producto.settalla(cursor.getstring(7));
                producto.setcolor(cursor.getstring(8));
                producto.setsostenible(cursor.getint(9) == 1);

                lista.add(producto);
            } while (cursor.movetonext());
        }

        cursor.close();

        return lista;
    }

    public int registrarpedido(pedido pedido) {
        sqlitedatabase db = this.getwritabledatabase();
        int pedidoid = -1;

        db.begintransaction();

        try {
            // registrar cabecera del pedido
            contentvalues valorespedido = new contentvalues();
            if (pedido.getusuarioid() > 0) {
                valorespedido.put("usuarioid", pedido.getusuarioid());
            } else {
                valorespedido.putnull("usuarioid");
            }
            valorespedido.put("cliente", pedido.getcliente());
            valorespedido.put("telefono", pedido.gettelefono());
            valorespedido.put("correo", pedido.getcorreo());
            valorespedido.put("direccion", pedido.getdireccion());
            valorespedido.put("referencia", pedido.getreferencia());
            valorespedido.put("fecha", obtenerfechaactual());
            valorespedido.put("total", pedido.gettotal());
            valorespedido.put("estado", "pedido confirmado");
            valorespedido.put("comentario", pedido.getcomentario());
            valorespedido.put("latitud", pedido.getlatitud());
            valorespedido.put("longitud", pedido.getlongitud());
            valorespedido.put("metodopago", pedido.getmetodopago());
            valorespedido.put("estadopago", pedido.getestadopago());
            valorespedido.put("codigooperacionpago", pedido.getcodigooperacionpago());

            long resultadopedido = db.insert(tabla_pedidos, null, valorespedido);

            if (resultadopedido == -1) {
                return -1;
            }

            pedidoid = (int) resultadopedido;

            // registrar detalles y reducir stock
            arraylist<pedidodetalle> detalles = pedido.getdetalles();
            int i;

            for (i = 0; i < detalles.size(); i++) {
                pedidodetalle detalle = detalles.get(i);
                producto producto = obtenerproductoporidendb(db, detalle.getproductoid());

                if (producto == null || producto.getstock() < detalle.getcantidad()) {
                    return -1;
                }

                contentvalues valoresdetalle = new contentvalues();
                valoresdetalle.put("pedidoid", pedidoid);
                valoresdetalle.put("productoid", detalle.getproductoid());
                valoresdetalle.put("nombreproducto", detalle.getnombreproducto());
                valoresdetalle.put("cantidad", detalle.getcantidad());
                valoresdetalle.put("precio", detalle.getprecio());
                valoresdetalle.put("subtotal", detalle.getsubtotal());

                long resultadodetalle = db.insert(tabla_pedido_detalle, null, valoresdetalle);

                if (resultadodetalle == -1) {
                    return -1;
                }

                contentvalues valoresstock = new contentvalues();
                valoresstock.put("stock", producto.getstock() - detalle.getcantidad());
                db.update(tabla_productos, valoresstock, "id = ?", new string[]{string.valueof(detalle.getproductoid())});
            }

            db.settransactionsuccessful();
        } finally {
            db.endtransaction();
        }

        return pedidoid;
    }

    public arraylist<pedido> listarpedidos() {
        arraylist<pedido> lista = new arraylist<>();
        normalizarestadospedidos();
        sqlitedatabase db = this.getreadabledatabase();

        cursor cursor = db.rawquery(
                "select id, usuarioid, cliente, telefono, correo, direccion, referencia, fecha, total, estado, comentario " +
                        ", latitud, longitud, metodopago, estadopago, codigooperacionpago " +
                        "from " + tabla_pedidos + " order by case estado when 'pedido confirmado' then 0 else 1 end, id desc",
                null
        );

        if (cursor.movetofirst()) {
            do {
                lista.add(obtenerpedidodesdecursor(cursor));
            } while (cursor.movetonext());
        }

        cursor.close();
        return lista;
    }

    public pedido obtenerpedidoporid(int id) {
        normalizarestadospedidos();
        sqlitedatabase db = this.getreadabledatabase();
        pedido pedido = null;

        cursor cursor = db.rawquery(
                "select id, usuarioid, cliente, telefono, correo, direccion, referencia, fecha, total, estado, comentario " +
                        ", latitud, longitud, metodopago, estadopago, codigooperacionpago " +
                        "from " + tabla_pedidos + " where id = ?",
                new string[]{string.valueof(id)}
        );

        if (cursor.movetofirst()) {
            pedido = obtenerpedidodesdecursor(cursor);
        }

        cursor.close();

        if (pedido != null) {
            pedido.setdetalles(listardetallespedido(id));
        }

        return pedido;
    }

    public arraylist<pedidodetalle> listardetallespedido(int pedidoid) {
        arraylist<pedidodetalle> lista = new arraylist<>();
        sqlitedatabase db = this.getreadabledatabase();

        cursor cursor = db.rawquery(
                "select id, pedidoid, productoid, nombreproducto, cantidad, precio, subtotal " +
                        "from " + tabla_pedido_detalle + " where pedidoid = ?",
                new string[]{string.valueof(pedidoid)}
        );

        if (cursor.movetofirst()) {
            do {
                pedidodetalle detalle = new pedidodetalle();
                detalle.setid(cursor.getint(0));
                detalle.setpedidoid(cursor.getint(1));
                detalle.setproductoid(cursor.getint(2));
                detalle.setnombreproducto(cursor.getstring(3));
                detalle.setcantidad(cursor.getint(4));
                detalle.setprecio(cursor.getdouble(5));
                detalle.setsubtotal(cursor.getdouble(6));
                lista.add(detalle);
            } while (cursor.movetonext());
        }

        cursor.close();
        return lista;
    }

    public boolean cambiarestadopedido(int pedidoid, string estado) {
        sqlitedatabase db = this.getwritabledatabase();

        contentvalues valores = new contentvalues();
        valores.put("estado", estado);

        int filas = db.update(tabla_pedidos, valores, "id = ?", new string[]{string.valueof(pedidoid)});
        return filas > 0;
    }

    public boolean cancelarpedido(int pedidoid) {
        pedido pedido = obtenerpedidoporid(pedidoid);

        if (pedido == null || !"pedido confirmado".equals(pedido.getestado())) {
            return false;
        }

        sqlitedatabase db = this.getwritabledatabase();
        db.begintransaction();

        try {
            arraylist<pedidodetalle> detalles = pedido.getdetalles();
            int i;

            for (i = 0; i < detalles.size(); i++) {
                pedidodetalle detalle = detalles.get(i);
                producto producto = obtenerproductoporidendb(db, detalle.getproductoid());

                if (producto != null) {
                    contentvalues valoresstock = new contentvalues();
                    valoresstock.put("stock", producto.getstock() + detalle.getcantidad());
                    db.update(tabla_productos, valoresstock, "id = ?", new string[]{string.valueof(detalle.getproductoid())});
                }
            }

            contentvalues valorespedido = new contentvalues();
            valorespedido.put("estado", "cancelado");
            db.update(tabla_pedidos, valorespedido, "id = ?", new string[]{string.valueof(pedidoid)});

            db.settransactionsuccessful();
        } finally {
            db.endtransaction();
        }

        return true;
    }

    private void normalizarestadospedidos() {
        sqlitedatabase db = this.getwritabledatabase();
        contentvalues valores = new contentvalues();

        valores.put("estado", "pedido confirmado");
        db.update(tabla_pedidos, valores, "estado = ?", new string[]{"pendiente"});

        valores.clear();
        valores.put("estado", "preparando pedido");
        db.update(tabla_pedidos, valores, "estado = ?", new string[]{"en proceso"});

        valores.clear();
        valores.put("estado", "entregado");
        db.update(tabla_pedidos, valores, "estado = ?", new string[]{"atendido"});
    }

    public boolean registrarpromocion(promocion promocion) {
        sqlitedatabase db = this.getwritabledatabase();
        long resultado = db.insert(tabla_promociones, null, obtenervalorespromocion(promocion));
        return resultado != -1;
    }

    public arraylist<promocion> listarpromociones() {
        activarpromocionesregistradas();
        return consultarpromociones(
                "select p.id, p.productoid, p.preciooriginal, p.preciooferta, p.fechainicio, p.fechafin, p.activa, pr.nombre, pr.imagen " +
                        "from " + tabla_promociones + " p inner join " + tabla_productos + " pr on p.productoid = pr.id order by p.id desc",
                null
        );
    }

    public arraylist<promocion> listarpromocionesactivas() {
        activarpromocionesregistradas();

        return consultarpromociones(
                "select p.id, p.productoid, p.preciooriginal, p.preciooferta, p.fechainicio, p.fechafin, p.activa, pr.nombre, pr.imagen " +
                        "from " + tabla_promociones + " p inner join " + tabla_productos + " pr on p.productoid = pr.id " +
                        "where p.activa = 1 order by p.id desc",
                null
        );
    }

    public int contarpromocionesactivas() {
        activarpromocionesregistradas();
        sqlitedatabase db = this.getreadabledatabase();
        cursor cursor = db.rawquery(
                "select count(*) from " + tabla_promociones + " where activa = 1",
                null
        );

        int total = 0;

        if (cursor.movetofirst()) {
            total = cursor.getint(0);
        }

        cursor.close();
        return total;
    }

    public promocion obtenerpromocionporproducto(int productoid) {
        activarpromocionesregistradas();
        arraylist<promocion> lista = consultarpromociones(
                "select p.id, p.productoid, p.preciooriginal, p.preciooferta, p.fechainicio, p.fechafin, p.activa, pr.nombre, pr.imagen " +
                        "from " + tabla_promociones + " p inner join " + tabla_productos + " pr on p.productoid = pr.id " +
                        "where p.productoid = ? and p.activa = 1 limit 1",
                new string[]{string.valueof(productoid)}
        );

        if (lista.isempty()) {
            return null;
        }

        return lista.get(0);
    }

    public boolean actualizarpromocion(promocion promocion) {
        sqlitedatabase db = this.getwritabledatabase();

        int filas = db.update(
                tabla_promociones,
                obtenervalorespromocion(promocion),
                "id = ?",
                new string[]{string.valueof(promocion.getid())}
        );

        return filas > 0;
    }

    public boolean eliminarpromocion(int id) {
        sqlitedatabase db = this.getwritabledatabase();
        int filas = db.delete(tabla_promociones, "id = ?", new string[]{string.valueof(id)});
        return filas > 0;
    }

    public void desactivarpromocionesvencidas() {
        // la promocion permanece activa hasta que el administrador la elimine.
        // las fechas se conservan como informacion y no cambian el estado automaticamente.
        activarpromocionesregistradas();
    }

    private void activarpromocionesregistradas() {
        sqlitedatabase db = this.getwritabledatabase();
        contentvalues valores = new contentvalues();
        valores.put("activa", 1);
        db.update(tabla_promociones, valores, "activa = 0", null);
    }

    public boolean registrarconsejo(consejo consejo) {
        sqlitedatabase db = this.getwritabledatabase();
        long resultado = db.insert(tabla_consejos, null, obtenervaloresconsejo(consejo));
        return resultado != -1;
    }

    public arraylist<consejo> listarconsejos() {
        insertarconsejosinicialessifaltan();
        return consultarconsejos("select id, titulo, descripcion, categoria, imagen, activo from " + tabla_consejos + " order by id desc", null);
    }

    public arraylist<consejo> listarconsejosactivos() {
        insertarconsejosinicialessifaltan();
        return consultarconsejos("select id, titulo, descripcion, categoria, imagen, activo from " + tabla_consejos + " where activo = 1 order by id asc", null);
    }

    public boolean actualizarconsejo(consejo consejo) {
        sqlitedatabase db = this.getwritabledatabase();

        int filas = db.update(
                tabla_consejos,
                obtenervaloresconsejo(consejo),
                "id = ?",
                new string[]{string.valueof(consejo.getid())}
        );

        return filas > 0;
    }

    public boolean eliminarconsejo(int id) {
        sqlitedatabase db = this.getwritabledatabase();
        int filas = db.delete(tabla_consejos, "id = ?", new string[]{string.valueof(id)});
        return filas > 0;
    }

    private pedido obtenerpedidodesdecursor(cursor cursor) {
        pedido pedido = new pedido();
        pedido.setid(cursor.getint(0));
        pedido.setusuarioid(cursor.getint(1));
        pedido.setcliente(cursor.getstring(2));
        pedido.settelefono(cursor.getstring(3));
        pedido.setcorreo(cursor.getstring(4));
        pedido.setdireccion(cursor.getstring(5));
        pedido.setreferencia(cursor.getstring(6));
        pedido.setfecha(cursor.getstring(7));
        pedido.settotal(cursor.getdouble(8));
        pedido.setestado(cursor.getstring(9));
        pedido.setcomentario(cursor.getstring(10));
        pedido.setlatitud(cursor.getdouble(11));
        pedido.setlongitud(cursor.getdouble(12));
        pedido.setmetodopago(cursor.getstring(13));
        pedido.setestadopago(cursor.getstring(14));
        pedido.setcodigooperacionpago(cursor.getstring(15));
        return pedido;
    }

    private producto obtenerproductoporidendb(sqlitedatabase db, int id) {
        cursor cursor = db.rawquery(
                "select id, nombre, descripcion, precio, stock, categoria, imagen, talla, color, sostenible from " +
                        tabla_productos + " where id = ?",
                new string[]{string.valueof(id)}
        );

        producto producto = null;

        if (cursor.movetofirst()) {
            producto = new producto();
            producto.setid(cursor.getint(0));
            producto.setnombre(cursor.getstring(1));
            producto.setdescripcion(cursor.getstring(2));
            producto.setprecio(cursor.getdouble(3));
            producto.setstock(cursor.getint(4));
            producto.setcategoria(cursor.getstring(5));
            producto.setimagen(cursor.getstring(6));
            producto.settalla(cursor.getstring(7));
            producto.setcolor(cursor.getstring(8));
            producto.setsostenible(cursor.getint(9) == 1);
        }

        cursor.close();
        return producto;
    }

    private contentvalues obtenervalorespromocion(promocion promocion) {
        contentvalues valores = new contentvalues();
        valores.put("productoid", promocion.getproductoid());
        valores.put("preciooriginal", promocion.getpreciooriginal());
        valores.put("preciooferta", promocion.getpreciooferta());
        valores.put("fechainicio", promocion.getfechainicio());
        valores.put("fechafin", promocion.getfechafin());
        valores.put("activa", promocion.isactiva() ? 1 : 0);
        return valores;
    }

    private arraylist<promocion> consultarpromociones(string sql, string[] argumentos) {
        arraylist<promocion> lista = new arraylist<>();
        sqlitedatabase db = this.getreadabledatabase();

        cursor cursor = db.rawquery(sql, argumentos);

        if (cursor.movetofirst()) {
            do {
                promocion promocion = new promocion();
                promocion.setid(cursor.getint(0));
                promocion.setproductoid(cursor.getint(1));
                promocion.setpreciooriginal(cursor.getdouble(2));
                promocion.setpreciooferta(cursor.getdouble(3));
                promocion.setfechainicio(cursor.getstring(4));
                promocion.setfechafin(cursor.getstring(5));
                promocion.setactiva(cursor.getint(6) == 1);
                promocion.setnombreproducto(cursor.getstring(7));
                promocion.setimagenproducto(cursor.getstring(8));
                lista.add(promocion);
            } while (cursor.movetonext());
        }

        cursor.close();
        return lista;
    }

    private contentvalues obtenervaloresconsejo(consejo consejo) {
        contentvalues valores = new contentvalues();
        valores.put("titulo", consejo.gettitulo());
        valores.put("descripcion", consejo.getdescripcion());
        valores.put("categoria", consejo.getcategoria());
        valores.put("imagen", consejo.getimagen());
        valores.put("activo", consejo.isactivo() ? 1 : 0);
        return valores;
    }

    private void insertarconsejosinicialessifaltan() {
        sqlitedatabase db = this.getwritabledatabase();
        normalizarconsejosiniciales(db);

        cursor cursor = db.rawquery("select count(*) from " + tabla_consejos, null);
        boolean hayconsejos = false;

        if (cursor.movetofirst()) {
            hayconsejos = cursor.getint(0) > 0;
        }

        cursor.close();

        if (hayconsejos) {
            return;
        }

        insertarconsejoinicial(db, "consumo responsable",
                "pequenos cuidados ayudan a conservar mejor tu ropa y calzado deportivo.",
                "cuidado general");
        insertarconsejoinicial(db, "lava con agua fria",
                "evita usar agua caliente para conservar mejor los colores y las fibras.",
                "lavado");
        insertarconsejoinicial(db, "seca a la sombra",
                "la luz directa del sol puede desgastar los colores y danar algunos materiales.",
                "secado");
        insertarconsejoinicial(db, "no uses blanqueador",
                "los quimicos fuertes pueden deteriorar las telas deportivas y los estampados.",
                "lavado");
        insertarconsejoinicial(db, "limpia tus zapatillas",
                "retira polvo y barro despues de usarlas para mantenerlas en buen estado.",
                "calzado");
    }

    private void normalizarconsejosiniciales(sqlitedatabase db) {
        contentvalues valores = new contentvalues();
        valores.put("titulo", "lava con agua fria");
        db.update(tabla_consejos, valores, "titulo = ?", new string[]{"lava con agua fria"});

        valores.clear();
        valores.put("descripcion", "los quimicos fuertes pueden deteriorar las telas deportivas y los estampados.");
        db.update(tabla_consejos, valores, "titulo = ?", new string[]{"no uses blanqueador"});

        valores.clear();
        valores.put("descripcion", "retira polvo y barro despues de usarlas para mantenerlas en buen estado.");
        db.update(tabla_consejos, valores, "titulo = ?", new string[]{"limpia tus zapatillas"});
    }

    private void insertarconsejoinicial(sqlitedatabase db, string titulo, string descripcion, string categoria) {
        cursor cursor = db.rawquery(
                "select id from " + tabla_consejos + " where titulo = ? limit 1",
                new string[]{titulo}
        );

        boolean existe = cursor.movetofirst();
        cursor.close();

        if (existe) {
            return;
        }

        contentvalues valores = new contentvalues();
        valores.put("titulo", titulo);
        valores.put("descripcion", descripcion);
        valores.put("categoria", categoria);
        valores.put("activo", 1);
        db.insert(tabla_consejos, null, valores);
    }

    private arraylist<consejo> consultarconsejos(string sql, string[] argumentos) {
        arraylist<consejo> lista = new arraylist<>();
        sqlitedatabase db = this.getreadabledatabase();

        cursor cursor = db.rawquery(sql, argumentos);

        if (cursor.movetofirst()) {
            do {
                consejo consejo = new consejo();
                consejo.setid(cursor.getint(0));
                consejo.settitulo(cursor.getstring(1));
                consejo.setdescripcion(cursor.getstring(2));
                consejo.setcategoria(cursor.getstring(3));
                consejo.setimagen(cursor.getstring(4));
                consejo.setactivo(cursor.getint(5) == 1);
                lista.add(consejo);
            } while (cursor.movetonext());
        }

        cursor.close();
        return lista;
    }

    private string obtenerfechaactual() {
        return new simpledateformat("yyyy-mm-dd hh:mm", locale.getdefault()).format(new date());
    }

    private string obtenerfechaactualcorta() {
        return new simpledateformat("yyyy-mm-dd", locale.getdefault()).format(new date());
    }
}

*/
