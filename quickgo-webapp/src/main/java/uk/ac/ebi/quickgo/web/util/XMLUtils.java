package uk.ac.ebi.quickgo.web.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtils {
    public static List<Element> getChildElements(Element elt, String name) {
        List<Element> list = new ArrayList<>();

        NodeList nodes = elt.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
            	if (name.equals(child.getNodeName())) {
            		list.add((Element)child);
            	}
            }
        }
        return list;
    }

    public static Element getChildElement(Element elt, String name) {
        NodeList nodes = elt.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (name.equals(node.getNodeName())) {
                	return (Element)node;
                }
            }
        }
        return null;
    }

    public static void getInnerText(StringBuilder sb, Element elt, String separator) {
        NodeList tns = elt.getChildNodes();
        for (int j = 0; j < tns.getLength(); j++) {
            Node tn = tns.item(j);
            if (tn.getNodeType() == Node.TEXT_NODE) {
                sb.append(tn.getNodeValue());
            }
            else if (tn.getNodeType() == Node.ELEMENT_NODE) {
                sb.append(separator);
                getInnerText(sb, (Element)tn, separator);
                sb.append(separator);
            }
        }
    }

    public static String getInnerText(Element elt) {
        StringBuilder sb = new StringBuilder();
        getInnerText(sb, elt, " ");
        return sb.toString();
    }
}
