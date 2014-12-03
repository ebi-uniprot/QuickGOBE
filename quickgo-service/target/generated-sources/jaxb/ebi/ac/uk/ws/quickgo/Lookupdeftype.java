//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.10.17 at 11:49:09 AM BST 
//


package ebi.ac.uk.ws.quickgo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for lookupdeftype complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="lookupdeftype">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="scope" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="namespace" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="def" type="{http://uk.ac.ebi/ws/quickgo}deftype"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="usage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="definition" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="synonym" type="{http://uk.ac.ebi/ws/quickgo}synonymtype" maxOccurs="unbounded"/>
 *         &lt;element name="alt_id" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="is_obsolete" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="replacedBy" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="consider" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="xref" type="{http://uk.ac.ebi/ws/quickgo}xreftype" maxOccurs="unbounded"/>
 *         &lt;element name="is_a" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="ancestors" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="accession" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gene_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="taxon_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="taxon_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lookupdeftype", propOrder = {
    "scope",
    "id",
    "name",
    "namespace",
    "def",
    "comment",
    "usage",
    "definition",
    "synonym",
    "altId",
    "isObsolete",
    "replacedBy",
    "consider",
    "xref",
    "isA",
    "ancestors",
    "accession",
    "geneName",
    "description",
    "taxonId",
    "taxonName"
})
public class Lookupdeftype {

    @XmlElement(required = true)
    protected String scope;
    @XmlElement(required = true)
    protected String id;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String namespace;
    @XmlElement(required = true)
    protected Deftype def;
    @XmlElement(required = true)
    protected String comment;
    @XmlElement(required = true)
    protected String usage;
    @XmlElement(required = true)
    protected String definition;
    @XmlElement(required = true)
    protected List<Synonymtype> synonym;
    @XmlElement(name = "alt_id", required = true)
    protected List<String> altId;
    @XmlElement(name = "is_obsolete")
    protected Boolean isObsolete;
    @XmlElement(required = true)
    protected List<String> replacedBy;
    @XmlElement(required = true)
    protected List<String> consider;
    @XmlElement(required = true)
    protected List<Xreftype> xref;
    @XmlElement(name = "is_a", required = true)
    protected List<String> isA;
    @XmlElement(required = true)
    protected String ancestors;
    @XmlElement(required = true)
    protected String accession;
    @XmlElement(name = "gene_name", required = true)
    protected String geneName;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(name = "taxon_id", required = true)
    protected String taxonId;
    @XmlElement(name = "taxon_name", required = true)
    protected String taxonName;

    /**
     * Gets the value of the scope property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the value of the scope property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScope(String value) {
        this.scope = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the namespace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the value of the namespace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamespace(String value) {
        this.namespace = value;
    }

    /**
     * Gets the value of the def property.
     * 
     * @return
     *     possible object is
     *     {@link Deftype }
     *     
     */
    public Deftype getDef() {
        return def;
    }

    /**
     * Sets the value of the def property.
     * 
     * @param value
     *     allowed object is
     *     {@link Deftype }
     *     
     */
    public void setDef(Deftype value) {
        this.def = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the usage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Sets the value of the usage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsage(String value) {
        this.usage = value;
    }

    /**
     * Gets the value of the definition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Sets the value of the definition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefinition(String value) {
        this.definition = value;
    }

    /**
     * Gets the value of the synonym property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the synonym property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSynonym().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Synonymtype }
     * 
     * 
     */
    public List<Synonymtype> getSynonym() {
        if (synonym == null) {
            synonym = new ArrayList<Synonymtype>();
        }
        return this.synonym;
    }

    /**
     * Gets the value of the altId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the altId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAltId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAltId() {
        if (altId == null) {
            altId = new ArrayList<String>();
        }
        return this.altId;
    }

    /**
     * Gets the value of the isObsolete property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsObsolete() {
        return isObsolete;
    }

    /**
     * Sets the value of the isObsolete property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsObsolete(Boolean value) {
        this.isObsolete = value;
    }

    /**
     * Gets the value of the replacedBy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the replacedBy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReplacedBy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getReplacedBy() {
        if (replacedBy == null) {
            replacedBy = new ArrayList<String>();
        }
        return this.replacedBy;
    }

    /**
     * Gets the value of the consider property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the consider property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConsider().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getConsider() {
        if (consider == null) {
            consider = new ArrayList<String>();
        }
        return this.consider;
    }

    /**
     * Gets the value of the xref property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xref property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXref().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Xreftype }
     * 
     * 
     */
    public List<Xreftype> getXref() {
        if (xref == null) {
            xref = new ArrayList<Xreftype>();
        }
        return this.xref;
    }

    /**
     * Gets the value of the isA property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the isA property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIsA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getIsA() {
        if (isA == null) {
            isA = new ArrayList<String>();
        }
        return this.isA;
    }

    /**
     * Gets the value of the ancestors property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAncestors() {
        return ancestors;
    }

    /**
     * Sets the value of the ancestors property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAncestors(String value) {
        this.ancestors = value;
    }

    /**
     * Gets the value of the accession property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Sets the value of the accession property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccession(String value) {
        this.accession = value;
    }

    /**
     * Gets the value of the geneName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeneName() {
        return geneName;
    }

    /**
     * Sets the value of the geneName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeneName(String value) {
        this.geneName = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the taxonId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxonId() {
        return taxonId;
    }

    /**
     * Sets the value of the taxonId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxonId(String value) {
        this.taxonId = value;
    }

    /**
     * Gets the value of the taxonName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxonName() {
        return taxonName;
    }

    /**
     * Sets the value of the taxonName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxonName(String value) {
        this.taxonName = value;
    }

}
