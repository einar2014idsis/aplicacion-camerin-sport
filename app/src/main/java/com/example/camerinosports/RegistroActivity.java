package com.example.camerinosports;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Usuario;
import com.example.camerinosports.viewmodel.UsuarioViewModel;

public class RegistroActivity extends AppCompatActivity {

    private EditText edtNombre;
    private EditText edtApellidos;
    private EditText edtEmailRegistro;
    private EditText edtPasswordRegistro;

    private TextView btnCrearCuentaRegistro;

    private UsuarioViewModel usuarioViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // inicializar viewmodel
        usuarioViewModel = new UsuarioViewModel(this);

        // enlazar controles
        edtNombre = findViewById(R.id.edtNombre);
        edtApellidos = findViewById(R.id.edtApellidos);
        edtEmailRegistro = findViewById(R.id.edtEmailRegistro);
        edtPasswordRegistro = findViewById(R.id.edtPasswordRegistro);
        btnCrearCuentaRegistro = findViewById(R.id.btnCrearCuentaRegistro);

        // evento del botón crear cuenta
        btnCrearCuentaRegistro.setOnClickListener(v -> crearCuenta());
    }

    // registrar un nuevo usuario
    private void crearCuenta() {

        String nombre = edtNombre.getText().toString().trim();
        String apellidos = edtApellidos.getText().toString().trim();
        String correo = edtEmailRegistro.getText().toString().trim();
        String password = edtPasswordRegistro.getText().toString().trim();

        // validar nombre
        if (nombre.isEmpty()) {
            edtNombre.setError("Ingresa tu nombre");
            edtNombre.requestFocus();
            return;
        }

        // validar apellidos
        if (apellidos.isEmpty()) {
            edtApellidos.setError("Ingresa tus apellidos");
            edtApellidos.requestFocus();
            return;
        }

        // validar correo
        if (correo.isEmpty()) {
            edtEmailRegistro.setError("Ingresa tu correo");
            edtEmailRegistro.requestFocus();
            return;
        }

        // validar contraseña
        if (password.isEmpty()) {
            edtPasswordRegistro.setError("Ingresa tu contraseña");
            edtPasswordRegistro.requestFocus();
            return;
        }

        // verificar si el correo ya existe
        if (usuarioViewModel.existeCorreo(correo)) {

            Toast.makeText(
                    this,
                    "Este correo ya está registrado",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // crear objeto usuario
        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(correo);
        usuario.setPassword(password);
        usuario.setRol("cliente");

        // registrar usuario
        boolean registrado = usuarioViewModel.registrarUsuario(usuario);

        if (registrado) {

            Toast.makeText(
                    this,
                    "Cuenta creada correctamente",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(
                    new Intent(
                            RegistroActivity.this,
                            MainActivity.class
                    )
            );

            finish();

        } else {

            Toast.makeText(
                    this,
                    "No se pudo crear la cuenta",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}