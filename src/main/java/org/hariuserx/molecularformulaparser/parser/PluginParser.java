package org.hariuserx.molecularformulaparser.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hari kishore
 */
public class PluginParser {

    private final Logger logger = LoggerFactory.getLogger(PluginParser.class);

    public Map<String, Float> getPluginData(){

        Map<String, Float> pluginData = new HashMap<>();

        ClassLoader classLoader = getClass().getClassLoader();

        String pluginFile = "static/AtomicMassData.plg";
        URL url = classLoader.getResource(pluginFile);

        if(url != null) {
            File file = new File(url.getFile());

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(file);


                document.getDocumentElement().normalize();

                NodeList nList = document.getElementsByTagName("element");

                for(int index = 0; index < nList.getLength(); index++){
                    Node nNode = nList.item(index);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        NodeList keys = eElement.getElementsByTagName("key");
                        NodeList massess = eElement.getElementsByTagName("mass");

                        if(keys.getLength() == 1 && massess.getLength() == 1){
                            String key = keys.item(0).getTextContent();
                            Float mass = null;
                            try{
                                mass = Float.valueOf(massess.item(0).getTextContent());
                            }catch (NumberFormatException e){
                                e.printStackTrace();
                                logger.warn("Unable to get mass for " + key + " Check the plugin file");
                            }

                            if(mass != null)
                                pluginData.put(key, mass);
                        }
                    }
                }

            } catch (ParserConfigurationException e) {
                logger.warn("Unable to initialize DocumentBuilder");
                e.printStackTrace();
            } catch (SAXException e) {
                logger.warn("Error while parsing file: " + pluginFile);
                e.printStackTrace();
            } catch (IOException e) {
                logger.warn("Unable to load plugin file: " + pluginFile);
                e.printStackTrace();
            }
        }else{
            logger.warn("Unable to find plugin file: " + pluginFile);
        }

        return pluginData;
    }
}
