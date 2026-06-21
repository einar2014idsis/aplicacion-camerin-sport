package com.example.camerinosports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Usuario;
import com.example.camerinosports.util.Validaciones;
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

        // evento del boton crear cuenta
        btnCrearCuentaRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearCuenta();
            }
        });
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
        if (!Validaciones.nombrePersona(nombre)) {
            edtNombre.setError("Usa solo letras; mínimo 2 caracteres");
            edtNombre.requestFocus();
            return;
        }

        // validar apellidos
        if (apellidos.isEmpty()) {
            edtApellidos.setError("Ingresa tus apellidos");
            edtApellidos.requestFocus();
            return;
        }
        if (!Validaciones.nombrePersona(apellidos)) {
            edtApellidos.setError("Usa solo letras");
            edtApellidos.requestFocus();
            return;
        }

        // validar correo
        if (correo.isEmpty()) {
            edtEmailRegistro.setError("Ingresa tu correo");
            edtEmailRegistro.requestFocus();
            return;
        }
        if (!Validaciones.correo(correo)) {
            edtEmailRegistro.setError("Ejemplo: usuario@gmail.com");
            edtEmailRegistro.requestFocus();
            return;
        }

        // validar contrasena
        if (password.isEmpty()) {
            edtPasswordRegistro.setError("Ingresa tu contraseña");
            edtPasswordRegistro.requestFocus();
            return;
        }
        if (!Validaciones.password(password)) {
            edtPasswordRegistro.setError("Mínimo 6 caracteres");
            edtPasswordRegistro.requestFocus();
            return;
        }
// crear objeto usuario
        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(correo);
        usuario.setPassword(password);
        usuario.setRol("cliente");
        btnCrearCuentaRegistro.setEnabled(false);
        btnCrearCuentaRegistro.setText("CREANDO...");

        usuarioViewModel.registrarUsuarioApi(usuario, new UsuarioViewModel.ResultadoUsuario() {
            @Override
            public void alCompletar(Usuario usuarioRegistrado) {
                btnCrearCuentaRegistro.setEnabled(true);
                btnCrearCuentaRegistro.setText("CREAR CUENTA");
                Toast.makeText(RegistroActivity.this,
                        "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegistroActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void alFallar(String mensaje) {
                btnCrearCuentaRegistro.setEnabled(true);
                btnCrearCuentaRegistro.setText("CREAR CUENTA");
                Toast.makeText(RegistroActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
