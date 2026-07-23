package com.example.demo1;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Servlet pour analyser des images téléchargées avec l'IA.
 * Utilise un modèle de vision (ex. : llama3.2-vision, llama-3.2-11b-vision-preview)
 *
 * @author JavaOne Demo
 */
@MultipartConfig
@WebServlet("/uploadServlet")
public class ImageAnalyzerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("TODO: Intégrer l'analyse d'image ici");
    }

    /**
     * Extrait le nom du fichier depuis les en-têtes HTTP
     */
    private static String getFilename(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return filename.substring(filename.lastIndexOf('/') + 1)
                              .substring(filename.lastIndexOf('\\') + 1);
            }
        }
        return null;
    }

    /**
     * Encode un InputStream en Base64 (nécessaire pour envoyer l'image à l'IA)
     */
    private static String encodeBase64(InputStream in) throws IOException {
        try (ByteArrayOutputStream tempBuffer = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = in.read(buffer)) != -1) {
                tempBuffer.write(buffer, 0, length);
            }
            return Base64.getEncoder().encodeToString(tempBuffer.toByteArray());
        }
    }
}
