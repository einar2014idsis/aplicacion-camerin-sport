package com.example.camerinosports.util;

import android.net.Uri;
import android.widget.ImageView;

import java.io.File;

public final class Imagenes {

    private Imagenes() {
    }

    public static void mostrar(ImageView imageView, String imagen, int imagenPredeterminada) {
        if (imagen == null || imagen.trim().isEmpty()) {
            imageView.setImageResource(imagenPredeterminada);
            return;
        }

        String valor = imagen.trim();

        if (valor.startsWith("content://") || valor.startsWith("file://")) {
            try {
                imageView.setImageURI(Uri.parse(valor));
                if (imageView.getDrawable() != null) {
                    return;
                }
            } catch (RuntimeException ignored) {
                // La URI puede pertenecer a otro dispositivo o haber perdido el permiso.
            }

            imageView.setImageResource(imagenPredeterminada);
            return;
        }

        File archivo = new File(valor);
        if (archivo.isFile()) {
            imageView.setImageURI(Uri.fromFile(archivo));
            if (imageView.getDrawable() != null) {
                return;
            }
        }

        int recurso = imageView.getContext()
                .getResources()
                .getIdentifier(valor, "drawable", imageView.getContext().getPackageName());

        imageView.setImageResource(recurso == 0 ? imagenPredeterminada : recurso);
    }
}
