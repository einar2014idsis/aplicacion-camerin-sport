package com.example.camerinosports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Usuario;
import com.example.camerinosports.model.UsuarioSesion;
import com.example.camerinosports.viewmodel.UsuarioViewModel;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private UsuarioViewModel usuarioViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        usuarioViewModel = new UsuarioViewModel(this);
        configurarEventos();
        cargarUsuario();
    }

    private void configurarEventos() {
        findViewById(R.id.btnVolverPerfilUsuario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnPerfilMisPedidos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PerfilUsuarioActivity.this, MisPedidosActivity.class));
            }
        });

        findViewById(R.id.btnPerfilConsejos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PerfilUsuarioActivity.this, ConsejosActivity.class));
            }
        });

        findViewById(R.id.btnEditarPerfilUsuario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PerfilUsuarioActivity.this, EditarPerfilActivity.class));
            }
        });

        findViewById(R.id.btnCerrarSesionPerfil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UsuarioSesion.cerrarSesion();
                Intent intent = new Intent(PerfilUsuarioActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void cargarUsuario() {
        usuarioViewModel.obtenerUsuarioApi(
                UsuarioSesion.getCorreoActual(),
                new UsuarioViewModel.ResultadoUsuario() {
                    @Override
                    public void alCompletar(Usuario usuario) {
                        mostrarUsuario(usuario);
                    }

                    @Override
                    public void alFallar(String mensaje) {
                        ponerTexto(R.id.txtNombrePerfilUsuario, "Cliente Camerino");
                        ponerTexto(R.id.txtCorreoPerfilUsuario, mensaje);
                        ponerTexto(R.id.txtTelefonoPerfilUsuario, "Telefono: No registrado");
                        ponerTexto(R.id.txtRolPerfilUsuario, "Rol: Cliente");
                    }
                });
    }

    private void mostrarUsuario(Usuario usuario) {
        ponerTexto(R.id.txtNombrePerfilUsuario, usuario.getNombre() + " " + usuario.getApellidos());
        ponerTexto(R.id.txtCorreoPerfilUsuario, usuario.getCorreo());
        ponerTexto(R.id.txtTelefonoPerfilUsuario, "Telefono: No registrado");
        ponerTexto(R.id.txtRolPerfilUsuario, "Rol: " + usuario.getRol());
    }

    private void ponerTexto(int id, String texto) {
        TextView textView = findViewById(id);
        textView.setText(texto);
    }
}
