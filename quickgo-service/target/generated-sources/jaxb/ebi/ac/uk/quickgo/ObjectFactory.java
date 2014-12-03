//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.10.17 at 11:49:09 AM BST 
//


package ebi.ac.uk.quickgo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ebi.ac.uk.quickgo package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Obo_QNAME = new QName("http://uk.ac.ebi/quickgo", "obo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ebi.ac.uk.quickgo
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Relationshiptype }
     * 
     */
    public Relationshiptype createRelationshiptype() {
        return new Relationshiptype();
    }

    /**
     * Create an instance of {@link Obotypedef }
     * 
     */
    public Obotypedef createObotypedef() {
        return new Obotypedef();
    }

    /**
     * Create an instance of {@link Termdeftype }
     * 
     */
    public Termdeftype createTermdeftype() {
        return new Termdeftype();
    }

    /**
     * Create an instance of {@link Synonymtype }
     * 
     */
    public Synonymtype createSynonymtype() {
        return new Synonymtype();
    }

    /**
     * Create an instance of {@link Xreftype }
     * 
     */
    public Xreftype createXreftype() {
        return new Xreftype();
    }

    /**
     * Create an instance of {@link Headertypedef }
     * 
     */
    public Headertypedef createHeadertypedef() {
        return new Headertypedef();
    }

    /**
     * Create an instance of {@link Deftype }
     * 
     */
    public Deftype createDeftype() {
        return new Deftype();
    }

    /**
     * Create an instance of {@link Synonymtypedef }
     * 
     */
    public Synonymtypedef createSynonymtypedef() {
        return new Synonymtypedef();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Obotypedef }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://uk.ac.ebi/quickgo", name = "obo")
    public JAXBElement<Obotypedef> createObo(Obotypedef value) {
        return new JAXBElement<Obotypedef>(_Obo_QNAME, Obotypedef.class, null, value);
    }

}
