package com.deportur.vista.util;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

/**
 * Utilidad para cargar y renderizar imágenes SVG
 */
public class SVGLoader {

    /**
     * Carga un archivo SVG desde el classpath y lo convierte en un ImageIcon
     * 
     * @param path Ruta del recurso SVG (ej. /com/deportur/resources/icons/user.svg)
     * @param width Ancho deseado
     * @param height Alto deseado
     * @return ImageIcon generado desde el SVG
     */
    public static ImageIcon loadSVG(String path, int width, int height) {
        try {
            URL url = SVGLoader.class.getResource(path);
            if (url == null) {
                System.err.println("No se pudo encontrar el recurso SVG: " + path);
                return SVGUtil.createPlaceholderIcon(path, width, height);
            }
            
            // Crear el documento SVG
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            SVGDocument document = factory.createSVGDocument(url.toString());
            
            // Configurar el transcoder para convertir a imagen
            BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
            
            // Establecer las dimensiones
            transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) height);
            
            // Transcodificar
            TranscoderInput input = new TranscoderInput(document);
            transcoder.transcode(input, null);
            
            // Obtener la imagen resultado
            BufferedImage image = transcoder.getImage();
            return new ImageIcon(image);
        } catch (Exception e) {
            System.err.println("Error al cargar el SVG: " + path + " - " + e.getMessage());
            return SVGUtil.createPlaceholderIcon(path, width, height);
        }
    }
    
    /**
     * Carga un SVG desde un InputStream
     */
    public static ImageIcon loadSVG(InputStream inputStream, int width, int height) {
        try {
            // Crear el documento SVG
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            SVGDocument document = factory.createSVGDocument(null, inputStream);
            
            // Configurar el transcoder
            BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
            transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) height);
            
            // Transcodificar
            TranscoderInput input = new TranscoderInput(document);
            transcoder.transcode(input, null);
            
            return new ImageIcon(transcoder.getImage());
        } catch (Exception e) {
            System.err.println("Error al cargar el SVG desde stream: " + e.getMessage());
            return new ImageIcon(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
        }
    }
    
    /**
     * Clase interna para transcodificar SVG a BufferedImage
     */
    private static class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage image = null;
        
        @Override
        public BufferedImage createImage(int w, int h) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }
        
        @Override
        public void writeImage(BufferedImage img, TranscoderOutput output) {
            this.image = img;
        }
        
        public BufferedImage getImage() {
            return image;
        }
    }
}