package com.example.demo1;

// TODO STEP 1 : Import LangChain4j classes for image analysis
// import dev.langchain4j.data.image.Image;
// import dev.langchain4j.data.message.ImageContent;
// import dev.langchain4j.data.message.TextContent;
// import dev.langchain4j.data.message.UserMessage;
// import dev.langchain4j.model.chat.ChatLanguageModel;
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
 * Servlet to analyze uploaded images using AI
 * Uses a vision model (e.g.: llama3.2-vision, llama-3.2-11b-vision-preview)
 *
 * @author JavaOne Demo
 */
@MultipartConfig
@WebServlet("/uploadServlet")
public class ImageAnalyzerServlet extends HttpServlet {

    // TODO STEP 2 : Inject the ChatLanguageModel dedicated to image analysis
    // IMPORTANT : Use @Named("vision-model") to inject the configured vision model
    // This model MUST support image analysis (vision)
    // See microprofile-config.properties for the vision-model configuration
    // @Inject
    // @Named("vision-model")
    // ChatLanguageModel visionModel;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // TODO STEP 3 : Retrieve the uploaded file
        // Part file = request.getPart("file");

        // TODO STEP 4 : Create a UserMessage with the image and a question
        // UserMessage userMessage = UserMessage.from(
        //     TextContent.from("Decrit cette image en détails."),
        //     ImageContent.from(encodeBase64(file.getInputStream()), file.getContentType())
        // );

        // TODO STEP 5 : Call the vision model to analyze the image
        // ChatResponse answer = visionModel.chat(userMessage);

        // TODO STEP 6 : Send back the response
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("TODO: Wire image analysis here");
        // response.getWriter().write(answer.aiMessage().text());
    }

    /**
     * Extracts the filename from the HTTP headers
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
     * Encodes an InputStream to Base64 (required to send the image to the AI)
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
