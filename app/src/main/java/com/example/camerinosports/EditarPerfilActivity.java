package com.example.camerinosports;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Usuario;
import com.example.camerinosports.model.UsuarioSesion;
import com.example.camerinosports.viewmodel.UsuarioViewModel;
import com.example.camerinosports.util.Validaciones;

public class EditarPerfilActivity extends AppCompatActivity {

    private UsuarioViewModel usuarioViewModel;
    private Usuario usuarioActual;
    private EditText edtNombre;
    private EditText edtApellidos;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        usuarioViewModel = new UsuarioViewModel(this);
        edtNombre = findViewById(R.id.edtNombreEditarPerfil);
        edtApellidos = findViewById(R.id.edtApellidosEditarPerfil);
        edtPassword = findViewById(R.id.edtPasswordEditarPerfil);

        cargarUsuario();
        configurarEventos();
    }

    private void cargarUsuario() {
        usuarioViewModel.obtenerUsuarioApi(
                UsuarioSesion.getCorreoActual(),
                new UsuarioViewModel.ResultadoUsuario() {
                    @Override
                    public void alCompletar(Usuario usuario) {
                        usuarioActual = usuario;
                        edtNombre.setText(usuarioActual.getNombre());
                        edtApellidos.setText(usuarioActual.getApellidos());
                        edtPassword.setText("");
                        edtPassword.setHint("Nueva contraseña (mínimo 6 caracteres)");
                    }

                    @Override
                    public void alFallar(String mensaje) {
                        Toast.makeText(EditarPerfilActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void configurarEventos() {
        findViewById(R.id.btnVolverEditarPerfil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnGuardarEditarPerfil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarCambios();
            }
        });
    }

    private void guardarCambios() {
        if (usuarioActual == null) {
            Toast.makeText(this, "espera mientras se carga el perfil", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = edtNombre.getText().toString().trim();
        String apellidos = edtApellidos.getText().toString().trim();
        String password = edtPassword.getText().toString();

        if (!Validaciones.nombrePersona(nombre)) {
            edtNombre.setError("Usa solo letras");
            edtNombre.requestFocus();
            return;
        }

        if (!Validaciones.nombrePersona(apellidos)) {
            edtApellidos.setError("Usa solo letras");
            edtApellidos.requestFocus();
            return;
        }

        if (!password.isEmpty() && !Validaciones.password(password)) {
            edtPassword.setError("Mínimo 6 caracteres");
            edtPassword.requestFocus();
            return;
        }

        usuarioActual.setNombre(nombre);
        usuarioActual.setApellidos(apellidos);
        usuarioActual.setPassword(password);
        findViewById(R.id.btnGuardarEditarPerfil).setEnabled(false);
        usuarioViewModel.actualizarUsuarioApi(usuarioActual, new UsuarioViewModel.ResultadoUsuario() {
            @Override
            public void alCompletar(Usuario usuario) {
                Toast.makeText(EditarPerfilActivity.this,
                        "perfil actualizado", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void alFallar(String mensaje) {
                findViewById(R.id.btnGuardarEditarPerfil).setEnabled(true);
                Toast.makeText(EditarPerfilActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
