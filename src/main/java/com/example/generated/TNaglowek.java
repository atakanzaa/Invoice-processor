//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2025.07.05 at 01:13:35 AM TRT 
//


package com.example.generated;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TNaglowek complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TNaglowek"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="KodFormularza" type="{http://crd.gov.pl/wzor/2023/06/29/12648/}TKodFormularza"/&gt;
 *         &lt;element name="WariantFormularza" type="{http://crd.gov.pl/wzor/2023/06/29/12648/}TZnakowy"/&gt;
 *         &lt;element name="DataWytworzeniaFa" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="SystemInfo" type="{http://crd.gov.pl/wzor/2023/06/29/12648/}TZnakowy"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TNaglowek", propOrder = {
    "kodFormularza",
    "wariantFormularza",
    "dataWytworzeniaFa",
    "systemInfo"
})
public class TNaglowek {

    @XmlElement(name = "KodFormularza", required = true)
    @XmlSchemaType(name = "string")
    protected TKodFormularza kodFormularza;
    @XmlElement(name = "WariantFormularza", required = true)
    protected String wariantFormularza;
    @XmlElement(name = "DataWytworzeniaFa", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataWytworzeniaFa;
    @XmlElement(name = "SystemInfo", required = true)
    protected String systemInfo;

    /**
     * Gets the value of the kodFormularza property.
     * 
     * @return
     *     possible object is
     *     {@link TKodFormularza }
     *     
     */
    public TKodFormularza getKodFormularza() {
        return kodFormularza;
    }

    /**
     * Sets the value of the kodFormularza property.
     * 
     * @param value
     *     allowed object is
     *     {@link TKodFormularza }
     *     
     */
    public void setKodFormularza(TKodFormularza value) {
        this.kodFormularza = value;
    }

    /**
     * Gets the value of the wariantFormularza property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWariantFormularza() {
        return wariantFormularza;
    }

    /**
     * Sets the value of the wariantFormularza property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWariantFormularza(String value) {
        this.wariantFormularza = value;
    }

    /**
     * Gets the value of the dataWytworzeniaFa property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataWytworzeniaFa() {
        return dataWytworzeniaFa;
    }

    /**
     * Sets the value of the dataWytworzeniaFa property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataWytworzeniaFa(XMLGregorianCalendar value) {
        this.dataWytworzeniaFa = value;
    }

    /**
     * Gets the value of the systemInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemInfo() {
        return systemInfo;
    }

    /**
     * Sets the value of the systemInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemInfo(String value) {
        this.systemInfo = value;
    }

}
