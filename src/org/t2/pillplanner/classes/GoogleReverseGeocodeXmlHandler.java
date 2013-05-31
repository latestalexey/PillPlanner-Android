package org.t2.pillplanner.classes;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
 
public class GoogleReverseGeocodeXmlHandler extends DefaultHandler 
{
    private boolean inLocalityName = false;
    private boolean inAddress = false;
    private boolean inCountry = false;
    private boolean finished = false;
    private StringBuilder lnbuilder;
    private StringBuilder adbuilder;
    private StringBuilder cobuilder;
    private String localityName;
    private String address;
    private String country;
     
    public String getLocalityName()
    {
        return this.localityName;
    }
    
    public String getAddress()
    {
        return this.address;
    }
    public String getCountry()
    {
        return this.country;
    }
     
    @Override
    public void characters(char[] ch, int start, int length)
           throws SAXException {
        super.characters(ch, start, length);
        if (this.inLocalityName && !this.finished)
        {
            if ((ch[start] != '\n') && (ch[start] != ' '))
            {
                lnbuilder.append(ch, start, length);
            }
        }
        if (this.inAddress && !this.finished)
        {
            if ((ch[start] != '\n') && (ch[start] != ' '))
            {
                adbuilder.append(ch, start, length);
            }
        }
        if (this.inCountry && !this.finished)
        {
            if ((ch[start] != '\n') && (ch[start] != ' '))
            {
                cobuilder.append(ch, start, length);
            }
        }
    }
 
    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException 
    {
        super.endElement(uri, localName, name);
         
        //if (!this.finished)
        {
            if (localName.equalsIgnoreCase("LocalityName"))
            {
                this.localityName = lnbuilder.toString();
                //this.finished = true;
            }
            if (localName.equalsIgnoreCase("Address"))
            {
                this.address = adbuilder.toString();
                //this.finished = true;
            }
            if (localName.equalsIgnoreCase("Country"))
            {
                this.country = cobuilder.toString();
                //this.finished = true;
            }
            
            /*if (builder != null)
            {
                builder.setLength(0);
            }*/
        }
    }
 
    @Override
    public void startDocument() throws SAXException 
    {
        super.startDocument();
        lnbuilder = new StringBuilder();
        adbuilder = new StringBuilder();
        cobuilder = new StringBuilder();
    }
 
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
    {
        super.startElement(uri, localName, name, attributes);
         
        if (localName.equalsIgnoreCase("LocalityName"))
        {
            this.inLocalityName = true;
        }
        if (localName.equalsIgnoreCase("Address"))
        {
            this.inAddress = true;
        }
        if (localName.equalsIgnoreCase("Country"))
        {
            this.inCountry = true;
        }
    }
}