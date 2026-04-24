package com.example.demo1;

// TODO ÉTAPE 1 : Importer les classes LangChain4j pour l'analyse d'image
// import dev.langchain4j.data.image.Image;
// import dev.langchain4j.data.message.ImageContent;
// import dev.langchain4j.data.message.TextContent;
// import dev.langchain4j.data.message.UserMessage;
// import dev.langchain4j.model.chat.ChatModel;
// import dev.langchain4j.model.chat.response.ChatResponse;
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

    // TODO ÉTAPE 2 : Injecter le ChatModel dédié à l'analyse d'image
    // IMPORTANT : Utiliser @Named("vision-model") pour injecter le modèle de vision configuré
    // Ce modèle DOIT supporter l'analyse d'image (vision)
    // Voir microprofile-config.properties pour la configuration du vision-model
    // @Inject
    // @Named("vision-model")
    // ChatModel visionModel;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // TODO ÉTAPE 3 : Récupérer le fichier téléchargé
        // Part file = request.getPart("file");

        // TODO ÉTAPE 4 : Créer un UserMessage avec l'image et une question
        // UserMessage userMessage = UserMessage.from(
        //     TextContent.from("Décris cette image en détails."),
        //     ImageContent.from(encodeBase64(file.getInputStream()), file.getContentType())
        // );

        // TODO ÉTAPE 5 : Appeler le modèle de vision pour analyser l'image
        // ChatResponse answer = visionModel.chat(userMessage);

        // TODO ÉTAPE 6 : Renvoyer la réponse
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("TODO: Intégrer l'analyse d'image ici");
        // response.getWriter().write(answer.aiMessage().text());
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
