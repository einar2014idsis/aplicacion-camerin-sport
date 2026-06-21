package com.example.camerinosports;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.camerinosports.viewmodel.UsuarioViewModel;
import com.example.camerinosports.model.Usuario;
import com.example.camerinosports.model.UsuarioSesion;
import com.example.camerinosports.util.Validaciones;

public class MainActivity extends AppCompatActivity {

    private View imgShoe;
    private View imgPlatform;
    private View logoCamerino;

    private TextView btnLogin;
    private TextView txtCrearCuenta;

    private EditText edtEmail;
    private EditText edtPassword;

    private UsuarioViewModel usuarioViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // inicializar viewmodel
        usuarioViewModel = new UsuarioViewModel(this);

        // ajustar pantalla completa
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });

        // enlazar controles
        logoCamerino = findViewById(R.id.logoCamerinoHeader);
        imgShoe = findViewById(R.id.imgShoe);
        imgPlatform = findViewById(R.id.imgPlatform);
        btnLogin = findViewById(R.id.btnLogin);
        txtCrearCuenta = findViewById(R.id.txtCrearCuenta);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        // iniciar animaciones
        animacionEntrada();
        animarZapatilla();
        animarPlataforma();
        animarBotonLogin();

        // configurar botones
        configurarBotonLogin();
        configurarCrearCuenta();
    }

    // configurar inicio de sesion
    private void configurarBotonLogin() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String correo = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // validar correo
            if (correo.isEmpty()) {
                edtEmail.setError("Ingresa tu correo");
                edtEmail.requestFocus();
                return;
            }
            if (!Validaciones.correo(correo)) {
                edtEmail.setError("Ingresa un correo válido");
                edtEmail.requestFocus();
                return;
            }

            // validar contrasena
            if (password.isEmpty()) {
                edtPassword.setError("Ingresa tu contraseña");
                edtPassword.requestFocus();
                return;
            }
            if (!Validaciones.password(password)) {
                edtPassword.setError("Mínimo 6 caracteres");
                edtPassword.requestFocus();
                return;
            }
            btnLogin.setEnabled(false);
            btnLogin.setText("INGRESANDO...");

            usuarioViewModel.iniciarSesion(correo, password, new UsuarioViewModel.ResultadoUsuario() {
                @Override
                public void alCompletar(Usuario usuario) {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("INICIAR SESIÓN");
                    abrirPantallaUsuario(usuario);
                }

                @Override
                public void alFallar(String mensaje) {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("INICIAR SESIÓN");
                    Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                }
            });
            }
        });
    }

    private void abrirPantallaUsuario(Usuario usuario) {
        String rol = usuario.getRol();
        UsuarioSesion.iniciarSesion(usuario.getCorreo(), rol);

        if ("admin".equals(rol)) {
            Toast.makeText(this, "Bienvenido administrador", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminActivity.class));
        } else {
            Toast.makeText(this, "Bienvenido a Camerino Sports", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CatalogoActivity.class));
        }

        finish();
    }

    // abrir pantalla de registro
    private void configurarCrearCuenta() {
        txtCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    // animacion inicial de logo, zapatilla y plataforma
    private void animacionEntrada() {
        logoCamerino.setAlpha(0f);
        imgShoe.setAlpha(0f);
        imgPlatform.setAlpha(0f);

        logoCamerino.setTranslationY(-25f);
        imgShoe.setTranslationY(25f);
        imgPlatform.setScaleX(0.75f);
        imgPlatform.setScaleY(0.75f);

        AnimatorSet entrada = new AnimatorSet();

        entrada.playTogether(
                ObjectAnimator.ofFloat(logoCamerino, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(logoCamerino, "translationY", -25f, 0f),
                ObjectAnimator.ofFloat(imgShoe, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(imgShoe, "translationY", 25f, 0f),
                ObjectAnimator.ofFloat(imgPlatform, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(imgPlatform, "scaleX", 0.75f, 1f),
                ObjectAnimator.ofFloat(imgPlatform, "scaleY", 0.75f, 1f)
        );

        entrada.setDuration(900);
        entrada.setInterpolator(new AccelerateDecelerateInterpolator());
        entrada.start();
    }

    // animar zapatilla flotante
    private void animarZapatilla() {
        ObjectAnimator flotar = ObjectAnimator.ofFloat(
                imgShoe,
                "translationY",
                0f,
                -10f,
                0f
        );

        flotar.setDuration(2200);
        flotar.setRepeatCount(ObjectAnimator.INFINITE);
        flotar.setRepeatMode(ObjectAnimator.RESTART);
        flotar.setInterpolator(new AccelerateDecelerateInterpolator());
        flotar.start();
    }

    // animar brillo de plataforma
    private void animarPlataforma() {
        ObjectAnimator pulso = ObjectAnimator.ofFloat(
                imgPlatform,
                "alpha",
                0.58f,
                1f,
                0.58f
        );

        pulso.setDuration(1600);
        pulso.setRepeatCount(ObjectAnimator.INFINITE);
        pulso.setRepeatMode(ObjectAnimator.RESTART);
        pulso.start();
    }

    // animar boton de login al presionar
    private void animarBotonLogin() {
        btnLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {

            switch (event.getAction()) {

                case android.view.MotionEvent.ACTION_DOWN:
                    v.animate()
                            .scaleX(0.97f)
                            .scaleY(0.97f)
                            .alpha(0.85f)
                            .setDuration(90)
                            .start();
                    break;

                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .alpha(1f)
                            .setDuration(120)
                            .start();
                    break;
            }

            return false;
            }
        });
    }
}
